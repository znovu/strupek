package pl.setblack.strupek.nakolanie.compiler.session

import java.util.UUID

import pl.setblack.strupek.nakolanie.context.Context
import pl.setblack.strupek.nakolanie.scanner.ProjectProvider
import pl.setblack.strupek.nakolanie.session.SessionId
import scalaz.concurrent.Task
import scalaz.{==>>, Order}

/**
  * All users sessions
  */
case class NewSession[T <: InitSessionService](nextStateModifier: T => T, session: CompilationSession.CompilationSessionAPI)

trait InitSessionService {

  def startSession(): Task[NewSession[_]]

  def getSession(id: SessionId): Task[Option[CompilationSession.CompilationSessionAPI]]
}

object CompilationSystem {

  class CompilationSessionSystem(
                                  private val sessions: ==>>[SessionId, CompilationSession.CompilationSessionAPI])
                                (implicit private val projectProvider: ProjectProvider, implicit private val ctx: Context) extends InitSessionService {
    implicit val stringOrdering = Order.fromScalaOrdering(scala.math.Ordering.String)
    implicit val sessionOrdering = Order.orderBy[SessionId, String](s => s.key)

    def this()(implicit  projectProvider: ProjectProvider, ctx: Context) = this(==>>.empty)

    override def startSession(): Task[NewSession[CompilationSessionSystem]] =
      Task.point {
        val newSession = new CompilationSession.InMemCompilationSession(SessionId(UUID.randomUUID().toString))
        NewSession({s => new CompilationSessionSystem(s.sessions + (newSession.id, newSession))}, newSession)
      }

    override def getSession(id: SessionId): Task[Option[CompilationSession.CompilationSessionAPI]] =
      Task.point(sessions.lookup(id))
  }

}


