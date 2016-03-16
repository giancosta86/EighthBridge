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
  * Settings for VisualVertex
  *
  * @param background
  * @param borderSize
  * @param borderColor
  * @param fontName
  * @param fontSize
  * @param fontColor
  * @param padding
  * @param rounding
  */
case class VisualVertexSettings(
                                 background: Color,
                                 borderSize: Double,
                                 borderColor: Color,
                                 fontName: String,
                                 fontSize: Double,
                                 fontColor: Color,
                                 padding: Double,
                                 rounding: Double
                               ) {

  @transient
  lazy val font = new Font(fontName, fontSize)
}
