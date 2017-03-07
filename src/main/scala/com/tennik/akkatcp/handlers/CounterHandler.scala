package com.tennik.akkatcp.handlers

import akka.actor.{ Actor, ActorLogging }
import com.tennik.akkatcp.models.Count

class CounterHandler extends Actor with ActorLogging {

  private val cnt = collection.mutable.Map[String, Int]().withDefaultValue(0)

  override def receive: Receive = {
    case Count(sndr, userName, _) =>
      log.info(s"count for $userName was incremented")
      cnt.update(userName, cnt(userName) + 1)
      sender ! Count(sndr, userName, cnt(userName))
  }
}
