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


import scalafx.geometry.Point2D

/**
  * Default BasicVertex implementation
  */
case class DefaultBasicVertex(
                              padding: Double = 8,
                              text: String = "",
                              styleClass: String = "",
                              center: Point2D = Point2D.Zero,
                              selected: Boolean = false,
                              id: UUID = UUID.randomUUID()
                            ) extends BasicVertex[DefaultBasicVertex] {


  override def visualCopy(center: Point2D, selected: Boolean): DefaultBasicVertex =
    copy(
      center = center,
      selected = selected
    )

  override val toString: String = text
}

