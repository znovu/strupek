package pl.setblack.strupek.nakolanie.compiler.inmem

import pl.setblack.strupek.nakolanie.compiler.CompileSession.CompilationStream

trait InMemCompiler {
  def compile(code: InMemCode): CompilationStream
}
