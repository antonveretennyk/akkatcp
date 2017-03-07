package com.tennik.akkatcp.handlers

import akka.actor.Actor
import akka.event.{Logging, LoggingAdapter}
import akka.io.Tcp
import akka.io.Tcp.{Received, Write}
import akka.util.ByteString
import com.tennik.akkatcp.dao.UserDAO
import com.tennik.akkatcp.models.User
import com.tennik.akkatcp.protobuf.test._
import org.mindrot.jbcrypt._

class LoginHandler extends Actor {
  val log: LoggingAdapter = Logging.getLogger(context.system, this)

  private val cnt = collection.mutable.Map[String, Int]().withDefaultValue(0)

  private val userDAO = new UserDAO

  def receive: Receive = {
    case Received(data) =>
      log.info(s"Data was received, sender == ${sender()}")
      val request = AutorizationRequest.parseFrom(data.toArray)
      userDAO.find(request.username).foreach {
        case user: User =>
          log.info(s"Find user with ${user.userName}")
          if (BCrypt.checkpw(request.pwd, user.password)) {
            cnt.update(user.userName, cnt(user.userName) + 1)
            log.info(s"count for ${user.userName} was incremented")
            val sndr = sender()
            log.info(s"sender is $sndr")
            sndr ! Write(payload(cnt(user.userName)))
          } else {
            sender() ! Write(errorPayload)
          }
        case _ =>
          log.info(s"There is no user match username = ${request.username}")
          sender() ! Write(errorPayload)
      }

    case _: Tcp.ConnectionClosed =>
      log.info("Connection was closed")
      context stop self
  }

  def errorPayload = ByteString(AutorizationResponse(ERROR_CODE.ERROR, None).toByteArray)

  def payload(cnt: Int) = ByteString(AutorizationResponse(ERROR_CODE.OK, Option(cnt)).toByteArray)

}
