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

import info.gianlucacosta.eighthbridge.util.Numbers

import scalafx.scene.control._

/**
  * Shows common input dialogs
  */
case object InputDialogs {
  def askYesNoCancel(message: String, header: String = ""): Option[Boolean] = {
    val yesButton = new ButtonType("Yes")
    val noButton = new ButtonType("No")
    val cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CancelClose)

    val alert = new Alert(Alert.AlertType.Confirmation) {
      headerText = header

      contentText = message

      buttonTypes = List(
        yesButton,
        noButton,
        cancelButton
      )
    }

    val inputResult = alert.showAndWait()

    inputResult match {
      case Some(`yesButton`) =>
        Some(true)

      case Some(`noButton`) =>
        Some(false)

      case _ =>
        None
    }
  }

  /**
    * Asks for a string, automatically trimming it
    *
    * @param message
    * @param initialValue
    * @param header
    * @return Some(user string, trimmed) or None
    */
  def askForString(message: String, initialValue: String = "", header: String = ""): Option[String] = {
    val inputDialog = new TextInputDialog(initialValue) {
      headerText = header
      contentText = message
      resizable = true
    }

    val inputResult = inputDialog.showAndWait()

    inputResult
      .map(_.trim)
  }


  def askForDouble(message: String, initialValue: Double = 0, minValue: Double = Double.MinValue, maxValue: Double = Double.MaxValue, header: String = ""): Option[Double] = {
    while (true) {
      val inputString = askForString(message, Numbers.smartString(initialValue), header)

      if (inputString.isEmpty) {
        return None
      }


      try {
        val value = inputString.get.toDouble

        if (value < minValue || value > maxValue) {
          Alerts.showWarning(s"Please, enter a number in the range [${Numbers.smartString(minValue)}; ${Numbers.smartString(maxValue)}]", header)
        }

        return Some(value)

      } catch {
        case _: NumberFormatException =>
          Alerts.showWarning("Please, enter a numeric value", header)
      }
    }

    throw new AssertionError()
  }

  def askForLong(message: String, initialValue: Long = 0, minValue: Long = Long.MinValue, maxValue: Long = Long.MaxValue, header: String = ""): Option[Long] = {
    while (true) {
      val inputDoubleResult = askForDouble(message, initialValue, minValue, maxValue, header)

      if (inputDoubleResult.isEmpty) {
        return None
      }

      val doubleValue = inputDoubleResult.get
      val longValue = math.round(doubleValue)

      val isLong = longValue - doubleValue == 0

      if (isLong) {
        return Some(longValue)
      } else {
        Alerts.showWarning("Please, input an integer number", header)
      }
    }

    throw new AssertionError()
  }


  def askForItem[T](message: String, itemPool: Seq[T], initialItem: Option[T] = None, header: String = ""): Option[T] = {
    require(itemPool.nonEmpty)

    val choiceDialog = new ChoiceDialog[T](initialItem.getOrElse(itemPool.head), itemPool) {
      headerText = header

      contentText = message
    }

    choiceDialog.showAndWait()
  }
}
