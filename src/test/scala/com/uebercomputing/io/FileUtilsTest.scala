package com.uebercomputing.io

import com.uebercomputing.test.BaseTest

class FileUtilsTest extends BaseTest {

  test("get relative file location") {
    val startDir = "/home/test/"
    val testInputs = Array(startDir + ".bashrc",
      startDir + "web/newsletter/grip-it.jpg")
    val expectedOutputs = Array("./.bashrc",
      "./web/newsletter/grip-it.jpg")

    val testTuples = (testInputs, expectedOutputs).zipped
    testTuples.foreach((testIn, expectedOut) => {
      val actualOutput = FileUtils.getRelativeFileLocationFromStartDirectory(testIn, startDir)
      assert(expectedOut === actualOutput)
    })
  }
}
