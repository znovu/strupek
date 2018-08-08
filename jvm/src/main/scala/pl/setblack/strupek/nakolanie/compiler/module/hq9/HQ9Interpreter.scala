package pl.setblack.strupek.nakolanie.compiler.module.hq9

import akka.NotUsed
import akka.stream.scaladsl.{Source, SourceQueue}
import pl.setblack.strupek.nakolanie.compiler.CompilationResult
import pl.setblack.strupek.nakolanie.compiler.CompilationResult.{ErrorLine, OutputLine}

class HQ9Interpreter {
  def interpret(code : String) :  Source[CompilationResult, NotUsed] = {
      Source.fromIterator[Char]( ()=> code.toLowerCase.iterator).map( c => interpretSymbol(c, code))

  }

  private def interpretSymbol ( character : Char, code : String) : CompilationResult =
      character match {
        case 'h' => OutputLine("Hello world!")
        case 'q' => OutputLine(code)
        case '9' => OutputLine("99 bottles of beer")
        case x:Char => ErrorLine(s"unknown symbol ${x}")
  }
}
