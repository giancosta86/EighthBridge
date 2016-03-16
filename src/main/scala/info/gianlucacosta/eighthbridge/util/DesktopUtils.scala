/*§
  ===========================================================================
  EighthBridge
  ===========================================================================
  Copyright (C) 2016 Gianluca Costa
  ===========================================================================
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
  ===========================================================================
*/

package info.gianlucacosta.eighthbridge.util

import java.awt.Desktop
import java.io.File
import java.net.URI

/**
  * Desktop utilities
  */
object DesktopUtils {
  private def runInThread(action: (Desktop) => Unit) {
    val externalThread = new Thread() {
      override def run() {
        val desktop = Desktop.getDesktop

        if (desktop == null) {
          throw new UnsupportedOperationException()
        }

        action(desktop)
      }
    }
    externalThread.start()
  }


  /**
    * Opens the given URL in a browser, without freezing the app
    *
    * @param url
    */
  def openBrowser(url: String) {
    runInThread(desktop => desktop.browse(new URI(url)))
  }


  /**
    * Opens the given file using the user's desktop environment settings, without freezing the app
    *
    * @param file
    */
  def openFile(file: File): Unit = {
    runInThread(desktop => desktop.open(file))
  }

  /**
    * Returns the user's home directory, if available
    *
    * @return Some(user home directory) or None
    */
  def homeDirectory: Option[File] = {
    val userHomeProperty = System.getProperty("user.home")

    if (userHomeProperty == null) {
      None
    } else {
      Some(new File(userHomeProperty))
    }
  }
}
