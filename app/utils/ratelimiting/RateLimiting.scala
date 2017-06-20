package utils.ratelimiting

import java.time.{ Duration, LocalDateTime }

import javax.inject.{ Inject, Named, Singleton }

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import akka.util.Timeout.durationToTimeout

import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.{ Request, Result, Results }

import com.mohiva.play.silhouette.api.Environment
import com.mohiva.play.silhouette.api.actions.{ SecuredActionBuilder, SecuredRequest, SecuredRequestHandler }

import utils.authentication.DefaultEnv

@Singleton
class SecuredRateLimitingAction @Inject() (
    securedRequestHandler: SecuredRequestHandler,
    environment: Environment[DefaultEnv],
    @Named(RateLimitActor.Name) val userLimitActor: ActorRef) extends SecuredActionBuilder(securedRequestHandler.apply(environment)) {

  import RateLimitActor._

  type SecReq[T] = SecuredRequest[DefaultEnv, T]

  private val logger: Logger = Logger(this.getClass())

  override def invokeBlock[A](request: Request[A], block: SecReq[A] => Future[Result]) = {
    val blockWithHeaders = withLimitHeaders(block)
    super.invokeBlock(request, blockWithHeaders)
  }

  private def withLimitHeaders[A](block: SecReq[A] => Future[Result]): SecReq[A] => Future[Result] = { request =>

    implicit val timeout: Timeout = 5.seconds

    val user = request.identity
    val userUUID = request.identity.userId

    (userLimitActor ? Update(user)).mapTo[Option[(UserLimit, Boolean)]] flatMap {
      case None => Future.successful(Results.Unauthorized) // if User isn't in the Map
      case Some((userLimit, permitted)) =>

        val remainingSecs = Duration.between(LocalDateTime.now, userLimit.expirationTime).getSeconds
        val headers = List(
          "X-Rate-Limit-Limit" -> userLimit.limit.toString,         // # of allowed requests in the current period
          "X-Rate-Limit-Remaining" -> userLimit.remaining.toString, // # of remaining requests in the current period
          "X-Rate-Limit-Reset" -> remainingSecs.toString)           // # of seconds left in the current period

        if (permitted) {
          logger.info("Permitted user: " + user.fullName)
          block(request).map(_.withHeaders(headers: _*))
        } else {
          logger.warn("Refused user: " + user.fullName)
          val remainingSecs = Duration.between(LocalDateTime.now, userLimit.expirationTime).getSeconds
          val refuseResponse = Results.TooManyRequests.withHeaders(headers: _*)
          Future.successful(refuseResponse)
        }
    }
  }

}
