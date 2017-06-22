package controllers

import play.api.mvc._
import play.api.routing._

class JsRouter extends Controller {

  def javascriptRoutes = Action { implicit request =>
    import routes.javascript._
    Ok(
      JavaScriptReverseRouter("jsRoutes")(
        AppController.user,
        SignUpController.submit,
        SignInController.submit,
        AppController.signOut,
        AppController.limit,
        AppController.limitUser,
        AppController.userOnly,
        AppController.adminOnly,
        AppController.userOrAdmin
      )
    ).as("text/javascript")
  }

}