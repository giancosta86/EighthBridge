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

import java.util.UUID

import info.gianlucacosta.eighthbridge.fx.canvas._
import info.gianlucacosta.eighthbridge.graphs.point2point.visual.VisualGraph
import info.gianlucacosta.helios.fx.geometry.extensions.GeometryExtensions._

import scalafx.Includes._
import scalafx.geometry.{BoundingBox, Point2D}
import scalafx.scene.Group
import scalafx.scene.input.{MouseButton, MouseEvent}
import scalafx.scene.shape.Rectangle

object BasicBackgroundNode {
  private val SelectionRectangleMinSize = 2
  private val EmptySelectionBounds = new BoundingBox(0, 0, 0, 0)
}

/**
  * Default, interactive implementation of BackgroundNode
  */
class BasicBackgroundNode[
V <: BasicVertex[V],
L <: BasicLink[L],
G <: VisualGraph[V, L, G]
] extends Group with BackgroundNode[V, L, G] {
  styleClass.add("graph")

  private var controller: BasicController[V, L, G] = _
  private var graph: G = _

  private var vertexNodes: Map[UUID, BasicVertexNode[V, L, G]] = _
  private var linkNodes: Map[UUID, BasicLinkNode[V, L, G]] = _

  private var dragAnchor: Point2D = _


  protected val backgroundRectangle = new Rectangle {
    styleClass.add("backgroundRectangle")
    x = 0
    y = 0
  }

  protected val selectionRectangle = new Rectangle {
    styleClass.add("selectionRectangle")
  }


  children.addAll(
    backgroundRectangle,
    selectionRectangle
  )


  override def setup(controller: GraphCanvasController[V, L, G], graph: G, vertexNodes: Map[UUID, VertexNode[V, L, G]], linkNodes: Map[UUID, LinkNode[V, L, G]]): Unit = {
    this.controller = controller.asInstanceOf[BasicController[V, L, G]]
    this.graph = graph
    this.vertexNodes = vertexNodes.asInstanceOf[Map[UUID, BasicVertexNode[V, L, G]]]
    this.linkNodes = linkNodes.asInstanceOf[Map[UUID, BasicLinkNode[V, L, G]]]
  }


  override def render() {
    backgroundRectangle.width = graph.dimension.width
    backgroundRectangle.height = graph.dimension.height

    selectionRectangle.x = graph.selectionBounds.minX
    selectionRectangle.y = graph.selectionBounds.minY
    selectionRectangle.width = graph.selectionBounds.width
    selectionRectangle.height = graph.selectionBounds.height
  }


  handleEvent(MouseEvent.MousePressed) {
    (mouseEvent: MouseEvent) => {
      mouseEvent.button match {
        case MouseButton.Primary =>
          dragAnchor = mouseEvent.point
          notifyGraphChanged(
            graph.deselectAll
          )

        case MouseButton.Secondary =>
          notifyGraphChanged(
            graph.deselectAll
          )

        case _ =>
      }

      ()
    }
  }


  handleEvent(MouseEvent.MouseDragged) {
    (mouseEvent: MouseEvent) => {
      mouseEvent.button match {
        case MouseButton.Primary =>
          controller.dragSelectionBounds(graph, dragAnchor, mouseEvent.point)
            .foreach(newGraph =>
              notifyGraphChanged(newGraph)
            )

        case _ =>
      }
    }
  }


  handleEvent(MouseEvent.MouseReleased) {
    (mouseEvent: MouseEvent) => {
      mouseEvent.button match {
        case MouseButton.Primary =>
          if (graph.selectionBounds.width < BasicBackgroundNode.SelectionRectangleMinSize
            && graph.selectionBounds.height < BasicBackgroundNode.SelectionRectangleMinSize) {
            controller.createVertex(graph, mouseEvent.point)
              .foreach(newGraph =>
                notifyGraphChanged(
                  newGraph
                    .visualCopy(selectionBounds = BasicBackgroundNode.EmptySelectionBounds)
                )
              )
          } else {
            val selectionVertexes = vertexNodes
              .values
              .filter(_.intersects(graph.selectionBounds))
              .map(_.vertex)
              .toSet

            val selectionLinks = linkNodes
              .values
              .filter(_.intersects(graph.selectionBounds))
              .map(_.link)
              .toSet


            notifyGraphChanged(
              graph
                .setSelection(selectionVertexes, selectionLinks)
                .visualCopy(
                  selectionBounds = BasicBackgroundNode.EmptySelectionBounds
                )
            )
          }

        case _ =>
      }
    }
  }
}
