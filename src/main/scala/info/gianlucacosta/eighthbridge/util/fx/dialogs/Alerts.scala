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

package info.gianlucacosta.eighthbridge.util.fx.dialogs

import java.util.function.{Consumer, Predicate}
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.Region

import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType

/**
  * Shows common alerts
  */
case object Alerts {
  private def showAlert(alertType: AlertType, message: String, header: String): Unit = {
    val alert = new Alert(alertType) {
      headerText = header
      contentText = message
      resizable = true
    }

    //This seems still required on Linux-based GUIs
    alert
      .dialogPane()
      .getChildren
      .filtered(new Predicate[Node] {
        override def test(node: Node): Boolean = node.isInstanceOf[Label]
      })
      .forEach(new Consumer[Node] {
        override def accept(node: Node): Unit = {
          node.asInstanceOf[Label].setMinHeight(Region.USE_PREF_SIZE)
        }
      })

    alert.showAndWait()
  }


  def showInfo(message: String, header: String = ""): Unit =
    showAlert(AlertType.Information, message, header)


  def showWarning(message: String, header: String = ""): Unit =
    showAlert(AlertType.Warning, message, header)


  def showError(message: String, header: String = ""): Unit =
    showAlert(AlertType.Error, message, header)

  def showException(exception: Exception, header: String = ""): Unit = {
    showAlert(
      AlertType.Error,
      if (exception.getMessage != null && exception.getMessage.nonEmpty) exception.getMessage else exception.getClass.getSimpleName,
      header
    )
  }
}
