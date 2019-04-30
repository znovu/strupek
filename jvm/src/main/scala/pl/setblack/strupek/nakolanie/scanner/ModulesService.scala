package pl.setblack.strupek.nakolanie.scanner

import java.nio.file.{Files, Path}

import pl.setblack.strupek.nakolanie.scanner
import pl.setblack.strupek.nakolanie.scanner.CodeModule.CodeModule
import scalaz.Maybe


object ModulesService {

  type InputChance = Maybe[CodeModule]

  trait ModulesService {
    def codeModule(name: String): InputChance
  }

  class FileBasedModulesService(private val base: Path) extends ModulesService {
    override def codeModule(name: String): InputChance = {
      if (isBadName(name)) {
        Maybe.Empty()
      } else {
        val modulePath = base.resolve(name)
        if (!Files.isDirectory(modulePath)) {
          Maybe.Empty()
        } else {
          Maybe.Just(new scanner.CodeModule.CodeModule(modulePath))
        }
      }
    }

    private def isBadName(name : String) = name.chars().filter( c => !Character.isLetterOrDigit(c)).findAny().isPresent
  }

}
