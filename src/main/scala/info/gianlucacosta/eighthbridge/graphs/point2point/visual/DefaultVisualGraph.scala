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

import info.gianlucacosta.eighthbridge.graphs.point2point.ArcBinding

import scalafx.geometry.{BoundingBox, Bounds, Dimension2D}

/**
  * Default VisualGraph implementation
  *
  * @param directed
  * @param dimension
  * @param selectionBounds
  * @param settings
  * @param vertexes
  * @param links
  * @param bindings
  */
case class DefaultVisualGraph(directed: Boolean,
                              dimension: Dimension2D,
                              selectionBounds: Bounds = new BoundingBox(0, 0, 0, 0),
                              settings: VisualGraphSettings = VisualGraphDefaultSettings,
                              vertexes: Set[VisualVertex] = Set(),
                              links: Set[VisualLink] = Set(),
                              bindings: Set[ArcBinding] = Set()) extends VisualGraph {

  override def visualCopy(directed: Boolean, dimension: Dimension2D, selectionBounds: Bounds): DefaultVisualGraph =
    copy(
      directed = directed,
      dimension = dimension,
      selectionBounds = selectionBounds
    )

  override def graphCopy(vertexes: Set[VisualVertex], links: Set[VisualLink], bindings: Set[ArcBinding]): DefaultVisualGraph =
    copy(
      vertexes = vertexes,
      links = links,
      bindings = bindings
    )
}
