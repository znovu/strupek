package pl.setblack.strupek.nakolanie.compiler.session

import org.scalatest.{AsyncFunSpec, Matchers}
import pl.setblack.strupek.nakolanie.code.Errors
import pl.setblack.strupek.nakolanie.scanner.{CodeProject, ProjectProvider}
import scalaz.{\/, \/-}
import delorean._

class CompilationSessionTest extends AsyncFunSpec with Matchers {
  describe("compilation session") {
    val projectProvider = new DummyProjectProvider()
    val session = new CompilationSession.Implementation(SessionId("magicId"))(projectProvider)
    it("shall prepare worker") {
      val worker = session.prepare("test2", "boolean")
      worker.map { _ should not be null } . unsafeToFuture
    }
  }
    /*
  class HappyProvider extends ProjectProvider {
    override def readProject(module: String, project: String): Errors.ModuleError \/ CodeProject.CodeProjectService = \/-()
  }*/

}

class DummyProjectProvider extends ProjectProvider {
  override def readProject(module: String, project: String): Errors.ModuleError \/ CodeProject.Service = ???
}