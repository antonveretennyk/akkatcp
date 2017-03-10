package com.tennik.akkatcp.handlers

import akka.actor.Actor
import akka.event.{ Logging, LoggingAdapter }
import akka.io.Tcp
import akka.io.Tcp.{ Event, Received, ResumeReading, Write }
import akka.util.ByteString
import com.tennik.akkatcp.dao.{ InMemoryUserDAO, UserDao }
import com.tennik.akkatcp.handlers.LoginHandler.Ack
import com.tennik.akkatcp.models.User
import com.tennik.akkatcp.protobuf.test._
import org.mindrot.jbcrypt._

import scala.util.Success

class LoginHandler(userDAO: UserDao) extends Actor {
  val log: LoggingAdapter = Logging.getLogger(context.system, this)

  override def preStart: Unit = sender() ! ResumeReading

  private val cnt = collection.mutable.Map[String, Int]().withDefaultValue(0)

  def receive: Receive = {
    case Received(data) =>
      log.info(s"Data was received, sender == ${sender()}")

      val maybeReq = AutorizationRequest.validate(data.toArray)

      maybeReq match {
        case Success(req) =>
          userDAO.find(req.username).foreach {
            case user: User =>
              auth(req, user)
            case _ =>
              log.info(s"There is no user match username = ${req.username}")
              sender() ! Write(errorPayload, Ack)
          }

        case _ =>
          log.error("Could not parse Authorization request")
          sender() ! Write(errorPayload, Ack)
      }

    case Ack => sender() ! ResumeReading

    case _: Tcp.ConnectionClosed =>
      log.info("Connection was closed")
      context stop self
  }

  private def auth(req: AutorizationRequest, user: User) = {
    log.info(s"Find user with ${user.userName}")
    if (BCrypt.checkpw(req.pwd, user.password)) {
      cnt.update(user.userName, cnt(user.userName) + 1)
      log.info(s"count for ${user.userName} was incremented")
      sender() ! Write(payload(cnt(user.userName)), Ack)

    } else {
      sender() ! Write(errorPayload, Ack)
    }
  }

  def errorPayload = ByteString(AutorizationResponse(ERROR_CODE.ERROR, None).toByteArray)

  def payload(cnt: Int) = ByteString(AutorizationResponse(ERROR_CODE.OK, Option(cnt)).toByteArray)

}

object LoginHandler {
  case object Ack extends Event
}
