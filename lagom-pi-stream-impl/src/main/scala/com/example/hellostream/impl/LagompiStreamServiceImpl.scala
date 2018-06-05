package com.example.hellostream.impl

import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.example.hellostream.api.LagompiStreamService
import com.example.hello.api.LagompiService

import scala.concurrent.Future

/**
  * Implementation of the LagompiStreamService.
  */
class LagompiStreamServiceImpl(lagompiService: LagompiService) extends LagompiStreamService {
  def stream = ServiceCall { hellos =>
    Future.successful(hellos.mapAsync(8)(lagompiService.hello(_).invoke()))
  }
}
