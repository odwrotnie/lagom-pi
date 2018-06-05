package com.example.hello.impl

import com.example.hello.api.LagompiService
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry

class LagompiServiceImpl(persistentEntityRegistry: PersistentEntityRegistry)
  extends LagompiService {

  override def leibniz(n: Long) = ServiceCall { _ =>
    val ref = persistentEntityRegistry.refFor[LagompiEntity]("Leibniz")
    ref.ask(L(n))
  }
}
