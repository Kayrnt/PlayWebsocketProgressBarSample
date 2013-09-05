package controllers

import play.api._
import play.api.mvc._
import views._

/**
 * User: Kayrnt
 * Date: 26/08/13
 * Time: 22:01
 */

object Application extends Controller {


  val jsAppRoute = "app"
  val jsAppAkkaRoute = "appAkka"

  // -- Actions

  /**
   * Home page
   */
  def index = Action {
    Ok(html.index("Progress bar sample with Web Socket", jsAppRoute))
  }

  /**
   * Home page with a session containing UUID that might be reused to see an ongoing process
   */
  def indexAkka = Action {
    request => {
      val (session, generatedUuid) = uuid(request)
      Ok(html.indexAkka("Progress bar sample with Web Socket with Akka", jsAppAkkaRoute)).withSession(session)
    }
  }

  //retrieve the uuid for the session and store a new one if required
  def uuid(request: RequestHeader) = {
    var session = request.session
    val uuid = session.get("uuid").getOrElse({
      val newUuid = java.util.UUID.randomUUID().toString()
      session = session.+("uuid", newUuid)
      newUuid
    })
    (session, uuid)
  }

  /**
   * Clear the session for Akka because we bind the session to UUID
   */
  def clearSession = Action {
    Ok("").withNewSession
  }

  // -- Javascript routing

  def javascriptRoutes = Action {
    implicit request =>
      import routes.javascript._
      Ok(
        Routes.javascriptRouter("jsRoutes")(
          Progress.process, ProgressAkka.process, routes.javascript.Application.clearSession
        )
      ).as("text/javascript")
  }


}
