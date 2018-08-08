package pl.setblack.strupek.nakolanie.compiler.module.hq9

import akka.stream.scaladsl.{Source, SourceQueueWithComplete}
import akka.stream.{Materializer, OverflowStrategy}
import pl.setblack.strupek.nakolanie.code.{Code, Errors}
import pl.setblack.strupek.nakolanie.compiler.CompilationResult.{OutputLine, Started}
import pl.setblack.strupek.nakolanie.compiler.CompileSession.CompilationStream
import pl.setblack.strupek.nakolanie.compiler.{CompilationResult, CompilationWorker, CompileService}
import pl.setblack.strupek.nakolanie.scanner.CodeProject
import scalaz.\/
import scalaz.concurrent.Task

import scala.concurrent.ExecutionContext


class HQ9Compiler(implicit val materializer : Materializer, implicit val executionContext : ExecutionContext) {
  val interpreter = new HQ9Interpreter()

  def compileSingle(content: Errors.ModuleError \/ Code.FileContents, queue: SourceQueueWithComplete[CompilationResult])  =  {
    queue.offer(Started )

    content.foreach( fileContent => {
      println(s"dping ${fileContent}")
      val result  = interpreter.interpret(fileContent.default)
      result.runForeach { x =>
        println(x)
        queue.offer(x)
      }.andThen{  case _ => queue.complete()}
      println("iki!")
    })
    println("done")
  }

  def compile(project: CodeProject.Interface): CompilationStream = {
    val resultStream = Source.queue[CompilationResult](10, OverflowStrategy.dropTail)
    val prematerialized = resultStream.preMaterialize()
    val struct = project.readStructure
    struct.map {
      _.files.map {
        file => project.readFile(file.path)
      }.map {
        compileSingle(_, prematerialized._1)
      }
    }
    //prematerialized._1.complete()
    println("complete")
    prematerialized._2
  }

}


class HQ9Worker extends CompilationWorker {
  override def putFile(name: String, content: String): CompilationStream = ???

  override def compile(): CompilationStream = ???

  override def close(): Task[CompileService.CloseError] = ???
}