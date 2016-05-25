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

package info.gianlucacosta.eighthbridge.graphs

import java.util.UUID

/**
  * A general-purpose, read-only graph.
  *
  * Every status-changing operation returns a new graph.
  *
  * @tparam V Vertex type
  * @tparam L Link type
  * @tparam B Binding type
  */
trait Graph[V <: Vertex, L <: Link, B <: Binding, G <: Graph[V, L, B, G]] { this: G =>
  def vertexes: Set[V]

  def links: Set[L]

  def bindings: Set[B]

  /**
    * Copies the graph.
    *
    * If you implement this trait as a "case class", you can implement this method just by using the Scala-provided copy() method.
    *
    * @param vertexes The new vertexes
    * @param links    The new links
    * @param bindings The new bindings
    * @return The resulting new graph
    */
  protected def graphCopy(vertexes: Set[V] = vertexes, links: Set[L] = links, bindings: Set[B] = bindings): G

  @transient
  private lazy val vertexMap =
    vertexes.map(vertex => vertex.id -> vertex)
      .toMap

  @transient
  private lazy val linkMap =
    links.map(link => link.id -> link)
      .toMap


  def addVertexes(vertexesToAdd: Set[V]): G = {
    val newVertexes = vertexes ++ vertexesToAdd

    require(newVertexes.size == vertexes.size + vertexesToAdd.size,
      "The vertexes to add must not belong to the graph")

    graphCopy(vertexes = newVertexes)
  }


  def addVertex(vertex: V) =
    addVertexes(Set(vertex))


  def replaceVertexes(replacingVertexes: Set[V]): G = {
    val newVertexes = vertexes.diff(replacingVertexes) ++ replacingVertexes

    require(
      newVertexes.size == vertexes.size,
      "The replacing vertexes must match vertexes belonging to the graph"
    )

    graphCopy(vertexes = newVertexes)
  }


  def replaceVertex(vertex: V) =
    replaceVertexes(Set(vertex))


  def removeVertexes(vertexesToRemove: Set[V]): G = {
    val newVertexes = vertexes.diff(vertexesToRemove)

    require(
      newVertexes.size == vertexes.size - vertexesToRemove.size,
      "All the vertexes to remove must belong to the graph"
    )

    val vertexIdsToRemove = vertexesToRemove.map(_.id)


    val (newBindings, bindingsToRemove) = bindings.partition(
      binding => binding.vertexIds.intersect(vertexIdsToRemove).isEmpty
    )


    val linkIdsToRemove = bindingsToRemove.map(_.linkId)
    val newLinks = links.filter(link => !linkIdsToRemove.contains(link.id))


    graphCopy(vertexes = newVertexes, links = newLinks, bindings = newBindings)
  }


  def removeVertex(vertex: V) =
    removeVertexes(Set(vertex))


  def bindLinks(bindingMapToAdd: Map[L, B]): G = {
    bindingMapToAdd.foreach {
      case (link, binding) =>
        require(link.id == binding.linkId, "Each binding must reference its associated link")
    }

    val linksToAdd = bindingMapToAdd.keySet
    val bindingsToAdd = bindingMapToAdd.values.toSet

    require(linksToAdd.size == bindingsToAdd.size, "The bindings must be distinct")

    bindingsToAdd.foreach(binding => {
      binding.vertexIds.foreach(vertexId =>
        require(vertexMap.contains(vertexId), "Each binding's vertexes must belong to the graph")
      )
    })


    val newLinks = links.union(linksToAdd)
    val newBindings = bindings.union(bindingsToAdd)

    require(newLinks.size == links.size + linksToAdd.size,
      "The links to add must not belong to the graph"
    )

    require(newBindings.size == bindings.size + bindingsToAdd.size,
      "The bindings to add must not belong to the graph"
    )

    graphCopy(links = newLinks, bindings = newBindings)
  }


  def bindLink(linkToAdd: L, bindingToAdd: B) =
    bindLinks(Map(linkToAdd -> bindingToAdd))


  def replaceLinks(replacingLinks: Set[L]): G = {
    val newLinks = links.diff(replacingLinks) ++ replacingLinks

    require(
      newLinks.size == links.size,
      "The replacing links must match links belonging to the graph"
    )

    graphCopy(links = newLinks)
  }


  def replaceLink(replacingLink: L) =
    replaceLinks(Set(replacingLink))


  def removeLinks(linksToRemove: Set[L]): G = {
    val newLinks = links.diff(linksToRemove)

    require(newLinks.size == links.size - linksToRemove.size,
      "All the links to remove must belong to the graph")

    val linkIdsToRemove = linksToRemove.map(_.id)

    val newBindings = bindings.filter(binding =>
      !linkIdsToRemove.contains(binding.linkId)
    )

    graphCopy(links = newLinks, bindings = newBindings)
  }


  def removeLink(linkToRemove: L) =
    removeLinks(Set(linkToRemove))


  def getVertex(id: UUID): Option[V] =
    vertexMap.get(id)


  def containsVertex(id: UUID): Boolean =
    getVertex(id).isDefined


  def getLink(id: UUID): Option[L] =
    linkMap.get(id)


  def containsLink(id: UUID): Boolean =
    getLink(id).isDefined

  @transient
  lazy val linkedVertexes: Set[V] =
    bindings
      .flatMap(binding =>
        binding.vertexIds.map(vertexId => getVertex(vertexId).get)
      )

  @transient
  lazy val unlinkedVertexes: Set[V] =
    vertexes.diff(linkedVertexes)
}
