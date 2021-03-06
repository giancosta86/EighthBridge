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

import info.gianlucacosta.eighthbridge.graphs.point2point.visual.VisualLink

/**
  * Link dedicated to the "basic" package
  */
trait BasicLink[L <: BasicLink[L]] extends VisualLink[L] {
  this: L =>
  def text: String

  def arrow: LinkArrow =
    LinkArrow.Default

  def handleRadius: LinkHandleRadius =
    LinkHandleRadius.Default

  override def toString: String =
    text
}
