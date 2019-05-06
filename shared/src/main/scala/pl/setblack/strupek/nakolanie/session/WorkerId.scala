package pl.setblack.strupek.nakolanie.session


import pl.setblack.strupek.nakolanie.stratchpad.API.Response
import upickle.default.{macroRW, ReadWriter => RW}

case class WorkerId( key : String)


object WorkerId {
  implicit def rw: RW[WorkerId] = macroRW
  implicit def responseRW  : RW [Response[WorkerId]] = macroRW
}
