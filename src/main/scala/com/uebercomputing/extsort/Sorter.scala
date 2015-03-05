package com.uebercomputing.extsort

import java.io.File

/**
 * Find files with extensions under sortDir and copy them (preserving subdirectory structure under sortDir)
 * to destDir/$EXTENSION/origSubdirs/file.$EXTENSION. Extensions have the concept of an extension list, i.e.
 * multiple ways a file extension might be encountered (for example, .gif or .GIF). The first entry in the
 * extension list is used as the canonical extension name and will be used for $EXTENSION dir.
 */
object Sorter {

  case class Config(sortDir: File = null, destDir: File = null, extensions: Set[Set[String]] = Set())

  def main(args: Array[String]): Unit = {
    val p = parser()
    // parser.parse returns Option[C]
    p.parse(args, Config()) map { config => println(config) }
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
      } text ("-ext can occur multiple times and each instance is a comma-separated list of equivalent extensions.")

    }
  }

  def splitExtensions(extArg: String): Set[String] = {
    extArg.split(",").toSet
  }
}
