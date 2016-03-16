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

package info.gianlucacosta.eighthbridge.util.fx.geometry

import scala.language.implicitConversions
import scalafx.geometry.Point2D
import scalafx.scene.input.MouseEvent


object MouseEventExtensions {
  implicit def convertMouseEvent(mouseEvent: MouseEvent): MouseEventExtensions =
    new MouseEventExtensions(mouseEvent)
}

/**
  * MouseEvent extensions
  *
  * @param mouseEvent
  */
class MouseEventExtensions private(mouseEvent: MouseEvent) {
  /**
    * Returns the event's point
    *
    * @return The event's point, as a Point2D
    */
  def point: Point2D = new Point2D(
    mouseEvent.x,
    mouseEvent.y
  )
}
