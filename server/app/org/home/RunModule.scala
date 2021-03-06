package org.home

import org.home.config.Bootstrap
import org.home.repositories.{Repository, RepositoryRedis}
import play.api.{Configuration, Environment}
import play.api.inject._

class RunModule extends Module {

  def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] = Seq(
    bind[Bootstrap].toSelf.eagerly(),
    bind[Repository].to[RepositoryRedis]

  )

}

