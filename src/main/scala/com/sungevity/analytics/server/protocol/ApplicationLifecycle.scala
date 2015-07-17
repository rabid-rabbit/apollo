package com.sungevity.analytics.server.protocol

import akka.actor.Actor
import com.sungevity.analytics.protocol.{GetApplicationRegistryEntry, RemoveApplicationRegistryEntry, RemoveApplicationRegistryEntryConfirmed, ApplicationRegistryEntryConfirmed, ApplicationRegistryEntry}
import scala.collection.mutable.{Set => MutableSet}

class ApplicationLifecycle extends Actor {

  val applicationRegistry = MutableSet.empty[ApplicationRegistryEntry]

  override def receive = {

    case entry: ApplicationRegistryEntry => {
      applicationRegistry += entry
      sender ! ApplicationRegistryEntryConfirmed
    }

    case RemoveApplicationRegistryEntry(applicationName) => {
      for(entry <- applicationRegistry.find(_.applicationName == applicationName)){
        applicationRegistry.remove(entry)
        sender ! RemoveApplicationRegistryEntryConfirmed
      }
    }

    case GetApplicationRegistryEntry(applicationName) => {
      for(entry <- applicationRegistry.find(_.applicationName == applicationName)){
        sender ! entry
      }
    }

  }

}
