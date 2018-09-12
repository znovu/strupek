package pl.setblack.strupek.nakolanie.compiler.session

import java.util.concurrent.atomic.AtomicReference

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Route, StandardRoute}
import pl.setblack.strupek.nakolanie.compiler.session.CompilationSystem.CompilationSessionSystem
import pl.setblack.strupek.nakolanie.scanner.CodeModule
import pl.setblack.strupek.nakolanie.session.SessionId.rw
import pl.setblack.strupek.nakolanie.session.SessionId
import scalaz.{Maybe, \/}
import scalaz.concurrent.Task
import upickle.default._

import scala.util.Try

class SessionsEndpoint( sessions: CompilationSessionSystem) {

  import delorean._

  private val sessionsReference = new AtomicReference(sessions)

  def createRoute(): Route = {
    val route =
      pathPrefix("session") {
        path(Segment) { sesid =>
          get {

            val sessionTask = sessionsReference.get().getSession(SessionId(sesid))

            val sessionChance: Task[StandardRoute] = sessionTask.map {
              sessionOption: Option[CompilationSession.Interface] =>
                sessionOption.map { existingSession =>
                  toResult(write(existingSession.id))
                }.getOrElse( toError(s"No such session ${sesid}") )
            }
            onComplete(sessionChance.unsafeToFuture()) {
              toResult(_)
            }
          }
        } ~
      post {
          entity(as[String]) { credentials =>
            onSuccess( sessionsReference.get().startSession().unsafeToFuture()) { newSession =>
              sessionsReference.updateAndGet(  newSession.nextStateModifier(_))
              val createdSession = newSession.session
              complete(HttpEntity(ContentTypes.`application/json`, write(createdSession.id)))
            }
          }
        }
      }
    route
  }

  private def toResult(res: Try[StandardRoute] ) =  \/.fromEither(res.toEither).leftMap[StandardRoute]{
    t => toError(t.getLocalizedMessage)
  } merge

  private def toResult(res: String):StandardRoute = complete(HttpEntity(ContentTypes.`application/json`, res))

  private def toError(error: String):StandardRoute =
    complete(HttpResponse(StatusCodes.NotFound, entity = error))
}
