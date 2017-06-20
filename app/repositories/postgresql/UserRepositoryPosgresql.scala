package repositories.postgresql

import java.util.UUID

import models.User
import repositories.{RepositorySlick, UserRepository}
import javax.inject.Inject

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.PasswordInfo
import play.api.db.slick.DatabaseConfigProvider
import utils.authorization.Roles

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class UserRepositoryPosgresql @Inject() (protected val dbConfigProvider: DatabaseConfigProvider) extends UserRepository with RepositorySlick {

  import driver.api._

  /**
    * Finds a user by its login info.
    *
    * @param loginInfo The login info of the user to find.
    * @return The found user or None if no user for the given login info could be found.
    */
  def find(loginInfo: LoginInfo): Future[Option[User]] = {
    val userQuery = for {
      dbLoginInfo <- loginInfoQuery(loginInfo)
      dbUserLoginInfo <- slickUserLoginInfos.filter(_.loginInfoId === dbLoginInfo.id)
      dbPasswordInfo <- slickPasswordInfos.filter(_.loginInfoId === dbLoginInfo.id)
      dbUser <- slickUsers.filter(_.id === dbUserLoginInfo.userID)
    } yield (dbUser, dbPasswordInfo)
    db.run(userQuery.result.headOption).map { resultOption =>
      resultOption.map {
        case (user, passwordInfo) =>
          User(
            UUID.fromString(user.userId),
            loginInfo,
            user.firstName,
            user.lastName,
            user.fullName,
            user.email,
            Some(PasswordInfo(passwordInfo.hasher, passwordInfo.password, passwordInfo.salt)),
            Roles.UserRole,
            user.rateLimit
          )
      }
    }
  }

  /**
    * Finds a user by its user ID.
    *
    * @param userID The ID of the user to find.
    * @return The found user or None if no user for the given ID could be found.
    */
  def find(userID: UUID): Future[Option[User]] = {
    val query = for {
      dbUser <- slickUsers.filter(_.id === userID.toString)
      dbUserLoginInfo <- slickUserLoginInfos.filter(_.userID === dbUser.id)
      dbLoginInfo <- slickLoginInfos.filter(_.id === dbUserLoginInfo.loginInfoId)
      dbPasswordInfo <- slickPasswordInfos.filter(_.loginInfoId === dbLoginInfo.id)
    } yield (dbUser, dbLoginInfo, dbPasswordInfo)
    db.run(query.result.headOption).map { resultOption =>
      resultOption.map {
        case (user, loginInfo, passwordInfo) =>
          User(
            UUID.fromString(user.userId),
            LoginInfo(loginInfo.providerID, loginInfo.providerKey),
            user.firstName,
            user.lastName,
            user.fullName,
            user.email,
            Some(PasswordInfo(passwordInfo.hasher, passwordInfo.password, passwordInfo.salt)),
            Roles.UserRole,
            user.rateLimit
          )
      }
    }
  }

  def find: Future[List[User]] = ???

  /**
    * Saves a user.
    *
    * @param user The user to save.
    * @return The saved user.
    */
  def save(user: User): Future[User] = {
    val dbUser = DBUser(user.userId.toString, user.firstName, user.lastName, user.fullName, user.email)
    val dbLoginInfo = DBLoginInfo(None, user.loginInfo.providerID, user.loginInfo.providerKey)
    // We don't have the LoginInfo id so we try to get it first.
    // If there is no LoginInfo yet for this user we retrieve the id on insertion.
    val loginInfoAction = {
      val retrieveLoginInfo = slickLoginInfos.filter(
        info => info.providerID === user.loginInfo.providerID &&
          info.providerKey === user.loginInfo.providerKey).result.headOption
      val insertLoginInfo = slickLoginInfos.returning(slickLoginInfos.map(_.id)).
        into((info, id) => info.copy(id = Some(id))) += dbLoginInfo
      for {
        loginInfoOption <- retrieveLoginInfo
        loginInfo <- loginInfoOption.map(DBIO.successful(_)).getOrElse(insertLoginInfo)
      } yield loginInfo
    }
    // combine database actions to be run sequentially
    val actions = (for {
      _ <- slickUsers.insertOrUpdate(dbUser)
      loginInfo <- loginInfoAction
      _ <- slickUserLoginInfos += DBUserLoginInfo(dbUser.userId, loginInfo.id.get)
    } yield ()).transactionally
    // run actions and return user afterwards
    db.run(actions).map(_ => user)
  }
}
