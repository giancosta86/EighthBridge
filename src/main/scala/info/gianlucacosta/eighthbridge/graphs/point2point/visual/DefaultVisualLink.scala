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

import java.util.UUID

import scalafx.geometry.Point2D

/**
  * Default VisualLink implementation
  *
  * @param internalPoints
  * @param text
  * @param selected
  * @param labelCenter
  * @param settings
  * @param selectedSettings
  * @param id
  */
case class DefaultVisualLink(
                              internalPoints: List[Point2D] = Nil,

                              text: String = "",

                              selected: Boolean = false,

                              labelCenter: Option[Point2D] = None,

                              settings: VisualLinkSettings = VisualLinkDefaultSettings,
                              selectedSettings: VisualLinkSettings = VisualLinkDefaultSelectedSettings,

                              id: UUID = UUID.randomUUID()

                            ) extends VisualLink[DefaultVisualLink] {
  override def visualCopy(internalPoints: List[Point2D], text: String, selected: Boolean, labelCenter: Option[Point2D]): DefaultVisualLink =
    copy(
      internalPoints = internalPoints,
      text = text,
      selected = selected,
      labelCenter = labelCenter
    )
}
