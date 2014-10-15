package org.home.components

import org.reactivecouchbase.ReactiveCouchbaseDriver

object DbDriver {
  val driver = ReactiveCouchbaseDriver()
  val bucket = driver.bucket("actors")
}
