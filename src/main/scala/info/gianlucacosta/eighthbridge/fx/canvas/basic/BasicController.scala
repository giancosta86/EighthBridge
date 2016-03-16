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
trait BasicController extends GraphCanvasController {
  override def createBackgroundNode(): BackgroundNode =
    new BasicBackgroundNode


  override def createVertexNode(vertex: VisualVertex): VertexNode =
    new BasicVertexNode()


  override def createLinkNode(sourceVertex: VisualVertex, targetVertex: VisualVertex, link: VisualLink): LinkNode =
    new BasicLinkNode(sourceVertex.id, targetVertex.id)


  def createVertex(graph: VisualGraph, center: Point2D): Option[VisualGraph]

  def createLink(graph: VisualGraph, sourceVertex: VisualVertex, targetVertex: VisualVertex): Option[VisualGraph]

  def setVertexSelectedState(graph: VisualGraph, vertex: VisualVertex, selected: Boolean): Option[VisualGraph]

  def setLinkSelectedState(graph: VisualGraph, link: VisualLink, selected: Boolean): Option[VisualGraph]

  def setSelection(graph: VisualGraph, selectionVertexes: Set[VisualVertex], selectionLinks: Set[VisualLink]): Option[VisualGraph]

  def editVertex(graph: VisualGraph, vertex: VisualVertex): Option[VisualGraph]

  def editLink(graph: VisualGraph, link: VisualLink): Option[VisualGraph]

  def dragSelection(graph: VisualGraph, delta: Point2D): Option[VisualGraph]

  def createLinkInternalPoint(graph: VisualGraph, link: VisualLink, newInternalPoints: List[Point2D], internalPoint: Point2D): Option[VisualGraph]

  def canDragLinkInternalPoint(graph: VisualGraph, link: VisualLink, newInternalPoints: List[Point2D], oldInternalPoint: Point2D, newInternalPoint: Point2D): Boolean

  def deleteLinkInternalPoint(graph: VisualGraph, link: VisualLink, newInternalPoints: List[Point2D], internalPoint: Point2D): Option[VisualGraph]

  def dragLinkLabel(graph: VisualGraph, link: VisualLink, oldCenter: Point2D, newCenter: Point2D): Option[VisualGraph]
}
