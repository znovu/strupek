package pl.setblack.strupek.nakolanie.compiler.session

import java.util.concurrent.atomic.AtomicReference

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Route, StandardRoute}
import pl.setblack.strupek.nakolanie.code.Errors
import pl.setblack.strupek.nakolanie.code.Errors.{ErrorInModule, MissingSession, ModuleError, SessionError}
import pl.setblack.strupek.nakolanie.compiler.WorkerId
import pl.setblack.strupek.nakolanie.compiler.session.CompilationSystem.CompilationSessionSystem
import pl.setblack.strupek.nakolanie.session.SessionId
import pl.setblack.strupek.nakolanie.session.SessionId.rw
import scalaz.concurrent.Task
import scalaz.{-\/, \/}
import upickle.default._

import scala.util.Try

class SessionsEndpoint(sessions: CompilationSessionSystem) {

  import delorean._

  private val sessionsReference = new AtomicReference(sessions)

  def createRoute(): Route = {
    val route =
      pathPrefix("session") {
        pathPrefix(Segment) { sesid =>
          pathPrefix("module" / Segment) { moduleName =>
            path ("project"/ Segment) { projectName =>
              post {
                val sessionTask = sessionsReference.get().getSession(SessionId(sesid))
                val projectChance: Task[SessionError \/ WorkerId] = sessionTask.flatMap {
                  sessionOption: Option[CompilationSession.CompilationSessionAPI] =>
                    sessionOption.map { existingSession =>
                      existingSession.prepare(moduleName, projectName)
                        .map {  worker => wrapModuleError(worker.map {_.id()})} //TODO what do we return?
                    }.getOrElse( Task.point( -\/(MissingSession(sesid))))
                }
                onComplete( projectChance.unsafeToFuture()) { x =>
                  complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, x.toString))//TODO change
                }
              }
            }
          } ~
            pathEnd {
              get {
                val sessionTask = sessionsReference.get().getSession(SessionId(sesid))
                val sessionChance: Task[StandardRoute] = sessionTask.map {
                  sessionOption: Option[CompilationSession.CompilationSessionAPI] =>
                    sessionOption.map { existingSession =>
                      toResult(write(existingSession.id))
                    }.getOrElse(toError(s"No such session ${sesid}"))
                }
                onComplete(sessionChance.unsafeToFuture()) {
                  toResult(_)
                }
              }
            }

        } ~
          post {
            entity(as[String]) { _ =>
              onSuccess(sessionsReference.get().startSession().unsafeToFuture()) { newSession =>
                sessionsReference.updateAndGet(newSession.nextStateModifier(_))
                val createdSession = newSession.session
                complete(HttpEntity(ContentTypes.`application/json`, write(createdSession.id)))
              }
            }
          }
      }
    route
  }

  private def wrapModuleError[T](result : ModuleError \/ T ) =
    result.leftMap ( error => ErrorInModule(error).asInstanceOf[Errors.SessionError])


  private def toResult(res: Try[StandardRoute]) = \/.fromEither(res.toEither).leftMap[StandardRoute] {
    t => toError(t.getLocalizedMessage)
  } merge

  private def toResult(res: String): StandardRoute = complete(HttpEntity(ContentTypes.`application/json`, res))

  private def toError(error: String): StandardRoute =
    complete(HttpResponse(StatusCodes.NotFound, entity = error))
}
