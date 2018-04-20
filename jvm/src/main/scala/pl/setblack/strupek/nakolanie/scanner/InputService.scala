package pl.setblack.strupek.nakolanie.scanner

import java.nio.file.{Files, Path}

import pl.setblack.strupek.nakolanie.scanner
import pl.setblack.strupek.nakolanie.scanner.CodeModule.CodeModule
import scalaz.Maybe


object InputService {

  type InputChance = (InputService, Maybe[CodeModule])

  trait InputService {
    def codeModule(name: String): InputChance
  }

  class FilesInputService(private val base: Path) extends InputService {
    override def codeModule(name: String): (InputService, Maybe[CodeModule]) = {
      val modulePath = base.resolve(name)
      if (isBadName(name) ||  !Files.isDirectory(modulePath)) {
        (this, Maybe.Empty())
      } else {
        (this, Maybe.Just(new scanner.CodeModule.CodeModule(modulePath)))
      }
    }

    private def isBadName(name : String) = name.chars().filter( c => !Character.isLetterOrDigit(c)).findAny().isPresent
  }

}
