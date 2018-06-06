package com.example.hello.api

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}

trait LagompiService extends Service {

  def leibniz(n: Long): ServiceCall[NotUsed, Double]

  override final def descriptor = {
    import Service._
    named("lagom-pi")
      .withCalls(
        ???
      )
      .withAutoAcl(true)
  }
}
