package pl.setblack.strupek.nakolanie.compiler.session

import java.util.concurrent.atomic.AtomicReference

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import pl.setblack.strupek.nakolanie.compiler.session.CompilationSystem.CompilationSessionSystem

class SessionsEndpoint( sessions: CompilationSessionSystem) {

  import delorean._
  private val sessionsReference = new AtomicReference(sessions)

  def createRoute(): Route = {
    val route =
      pathPrefix("session") {
        path(Segment) { sesid =>
          get {
            println(s"session to ${sesid}")
            onSuccess( sessionsReference.get().getSession(SessionId(sesid)).unsafeToFuture()) {
                res: Option[CompilationSession.Interface] =>
                   val result = res.map {
                      session => s"<h1> got session ${session.id}</h1>"
                  }.getOrElse(s"<h1>es ist mir sehr alles eins</h1>")
                  complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, result))
            }
          }
        } ~
      post {
          entity(as[String]) { credentials =>
            onSuccess( sessionsReference.get().startSession().unsafeToFuture()) { newSession =>
              sessionsReference.updateAndGet(  newSession.nextStateModifier(_))
              val createdSesson = newSession.session
              complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, s"""{ "session" : ${createdSesson.id}}"""))
            }

            /*sessionsReference.updateAndGet{ sess =>

            }

            val newSession = sessions.startSession()
            onSuccess(newSession.unsafeToFuture()) { res =>
              val sessionId = res.session.id
              ///val sessions = res.nextState
              ???
              complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, s"""{ "session" : ${sessionId}}"""))
            }*/
          }
        }
      }
    route
  }
}
