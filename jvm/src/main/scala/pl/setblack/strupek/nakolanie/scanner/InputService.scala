package pl.setblack.strupek.nakolanie.scanner

import java.nio.file.Path

import pl.setblack.strupek.nakolanie.scanner.CodeModule.CodeModule
import scalaz.Maybe


object InputService {

  type InputChance = (InputService, Maybe[CodeModule])

  trait InputService {
      def codeModule( name : String) : InputChance
  }

  class FilesInputService(private val base : Path) extends InputService {
    override def codeModule(name: String): (InputService, Maybe[CodeModule]) = (this, Maybe.Empty())
  }

}
