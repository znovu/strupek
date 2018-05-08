package pl.setblack.strupek.nakolanie.compiler.session

import java.util.UUID

import pl.setblack.strupek.nakolanie.compiler
import pl.setblack.strupek.nakolanie.compiler.session
import scalaz.concurrent.Task
import scalaz.{==>>, Order}

/**
  * All users sessions
  */

case class NewSession(next: InitSessionService, session: CompilationSession.Interface)

trait InitSessionService {

  def startSession(): Task[NewSession]

  def getSession(id: SessionId): Task[Option[CompilationSession.Interface]]
}


object CompilationSystem {

  class CompilationSessionSystem(private val sessions: ==>>[SessionId, CompilationSession.Interface]) extends InitSessionService {
    implicit val stringOrdering = Order.fromScalaOrdering(scala.math.Ordering.String)
    implicit  val sessionOrdering = Order.orderBy[SessionId, String](s =>s.key )
    def this() = this(==>>.empty)

    override def startSession(): Task[NewSession] =
      Task.point {

        val newSession = new CompilationSession.Implementation(SessionId(UUID.randomUUID().toString))
        NewSession(new CompilationSessionSystem(this.sessions + (newSession.id, newSession)), newSession)
      }


    override def getSession(id: SessionId): Task[Option[CompilationSession.Interface]] =
      Task.point(sessions.lookup(id))

  }

}