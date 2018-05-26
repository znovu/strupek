package pl.setblack.strupek.nakolanie.compiler

import java.nio.file.Path

import akka.NotUsed
import akka.stream.scaladsl.Source
import pl.setblack.strupek.nakolanie.compiler.CompileService.CloseError
import scalaz.concurrent.Task




trait CompilationWorker  {
  type CompilationStream  = Source[CompilationResult, NotUsed]

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


    case object Started extends CompilationResult

    case class OutputLine( output : String,  error  :String) extends CompilationResult

    case object Finished

}