package pl.setblack.strupek.nakolanie.context

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

import scala.concurrent.ExecutionContext

object JVMContext extends  Context {
  private implicit val system = ActorSystem("compiler")
  override def materializer = ActorMaterializer()
  override def executionContext: ExecutionContext = ExecutionContext.global
}
