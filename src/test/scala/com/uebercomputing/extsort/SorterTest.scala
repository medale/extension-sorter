package com.uebercomputing.extsort

import com.uebercomputing.test.BaseTest
import java.nio.file.Paths
import java.nio.file.Files
import java.io.File

class SorterTest extends BaseTest {

  val TempDir = System.getProperty("java.io.tmpdir")
  val TestSortDir = "/opt/rpm1/sorter/media"

  type FixtureParam = File

  override def withFixture(test: OneArgTest) = {
    var destDir: File = null
    try {
      destDir = getTempDir()
      test(destDir)
    } finally {
      destDir.delete()
    }
  }

  test("args parser - invalid") { destDir =>
    val p = Sorter.parser()
    val args = Array[String]()
    val configOpt = p.parse(args, Sorter.Config())
    assert(configOpt === None)
  }

  test("args parser - valid") { destDir =>
    val p = Sorter.parser()
    val args = Array[String]("--sortDir", TestSortDir,
      "--destDir", destDir.getAbsolutePath)
    val configOpt = p.parse(args, Sorter.Config())
    assert(configOpt.isDefined)
    val config = configOpt.get
    assert(config.sortDir.getCanonicalPath === TestSortDir)
    assert(config.destDir === destDir)
    assert(config.extensions === Set())
  }

  test("args parser - valid extension1") { destDir =>
    val p = Sorter.parser()
    val args = Array[String]("--sortDir", TestSortDir,
      "--destDir", destDir.getAbsolutePath,
      "--ext", "foo,FOO")
    val configOpt = p.parse(args, Sorter.Config())
    assert(configOpt.isDefined)
    val config = configOpt.get
    val exts = config.extensions
    assert(exts.size === 1)
    assert(exts.contains(Set("foo", "FOO")))
  }

  test("args parser - valid with two extension sets") { destDir =>
    val p = Sorter.parser()
    val args = Array[String]("--sortDir", TestSortDir,
      "--destDir", destDir.getAbsolutePath,
      "--ext", "foo,FOO",
      "--ext", "bar,BAZ")
    val configOpt = p.parse(args, Sorter.Config())
    assert(configOpt.isDefined)
    val config = configOpt.get
    val exts = config.extensions
    assert(exts.size === 2)
    assert(exts.contains(Set("foo", "FOO")))
    assert(exts.contains(Set("bar", "BAZ")))
  }

  def getTempDir(): File = {
    val dirPath = Paths.get(TempDir)
    val prefix = "extsorter"
    val tempDir = Files.createTempDirectory(dirPath, prefix)
    tempDir.toFile()
  }

}
