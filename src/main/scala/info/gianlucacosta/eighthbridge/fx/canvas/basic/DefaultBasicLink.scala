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

package info.gianlucacosta.eighthbridge.fx.canvas.basic

import java.util.UUID

import scalafx.geometry.{Point2D}

/**
  * Default BasicLink implementation
  */
case class DefaultBasicLink(
                            text: String = "",
                            arrow: LinkArrow = LinkArrow(),
                            handleRadius: LinkHandleRadius = LinkHandleRadius(),
                            styleClass: String = "",
                            internalPoints: List[Point2D] = List(),
                            selected: Boolean = false,
                            labelCenter: Option[Point2D] = None,
                            id: UUID = UUID.randomUUID()
                          ) extends BasicLink[DefaultBasicLink] {


  override def visualCopy(internalPoints: List[Point2D], selected: Boolean, labelCenter: Option[Point2D]): DefaultBasicLink =
    copy(
      internalPoints = internalPoints,
      selected = selected,
      labelCenter = labelCenter
    )
}
