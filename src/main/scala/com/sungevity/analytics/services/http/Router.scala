package com.sungevity.analytics.services.http

import com.sungevity.analytics.api.{SparkApplicationContextAware, SparkApplicationContext}
import spray.routing.HttpServiceActor

class Router(val applicationContext: SparkApplicationContext) extends HttpServiceActor with HDFSStaticFiles with SparkApplicationContextAware {

  override def receive = runRoute {
    pathPrefix("hdfs") {

      getFile

    }
  }

}
