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

import info.gianlucacosta.eighthbridge.graphs.Link

import scalafx.geometry.Point2D

/**
  * A link for VisualGraph
  */
trait VisualLink[L <: VisualLink[L]] extends Link { this: L =>
  def internalPoints: List[Point2D]

  def text: String

  def selected: Boolean

  def labelCenter: Option[Point2D]

  def settings: VisualLinkSettings

  def selectedSettings: VisualLinkSettings


  def visualCopy(
                  internalPoints: List[Point2D] = internalPoints,
                  text: String = text,

                  selected: Boolean = selected,
                  labelCenter: Option[Point2D] = labelCenter): L


  override def toString: String = text
}