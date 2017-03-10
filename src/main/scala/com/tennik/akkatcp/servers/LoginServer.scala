package com.tennik.akkatcp.servers

import java.net.InetSocketAddress

import akka.actor._
import akka.event.Logging
import akka.event.LoggingAdapter
import akka.actor.{ Actor, ActorLogging, Props }
import akka.io.{ IO, Tcp }
import com.tennik.akkatcp.dao.InMemoryUserDAO
import com.tennik.akkatcp.handlers.LoginHandler

class LoginServer extends Actor {

  import Tcp._
  import context.system

  val log: LoggingAdapter = Logging.getLogger(context.system, this)

  tcp ! Tcp.Bind(self, new InetSocketAddress("localhost", 6666), pullMode = true)

  def tcp = IO(Tcp)

  def handler: ActorRef = context.actorOf(Props(new LoginHandler(userDAO = new InMemoryUserDAO)))

  def receive: PartialFunction[Any, Unit] = {
    case CommandFailed(_: Bind) =>
      log.warning(s"Binding for address localhost:6666 was failed")
      context stop self

    case Connected(remote, local) =>
      log.info(s"Connection for $remote was established")
      val connection = sender()
      connection ! Register(handler, keepOpenOnPeerClosed = true)
      sender() ! ResumeAccepting(batchSize = 1)

    case Bound(localAddress) =>
      sender() ! ResumeAccepting(batchSize = 1)

  }

}
