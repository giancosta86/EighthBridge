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
import info.gianlucacosta.eighthbridge.graphs.point2point.visual.{VisualGraph, VisualVertex}
import info.gianlucacosta.eighthbridge.util.fx.geometry.MouseEventExtensions._
import info.gianlucacosta.eighthbridge.util.fx.geometry.Point2DExtensions._

import scalafx.Includes._
import scalafx.geometry.{Point2D, VPos}
import scalafx.scene.Group
import scalafx.scene.input.{MouseButton, MouseEvent}
import scalafx.scene.shape.Rectangle
import scalafx.scene.text.Text

/**
  * Default, interactive implementation of VertexNode
  */
class BasicVertexNode extends Group with VertexNode {
  private var controller: BasicController = _
  private var graph: VisualGraph = _
  private var _vertex: VisualVertex = _

  override def vertex: VisualVertex = _vertex

  private def vertex_=(newVertex: VisualVertex): Unit = {
    this._vertex = newVertex
  }

  private var dragAnchor: Point2D = _

  private val labelTextBox = new Text {
    textOrigin = VPos.Top
  }
  private val vertexBox = new Rectangle
  children.addAll(vertexBox, labelTextBox)


  override def setup(controller: GraphCanvasController, graph: VisualGraph, vertex: VisualVertex): Unit = {
    this.controller = controller.asInstanceOf[BasicController]
    this.graph = graph
    this.vertex = vertex

    require(this.controller != null)
    require(this.graph != null)
    require(this.vertex != null)
  }


  override def render(): Unit = {
    val settings =
      if (vertex.selected) {
        vertex.selectedSettings
      } else {
        vertex.settings
      }

    labelTextBox.font = settings.font
    labelTextBox.text = vertex.text
    labelTextBox.fill = settings.fontColor
    labelTextBox.strokeWidth = 0

    val textBounds = labelTextBox.getBoundsInParent
    val labelWidth = textBounds.getWidth
    val labelHeight = textBounds.getHeight

    val labelX = vertex.center.x - labelWidth / 2
    val labelY = vertex.center.y - labelHeight / 2

    labelTextBox.x = labelX
    labelTextBox.y = labelY

    val padding = settings.padding

    vertexBox.fill = settings.background
    vertexBox.strokeWidth = settings.borderSize
    vertexBox.stroke = settings.borderColor

    vertexBox.width = labelWidth + 2 * padding
    vertexBox.height = labelHeight + 2 * padding
    vertexBox.layoutX = labelX - padding
    vertexBox.layoutY = labelY - padding
    vertexBox.arcWidth = settings.rounding
    vertexBox.arcHeight = settings.rounding
  }


  handleEvent(MouseEvent.Any) {
    (mouseEvent: MouseEvent) => {
      mouseEvent.consume()
    }
  }


  handleEvent(MouseEvent.MousePressed) {
    (mouseEvent: MouseEvent) => {
      mouseEvent.button match {
        case MouseButton.PRIMARY =>
          mouseEvent.clickCount match {
            case 1 =>
              dragAnchor = mouseEvent.point
              if (mouseEvent.controlDown) {
                controller.setVertexSelectedState(graph, vertex, !vertex.selected)
                  .foreach(newGraph => notifyGraphChanged(newGraph))
              } else if (!vertex.selected) {
                controller.setSelection(graph, Set(vertex), Set())
                  .foreach(newGraph => notifyGraphChanged(newGraph))
              }

            case 2 =>
              val selectedVertexes = graph.selectedVertexes

              if (selectedVertexes.size == 1 && graph.selectedLinks.isEmpty) {
                val selectedVertex = selectedVertexes.head

                controller.editVertex(graph, selectedVertex)
                  .foreach(newGraph => notifyGraphChanged(newGraph))
              }


            case _ =>
          }

        case MouseButton.SECONDARY =>
          mouseEvent.clickCount match {
            case 1 =>
              if (graph.selectedVertexes.size == 1 && !graph.selectedVertexes.contains(vertex) && graph.selectedLinks.isEmpty) {
                val selectedVertex = graph.selectedVertexes.head

                controller.createLink(graph, selectedVertex, vertex)
                  .foreach(newGraph => notifyGraphChanged(newGraph))
              }

            case _ =>
          }

        case _ =>
      }
      ()
    }
  }


  handleEvent(MouseEvent.MouseDragged) {
    (mouseEvent: MouseEvent) => {
      mouseEvent.button match {
        case MouseButton.PRIMARY =>
          val mousePoint = mouseEvent.point
          val delta = mousePoint - dragAnchor


          if (vertex.selected) {
            controller.dragSelection(graph, delta)
              .foreach(newGraph => {
                dragAnchor = mousePoint
                notifyGraphChanged(newGraph)
              })
          }
        case _ =>
      }
    }
  }
}
