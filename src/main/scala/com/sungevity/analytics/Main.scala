package com.sungevity.analytics

import java.io.File

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import com.sungevity.analytics.server.{Configuration, Router}
import com.sungevity.analytics.utils.IOUtils
import com.typesafe.config.ConfigFactory
import spray.can.Http

object Main extends App {

  def help() {
    println(s"\nUsage: ${this.getClass.getName} <configuration file>\n")
  }

  if (args.length < 1) {
    Console.err.println("Incorrect number of input arguments.")
    help()
    sys.exit(1)
  }

  if (!IOUtils.isReadable(args(0))) {
    Console.err.println("Could not open configuration file.")
    sys.exit(2)
  }

  implicit val config = ConfigFactory.parseFile(new File(args(0)))

  implicit val system = ActorSystem("Apollo", config)

  val handler = system.actorOf(Props(new Router(config)), name = "router")

  val configuration = system.actorOf(Props(new Configuration(config)), name = "configuration")

  IO(Http) ! Http.Bind(handler, interface = "localhost", port = 9000)


}
