package com.tennik.akkatcp.models

import akka.actor.ActorRef

case class Count(sender: ActorRef, userName: String, counter: Int = 1)
