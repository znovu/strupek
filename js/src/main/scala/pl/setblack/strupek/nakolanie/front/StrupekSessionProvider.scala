package pl.setblack.strupek.nakolanie.front

import pl.setblack.strupek.nakolanie.session.SessionId

import scala.scalajs.js.{JSON, Promise}
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel, ScalaJSDefined}
import org.scalajs.dom.experimental.{Fetch, HttpMethod, RequestInit}

import upickle.default._




@JSExportTopLevel("StrupekSessionProvider")
object StrupekSessionProvider {

  @JSExport
  def startSession() : Promise[SessionId] ={
    Promise.resolve[SessionId](
      Fetch.fetch("/api/session", RequestInit(method = HttpMethod.POST ) )
        .then[SessionId]( x => {
          x.text().then[SessionId](  (y:String) => {
            val session = read[SessionId](y)(SessionId.rw)
            session
          })//


           /*x.json().then[SessionId]( received => {
             println(JSON.stringify(received))
             println("rec:"+received)
             val session = received.asInstanceOf[JSSession]
             println("odebralem:"+session.key)
             SessionId(session.key)
           })*/


        }))

    //Promise.reject("a mnie się nie podoba wasza wredna gęba")
  }

}
