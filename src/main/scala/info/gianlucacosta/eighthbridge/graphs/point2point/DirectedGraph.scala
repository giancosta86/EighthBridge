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

package info.gianlucacosta.eighthbridge.graphs.point2point

import info.gianlucacosta.eighthbridge.graphs.{Graph, Link, Vertex}

/**
  * A directed graph - that is, a graph whose Binding type parameter resolved to ArcBinding
  * @tparam V Vertex type
  * @tparam L Link type
  * @tparam G
  */
trait DirectedGraph[V <: Vertex, L <: Link, G <: DirectedGraph[V, L, G]] extends Graph[V, L, ArcBinding, G] { this: G =>
  /**
    * Returns a set of the vertexes that are source of an arc entering the given vertex
    *
    * @param vertex
    * @return
    */
  def enteringVertexes(vertex: V): Set[V] =
    bindings
      .filter(binding => binding.targetVertexId == vertex.id)
      .map(binding => getVertex(binding.sourceVertexId).get)


  /**
    * Returns a set of the vertexes that are target of an arc leaving the given vertex
    *
    * @param vertex
    * @return
    */
  def exitingVertexes(vertex: V): Set[V] =
    bindings
      .filter(binding => binding.sourceVertexId == vertex.id)
      .map(binding => getVertex(binding.targetVertexId).get)


  /**
    * Returns all the arcs starting in sourceVertex and ending in targetVertex
    * @param sourceVertex The source vertex
    * @param targetVertex The target vertex
    * @return A set of links
    */
  def getArcsBetween(sourceVertex: V, targetVertex: V): Set[L] =
    bindings
      .filter(binding =>
        binding.sourceVertexId == sourceVertex.id
          &&
          binding.targetVertexId == targetVertex.id
      )
      .map(binding => getLink(binding.linkId).get)
}
