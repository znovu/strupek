package pl.setblack.strupek.nakolanie.session

import upickle.default.{macroRW, ReadWriter => RW}

case class SessionId( key : String)


object SessionId {
  implicit def rw: RW[SessionId] = macroRW
}
