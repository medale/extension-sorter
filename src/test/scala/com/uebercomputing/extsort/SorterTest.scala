package com.uebercomputing.extsort

import com.uebercomputing.test.BaseFixtureTest
import java.nio.file.Paths
import java.nio.file.Files
import java.io.File

class SorterTest extends BaseFixtureTest {

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
    assert(exts.contains(List("foo", "FOO")))
  }

  test("args parser - invalid extension1") { destDir =>
    val p = Sorter.parser()
    val args = Array[String]("--sortDir", TestSortDir,
      "--destDir", destDir.getAbsolutePath,
      "--ext", "")
    val configOpt = p.parse(args, Sorter.Config())
    assert(configOpt.isEmpty)
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
    assert(exts.contains(List("foo", "FOO")))
    assert(exts.contains(List("bar", "BAZ")))
  }

  test("sort with foo,FOO and baz") { destDir =>
    val args = Array[String]("--sortDir", "src/test/resources/media",
      "--destDir", destDir.getAbsolutePath,
      "--ext", "foo,FOO",
      "--ext", "baz")
    Sorter.main(args)
    assertFilesCopied(destDir)
  }

  def assertFilesCopied(destDir: File) {
    val expectedFiles = List("foo/2014/01/09/b.FOO",
      "foo/2014/01/09/a.foo",
      "baz/2014/01/09/d.baz",
      "other/2014/01/09/c.bar",
      "foo/2014/01/15/15.FOO",
      "baz/2014/01/15/15.baz",
      "other/2014/01/15/15.bar",
      "foo/2014/01/15/15.foo",
      "other/2015/01/21/2015.baz2",
      "other/2015/01/21/2015.foo2",
      "foo/2015/01/21/2015.FOO",
      "foo/2015/01/21/2015.foo")
    for (fileStr <- expectedFiles) {
      val file = new File(destDir, fileStr)
      assert(file.exists() && file.isFile(), s"Did not create file ${file.getAbsolutePath}")
    }
  }

  def getTempDir(): File = {
    val dirPath = Paths.get(TempDir)
    val prefix = "extsorter"
    val tempDir = Files.createTempDirectory(dirPath, prefix)
    tempDir.toFile()
  }

}
