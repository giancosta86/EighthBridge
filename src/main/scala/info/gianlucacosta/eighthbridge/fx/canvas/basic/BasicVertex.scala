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

import info.gianlucacosta.eighthbridge.graphs.point2point.visual.VisualVertex

import scalafx.geometry.Dimension2D


object BasicVertex {
  def estimateVertexSize(text: String, fontDimension: Dimension2D, padding: Double): Dimension2D = {
    val textLines = text.split("\n")

    val width =
      2 * padding +
        fontDimension.width *
          textLines
            .map(_.length)
            .max

    val height =
      2 * padding +
        fontDimension.height * textLines.length

    new Dimension2D(width, height)
  }
}

/**
  * Vertex dedicated to the "basic" package
  */
trait BasicVertex[V <: BasicVertex[V]] extends VisualVertex[V] {
  this: V =>
  def text: String

  /**
    * This property must now be implemented, and should return the
    * overall dimension expected for the rendered vertex (as it will be
    * used by renderers).
    *
    * You can return the exact size you need, a custom heuristic value
    * or employ the provided heuristic function: BasicVertex.estimateVertexSize()
    *
    * @return The vertex dimension
    */
  def dimension: Dimension2D

  override def toString: String = text
}
