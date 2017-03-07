package com.tennik.akkatcp.servers

import java.net.InetSocketAddress

import akka.actor.{ ActorRef, ActorSystem }
import akka.testkit.{ ImplicitSender, TestActorRef, TestKit, TestProbe }
import org.scalatest.{ BeforeAndAfterAll, Matchers, WordSpecLike }

class LoginServerTest() extends TestKit(ActorSystem("LoginServer")) with ImplicitSender
    with WordSpecLike with Matchers with BeforeAndAfterAll {
  import akka.io.Tcp._

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  val tcpManager = TestProbe()
  val handlerProbe = TestProbe()
  val sutRef = TestActorRef(new LoginServer {
    override def tcp: ActorRef = tcpManager.ref

    override def handler: ActorRef = handlerProbe.ref
  })
  val local = new InetSocketAddress("localhost", 6666)
  val remote = new InetSocketAddress("localhost", 6667)

  "LoginServer" must {
    "send a message Bind to the Tcp Manager at start" in {
      tcpManager.expectMsg(Bind(sutRef, local))
    }

    "send registered message on Connected" in {
      sutRef ! Connected(remote, local)
      expectMsg(Register(handlerProbe.ref))
    }
  }

}
