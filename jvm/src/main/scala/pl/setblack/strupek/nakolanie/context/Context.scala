package pl.setblack.strupek.nakolanie.context

import java.util.UUID

import akka.stream.Materializer

import scala.concurrent.ExecutionContext

trait Context {
  def materializer: Materializer
  def executionContext: ExecutionContext
  def uuidProvider: () => UUID
}
