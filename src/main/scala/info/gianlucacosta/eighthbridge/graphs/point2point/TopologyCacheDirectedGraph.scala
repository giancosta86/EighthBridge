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

import info.gianlucacosta.eighthbridge.graphs.{Link, Vertex}

import scala.annotation.tailrec

/**
  * Directed graph overriding topological functions such as getEnteringVertexes() and fold() so that
  * they employ an internal cache which is lazily initialized - leading to far better performances.
  *
  * @tparam V Vertex type
  * @tparam L Link type
  * @tparam G
  */
trait TopologyCacheDirectedGraph[V <: Vertex, L <: Link, G <: TopologyCacheDirectedGraph[V, L, G]]
  extends DirectedGraph[V, L, G] {
  this: G =>

  @transient
  private lazy val topologyCache: Set[(V, L, V)] =
    createTopologyCache()


  private def createTopologyCache(): Set[(V, L, V)] =
    createTopologyCache(
      Set(),
      bindings.toList
    )


  @tailrec
  private def createTopologyCache(
                                   cumulatedCache: Set[(V, L, V)],
                                   bindingsToVisit: List[ArcBinding]
                                 ): Set[(V, L, V)] = {
    bindingsToVisit match {
      case headBinding :: tailBindings =>
        val headBindingCache =
          createTopologyCache(headBinding)

        createTopologyCache(
          cumulatedCache
            + headBindingCache,

          tailBindings
        )

      case Nil =>
        cumulatedCache
    }
  }


  private def createTopologyCache(binding: ArcBinding): (V, L, V) = {
    val sourceVertex =
      getVertex(binding.sourceVertexId).get

    val arc =
      getLink(binding.linkId).get

    val targetVertex =
      getVertex(binding.targetVertexId).get


    (sourceVertex, arc, targetVertex)
  }


  @transient
  private lazy val exitingVertexesMap: Map[V, Set[V]] =
    topologyCache
      .map(cacheItem => {
        val sourceVertex =
          cacheItem._1

        val targetVertex =
          cacheItem._3

        sourceVertex -> targetVertex
      })
      .groupBy(_._1)
      .mapValues(_.map(_._2))


  @transient
  private lazy val exitingArcsMap: Map[V, Set[L]] =
    topologyCache
      .map(cacheItem => {
        val sourceVertex =
          cacheItem._1

        val arc =
          cacheItem._2

        sourceVertex -> arc
      })
      .groupBy(_._1)
      .mapValues(_.map(_._2))


  @transient
  private lazy val enteringVertexesMap: Map[V, Set[V]] =
    topologyCache
      .map(cacheItem => {
        val sourceVertex =
          cacheItem._1

        val targetVertex =
          cacheItem._3

        targetVertex -> sourceVertex
      })
      .groupBy(_._1)
      .mapValues(_.map(_._2))


  @transient
  private lazy val enteringArcsMap: Map[V, Set[L]] =
    topologyCache
      .map(cacheItem => {
        val targetVertex =
          cacheItem._3

        val arc =
          cacheItem._2

        targetVertex -> arc
      })
      .groupBy(_._1)
      .mapValues(_.map(_._2))


  @transient
  private lazy val arcsBetweenMap: Map[(V, V), Set[L]] =
    topologyCache
      .map(cacheItem => {
        val sourceVertex =
          cacheItem._1

        val arc =
          cacheItem._2

        val targetVertex =
          cacheItem._3

        (sourceVertex -> targetVertex) -> arc
      })
      .groupBy(_._1)
      .mapValues(_.map(_._2))


  override def getEnteringVertexes(vertex: V): Set[V] =
    enteringVertexesMap.getOrElse(
      vertex,
      Set()
    )


  override def getExitingVertexes(vertex: V): Set[V] =
    exitingVertexesMap.getOrElse(
      vertex,
      Set()
    )


  override def getEnteringArcs(vertex: V): Set[L] =
    enteringArcsMap.getOrElse(
      vertex,
      Set()
    )


  override def getExitingArcs(vertex: V): Set[L] =
    exitingArcsMap.getOrElse(
      vertex,
      Set()
    )


  override def getArcsBetween(sourceVertex: V, targetVertex: V): Set[L] =
    arcsBetweenMap.getOrElse(
      sourceVertex -> targetVertex,
      Set()
    )


  override def getLinksBetween(linkVertexes: Set[V]): Set[L] = {
    require(linkVertexes.size == 2)

    val firstVertex =
      linkVertexes.head

    val secondVertex =
      linkVertexes.last

    val firstToSecondArcs =
      arcsBetweenMap.getOrElse(
        firstVertex -> secondVertex,
        Set()
      )

    val secondToFirstArcs =
      arcsBetweenMap.getOrElse(
        secondVertex -> firstVertex,
        Set()
      )

    firstToSecondArcs ++
      secondToFirstArcs
  }


  override def getLinksBetween(linkVertexes: V*): Set[L] =
    getLinksBetween(linkVertexes.toSet)


  override def getLinkedVertexes(vertex: V): Set[V] = {
    val enteringVertexes =
      enteringVertexesMap.getOrElse(
        vertex,
        Set()
      )

    val exitingVertexes =
      exitingVertexesMap.getOrElse(
        vertex,
        Set()
      )


    enteringVertexes ++
      exitingVertexes
  }


  override def fold[T](initialValue: T)(vertexProcessor: VertexFoldProcessor[T]): T =
    fold(
      initialValue,
      vertexProcessor,
      enteringArcsMap,
      exitingArcsMap,
      exitingVertexesMap,
      Set(),
      Set(),
      rootVertexes.toList
    )
}
