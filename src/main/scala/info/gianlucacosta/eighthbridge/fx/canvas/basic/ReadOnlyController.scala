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

package info.gianlucacosta.eighthbridge.fx.canvas.basic

import info.gianlucacosta.eighthbridge.graphs.point2point.visual.{VisualGraph, VisualLink, VisualVertex}

import scalafx.geometry.Point2D

/**
  * Controller only showing a graph - totally preventing interactivity
  */
class ReadOnlyController[V <: BasicVertex[V], L <: BasicLink[L], G <: VisualGraph[V, L, G]] extends BasicController[V, L, G] {
  override def dragSelectionBounds(graph: G, dragAnchor: Point2D, currentPoint: Point2D): Option[G] =
    None

  override def createVertex(graph: G, center: Point2D): Option[G] =
    None

  override def createLink(graph: G, sourceVertex: V, targetVertex: V): Option[G] =
    None

  override def setLinkSelectedState(graph: G, link: L, selected: Boolean): Option[G] =
    None

  override def editVertex(graph: G, vertex: V): Option[G] =
    None

  override def setSelection(graph: G, selectionVertexes: Set[V], selectionLinks: Set[L]): Option[G] =
    None

  override def editLink(graph: G, link: L): Option[G] =
    None

  override def setVertexSelectedState(graph: G, vertex: V, selected: Boolean): Option[G] =
    None

  override def deleteSelection(graph: G): Option[G] =
    None

  override def dragSelection(graph: G, delta: Point2D): Option[G] =
    None

  override def createLinkInternalPoint(graph: G, link: L, newInternalPoints: List[Point2D], internalPoint: Point2D): Option[G] =
    None

  override def deleteLinkInternalPoint(graph: G, link: L, newInternalPoints: List[Point2D], internalPoint: Point2D): Option[G] =
    None

  override def canDragLinkInternalPoint(graph: G, link: L, newInternalPoints: List[Point2D], oldInternalPoint: Point2D, newInternalPoint: Point2D): Boolean =
    false

  override def dragLinkLabel(graph: G, link: L, oldCenter: Point2D, newCenter: Point2D): Option[G] =
    None
}
