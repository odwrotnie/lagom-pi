package com.example.hellostream.api

import akka.NotUsed
import akka.stream.scaladsl.Source
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}

/**
  * The lagom-pi stream interface.
  *
  * This describes everything that Lagom needs to know about how to serve and
  * consume the LagompiStream service.
  */
trait LagompiStreamService extends Service {

  def stream: ServiceCall[NotUsed, Source[Double, NotUsed]]

  override final def descriptor = {
    import Service._

    named("lagom-pi-stream")
      .withCalls(
        namedCall("stream", stream)
      ).withAutoAcl(true)
  }
}
