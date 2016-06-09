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

package info.gianlucacosta.eighthbridge.fx.canvas.basic.editing

import info.gianlucacosta.eighthbridge.fx.canvas.basic.{BasicController, BasicLink, BasicVertex}
import info.gianlucacosta.eighthbridge.graphs.point2point.visual.{VisualGraph, VisualLink, VisualVertex}
import info.gianlucacosta.helios.fx.dialogs.Alerts

import scalafx.geometry.Point2D

/**
  * Interactive controller mixin providing full interactivity and the ability to easily edit vertexes and links
  *
  * @tparam V Vertex
  * @tparam L Link
  */
trait InteractiveEditingController[V <: BasicVertex[V], L <: BasicLink[L], G <: VisualGraph[V, L, G]] extends BasicController[V, L, G] {
  override def setVertexSelectedState(graph: G, vertex: V, selected: Boolean): Option[G] =
    Some(
      graph.replaceVertex(vertex.visualCopy(selected = selected))
    )


  override def setLinkSelectedState(graph: G, link: L, selected: Boolean): Option[G] =
    Some(
      graph.replaceLink(link.visualCopy(selected = selected))
    )


  override def setSelection(graph: G, selectionVertexes: Set[V], selectionLinks: Set[L]): Option[G] =
    Some(
      graph.setSelection(selectionVertexes, selectionLinks)
    )


  override def deleteSelection(graph: G): Option[G] = {
    val selectedVertexes = graph.selectedVertexes
    val selectedLinks = graph.selectedLinks

    Some(
      graph
        .removeVertexes(selectedVertexes)
        .removeLinks(selectedLinks)
    )
  }


  override def dragSelection(graph: G, delta: Point2D): Option[G] =
    Some(
      graph.moveSelectedVertexesBy(delta)
    )


  override def createLinkInternalPoint(graph: G, link: L, newInternalPoints: List[Point2D], internalPoint: Point2D): Option[G] = {
    val newLink = link.visualCopy(internalPoints = newInternalPoints)

    Some(
      graph.replaceLink(newLink)
    )
  }


  override def canDragLinkInternalPoint(graph: G, link: L, newInternalPoints: List[Point2D], oldInternalPoint: Point2D, newInternalPoint: Point2D): Boolean =
    true


  override def deleteLinkInternalPoint(graph: G, link: L, newInternalPoints: List[Point2D], internalPoint: Point2D): Option[G] = {
    val newLink = link.visualCopy(internalPoints = newInternalPoints)

    Some(
      graph.replaceLink(newLink)
    )
  }

  override def dragLinkLabel(graph: G, link: L, oldCenter: Point2D, newCenter: Point2D): Option[G] = {
    val newLink = link.visualCopy(
      labelCenter = Some(
        newCenter
      )
    )

    Some(
      graph.replaceLink(newLink)
    )
  }


  override def editVertex(graph: G, vertex: V): Option[G] = {
    while (true) {
      try {
        val editResult = interactiveVertexEditing(graph, vertex.asInstanceOf[V])

        if (editResult.isEmpty) {
          return None
        }

        val newVertex = editResult.get

        return Some(graph.replaceVertex(newVertex))
      } catch {
        case ex: IllegalArgumentException =>
          Alerts.showWarning(ex.getMessage, "Edit vertex")
      }
    }

    throw new AssertionError()
  }


  /**
    * Interacts with the user about the vertex properties.
    *
    * It can throw IllegalArgumentException, making the system notify the error and ask again.
    *
    * @param graph  The graph
    * @param vertex The vertex to edit
    * @return Some(new vertex) if the editing is complete, None if the user canceled the editing
    *
    */
  protected def interactiveVertexEditing(graph: G, vertex: V): Option[V]


  override def editLink(graph: G, link: L): Option[G] = {
    while (true) {
      try {
        val editResult = interactiveLinkEditing(graph, link.asInstanceOf[L])

        if (editResult.isEmpty) {
          return None
        }

        val newLink = editResult.get

        return Some(graph.replaceLink(newLink))
      } catch {
        case ex: IllegalArgumentException =>
          Alerts.showWarning(ex.getMessage, "Edit vertex")
      }
    }

    throw new AssertionError()
  }


  /**
    * Interacts with the user about the link properties.
    *
    * It can throw IllegalArgumentException, making the system notify the error and ask again.
    *
    * @param graph The graph
    * @param link  The link to edit
    * @return Some(new link) if the editing is complete, None if the user canceled the editing
    *
    */
  protected def interactiveLinkEditing(graph: G, link: L): Option[L]
}
