package pl.setblack.strupek.nakolanie.compiler.module.hq9

import akka.Done
import akka.stream.scaladsl.{Source, SourceQueueWithComplete}
import akka.stream.{Materializer, OverflowStrategy}
import pl.setblack.strupek.nakolanie.code.{Code, Errors}
import pl.setblack.strupek.nakolanie.compiler.CompilationResult.{OutputLine, Started}
import pl.setblack.strupek.nakolanie.compiler.CompileSession.CompilationStream
import pl.setblack.strupek.nakolanie.compiler.module.InMemCode
import pl.setblack.strupek.nakolanie.compiler.{CompilationMode, CompilationResult, CompilationWorker, CompileService}
import pl.setblack.strupek.nakolanie.scanner.CodeProject
import scalaz.\/
import scalaz.concurrent.Task

import scala.concurrent.{ExecutionContext, Future}


class HQ9Compiler(implicit val materializer: Materializer, implicit val executionContext: ExecutionContext) {
  import delorean._
  import scalaz.concurrent.Strategy.DefaultStrategy
  val interpreter = new HQ9Interpreter()

  def compileSingle(content: String, queue: SourceQueueWithComplete[CompilationResult]) : Task[Done] = {
    queue.offer(Started)
    val result = interpreter.interpret(content)
    result.runForeach { x =>
      queue.offer(x)
    }.toTask
  }

  def compile(code: InMemCode): CompilationStream = {
    val resultStream = Source.queue[CompilationResult](10, OverflowStrategy.dropTail)
    val prematerialized = resultStream.preMaterialize()
    val  queue = prematerialized._1
    val x= code.allFiles().map { aFile =>

      compileSingle(aFile.code, queue)
    }.foldLeft( Task.now(akka.Done).asInstanceOf[Task[Done]])((a:Task[Done],b:Task[Done]) => a.flatMap( _ => b))
        .map { _=> queue.complete()}.unsafePerformAsync {_ =>}

    prematerialized._2
  }

}


class HQ9Worker extends CompilationWorker {
  override def putFile(name: String, content: String): CompilationStream = ???


  override def compile(mode: CompilationMode.CompilationMode): CompilationStream = ???

  override def close(): Task[CompileService.CloseError] = ???
}