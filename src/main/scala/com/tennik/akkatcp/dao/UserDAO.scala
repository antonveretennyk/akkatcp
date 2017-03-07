package com.tennik.akkatcp.dao

import java.util.concurrent.{ ConcurrentHashMap, ConcurrentMap }
import scala.concurrent.ExecutionContext.Implicits.global

import com.tennik.akkatcp.models.User
import org.mindrot.jbcrypt.BCrypt

import scala.collection.JavaConverters._
import scala.concurrent.Future

//in memory storage
class UserDAO {

  val storage: ConcurrentMap[String, User] = new ConcurrentHashMap[String, User]()

  import UserDAO._
  storage.put("admin", User("admin", pwd("password")))
  storage.put("user1", User("user", pwd("password1")))
  storage.put("user2", User("user", pwd("password2")))

  def list: Future[Iterable[User]] = Future(storage.values().asScala)
  def find(userName: String): Option[User] = Option(storage.get(userName))

  def create(user: User): Future[Option[User]] = Future { Option(storage.put(user.userName, User(user.userName, pwd(user.password)))) }
  def update(user: User): Future[Option[User]] = Future {
    val oldUser = storage.get(user.userName)
    storage.put(user.userName, User(user.userName, pwd(user.password)))
    Option(oldUser)
  }
  def delete(userName: String): Future[Option[User]] = Future {
    val oldUser = storage.get(userName)
    storage.remove(userName, oldUser)
    Option(oldUser)
  }
}

object UserDAO {
  def pwd(p: String): String = BCrypt.hashpw(p, BCrypt.gensalt())
}
