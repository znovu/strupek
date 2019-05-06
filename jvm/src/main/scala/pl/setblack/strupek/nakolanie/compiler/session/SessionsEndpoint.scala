package pl.setblack.strupek.nakolanie.compiler.session

import java.util.concurrent.atomic.AtomicReference

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Route, StandardRoute}
import pl.setblack.strupek.nakolanie.code.Errors
import pl.setblack.strupek.nakolanie.code.Errors.{ErrorInModule, MissingSession, ModuleError, SessionError}
import pl.setblack.strupek.nakolanie.compiler.session.CompilationSystem.CompilationSessionSystem
import pl.setblack.strupek.nakolanie.session.{SessionId, WorkerId}
import pl.setblack.strupek.nakolanie.session.SessionId.rw
import pl.setblack.strupek.nakolanie.stratchpad.API.Response
import scalaz.concurrent.Task
import scalaz.{-\/, \/}
import upickle.default._
import slogging.StrictLogging

import scala.util.Try

class SessionsEndpoint(sessions: CompilationSessionSystem) extends StrictLogging{

  import delorean._

  private val sessionsReference = new AtomicReference(sessions)

  def createRoute(): Route = {
    val route =
      pathPrefix("session") {
        pathPrefix(Segment) { sesid =>
          pathPrefix("module" / Segment) { moduleName =>
            path("project" / Segment) { projectName =>
              post {
                val sessionTask = sessionsReference.get().getSession(SessionId(sesid))
                val projectChance: Task[SessionError \/ WorkerId] = sessionTask.flatMap {
                  sessionOption: Option[CompilationSession.CompilationSessionAPI] =>
                    sessionOption.map { existingSession =>
                      existingSession.prepare(moduleName, projectName)
                        .map { worker => wrapModuleError(worker.map {
                          _.id()
                        })
                        } //TODO what do we return?
                    }.getOrElse(Task.point(-\/(MissingSession(sesid))))
                }
                onComplete(projectChance.unsafeToFuture()) { x =>
                  val   y: Try[StandardRoute] = x
                    .map( toResult(_))
                    .map( write(_))
                    .map( toResult(_))

                  //val res: Try[StandardRoute] = x.map(_.toString).map(toResult(_))
                  toResult(y)
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

  private def wrapModuleError[T](result: ModuleError \/ T) =
    result.leftMap(error => ErrorInModule(error).asInstanceOf[Errors.SessionError])

  private def toResult(res : SessionError \/ WorkerId ) =
      res
        .leftMap (  error => Response.error(error).asInstanceOf[Response[WorkerId]] )
        .map( result => Response(Some(result)) )
        .merge

  private def toResult(res: Try[StandardRoute]) = \/.fromEither(res.toEither).leftMap[StandardRoute] {
    t => toError(t)
  } merge

  private def toResult(res: String): StandardRoute = complete(HttpEntity(ContentTypes.`application/json`, res))

  private def toError(t: Throwable): StandardRoute = {
    val message = t.getLocalizedMessage
    logger.warn(message, t)
    toError(message)
  }

  import Bogus._

  private def toError(error: String, code : StatusCode = StatusCodes.InternalServerError): StandardRoute =
    complete(HttpResponse(StatusCodes.InternalServerError, entity = write(Response[Bogus](None, error))))
}


case class Bogus ( id: String) {

}

object Bogus  {
  import upickle.default.{macroRW, ReadWriter => RW}
  implicit def bogusRW : RW[Bogus] = macroRW
  implicit def bogusResponseRW  : RW [Response[Bogus]] = macroRW
}
