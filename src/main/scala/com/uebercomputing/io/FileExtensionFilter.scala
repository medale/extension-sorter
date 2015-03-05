package com.uebercomputing.io

import java.io.FileFilter
import java.io.File

/**
 * Only show files that match one of the extensions or directories.
 */
class FileExtensionFilter(extensions: String*) extends FileFilter {

  def accept(path: File): Boolean = {
    val result = if (path.isDirectory()) true
    else {
      val name = path.getName
      extensions.foldLeft(false)((resultSoFar, currSuffix) => resultSoFar || name.endsWith(currSuffix))
    }
    result
  }
}
