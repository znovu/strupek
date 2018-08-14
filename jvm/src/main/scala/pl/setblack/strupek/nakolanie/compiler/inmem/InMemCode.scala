package pl.setblack.strupek.nakolanie.compiler.inmem

import pl.setblack.strupek.nakolanie.code.Errors
import pl.setblack.strupek.nakolanie.scanner.CodeProject
import scalaz.{==>>, Order, \/, \/-}

case class InMemCode(private val codeFiles : String ==>> Code) {
  implicit  val pathOrdering = Order.fromScalaOrdering( Ordering.String)


  def putFile( path: String, content : String ): InMemCode = this.copy( codeFiles  = codeFiles + (path, Code(path, content)))

  def allFiles( ) : List[Code] = codeFiles.values
}

object InMemCode {

  type CodeChance = Errors.ModuleError \/ InMemCode

  def empty() : InMemCode = InMemCode(==>>.empty)

  def fromProject( project: CodeProject.Service):CodeChance  = {
    val struct = project.readStructure

    val result = struct.flatMap {
      _.files.map {
        file =>
          project.readFile(file.path).map {
            _.default
          }.map {
            Code(file.path, _)
          }
      }.foldLeft (\/-(InMemCode.empty()).asInstanceOf[CodeChance]) (combineCode)
    }
    result
  }

  private def combineCode( inMemCode: CodeChance, newCode : Errors.ModuleError \/ Code) =
    inMemCode.flatMap( inMem =>newCode.map(code => inMem.putFile(code.path, code.code)))

}


case class Code(val path : String, val code : String) {

}