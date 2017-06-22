package utils.authentication

import play.api.libs.json.Json

/** Data used for User sign-in
  */
case class SignInData(
  email: String,
  password: String
)

object SignInData {
  implicit val jsonFormat = Json.format[SignInData]
}