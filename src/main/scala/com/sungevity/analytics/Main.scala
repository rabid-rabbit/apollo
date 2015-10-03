package com.sungevity.analytics

import java.io.File

import akka.actor.{Props, ActorSystem}
import akka.io.IO
import akka.routing.FromConfig
import com.sungevity.analytics.api.SparkApplicationContext
import com.sungevity.analytics.services.Configuration
import com.sungevity.analytics.services.http.Router
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

  val sparkContext = new SparkApplicationContext(config, Set("spark.cores.max" -> "1")) {
    override def applicationName: String = "Apollo"
  }

  implicit val system = ActorSystem("Apollo", config)

  val configuration = system.actorOf(Props(new Configuration(config)).withRouter(FromConfig()), name = "configuration")

  val scheduler = system.actorOf(Props(new services.Scheduler).withRouter(FromConfig()), name = "scheduler")

  val httpServer = system.actorOf(Props(new Router(sparkContext)).withRouter(FromConfig()), "http-server")

  IO(Http) ! Http.Bind(httpServer, interface = config.getString("spray.can.server.hostname"), port = config.getInt("spray.can.server.port"))


}
