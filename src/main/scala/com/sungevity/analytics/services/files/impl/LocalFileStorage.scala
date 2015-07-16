package com.sungevity.analytics.services.files.impl

import java.io.File
import java.net.URI
import java.nio.file._

import com.sungevity.analytics.services.files.FileStorage
import com.sungevity.analytics.utils.IOUtils

import scala.concurrent.{ExecutionContext, Future}

class LocalFileStorage(basePath: String) extends FileStorage {

  def put(name: String, data: Iterator[String])(implicit executionContext: ExecutionContext): Future[URI] = Future {

    val outputPath = new File(basePath, name).toURI


    for {
      line <- data
    } {
      IOUtils.write(outputPath.getPath, line, StandardOpenOption.APPEND, StandardOpenOption.CREATE)
    }

    outputPath

  }

  def get(uri: URI)(implicit executionContext: ExecutionContext): Future[Iterator[String]] = uri.getScheme match {

    case "file" => Future {
      scala.io.Source.fromFile(new File(uri)).getLines()
    }

    case _ => throw new IllegalStateException(s"schema [${uri.getScheme}] is not supported by ${this.getClass.getSimpleName}")

  }

}
