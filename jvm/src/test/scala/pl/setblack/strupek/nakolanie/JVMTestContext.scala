package pl.setblack.strupek.nakolanie

import java.util.UUID

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import pl.setblack.strupek.nakolanie.context.Context

import scala.concurrent.ExecutionContext

object JVMTestContext extends  Context {
  private implicit val system = ActorSystem("compiler")
  override def materializer = ActorMaterializer()
  override def executionContext: ExecutionContext = ExecutionContext.global
  override def uuidProvider: () => UUID =  () => new UUID(0, 1)
}
