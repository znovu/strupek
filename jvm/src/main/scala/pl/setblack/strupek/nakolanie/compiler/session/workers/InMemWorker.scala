package pl.setblack.strupek.nakolanie.compiler.session.workers

import pl.setblack.strupek.nakolanie.compiler.CompileSession.CompilationStream
import pl.setblack.strupek.nakolanie.compiler.inmem.{InMemCode, InMemCompiler}
import pl.setblack.strupek.nakolanie.compiler.{CompilationMode, CompilationWorker, CompileService}
import scalaz.concurrent.Task

class InMemWorker(private val code: InMemCode, private val compiler :  InMemCompiler) extends CompilationWorker {

  override def putFile(name: String, content: String): CompilationStream = ???

  override def compile(mode: CompilationMode.CompilationMode): CompilationStream = compiler.compile(code)

  override def close(): Task[CompileService.CloseError] = ???
}
