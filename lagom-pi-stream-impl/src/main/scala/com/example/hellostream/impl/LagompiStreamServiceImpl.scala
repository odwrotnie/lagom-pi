package com.example.hellostream.impl

import akka.NotUsed
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

    def str(i: Long = 0): Stream[Long] = i #:: str(i + 1)
    val src: Source[Long, NotUsed] = Source(str(0))
    var pi: Double = 0d

    Future.successful(src.mapAsync(1) { x: Long =>
      ???
    })
  }
}
