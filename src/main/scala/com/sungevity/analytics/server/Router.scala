package com.sungevity.analytics.server

import akka.actor.{Actor, ActorLogging, _}
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.config.Config
import spray.can.Http
import spray.can.server.Stats
import spray.http.HttpHeaders.`Content-Disposition`
import spray.http._
import spray.util._

import scala.concurrent.duration._

class Router(config: Config) extends Actor with ActorLogging {

  import HttpMethods._
  import MediaTypes._
  import context.dispatcher

  override def receive = {

    case _: Http.Connected => {
      sender ! Http.Register(self)
    }

    case HttpRequest(GET, Uri.Path("/server-stats"), _, _, _) =>

      implicit val timeout: Timeout = 1.second

      val client = sender
      context.actorSelection("/user/IO-HTTP/listener-0") ? Http.GetStats onSuccess {
        case x: Stats => client ! statsPresentation(x)
      }

    case HttpRequest(GET, Uri.Path("/nday-performance-analyzer"), _, _, _) => {

      implicit val timeout: Timeout = 1 day

      val client = sender

      context actorOf Props(new Streamer(client))

    }

    case HttpRequest(GET, _, _, _, _) =>
      sender ! index
  }

  lazy val index = HttpResponse(
    entity = HttpEntity(`text/html`,
      <html>
        <body>
          <h1>Say hello to <i>spray-can</i>!</h1>
          <p>Defined resources:</p>
          <ul>
            <li><a href="/ping">/ping</a></li>
            <li><a href="/stream">/stream</a></li>
            <li><a href="/server-stats">/server-stats</a></li>
            <li><a href="/crash">/crash</a></li>
            <li><a href="/timeout">/timeout</a></li>
            <li><a href="/timeout/timeout">/timeout/timeout</a></li>
            <li><a href="/stop">/stop</a></li>
          </ul>
          <p>Test file upload</p>
          <form action ="/file-upload" enctype="multipart/form-data" method="post">
            <input type="file" name="datafile" multiple=""></input>
            <br/>
            <input type="submit">Submit</input>
          </form>
        </body>
      </html>.toString()
    )
  )

  def statsPresentation(s: Stats) = HttpResponse(
    entity = HttpEntity(`text/html`,
      <html>
        <body>
          <h1>HttpServer Stats</h1>
          <table>
            <tr><td>uptime:</td><td>{s.uptime.formatHMS}</td></tr>
            <tr><td>totalRequests:</td><td>{s.totalRequests}</td></tr>
            <tr><td>openRequests:</td><td>{s.openRequests}</td></tr>
            <tr><td>maxOpenRequests:</td><td>{s.maxOpenRequests}</td></tr>
            <tr><td>totalConnections:</td><td>{s.totalConnections}</td></tr>
            <tr><td>openConnections:</td><td>{s.openConnections}</td></tr>
            <tr><td>maxOpenConnections:</td><td>{s.maxOpenConnections}</td></tr>
            <tr><td>requestTimeouts:</td><td>{s.requestTimeouts}</td></tr>
          </table>
        </body>
      </html>.toString()
    )
  )

  class Streamer(client: ActorRef) extends Actor with ActorLogging {
    log.debug("Starting streaming response ...")

    val worker = context.actorSelection("akka.tcp://AnomalyDetection@127.0.0.1:2553/user/nday-performance-analyzer")

    worker ! "run"

    client ! ChunkedResponseStart(
      HttpResponse(
        headers = List(`Content-Disposition`("attachment", Map("filename" -> "nday-performance-analyzer.csv")))
      )
    )

    def receive = {

      case data: Array[Byte] if data.length > 0 =>
        log.info("Sending response chunk ...")
        client ! MessageChunk(data)

      case "done" =>
        log.info("Finalizing response stream ...")
        client ! ChunkedMessageEnd
        context.stop(self)

      case x: Http.ConnectionClosed =>
        log.info("Canceling response stream due to {} ...", x)
        context.stop(self)
    }

  }

}

