package com.tennik.akkatcp.handlers

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import akka.util.ByteString
import com.tennik.akkatcp.protobuf.test.{AutorizationRequest, AutorizationResponse, ERROR_CODE}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.duration._

class LoginHandlerTest() extends TestKit(ActorSystem("LoginHandlerSystem"))
    with ImplicitSender with WordSpecLike with Matchers with BeforeAndAfterAll {

  import akka.io.Tcp._

  val counter = 1
  def payload(cnt: Int) = ByteString(AutorizationResponse(ERROR_CODE.OK, Option(cnt)).toByteArray)

  def adminAuthRequest = ByteString(AutorizationRequest.toByteArray(AutorizationRequest("admin", "password")))
  def user1AuthRequest = ByteString(AutorizationRequest.toByteArray(AutorizationRequest("user1", "password1")))

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "LoginHandler" must {

    "response with counter == 1 if receive Received message with \"admin\" user" in {

      val sut = TestActorRef(Props[LoginHandler], "Hndlr")

      sut ! Received(adminAuthRequest)

      expectMsg(Write(payload(1)))

    }

    "respose with counter == 2 for 2 received messages" in {
      val sut = TestActorRef(Props[LoginHandler], "DoubleHndlr")
      within(500 millis) {
        sut ! Received(adminAuthRequest)
        expectMsg(Write(payload(1)))

        sut ! Received(adminAuthRequest)
        expectMsg(Write(payload(2)))
      }
    }

    "response with counter1 == 2, counter2 =1 in case of requests for different users" in {
      val sut = TestActorRef(Props[LoginHandler], "TripleHndlr")
      within(500 millis) {
        sut ! Received(adminAuthRequest)
        expectMsg(Write(payload(1)))

        sut ! Received(user1AuthRequest)
        expectMsg(Write(payload(1)))

        sut ! Received(adminAuthRequest)
        expectMsg(Write(payload(2)))

      }
    }
  }
}
