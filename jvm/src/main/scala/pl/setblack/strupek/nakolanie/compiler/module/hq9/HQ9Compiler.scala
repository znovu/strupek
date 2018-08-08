package pl.setblack.strupek.nakolanie.compiler.module.hq9

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, Materializer, OverflowStrategy}
import akka.stream.scaladsl.{Source, SourceQueueWithComplete}
import pl.setblack.strupek.nakolanie.code.{Code, Errors}
import pl.setblack.strupek.nakolanie.compiler.{CompilationResult, CompilationWorker, CompileService}
import pl.setblack.strupek.nakolanie.compiler.CompilationResult.Started
import pl.setblack.strupek.nakolanie.scanner.CodeProject
import scalaz.concurrent.Task
import pl.setblack.strupek.nakolanie.compiler.CompileSession.CompilationStream
import scalaz.\/


class HQ9Compiler(implicit val materializer : Materializer) {
  val interpreter = new HQ9Interpreter()

  def compileSingle(content: Errors.ModuleError \/ Code.FileContents, queue: SourceQueueWithComplete[CompilationResult])  =  {
    queue.offer(Started )

    content.foreach( fileContent => {
      val result  = interpreter.interpret(fileContent.default)
      result.runForeach( queue.offer(_))
    })
  }

  def compile(project: CodeProject.Interface): CompilationStream = {
    val resultStream = Source.queue[CompilationResult](10, OverflowStrategy.backpressure)
    val prematerialized = resultStream.preMaterialize()
    val struct = project.readStructure
    struct.map {
      _.files.map {
        file => project.readFile(file.path)
      }.map {
        compileSingle(_, prematerialized._1)
      }
    }
    prematerialized._1.complete()
    prematerialized._2
  }

}


class HQ9Worker extends CompilationWorker {
  override def putFile(name: String, content: String): CompilationStream = ???

  override def compile(): CompilationStream = ???

  override def close(): Task[CompileService.CloseError] = ???
}