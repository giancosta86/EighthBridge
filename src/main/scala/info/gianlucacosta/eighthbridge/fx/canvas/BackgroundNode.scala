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

import java.util.UUID

import info.gianlucacosta.eighthbridge.graphs.point2point.visual.{VisualGraph, VisualLink, VisualVertex}

/**
  * JavaFX node rendering the graph background and the selection rectangle
  */
trait BackgroundNode[
  V <: VisualVertex[V],
  L <: VisualLink[L],
  G <: VisualGraph[V, L, G]
] extends GraphCanvasNode[V, L, G] {
  /**
    * Called by GraphCanvas at every rendering - before actually rendering any node
    *
    * @param controller
    * @param graph
    * @param vertexNodes
    * @param linkNodes
    */
  def setup(
             controller: GraphCanvasController[V, L, G],
             graph: G,
             vertexNodes: Map[UUID, VertexNode[V, L, G]],
             linkNodes: Map[UUID, LinkNode[V, L, G]]
           )
}
