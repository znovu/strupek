package pl.setblack.strupek.nakolanie.scanner

import org.scalatest.{FunSpec, Matchers}
import pl.setblack.strupek.nakolanie.TestResources
import pl.setblack.strupek.nakolanie.scanner.CodeModule.CodeModule
import pl.setblack.strupek.nakolanie.scanner.InputService.FilesInputService

import scalaz.Maybe.Empty


class InputServiceTest extends FunSpec with Matchers{
  describe("input service") {
      val path = TestResources.modules
      val inputService = new FilesInputService(path)
      it ( "should not give non existing service") {
          inputService.codeModule("some non existin path")._2 should be (Empty[CodeModule]())
      }
  }
}
