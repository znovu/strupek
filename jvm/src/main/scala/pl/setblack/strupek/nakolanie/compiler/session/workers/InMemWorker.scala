package pl.setblack.strupek.nakolanie.compiler.session.workers

import java.util.concurrent.atomic.AtomicReference

import akka.stream.scaladsl.Source
import pl.setblack.strupek.nakolanie.compiler.CompileSession.CompilationStream
import pl.setblack.strupek.nakolanie.compiler.inmem.{InMemCode, InMemCompiler}
import pl.setblack.strupek.nakolanie.compiler.{CompilationMode, CompilationWorker, CompileService}
import scalaz.concurrent.Task

class InMemWorker(code: InMemCode, private val compiler :  InMemCompiler) extends CompilationWorker {

  private val codeRef: AtomicReference[InMemCode] = new AtomicReference[InMemCode](code)

  override def putFile(name: String, content: String): CompilationStream = {
      codeRef.updateAndGet(_.putFile(name,content))
      Source.empty
  }


  override def compile(mode: CompilationMode.CompilationMode): CompilationStream = compiler.compile(codeRef.get())

  override def close(): Task[CompileService.CloseError] = ???
}
