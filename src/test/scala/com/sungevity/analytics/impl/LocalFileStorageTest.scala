package com.sungevity.analytics.impl

import com.sungevity.analytics.services.files.impl.LocalFileStorage
import com.sungevity.analytics.utils.IOUtils
import org.scalatest.WordSpec
import org.scalatest.concurrent.ScalaFutures

import scala.concurrent.ExecutionContext.Implicits.global

class LocalFileStorageTest extends WordSpec with ScalaFutures {

  "LocalFileStorage" should {

    "be able to store data as a file and retrieve it back" in {

      val expected = List("1", "2", "3")

      val storage = new LocalFileStorage("/tmp")

      val actual = for {

        uri <- storage.put(IOUtils.randomName, expected.toIterator)
        actual <- storage.get(uri)

      } yield {
          actual
      }

      whenReady(actual) {
        actual =>
          assert(actual.mkString === expected.mkString)
      }

    }

  }

}
