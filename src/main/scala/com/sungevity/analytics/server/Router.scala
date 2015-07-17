package com.sungevity.analytics.server

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import com.sungevity.analytics.protocol.{ApplicationRegistryEntry, GetApplicationRegistryEntry}
import com.sungevity.analytics.server.protocol._
import com.typesafe.config.Config
import spray.can.Http
import spray.can.server.Stats
import spray.http._

import scala.concurrent.duration._

class Router(config: Config) extends Actor with ActorLogging {

  import HttpMethods._
  import MediaTypes._
  import context.dispatcher

  val configuration = context.actorOf(Props(new Configuration(config)), name = "configuration")

  val lifecycle = context.actorOf(Props[ApplicationLifecycle], name = "lifecycle")

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

    case HttpRequest(GET, Uri.Path(path), _, _, _) => {

      implicit val timeout: Timeout = 1 day

      lifecycle ? GetApplicationRegistryEntry(path.drop(1)) onSuccess {
        case entry: ApplicationRegistryEntry => {
          val client = sender
          val worker = context.actorSelection(entry.address)
          context actorOf Props(new DataStream(client, worker))
        }
      }

    }

    case HttpRequest(GET, _, _, _, _) =>
      sender ! index
  }

  lazy val index = HttpResponse(
    entity = HttpEntity(`text/html`,
      view.index.toString()
    )
  )

  def statsPresentation(s: Stats) = HttpResponse(
    entity = HttpEntity(`text/html`,
      view.stats(s).toString()
    )
  )

}

