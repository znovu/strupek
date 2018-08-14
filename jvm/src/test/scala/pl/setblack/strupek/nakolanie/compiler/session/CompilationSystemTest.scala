package pl.setblack.strupek.nakolanie.compiler.session

import org.scalatest.{AsyncFunSpec, FunSpec, Matchers}
import pl.setblack.strupek.nakolanie.TestResources
import pl.setblack.strupek.nakolanie.compiler.session.CompilationSystem.CompilationSessionSystem
import pl.setblack.strupek.nakolanie.context.JVMContext

class CompilationSystemTest extends AsyncFunSpec with Matchers {

  import delorean._
  implicit val projectProvider = new SimpleProjectProvider(TestResources.modules)
  implicit val ctx = JVMContext
  describe("compilation system") {
    val sys = new CompilationSessionSystem()
    it("shall create new session") {
      sys.startSession()
        .map(x => x.session.id.key should not be empty)
        .unsafeToFuture
    }
    it("shall create 2 different sessions") {
      sys.startSession()
        .flatMap(x => {
          val ses2 = x.next
          ses2.startSession().map(
            y => (y.session.id should not equal(x.session.id))
          )
        }).unsafeToFuture
    }

    it ("shall recognize created session ") {
      (for {
        newSession <- sys.startSession
        newSys = newSession.next
        existing <- newSys.getSession(newSession.session.id)
      } yield newSession.session.id should equal(existing.get.id)) unsafeToFuture
    }
  }
}
