package com.uebercomputing.extsort

import java.io.File
import com.uebercomputing.io.FileExtensionFilter
import java.io.FileFilter
import com.uebercomputing.io.{ FileUtils => UeberFileUtils }
import org.apache.commons.io.FileUtils
import com.uebercomputing.io.OtherExtensionFilter

/**
 * Find files with extensions under sortDir and copy them (preserving subdirectory structure under sortDir)
 * to destDir/$EXTENSION/origSubdirs/file.$EXTENSION. Extensions have the concept of an extension list, i.e.
 * multiple ways a file extension might be encountered (for example, .gif or .GIF). The first entry in the
 * extension list is used as the canonical extension name and will be used for $EXTENSION dir.
 *
 * After sbt assembly
 * java -jar target/scala-2.11/extension-sorter-assembly-1.0.jar --sortDir src/test/resources/media --destDir /tmp/sorted --ext foo,FOO --ext baz2
 */
object Sorter {

  case class Config(sortDir: File = null, destDir: File = null, extensions: Set[List[String]] = Set())

  val OtherDirName = "other"

  def sortCommon(sortDirRoot: File, commonDestDir: File, fileFilter: FileFilter): Unit = {

    val sortDirRootAbsolute = sortDirRoot.getAbsolutePath

    def sortCommon(sortSubDir: File): Unit = {
      val filteredFiles = sortSubDir.listFiles(fileFilter)
      filteredFiles.map { file =>
        file match {
          case dir if dir.isDirectory() => sortCommon(dir)
          case file => {
            val fileAbsolutePath = file.getAbsolutePath
            val fileRelativePath = UeberFileUtils.getRelativeFileLocationFromStartDirectory(fileAbsolutePath, sortDirRootAbsolute)
            val destFile = new File(commonDestDir, fileRelativePath)
            FileUtils.copyFile(file, destFile)
          }
        }
      }
    }

    sortCommon(sortDirRoot)
  }

  def sort(config: Config, commonExtensions: List[String]): Unit = {
    val commonName = commonExtensions(0)
    val fileFilter = new FileExtensionFilter(commonExtensions: _*)
    val commonDestDir = new File(config.destDir, commonName)
    sortCommon(config.sortDir, commonDestDir, fileFilter)
  }

  def sort(config: Config): Unit = {
    for (commonExtensions <- config.extensions) {
      sort(config, commonExtensions)
    }
    sortOthers(config)
  }

  def sortOthers(config: Config): Unit = {
    //sort other files to "other" bin
    val commonDestDir = new File(config.destDir, OtherDirName)
    val sortedExtensions = config.extensions.flatMap(identity).toSeq
    val fileFilter = new OtherExtensionFilter(sortedExtensions: _*)
    sortCommon(config.sortDir, commonDestDir, fileFilter)
  }

  def main(args: Array[String]): Unit = {
    val p = parser()
    // parser.parse returns Option[C]
    p.parse(args, Config()) map { config =>
      println(config)
      sort(config)
    }
  }

  def parser(): scopt.OptionParser[Config] = {
    new scopt.OptionParser[Config]("ExtensionSorter") {

      head("ExtensionSorter", "1.x")

      opt[String]("sortDir") required () action { (sortDirArg, config) =>
        config.copy(sortDir = new File(sortDirArg))
      } validate { x =>
        val dir = new File(x)
        if (dir.exists() && dir.canRead() && dir.isDirectory()) success
        else failure("Option --sortDir must be readable directory")
      } text ("sortDir is string with relative or absolute location of dir to be sorted.")

      opt[String]("destDir") required () action { (destDirArg, config) =>
        config.copy(destDir = new File(destDirArg))
      } text ("destDir is string with relative or absolute location of dir where sorted output will be written.")

      opt[String]("ext") unbounded () optional () action { (extArg, config) =>
        config.copy(extensions = config.extensions + splitExtensions(extArg))
      } validate { extArg =>
        if (extArg.length > 0 && splitExtensions(extArg).size > 0) success
        else failure(s"Option --ext must contain at least one extension (or multiple extensions of the same class (e.g. jpg,JPG) separated by comma)")
      } text ("-ext can occur multiple times and each instance is a comma-separated list of equivalent extensions.")

    }
  }

  def splitExtensions(extArg: String): List[String] = {
    extArg.split(",").toList
  }
}
