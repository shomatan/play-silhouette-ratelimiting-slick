package repositories

import com.mohiva.play.silhouette.api.LoginInfo
import models.User

import scala.concurrent.Future


trait UserRepository {

  /** Finds a user by its login info.
    *
    *  @param loginInfo The login info of the user to find.
    *  @return The found user or None if no user for the given login info could be found.
    */
  def find(loginInfo: LoginInfo): Future[Option[User]]

  /** Finds a user by its user ID.
    *
    *  @param userID The ID of the user to find.
    *  @return The found user or None if no user for the given ID could be found.
    */
  def find(userID: Long): Future[Option[User]]

  /** Finds all users.
    *
    *  @return All active users.
    */
  def find: Future[List[User]]

  /** Saves a user.
    *
    *  @param user The user to save.
    *  @return The saved user.
    */
  def save(user: User): Future[User]
}
