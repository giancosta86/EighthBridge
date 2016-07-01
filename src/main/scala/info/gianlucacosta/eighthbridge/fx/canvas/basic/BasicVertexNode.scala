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
import info.gianlucacosta.helios.fx.styles.PseudoClasses

import scala.collection.JavaConversions._
import scalafx.Includes._
import scalafx.geometry.{Point2D, Pos}
import scalafx.scene.Group
import scalafx.scene.control.Label
import scalafx.scene.input.{MouseButton, MouseEvent}
import scalafx.scene.shape.Rectangle
import scalafx.scene.text.TextAlignment

/**
  * Default, interactive implementation of VertexNode
  */
class BasicVertexNode[
V <: BasicVertex[V],
L <: BasicLink[L],
G <: VisualGraph[V, L, G]
] extends Group with VertexNode[V, L, G] {
  private var controller: BasicController[V, L, G] = _
  private var graph: G = _
  private var _vertex: V = _

  override def vertex: V = _vertex

  private def vertex_=(newVertex: V): Unit = {
    this._vertex = newVertex
  }

  private var dragAnchor: Point2D = _

  protected val body = new Rectangle {
    styleClass.add("body")
  }

  protected val label = new Label {
    styleClass.add("label")
    alignment = Pos.Center
    textAlignment = TextAlignment.Center
  }

  children.addAll(body, label)


  override def setup(controller: GraphCanvasController[V, L, G], graph: G, vertex: V): Unit = {
    this.controller = controller.asInstanceOf[BasicController[V, L, G]]
    this.graph = graph
    this.vertex = vertex
  }


  override def render(): Unit = {
    styleClass.setAll("vertex")
    styleClass.addAll(vertex.styleClasses)


    this.pseudoClassStateChanged(
      PseudoClasses.Selected,
      vertex.selected
    )

    val dimension = vertex.dimension

    val width = dimension.width
    val height = dimension.height

    body.width = width
    body.height = height
    body.layoutX = vertex.center.x - width / 2
    body.layoutY = vertex.center.y - height / 2


    label.text = vertex.text

    label.setPrefSize(width, height)
    label.layoutX = body.layoutX()
    label.layoutY = body.layoutY()
  }


  handleEvent(MouseEvent.Any) {
    (mouseEvent: MouseEvent) => {
      mouseEvent.consume()
    }
  }


  handleEvent(MouseEvent.MousePressed) {
    (mouseEvent: MouseEvent) => {
      mouseEvent.button match {
        case MouseButton.Primary =>
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

        case MouseButton.Secondary =>
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
        case MouseButton.Primary =>
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
