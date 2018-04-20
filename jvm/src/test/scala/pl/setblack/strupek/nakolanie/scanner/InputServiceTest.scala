package pl.setblack.strupek.nakolanie.scanner

import org.scalacheck.Gen
import org.scalacheck.Gen.{alphaNumChar, listOf}
import org.scalatest.prop.PropertyChecks
import org.scalatest.{FunSpec, Matchers}
import pl.setblack.strupek.nakolanie.TestResources
import pl.setblack.strupek.nakolanie.scanner.CodeModule.CodeModule
import pl.setblack.strupek.nakolanie.scanner.InputService.FilesInputService
import scalaz.Maybe.Empty


class InputServiceTest extends FunSpec with Matchers with PropertyChecks{
  describe("input service") {
    val path = TestResources.modules
    val inputService = new FilesInputService(path)
    it("should return emtpy for non existing path") {
      inputService.codeModule("some non existin path")._2 should be(Empty[CodeModule]())
    }

    it("should return module for simple path") {
      inputService.codeModule("test2")._2.toOption should be (defined)
    }

    it("should return empty for path with any non alphanum chars") {
      import Gen._
      val anyChar: Gen[Char] = choose(1.toChar, 128.toChar)
      val myChars = frequency((1,numChar), (7,alphaChar), (2, anyChar))
      val randomStr: Gen[String] =
        listOf(myChars).map(_.mkString)
      implicit val generatorDrivenConfig =
        PropertyCheckConfiguration( minSuccessful = 100)
      forAll (randomStr) {
        (name : String) =>
          val module = inputService.codeModule(name)._2
          if ( name.contains(".")) {
            module should be (Empty())
          }
      }
    }
    it ("should return empty for path traversing" ) {
      inputService.codeModule("../")._2 should be(Empty[CodeModule]())
    }
  }
}
