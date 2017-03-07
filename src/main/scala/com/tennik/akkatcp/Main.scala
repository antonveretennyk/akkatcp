package com.tennik.akkatcp

import akka.actor.{ ActorRef, ActorSystem, Props }
import com.tennik.akkatcp.dao.UserDAO
import com.tennik.akkatcp.servers.LoginServer

object Main extends App {
  val system = ActorSystem("servers")
  val server = system.actorOf(Props[LoginServer], "LoginServer")
}
