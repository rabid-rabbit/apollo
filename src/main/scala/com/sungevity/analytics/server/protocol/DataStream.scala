package com.sungevity.analytics.server.protocol

import akka.actor.{ActorSelection, ActorLogging, Actor, ActorRef}
import spray.can.Http
import spray.http.HttpHeaders.`Content-Disposition`
import spray.http.{ChunkedMessageEnd, MessageChunk, HttpResponse, ChunkedResponseStart}

class DataStream(client: ActorRef, worker: ActorSelection) extends Actor with ActorLogging {
  log.debug("Starting streaming response ...")

  worker ! "run"

  client ! ChunkedResponseStart(
    HttpResponse(
      headers = List(`Content-Disposition`("attachment", Map("filename" -> "nday-performance-analyzer.csv")))
    )
  )

  def receive = {

    case data: Array[Byte] if data.length > 0 =>
      client ! MessageChunk(data)

    case "done" =>
      client ! ChunkedMessageEnd
      context.stop(self)

    case x: Http.ConnectionClosed =>
      context.stop(self)
  }

}
