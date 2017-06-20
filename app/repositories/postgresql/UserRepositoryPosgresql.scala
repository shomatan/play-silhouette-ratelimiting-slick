package repositories.postgresql

import repositories.{RepositorySlick, UserRepository}
import java.sql.Timestamp
import javax.inject.Inject

import com.mohiva.play.silhouette.api.LoginInfo
import models.User
import org.joda.time.DateTime
import play.api.db.slick.DatabaseConfigProvider
import utils.ratelimiting.RateLimitActor

import scala.concurrent.Future

class UserRepositoryPosgresql @Inject() (protected val dbConfigProvider: DatabaseConfigProvider) extends UserRepository with RepositorySlick {

  import driver.api._

  private val Users = TableQuery[UserTable]

  private case class DBUser(
    userId: String,
    firstName: Option[String],
    lastName: Option[String],
    fullName: Option[String],
    email: Option[String],
    rateLimit: Long = RateLimitActor.DefaultLimit
  )

  //def find(loginInfo: LoginInfo): Future[Option[User]] = db.run(Users.filter(_.id === id).result.headOption)

  def find(): Future[List[User]] = ???
  def find(loginInfo: LoginInfo): Future[Option[User]] = ???
  def find(userID: Long): Future[Option[User]] = ???
  def save(user: models.User): Future[models.User] = ???



  private class UserTable(tag: Tag) extends Table[DBUser](tag, "users") {

    implicit val dateColumnType = MappedColumnType.base[DateTime, Long](d => d.getMillis, d => new DateTime(d))

    def id = column[String]("user_id", O.PrimaryKey)
    def firstName = column[Option[String]]("first_name")
    def lastName = column[Option[String]]("last_name")
    def fullName = column[Option[String]]("full_name")
    def email = column[Option[String]]("email")
    def rateLimit = column[Long]("rate_limit")

    def * = (id, firstName, lastName, fullName, email, rateLimit) <> (DBUser.tupled, DBUser.unapply _)
  }
}
