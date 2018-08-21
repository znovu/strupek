package pl.setblack.strupek.nakolanie.server

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives.{as, complete, entity, get, path, post, onSuccess}
import akka.http.scaladsl.server.Route
import pl.setblack.strupek.nakolanie.compiler.session.CompilationSystem.CompilationSessionSystem

class SessionsEndpoint(private val sessions : CompilationSessionSystem) {

import delorean._

  def createRoute(): Route = {
    val route =
      path("session") {
        post {
          entity(as[String]) { credentials =>
            val newSession = sessions.startSession()
            onSuccess (newSession.unsafeToFuture()) { res =>
               val sessionId = res.session.id
                complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, s"<h1>created session  ${sessionId}</h1>"))
            }
          }
        }
      }
    route
  }
}
