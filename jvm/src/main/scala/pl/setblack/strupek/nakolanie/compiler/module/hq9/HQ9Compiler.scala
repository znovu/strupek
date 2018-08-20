package pl.setblack.strupek.nakolanie.compiler.module.hq9

import akka.Done
import akka.stream.OverflowStrategy
import akka.stream.scaladsl.{Source, SourceQueueWithComplete}
import pl.setblack.strupek.nakolanie.compiler.CompilationResult
import pl.setblack.strupek.nakolanie.compiler.CompilationResult.Started
import pl.setblack.strupek.nakolanie.compiler.CompileSession.CompilationStream
import pl.setblack.strupek.nakolanie.compiler.inmem.{InMemCode, InMemCompiler}
import pl.setblack.strupek.nakolanie.context.Context
import scalaz.concurrent.Task

//(implicit val materializer: Materializer, implicit val executionContext: ExecutionContext)
class HQ9Compiler(implicit val ctx: Context) extends InMemCompiler{
  import delorean._
  import scalaz.concurrent.Strategy.DefaultStrategy
  implicit val materializer  = ctx.materializer
  implicit val ec  = ctx.executionContext
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
        .map { _=>
          queue.offer(CompilationResult.ProgramEnd)
          queue.complete()
        }.unsafePerformAsync {_ =>}

    prematerialized._2
  }

}


