package org.home

import org.home.config.Bootstrap
import play.api.{Configuration, Environment}
import play.api.inject._

class RunModule extends Module{

  def bindings(environment: Environment,
               configuration: Configuration) = Seq(
      bind[Bootstrap].toSelf.eagerly()
  )

}

