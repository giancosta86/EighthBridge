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

import scalafx.geometry.Point2D
import scalafx.scene.shape.Line

/**
  * Simplified segment
  */
class Segment extends Line {
  def startPoint = new Point2D(startX.value, startY.value)

  def startPoint_=(newPoint: Point2D) = {
    startX = newPoint.x
    startY = newPoint.y
  }

  def endPoint = new Point2D(endX.value, endY.value)

  def endPoint_=(newPoint: Point2D) = {
    endX = newPoint.x
    endY = newPoint.y
  }
}
