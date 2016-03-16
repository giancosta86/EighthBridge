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

package info.gianlucacosta.eighthbridge.graphs.point2point.visual

import scalafx.scene.paint.Color
import scalafx.scene.text.Font

/**
  * Settings for VisualLink
  *
  * @param lineColor
  * @param lineSize
  * @param fontName
  * @param fontSize
  * @param fontColor
  * @param arrowAngle
  * @param arrowRelativePosition
  * @param arrowLength
  * @param handleRadiusX
  * @param handleRadiusY
  * @param handleColor
  * @param labelConnectorColor
  * @param labelConnectorDashArray
  */
case class VisualLinkSettings(
                               lineColor: Color,
                               lineSize: Double,
                               fontName: String,
                               fontSize: Double,
                               fontColor: Color,

                               arrowAngle: Double,
                               arrowRelativePosition: Double,
                               arrowLength: Double,

                               handleRadiusX: Double,
                               handleRadiusY: Double,
                               handleColor: Color,

                               labelConnectorColor: Color,
                               labelConnectorDashArray: Iterable[Double]
                             ) {

  @transient
  lazy val font = new Font(fontName, fontSize)
}
