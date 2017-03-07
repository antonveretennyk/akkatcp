package com.tennik.akkatcp.dao

import java.util.concurrent.{ ConcurrentHashMap, ConcurrentMap }

import com.tennik.akkatcp.models.User
import org.mindrot.jbcrypt.BCrypt

import scala.collection.JavaConverters._

trait UserDao {
  def list: Iterable[User]
  def find(userName: String): Option[User]
  def create(user: User): Option[User]
  def update(user: User): Option[User]
  def delete(userName: String): Option[User]
}

//in memory storage
class InMemoryUserDAO extends UserDao {

  val storage: ConcurrentMap[String, User] = new ConcurrentHashMap[String, User]()

  import InMemoryUserDAO._

  def init() {
    storage.put("admin", User("admin", pwd("password")))
    storage.put("user1", User("user", pwd("password1")))
    storage.put("user2", User("user", pwd("password2")))
  }

  init()

  def list: Iterable[User] = storage.values().asScala
  def find(userName: String): Option[User] = Option(storage.get(userName))

  def create(user: User): Option[User] = Option(storage.put(user.userName, User(user.userName, pwd(user.password))))
  def update(user: User): Option[User] = {
    val oldUser = storage.get(user.userName)
    storage.put(user.userName, User(user.userName, pwd(user.password)))
    Option(oldUser)
  }
  def delete(userName: String): Option[User] = {
    val oldUser = storage.get(userName)
    storage.remove(userName, oldUser)
    Option(oldUser)
  }
}

object InMemoryUserDAO {
  def pwd(p: String): String = BCrypt.hashpw(p, BCrypt.gensalt())
}
