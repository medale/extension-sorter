package com.uebercomputing.io

import com.uebercomputing.test.BaseTest
import java.io.File

/**
 * Only accept files that match the constructor extensions.
 */
class FileExtensionFilterTest extends BaseTest {

  test("accept tests") {
    val filter = new FileExtensionFilter(".pst", ".PST")
    val extensions = List(".pst", ".PST", ".avro")
    val expecteds = List(true, true, false)
    for ((extension, expected) <- extensions zip expecteds) {
      val path = new File(s"file${extension}")
      assert(filter.accept(path) === expected)
    }
  }

  test("accept dir") {
    val filter = new FileExtensionFilter(".pst", ".PST")
    val dir = new File("src")
    assert(filter.accept(dir))
  }
}
