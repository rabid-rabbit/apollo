package com.sungevity.analytics.services

import akka.actor.{ActorRef, Actor}
import com.sungevity.analytics.protocol._
import org.slf4j.LoggerFactory
import com.typesafe.akka.extension.quartz.QuartzSchedulerExtension

class Scheduler extends Actor{

  val log = LoggerFactory.getLogger(getClass.getName)

  val scheduler = QuartzSchedulerExtension(context.system)

  private def start(actor: ActorRef, schedule: String) = {
    val scheduleName = s"${actor.path.name}:$schedule"
    scheduler.schedules.get(scheduleName.toUpperCase) match {
      case None => scheduler.createSchedule(name = scheduleName, cronExpression = schedule)
      case _ => scheduler.cancelJob(scheduleName)
    }
    scheduler.schedule(scheduleName, actor, StartApplication)
    log.info(s"successfully scheduled [${actor.path.name}] each [$schedule]")
  }

  private def end(actor: ActorRef, schedule: String) = {
    val scheduleName = s"${actor.path.name}:$schedule"
    scheduler.schedules.get(scheduleName.toUpperCase) map (_ => scheduler.cancelJob(scheduleName))
    log.info(s"successfully unscheduled [$actor] from [$schedule]")
  }

  override def receive = {

    case Schedule(actor, schedule) => {
      try {
        start(actor, schedule)
        sender ! Scheduled
      } catch {
        case t: Throwable => {
          log.warn(s"Could not schedule [$actor]", t)
          sender ! new Exception(t.getMessage, t.getCause)
        }
      }
    }

    case UnSchedule(actor, schedule) => {
      try {
        end(actor, schedule)
        sender ! Unscheduled
      } catch {
        case t: Throwable => {
          log.warn(s"Could not unschedule [$actor]", t)
          sender ! new Exception(t.getMessage, t.getCause)
        }
      }
    }

  }

}
