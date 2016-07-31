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

package info.gianlucacosta.eighthbridge.fx.canvas

import info.gianlucacosta.eighthbridge.graphs.point2point.visual.{VisualGraph, VisualLink, VisualVertex}

import scalafx.geometry.Dimension2D


/**
  * Controller providing behavior for GraphCanvas
  */
trait GraphCanvasController[
V <: VisualVertex[V],
L <: VisualLink[L],
G <: VisualGraph[V, L, G]
] {
  def createBackgroundNode(graphCanvas: GraphCanvas[V, L, G]): BackgroundNode[V, L, G]

  def createVertexNode(graphCanvas: GraphCanvas[V, L, G], vertex: V): VertexNode[V, L, G]

  def createLinkNode(graphCanvas: GraphCanvas[V, L, G], sourceVertex: V, targetVertex: V, link: L): LinkNode[V, L, G]

  def deleteSelection(graphCanvas: GraphCanvas[V, L, G], graph: G): Option[G]

  def getCanvasDimension(graphCanvas: GraphCanvas[V, L, G]): Dimension2D

  def renderDirected: Boolean
}
