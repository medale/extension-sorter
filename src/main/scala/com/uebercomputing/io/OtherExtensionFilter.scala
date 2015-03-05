package com.uebercomputing.io

import java.io.FileFilter
import java.io.File

/**
 * Only accept files with extensions that are not in excludeExtensions or directories.
 */
class OtherExtensionFilter(excludeExtensions: String*) extends FileFilter {

  def accept(path: File): Boolean = {
    val result = if (path.isDirectory()) true
    else {
      val name = path.getName
      excludeExtensions.foldLeft(true)((resultSoFar, currSuffix) => resultSoFar && !name.endsWith(currSuffix))
    }
    result
  }
}
