package badlam

import pl.setblack.badlam.{Cardinals, Lambda}
import pl.setblack.badlam.analysis.SmartDisplay

object  Playfield {

  def main(args: Array[String]): Unit = {
        val autocall:Lambda = x => x(x)
        println(SmartDisplay.web.display(autocall))
        val OMEGA = autocall(autocall)
  }

}
