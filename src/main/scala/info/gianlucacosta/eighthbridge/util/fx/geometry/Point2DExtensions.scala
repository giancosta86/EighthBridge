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
import scalafx.geometry.{Dimension2D, Point2D}


object Point2DExtensions {
  implicit def convertPoint2D(source: Point2D): Point2DExtensions =
    new Point2DExtensions(source.x, source.y)
}

/**
  * Vectorial and general extensions for the Point2D class
  *
  * @param x
  * @param y
  */
class Point2DExtensions private(x: Double, y: Double) extends Point2D(x, y) {
  def +(other: Point2D): Point2D = new Point2D(
    x + other.x,
    y + other.y
  )

  def -(other: Point2D): Point2D = new Point2D(
    x - other.x,
    y - other.y
  )

  def *(factor: Double): Point2D = new Point2D(
    x * factor,
    y * factor
  )

  def /(divisor: Double): Point2D = new Point2D(
    x / divisor,
    y / divisor
  )


  def clip(width: Double, height: Double): Point2D = new Point2D(
    math.max(
      0,
      math.min(
        x,
        width
      )
    ),

    math.max(
      0,
      math.min(
        y,
        height
      )
    )
  )

  def clip(dimension: Dimension2D): Point2D =
    clip(dimension.width, dimension.height)
}
