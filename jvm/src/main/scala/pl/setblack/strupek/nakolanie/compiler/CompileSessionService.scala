package pl.setblack.strupek.nakolanie.compiler

import java.nio.file.Path

import akka.NotUsed
import akka.stream.scaladsl.Source
import pl.setblack.strupek.nakolanie.compiler.CompileService.CloseError
import pl.setblack.strupek.nakolanie.compiler.CompileSession.CompilationStream
import scalaz.concurrent.Task


object CompileSession {
  type CompilationStream  = Source[CompilationResult, NotUsed]
}


trait CompilationWorker  {

  def putFile( name : String, content : String) : CompilationStream

  def compile() : CompilationStream

  def close() : Task[CloseError]
}


object  CompileService {

  case class CloseError( problem : String)

}

case class CompilationType(name : String)

trait SourcesProvider {

  def copyFilesTo(destination: Path) : Task[CopyStats]

  case class CopyStats( copiedFiles : Int);
}


sealed trait CompilationResult {


}

object  CompilationResult {
  case object Started extends CompilationResult

  case class OutputLine( output : String = "") extends CompilationResult

  case class ErrorLine( output : String = "") extends CompilationResult

  case class WarnLine( output : String = "") extends CompilationResult

  case object Finished
}