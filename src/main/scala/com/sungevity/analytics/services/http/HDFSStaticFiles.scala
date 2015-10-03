package com.sungevity.analytics.services.http

import akka.actor._
import com.sungevity.analytics.api.{SparkApplicationContextAware, HDFSShare}
import org.apache.commons.io.FilenameUtils
import spray.http.HttpHeaders.`Content-Disposition`
import spray.http.MediaTypes._
import spray.http._
import spray.httpx.marshalling._
import spray.routing._

trait HDFSStaticFiles extends Directives with HDFSShare {

  self: SparkApplicationContextAware =>

  implicit def streamToResponseMarshallable(stream: Stream[String]): ToResponseMarshallable = {

    val streamMarshaller = ToResponseMarshaller.fromMarshaller()(Marshaller[Stream[String]] {
      (value, ctx) =>
        if (value.isEmpty) { ctx.marshalTo(HttpEntity.Empty) }
        else actorRefFactory.actorOf(Props(new MetaMarshallers.ChunkingActor(Marshaller.stringMarshaller(`text/plain`), ctx))) ! value
    })

    new ToResponseMarshallable {
      override def marshal(ctx: ToResponseMarshallingContext): Unit = {
        streamMarshaller(stream, ctx)
      }
    }

  }

  implicit def actorRefFactory: ActorContext

  def getFile = path(Rest) {

    resource =>

      get {

          stream(resource).map {

            stream =>

              respondWithHeader(`Content-Disposition`("attachment", Map("filename" -> FilenameUtils.getName(resource)))) {

                complete(stream.toStream.filter(!_.isEmpty).map(_ + "\n"))

              }

          }.getOrElse(complete(s"$resource not found."))

      } ~ complete("Method is not supported.")

  } ~ complete("Please specify resource path.")

}
