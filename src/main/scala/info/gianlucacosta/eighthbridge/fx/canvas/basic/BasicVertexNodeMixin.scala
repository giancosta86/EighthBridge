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

import javafx.beans.property.SimpleDoubleProperty

import info.gianlucacosta.eighthbridge.fx.canvas.VertexNode
import info.gianlucacosta.eighthbridge.graphs.point2point.visual.VisualGraph
import info.gianlucacosta.helios.fx.geometry.extensions.GeometryExtensions._

import scalafx.Includes._
import scalafx.geometry.Point2D
import scalafx.scene.input.{MouseButton, MouseEvent}


/**
  * Trait containing behaviour and event handling routines that can be shared
  * by different VertexNode implementations that can be plugged into the "Basic" package
  *
  * @tparam V
  * @tparam L
  * @tparam G
  */
trait BasicVertexNodeMixin[
V <: BasicVertex[V],
L <: BasicLink[L],
G <: VisualGraph[V, L, G]
] extends BasicGraphCanvasNode[V, L, G]
  with VertexNode[V, L, G] {
  private var dragAnchor: Point2D = _

  protected val centerX =
    new SimpleDoubleProperty(0)


  protected val centerY =
    new SimpleDoubleProperty(0)


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
              dragAnchor =
                mouseEvent.point

              if (mouseEvent.controlDown) {
                controller.setVertexSelectedState(graph, vertex, !vertex.selected)
                  .foreach(newGraph =>
                    graph =
                      newGraph
                  )
              } else if (!vertex.selected) {
                controller.setSelection(graph, Set(vertex), Set())
                  .foreach(newGraph =>
                    graph =
                      newGraph
                  )
              }

            case 2 =>
              val selectedVertexes =
                graph.selectedVertexes

              if (selectedVertexes.size == 1 && graph.selectedLinks.isEmpty) {
                val selectedVertex =
                  selectedVertexes.head

                controller.editVertex(graph, selectedVertex)
                  .foreach(newGraph =>
                    graph =
                      newGraph
                  )
              }

            case _ =>
          }

        case MouseButton.Secondary =>
          mouseEvent.clickCount match {
            case 1 =>
              if (graph.selectedVertexes.size == 1 && !graph.selectedVertexes.contains(vertex) && graph.selectedLinks.isEmpty) {
                val selectedVertex =
                  graph.selectedVertexes.head

                controller.createLink(graph, selectedVertex, vertex)
                  .foreach(newGraph =>
                    graph =
                      newGraph
                  )
              }
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
          val mousePoint =
            mouseEvent.point

          val delta =
            mousePoint - dragAnchor


          if (vertex.selected) {
            controller.dragSelection(graphCanvas, delta)
              .foreach(newGraph => {
                dragAnchor =
                  mousePoint

                graph =
                  newGraph
              })
          }
        case _ =>
      }
    }
  }


  override def render(): Unit = {
    super.render()

    centerX() =
      vertex.center.x

    centerY() =
      vertex.center.y
  }
}
