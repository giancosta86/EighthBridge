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

import info.gianlucacosta.eighthbridge.fx.canvas._
import info.gianlucacosta.eighthbridge.graphs.point2point.visual.{VisualGraph, VisualLink, VisualVertex}

import scalafx.geometry.Point2D

/**
  * Enhanced controller supporting interactivity for all the nodes in this package.
  *
  * When implementing this trait, you can make any interactive method return None to disable the related feature.
  *
  * You can also employ one of the concrete subclasses provided by the package, or one of the mixin subtraits.
  */
trait BasicController[V <: VisualVertex[V], L <: VisualLink[L], G <: VisualGraph[V, L, G]] extends GraphCanvasController[V, L, G] {
  override def createBackgroundNode(): BackgroundNode[V, L, G] =
    new BasicBackgroundNode


  override def createVertexNode(vertex: V): VertexNode[V, L, G] =
    new BasicVertexNode()


  override def createLinkNode(sourceVertex: V, targetVertex: V, link: L): LinkNode[V, L, G] =
    new BasicLinkNode(sourceVertex.id, targetVertex.id)


  def createVertex(graph: G, center: Point2D): Option[G]

  def createLink(graph: G, sourceVertex: V, targetVertex: V): Option[G]

  def setVertexSelectedState(graph: G, vertex: V, selected: Boolean): Option[G]

  def setLinkSelectedState(graph: G, link: L, selected: Boolean): Option[G]

  def setSelection(graph: G, selectionVertexes: Set[V], selectionLinks: Set[L]): Option[G]

  def editVertex(graph: G, vertex: V): Option[G]

  def editLink(graph: G, link: L): Option[G]

  def dragSelection(graph: G, delta: Point2D): Option[G]

  def createLinkInternalPoint(graph: G, link: L, newInternalPoints: List[Point2D], internalPoint: Point2D): Option[G]

  def canDragLinkInternalPoint(graph: G, link: L, newInternalPoints: List[Point2D], oldInternalPoint: Point2D, newInternalPoint: Point2D): Boolean

  def deleteLinkInternalPoint(graph: G, link: L, newInternalPoints: List[Point2D], internalPoint: Point2D): Option[G]

  def dragLinkLabel(graph: G, link: L, oldCenter: Point2D, newCenter: Point2D): Option[G]
}
