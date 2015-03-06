package com.uebercomputing.io

import java.io.File

object FileUtils {

  val RelativePrefix = "." + File.separator

  /**
   * Return a file location string that is relative to the start directory
   * from an absolute file location. So /home/test/.bashrc becomes ./.bashrc
   * if /home/test/ is the starting dir.
   *
   * @param absoluteFileLocation
   * @param startDirectory - the directory to "subtract" from absolute file location
   * @return file location relative to start directory.
   */
  def getRelativeFileLocationFromStartDirectory(absoluteFileLocation: String,
                                                startDirectory: String): String = {
    val startDirectoryPrefix = if (!startDirectory.endsWith(File.separator)) {
      startDirectory + File.separator
    } else {
      startDirectory
    }
    val relativeFileLocation = absoluteFileLocation.replaceFirst(
      startDirectoryPrefix, RelativePrefix)
    relativeFileLocation
  }

}
