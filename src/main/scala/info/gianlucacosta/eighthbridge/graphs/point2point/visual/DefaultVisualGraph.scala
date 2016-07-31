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

/**
  * Default VisualGraph implementation
  *
  * @param vertexes
  * @param links
  * @param bindings
  */
case class DefaultVisualGraph[V <: VisualVertex[V], L <: VisualLink[L]](
                                                                         vertexes: Set[V] = Set[V](),
                                                                         links: Set[L] = Set[L](),
                                                                         bindings: Set[ArcBinding] = Set[ArcBinding]()) extends VisualGraph[V, L, DefaultVisualGraph[V, L]] {

  override def graphCopy(vertexes: Set[V], links: Set[L], bindings: Set[ArcBinding]): DefaultVisualGraph[V, L] =
    copy(
      vertexes = vertexes,
      links = links,
      bindings = bindings
    )
}
