package pl.setblack.strupek.nakolanie.compiler.session

import java.nio.file.{Files, Path}

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import akka.testkit.TestProbe
import delorean._
import org.scalatest.{Assertion, AsyncFunSpec, Matchers}
import pl.setblack.strupek.nakolanie.TestResources
import pl.setblack.strupek.nakolanie.code.Errors
import pl.setblack.strupek.nakolanie.code.Errors.ModuleError
import pl.setblack.strupek.nakolanie.compiler.CompileSession.CompilationStream
import pl.setblack.strupek.nakolanie.scanner.CodeModule.CodeModule
import pl.setblack.strupek.nakolanie.scanner.{CodeProject, ProjectProvider}
import scalaz.{-\/, \/}
import org.typelevel.scalatest.DisjunctionValues._
import pl.setblack.strupek.nakolanie.compiler.CompilationResult
import akka.pattern.pipe
import pl.setblack.strupek.nakolanie.context.JVMContext

import scala.collection.immutable
import scala.concurrent.Future


class CompilationSessionTest extends AsyncFunSpec with Matchers {
  implicit val ctx = JVMContext
  implicit val materializer = ctx.materializer


  describe("compilation session") {
    val projectProvider = new SimpleProjectProvider(TestResources.modules)
    val session = new CompilationSession.InMemCompilationSession(SessionId("magicId"))(projectProvider, ctx)
    val worker = session.prepare("hq9sample", "prj1")
    it("shall prepare worker") {
      worker.map { _.isRight should be (true) } . unsafeToFuture
    }
    it("shall start compiling code") {
      val futureStream: Future[CompilationStream] = worker.map { _.value.compile() }. unsafeToFuture
      futureStream.flatMap {
        stream: CompilationStream =>
          val result: Future[immutable.Seq[CompilationResult]] =  stream.runWith(Sink.seq[CompilationResult])
          result.map( _.head should be (CompilationResult.Started))
      }
    }
    it("shall  compile  hello world") {
      val futureStream: Future[CompilationStream] = worker.map { _.value.compile() }. unsafeToFuture
      futureStream.flatMap {
        stream: CompilationStream =>
          val result: Future[immutable.Seq[CompilationResult]] =  stream.runWith(Sink.seq[CompilationResult])
          result.map( _.filter( _.isInstanceOf[CompilationResult.OutputLine] ).head should be (CompilationResult.OutputLine("Hello, world!")))
      }
    }
    it("shall  compile  new version of project") {
      val futureStream: Future[CompilationStream] = worker.
        map { w =>
          w.value.putFile("src/f1.hq9","q")
          w.value.compile()
        }
        . unsafeToFuture

      futureStream.flatMap {
        stream: CompilationStream =>
          val result: Future[immutable.Seq[CompilationResult]] =  stream.runWith(Sink.seq[CompilationResult])
          result.map( _.filter( _.isInstanceOf[CompilationResult.OutputLine] ).head should be (CompilationResult.OutputLine("q")))
      }
    }

  }
    /*
  class HappyProvider extends ProjectProvider {
    override def readProject(module: String, project: String): Errors.ModuleError \/ CodeProject.CodeProjectService = \/-()
  }*/
}

class SimpleProjectProvider(val startPath: Path) extends ProjectProvider {
  override def readProject(module: String, project: String): Errors.ModuleError \/ CodeProject.Service = {
      val modulePath = startPath.resolve(module)
      return if (Files.exists(modulePath)) {
        new CodeModule(modulePath).getProject(project)
      } else {
        -\/(Errors.MissingFolder.asInstanceOf[ModuleError])
      }
  }
}