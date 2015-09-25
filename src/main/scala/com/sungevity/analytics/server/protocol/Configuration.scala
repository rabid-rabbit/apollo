package com.sungevity.analytics.server.protocol

import akka.actor.Actor
import com.typesafe.config.Config
import org.slf4j.LoggerFactory

class Configuration(config: Config) extends Actor{

  val log = LoggerFactory.getLogger(getClass.getName)

  override def receive = {

    case "get-configuration" => {
      sender ! config
      log.info(s"configuration has been sent to [$sender]")
    }

  }

}
