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

import info.gianlucacosta.eighthbridge.fx.canvas._
import info.gianlucacosta.eighthbridge.graphs.point2point.visual.VisualGraph
import info.gianlucacosta.helios.fx.geometry.DiagonalBounds
import info.gianlucacosta.helios.fx.geometry.extensions.GeometryExtensions._

import scalafx.Includes._
import scalafx.geometry.{BoundingBox, Bounds, Point2D}
import scalafx.scene.Group
import scalafx.scene.input.{MouseButton, MouseEvent}
import scalafx.scene.shape.Rectangle

object BasicBackgroundNode {
  private val SelectionRectangleMinSize =
    2

  private val EmptySelectionBounds =
    new BoundingBox(0, 0, 0, 0)
}

/**
  * Default, interactive implementation of BackgroundNode
  */
class BasicBackgroundNode[
V <: BasicVertex[V],
L <: BasicLink[L],
G <: VisualGraph[V, L, G]
](val graphCanvas: GraphCanvas[V, L, G])
  extends Group
    with BackgroundNode[V, L, G]
    with BasicGraphCanvasNode[V, L, G] {
  styleClass.add("graph")

  private var dragAnchor: Point2D = _


  private val selectionBoundsX =
    new SimpleDoubleProperty(0)

  private val selectionBoundsY =
    new SimpleDoubleProperty(0)

  private val selectionBoundsWidth =
    new SimpleDoubleProperty(0)

  private val selectionBoundsHeight =
    new SimpleDoubleProperty(0)


  private def selectionBounds: Bounds =
    new BoundingBox(
      selectionBoundsX(),
      selectionBoundsY(),
      selectionBoundsWidth(),
      selectionBoundsHeight()
    )

  private def selectionBounds_=(newValue: Bounds): Unit = {
    selectionBoundsX() = newValue.minX
    selectionBoundsY() = newValue.minY
    selectionBoundsWidth() = newValue.width
    selectionBoundsHeight() = newValue.height
  }


  protected val backgroundRectangle =
    new Rectangle {
      styleClass.add("backgroundRectangle")
      x = 0
      y = 0

      width <==
        graphCanvas.width

      height <==
        graphCanvas.height
    }

  protected val selectionRectangle =
    new Rectangle {
      styleClass.add("selectionRectangle")

      x <==
        selectionBoundsX

      y <==
        selectionBoundsY

      width <==
        selectionBoundsWidth

      height <==
        selectionBoundsHeight
    }


  children.addAll(
    backgroundRectangle,
    selectionRectangle
  )


  override def render(): Unit = {
    //Just do nothing
  }


  handleEvent(MouseEvent.MousePressed) {
    (mouseEvent: MouseEvent) => {
      mouseEvent.button match {
        case MouseButton.Primary =>
          dragAnchor =
            mouseEvent.point

          graph =
            graph.deselectAll

        case MouseButton.Secondary =>
          graph =
            graph.deselectAll

        case _ =>
      }

      ()
    }
  }


  handleEvent(MouseEvent.MouseDragged) {
    (mouseEvent: MouseEvent) => {
      mouseEvent.button match {
        case MouseButton.Primary =>
          if (controller.canDrawSelectionRectangle) {
            val currentPoint =
              mouseEvent.point

            val clippedPoint =
              currentPoint.clip(graphCanvas.dimension)

            selectionBounds =
              new DiagonalBounds(dragAnchor, clippedPoint)
          }
        case _ =>
      }
    }
  }


  handleEvent(MouseEvent.MouseReleased) {
    (mouseEvent: MouseEvent) => {
      mouseEvent.button match {
        case MouseButton.Primary =>
          if (selectionBounds.width < BasicBackgroundNode.SelectionRectangleMinSize
            && selectionBounds.height < BasicBackgroundNode.SelectionRectangleMinSize) {
            controller.createVertex(graph, mouseEvent.point)
              .foreach(newGraph => {
                selectionBounds =
                  BasicBackgroundNode.EmptySelectionBounds

                graph =
                  newGraph
              })
          } else {
            val selectionVertexes = graphCanvas.vertexNodes
              .values
              .filter(_.intersects(selectionBounds))
              .map(_.vertex)
              .toSet

            val selectionLinks = graphCanvas.linkNodes
              .values
              .filter(_.intersects(selectionBounds))
              .map(_.link)
              .toSet

            selectionBounds =
              BasicBackgroundNode.EmptySelectionBounds

            graph =
              graph.setSelection(
                selectionVertexes,
                selectionLinks
              )
          }

        case _ =>
      }
    }
  }
}
