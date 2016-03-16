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
import scalafx.geometry.{Bounds, Point2D}


object BoundsExtensions {
  implicit def convertBounds(bounds: Bounds): BoundsExtensions =
    new BoundsExtensions(bounds)
}

/**
  * Extensions for the Bounds class
  *
  * @param bounds
  */
class BoundsExtensions private(bounds: Bounds) {
  /**
    * Computes the center of the bounds
    *
    * @return The center as a Point2D
    */
  def centerPoint2D: Point2D = new Point2D(
    bounds.minX + bounds.width / 2,
    bounds.minY + bounds.height / 2
  )
}
