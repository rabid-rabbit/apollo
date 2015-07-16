package com.sungevity.analytics.services.files

import java.net.URI

import scala.concurrent.{ExecutionContext, Future}

trait FileStorage {

  def put(name: String, data: Iterator[String])(implicit executionContext: ExecutionContext): Future[URI]

  def get(url: URI)(implicit executionContext: ExecutionContext): Future[Iterator[String]]

}
