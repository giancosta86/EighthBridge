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

/**
  * Controller telling GraphCanvas how to create JavaFX nodes for GraphComponents and how to handle node deletion
  */
trait GraphCanvasController[V <: VisualVertex[V], L <: VisualLink[L], G <: VisualGraph[V, L, G]] {
  def createBackgroundNode(): BackgroundNode[V, L, G]

  def createVertexNode(vertex: V): VertexNode[V, L, G]

  def createLinkNode(sourceVertex: V, targetVertex: V, link: L): LinkNode[V, L, G]

  def deleteSelection(graph: G): Option[G]
}
