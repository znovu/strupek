package pl.setblack.strupek.nakolanie.compiler.session.workers

import pl.setblack.strupek.nakolanie.compiler.CompileSession.CompilationStream
import pl.setblack.strupek.nakolanie.compiler.module.InMemCode
import pl.setblack.strupek.nakolanie.compiler.{CompilationMode, CompilationWorker, CompileService}
import scalaz.concurrent.Task

class InMemWorker(private val code: InMemCode ) extends CompilationWorker {

  override def putFile(name: String, content: String): CompilationStream = ???

  override def compile(mode: CompilationMode.CompilationMode): CompilationStream = {
  ???
  }

  override def close(): Task[CompileService.CloseError] = ???
}
