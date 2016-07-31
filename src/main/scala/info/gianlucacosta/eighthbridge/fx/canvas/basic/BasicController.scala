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
import info.gianlucacosta.eighthbridge.graphs.point2point.visual.VisualGraph
import info.gianlucacosta.helios.fx.geometry.extensions.GeometryExtensions._

import scalafx.geometry.{Dimension2D, Point2D}


object BasicController {
  val GraphPadding =
    20
}


/**
  * Enhanced controller supporting interactivity for all the nodes in this package.
  *
  * When implementing or overriding this trait, you can make any interactive method return None
  * to disable the related feature.
  *
  * You can also employ one of the concrete subclasses provided by the package,
  * or one of the mixin subtraits.
  */
trait BasicController[
V <: BasicVertex[V],
L <: BasicLink[L],
G <: VisualGraph[V, L, G]
] extends GraphCanvasController[V, L, G] {
  override def createBackgroundNode(graphCanvas: GraphCanvas[V, L, G]): BackgroundNode[V, L, G] =
    new BasicBackgroundNode(graphCanvas)


  override def createVertexNode(graphCanvas: GraphCanvas[V, L, G], vertex: V): VertexNode[V, L, G] =
    new BasicVertexNode(graphCanvas)


  override def createLinkNode(graphCanvas: GraphCanvas[V, L, G], sourceVertex: V, targetVertex: V, link: L): LinkNode[V, L, G] =
    new BasicLinkNode(graphCanvas, sourceVertex.id, targetVertex.id)


  def createVertex(graph: G, center: Point2D): Option[G]

  def createLink(graph: G, sourceVertex: V, targetVertex: V): Option[G]


  def editVertex(graph: G, vertex: V): Option[G]

  def editLink(graph: G, link: L): Option[G]


  def canDrawSelectionRectangle: Boolean

  def setVertexSelectedState(graph: G, vertex: V, selected: Boolean): Option[G]

  def setLinkSelectedState(graph: G, link: L, selected: Boolean): Option[G]

  def setSelection(graph: G, selectionVertexes: Set[V], selectionLinks: Set[L]): Option[G]


  def dragSelection(graphCanvas: GraphCanvas[V, L, G], delta: Point2D): Option[G] = {
    val graph =
      graphCanvas.graph

    Some(
      graph.replaceVertexes(
        graph.selectedVertexes.map(vertex => {
          val newCenter =
            (vertex.center + delta).clip(graphCanvas.dimension)

          vertex.visualCopy(center = newCenter)
        })
      )
    )
  }

  def createLinkInternalPoint(graph: G, link: L, newInternalPoints: List[Point2D], internalPoint: Point2D): Option[G]

  def canDragLinkInternalPoint(graph: G, link: L, newInternalPoints: List[Point2D], oldInternalPoint: Point2D, newInternalPoint: Point2D): Boolean

  def deleteLinkInternalPoint(graph: G, link: L, newInternalPoints: List[Point2D], internalPoint: Point2D): Option[G]

  def dragLinkLabel(graph: G, link: L, oldCenter: Point2D, newCenter: Point2D): Option[G]


  def minCanvasDimension: Dimension2D =
    new Dimension2D(
      800,
      600
    )

  override def getCanvasDimension(graphCanvas: GraphCanvas[V, L, G]): Dimension2D = {
    if (graphCanvas.vertexNodes.isEmpty)
      minCanvasDimension
    else {
      val maxRightEdge =
        graphCanvas
          .vertexNodes
          .values
          .map(vertexNode =>
            vertexNode.vertex.center.x + vertexNode.width() / 2
          )
          .max

      val maxBottomEdge =
        graphCanvas
          .vertexNodes
          .values
          .map(vertexNode =>
            vertexNode.vertex.center.y + vertexNode.height() / 2
          )
          .max


      new Dimension2D(
        math.max(
          maxRightEdge + BasicController.GraphPadding,
          minCanvasDimension.width
        ),

        math.max(
          maxBottomEdge + BasicController.GraphPadding,
          minCanvasDimension.height
        )
      )
    }
  }
}
