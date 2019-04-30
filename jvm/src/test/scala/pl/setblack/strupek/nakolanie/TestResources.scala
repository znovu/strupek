package pl.setblack.strupek.nakolanie

import java.nio.file.Paths

import pl.setblack.strupek.nakolanie.scanner.CodeModuleServiceTest

object TestResources {
  val modules = Paths.get(classOf[CodeModuleServiceTest].getResource("/modules").toURI)
  val test1 = Paths.get(classOf[CodeModuleServiceTest].getResource("/modules/test1").toURI)
  val hq9sample = Paths.get(classOf[CodeModuleServiceTest].getResource("/modules/hq9sample").toURI)

}




object Crosser {

  def main(args: Array[String]): Unit = {
    cross()
  }

import RandomAbsolute.random
  def cross(): Int = {

    def mergeBits(bits: List[Boolean], crossingPlace: Int, bits1:List[Boolean]):List[Boolean] = {
      bits.take(crossingPlace) ++ bits1.drop(crossingPlace)
    }

    random(7)  ( it => {
      println(it)
      4
    }
    )

  }
}

object RandomAbsolute {

  def random[T](bound:Int) (funit :Int => T)  = {
      funit(bound*3)
  }
}