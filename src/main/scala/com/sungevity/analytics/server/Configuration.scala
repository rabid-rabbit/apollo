package com.sungevity.analytics.server

import akka.actor.Actor
import com.typesafe.config.Config

class Configuration(config: Config) extends Actor{

  override def receive = {

    case "get-configuration" => sender ! config

  }

}
