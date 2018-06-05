package com.example.hellostream.impl

import akka.stream.scaladsl.Source
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.example.hellostream.api.LagompiStreamService
import com.example.hello.api.LagompiService
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.{ExecutionContext, Future}

class LagompiStreamServiceImpl(lagompiService: LagompiService)(implicit ec: ExecutionContext)
  extends LagompiStreamService
    with LazyLogging {

  def stream = ServiceCall { _ =>

    val src = Source(0l to 100000l)
    var pi: Double = 0d

    Future.successful(src.mapAsync(1) { x: Long =>
      lagompiService.leibniz(x)
        .invoke()
        .map { l =>
          logger.info(s"PI (iteration: $x) = $pi")
          pi = pi + l * 4
          pi
        }
    })
  }
}
