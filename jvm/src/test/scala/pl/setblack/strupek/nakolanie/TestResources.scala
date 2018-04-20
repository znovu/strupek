package pl.setblack.strupek.nakolanie

import java.nio.file.Paths

import pl.setblack.strupek.nakolanie.scanner.CodeModuleServiceTest

object TestResources {
  val modules = Paths.get(classOf[CodeModuleServiceTest].getResource("/modules").toURI)
  val test1 = Paths.get(classOf[CodeModuleServiceTest].getResource("/modules/test1").toURI)
}
