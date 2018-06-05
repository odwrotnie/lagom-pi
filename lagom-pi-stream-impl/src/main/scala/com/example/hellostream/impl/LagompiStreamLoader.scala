package com.example.hellostream.impl

import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.server._
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import play.api.libs.ws.ahc.AhcWSComponents
import com.example.hellostream.api.LagompiStreamService
import com.example.hello.api.LagompiService
import com.softwaremill.macwire._

class LagompiStreamLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new LagompiStreamApplication(context) {
      override def serviceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new LagompiStreamApplication(context) with LagomDevModeComponents

  override def describeService = Some(readDescriptor[LagompiStreamService])
}

abstract class LagompiStreamApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with AhcWSComponents {

  // Bind the service that this server provides
  override lazy val lagomServer = serverFor[LagompiStreamService](wire[LagompiStreamServiceImpl])

  // Bind the LagompiService client
  lazy val lagompiService = serviceClient.implement[LagompiService]
}
