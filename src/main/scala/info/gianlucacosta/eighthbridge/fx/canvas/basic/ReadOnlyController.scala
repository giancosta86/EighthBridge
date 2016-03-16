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
class ReadOnlyController extends BasicController {
  override def createVertex(graph: VisualGraph, center: Point2D): Option[VisualGraph] =
    None

  override def createLink(graph: VisualGraph, sourceVertex: VisualVertex, targetVertex: VisualVertex): Option[VisualGraph] =
    None

  override def setLinkSelectedState(graph: VisualGraph, link: VisualLink, selected: Boolean): Option[VisualGraph] =
    None

  override def editVertex(graph: VisualGraph, vertex: VisualVertex): Option[VisualGraph] =
    None

  override def setSelection(graph: VisualGraph, selectionVertexes: Set[VisualVertex], selectionLinks: Set[VisualLink]): Option[VisualGraph] =
    None

  override def editLink(graph: VisualGraph, link: VisualLink): Option[VisualGraph] =
    None

  override def setVertexSelectedState(graph: VisualGraph, vertex: VisualVertex, selected: Boolean): Option[VisualGraph] =
    None

  override def deleteSelection(graph: VisualGraph): Option[VisualGraph] =
    None

  override def dragSelection(graph: VisualGraph, delta: Point2D): Option[VisualGraph] =
    None

  override def createLinkInternalPoint(graph: VisualGraph, link: VisualLink, newInternalPoints: List[Point2D], internalPoint: Point2D): Option[VisualGraph] =
    None

  override def deleteLinkInternalPoint(graph: VisualGraph, link: VisualLink, newInternalPoints: List[Point2D], internalPoint: Point2D): Option[VisualGraph] =
    None

  override def canDragLinkInternalPoint(graph: VisualGraph, link: VisualLink, newInternalPoints: List[Point2D], oldInternalPoint: Point2D, newInternalPoint: Point2D): Boolean =
    false

  override def dragLinkLabel(graph: VisualGraph, link: VisualLink, oldCenter: Point2D, newCenter: Point2D): Option[VisualGraph] =
    None
}
