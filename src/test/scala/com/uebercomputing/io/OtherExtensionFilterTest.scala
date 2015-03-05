package com.uebercomputing.io

import com.uebercomputing.test.BaseTest
import java.io.File

class OtherExtensionFilterTest extends BaseTest {

  test("accept tests") {
    val filter = new OtherExtensionFilter(".pst", ".PST")
    val extensions = List(".pst", ".PST", ".avro")
    val expecteds = List(false, false, true)
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
