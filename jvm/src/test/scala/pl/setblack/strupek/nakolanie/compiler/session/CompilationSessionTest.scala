package pl.setblack.strupek.nakolanie.compiler.session

import org.scalatest.{AsyncFunSpec, Matchers}
import pl.setblack.strupek.nakolanie.code.Errors
import pl.setblack.strupek.nakolanie.scanner.{CodeProject, ProjectProvider}
import scalaz.{\/, \/-}

class CompilationSessionTest extends AsyncFunSpec with Matchers {
  describe("compilation session") {
    val session = new CompilationSession.Implementation(SessionId("magicId"))
   it("shall prepare worker") {
      //session.prepare("test2", "boolean")
    }
  }

  class HappyProvider extends ProjectProvider {
    override def readProject(module: String, project: String): Errors.ModuleError \/ CodeProject.CodeProjectService = \/-()
  }
}

class DummyProjectProvider extends ProjectProvider {
  override def readProject(module: String, project: String): Errors.ModuleError \/ CodeProject.Interface = ???
}