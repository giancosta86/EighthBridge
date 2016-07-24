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

import java.util.UUID

import info.gianlucacosta.eighthbridge.graphs._

import scala.annotation.tailrec

/**
  * A directed graph - that is, a graph whose Binding type parameter resolved to ArcBinding
  *
  * @tparam V Vertex type
  * @tparam L Link type
  * @tparam G
  */
trait DirectedGraph[V <: Vertex, L <: Link, G <: DirectedGraph[V, L, G]] extends Graph[V, L, ArcBinding, G] {
  this: G =>
  /**
    * Adds a link from <i>sourceVertex</i> to <i>targetVertex</i>
    *
    * @param sourceVertex
    * @param targetVertex
    * @param link
    * @return
    */
  def addLink(sourceVertex: V, targetVertex: V, link: L): G = {
    val binding = new ArcBinding(
      id = UUID.randomUUID(),
      sourceVertexId = sourceVertex.id,
      targetVertexId = targetVertex.id,
      linkId = link.id
    )

    addLink(link, binding)
  }

  /**
    * Returns the set of the vertexes that are source of any arc entering the given vertex
    *
    * @param vertex
    * @return
    */
  def getEnteringVertexes(vertex: V): Set[V] =
    bindings
      .filter(binding => binding.targetVertexId == vertex.id)
      .map(binding => getVertex(binding.sourceVertexId).get)


  /**
    * Returns the set of the vertexes that are target of any arc leaving the given vertex
    *
    * @param vertex
    * @return
    */
  def getExitingVertexes(vertex: V): Set[V] =
    bindings
      .filter(binding => binding.sourceVertexId == vertex.id)
      .map(binding => getVertex(binding.targetVertexId).get)


  /**
    * Returns all the arcs starting in sourceVertex and ending in targetVertex
    *
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


  /**
    * Returns the set of arcs whose source is the given vertex
    *
    * @param vertex
    * @return
    */
  def getExitingArcs(vertex: V): Set[L] =
    bindings
      .filter(_.sourceVertexId == vertex.id)
      .map(binding => getLink(binding.linkId).get)



  /**
    * Returns the set of arcs whose target is the given vertex
    *
    * @param vertex
    * @return
    */
  def getEnteringArcs(vertex: V): Set[L] =
    bindings
      .filter(_.targetVertexId == vertex.id)
      .map(binding => getLink(binding.linkId).get)


  /**
    * The vertexes having no entering arcs
    */
  lazy val rootVertexes: Set[V] =
    vertexes
      .filter(getEnteringArcs(_).isEmpty)


  /**
    * Function passed to fold(). Its signature must be:
    * (cumulatedValue, currentEnteringArcs, currentVertex, currentExitingArcs) => newCumulatedValue
    *
    * where:
    *
    * <ul>
    * <li><b>cumulatedValue</b> is the value cumulated until now</li>
    * <li><b>currentEnteringArcs</b> is the set of arcs entering the current vertex</li>
    * <li><b>currentVertex</b> is the vertex now explored by fold()</li>
    * <li><b>currentExitingArcs</b> is the set of arcs exiting the current vertex</li>
    * </ul>
    *
    * The function must return <b>newCumulatedValue</b>, used by fold() as the return value or to call the next VertexFoldProcessor
    *
    * @tparam T The type of the cumulated value
    */
  type VertexFoldProcessor[T] =
  (T, Set[L], V, Set[L]) => T


  /**
    * Takes an initial value and applies the given <b>vertexProcessor</b> to every vertex
    * reachable from the root vertexes, with the following rules:
    *
    * <ul>
    * <li>Each node will be processed <b>only</b> if all of its <i>entering vertexes</i> have been processed</li>
    *
    * <li>Any cycle in the graph will cause a CircularGraphException</li>
    * </ul>
    *
    * @param initialValue
    * @param vertexProcessor
    * @tparam T
    * @return
    */
  def fold[T](initialValue: T)(vertexProcessor: VertexFoldProcessor[T]): T = {

    val boundObjects: Set[(V, L, V)] =
      bindings
        .map(binding => {
          val sourceVertex =
            getVertex(binding.sourceVertexId).get

          val link =
            getLink(binding.linkId).get

          val targetVertex =
            getVertex(binding.targetVertexId).get


          (sourceVertex, link, targetVertex)
        })

    val enteringArcsMap: Map[V, Set[L]] =
      boundObjects
        .groupBy(_._3)
        .mapValues(_.map(tuple => tuple._2))

    val exitingArcsMap: Map[V, Set[L]] =
      boundObjects
        .groupBy(_._1)
        .mapValues(_.map(tuple => tuple._2))


    val exitingVertexesMap: Map[V, Set[V]] =
      boundObjects
        .groupBy(_._1)
        .mapValues(_.map(tuple => tuple._3))



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


  @tailrec
  private def fold[T](
                       cumulatedValue: T,
                       vertexProcessor: VertexFoldProcessor[T],
                       enteringArcsMap: Map[V, Set[L]],
                       exitingArcsMap: Map[V, Set[L]],
                       exitingVertexesMap: Map[V, Set[V]],
                       expandedVertexes: Set[V],
                       exploredArcs: Set[L],
                       fringe: List[V]
                     ): T = {
    fringe match {
      case currentVertex :: fringeTail =>
        if (expandedVertexes.contains(currentVertex)) {
          throw new CircularGraphException
        }


        val currentEnteringArcs =
          enteringArcsMap.getOrElse(currentVertex, Set())


        if (!currentEnteringArcs.subsetOf(exploredArcs))
          fold(
            cumulatedValue,

            vertexProcessor,

            enteringArcsMap,
            exitingArcsMap,
            exitingVertexesMap,

            expandedVertexes,

            exploredArcs,

            fringeTail
          )
        else {
          val currentExitingArcs =
            exitingArcsMap.getOrElse(currentVertex, Set())

          val currentExitingVertexes =
            exitingVertexesMap.getOrElse(currentVertex, Set())


          val newValue =
            vertexProcessor(
              cumulatedValue,
              currentEnteringArcs,
              currentVertex,
              currentExitingArcs
            )


          fold(
            newValue,

            vertexProcessor,

            enteringArcsMap,
            exitingArcsMap,
            exitingVertexesMap,

            expandedVertexes +
              currentVertex,

            exploredArcs ++
              currentExitingArcs,

            (fringeTail ++
              currentExitingVertexes).distinct
          )
        }


      case Nil =>
        cumulatedValue
    }
  }
}
