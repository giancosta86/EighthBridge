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
import info.gianlucacosta.eighthbridge.graphs.point2point.visual.{VisualGraph, VisualLink, VisualLinkSettings, VisualVertex}
import info.gianlucacosta.eighthbridge.util.fx.geometry.BoundsExtensions._
import info.gianlucacosta.eighthbridge.util.fx.geometry.MouseEventExtensions._
import info.gianlucacosta.eighthbridge.util.fx.geometry.Point2DExtensions._
import info.gianlucacosta.eighthbridge.util.fx.geometry._

import scalafx.Includes._
import scalafx.geometry.{Point2D, VPos}
import scalafx.scene.Group
import scalafx.scene.input.{MouseButton, MouseEvent}
import scalafx.scene.shape._
import scalafx.scene.text.Text

/**
  * Default, interactive implementation of LinkNode
  *
  * @param sourceVertexId
  * @param targetVertexId
  */
class BasicLinkNode[V <: VisualVertex[V], L <: VisualLink[L], G <: VisualGraph[V, L, G]](val sourceVertexId: UUID, val targetVertexId: UUID) extends Group with LinkNode[V, L, G] {

  private class LinkSegment(indexOfNewInternalPoint: Int, linkSettings: VisualLinkSettings) extends Segment {
    strokeWidth = linkSettings.lineSize
    stroke = linkSettings.lineColor
    strokeLineCap = StrokeLineCap.ROUND

    handleEvent(MouseEvent.MousePressed) {
      (mouseEvent: MouseEvent) => {
        mouseEvent.button match {
          case MouseButton.SECONDARY =>

            val internalPoint = mouseEvent.point

            val newInternalPoints = {
              val (precedingInternalPoints, followingInternalPoints) = link.internalPoints.splitAt(indexOfNewInternalPoint)

              precedingInternalPoints ++ (internalPoint :: followingInternalPoints)
            }

            controller.createLinkInternalPoint(graph, link, newInternalPoints, internalPoint)
              .foreach(newGraph => notifyGraphChanged(newGraph))

            mouseEvent.consume()


          case _ =>
        }
      }
    }
  }

  private class LinkArrow(segment: LinkSegment, linkSettings: VisualLinkSettings) extends Group {
    private val leftSegment = new Line()
    private val rightSegment = new Line()

    children.addAll(leftSegment, rightSegment)

    val relativePosition = linkSettings.arrowRelativePosition
    val angle = linkSettings.arrowAngle
    val length = linkSettings.arrowLength

    val startPoint = segment.startPoint
    val stopPoint = segment.endPoint

    val anchor = new Point2D(
      startPoint.x + relativePosition * (stopPoint.x - startPoint.x),
      startPoint.y + relativePosition * (stopPoint.y - startPoint.y)
    )

    val gamma = Math.atan2(anchor.y - startPoint.y, anchor.x - startPoint.x)
    val dPx = length * Math.cos(gamma - angle)
    val dPy = length * Math.sin(gamma - angle)

    val epsilon = Math.PI - gamma - angle
    val dQx = length * Math.cos(epsilon)
    val dQy = length * Math.sin(epsilon)


    val (leftEndX, leftEndY, rightEndX, rightEndY) =

      if (anchor.y <= startPoint.y) {
        (
          anchor.x - dPx,
          anchor.y - dPy,

          anchor.x + dQx,
          anchor.y - dQy
          )
      } else {
        (
          anchor.x + dQx,
          anchor.y - dQy,

          anchor.x - dPx,
          anchor.y - dPy
          )
      }

    leftSegment.startX = anchor.x
    leftSegment.startY = anchor.y
    leftSegment.endX = leftEndX
    leftSegment.endY = leftEndY

    rightSegment.startX = anchor.x
    rightSegment.startY = anchor.y
    rightSegment.endX = rightEndX
    rightSegment.endY = rightEndY

    leftSegment.strokeWidth = linkSettings.lineSize
    leftSegment.stroke = linkSettings.lineColor

    rightSegment.strokeWidth = linkSettings.lineSize
    rightSegment.stroke = linkSettings.lineColor
  }


  private class InternalPointHandle(initialCenter: Point2D) extends Ellipse {
    opacity <== when(hover) choose 1 otherwise 0

    def center: Point2D = new Point2D(
      centerX.value,
      centerY.value
    )

    def center_=(newPoint: Point2D) = {
      centerX = newPoint.x
      centerY = newPoint.y
    }


    def render(linkSettings: VisualLinkSettings): Unit = {
      radiusX = linkSettings.handleRadiusX
      radiusY = linkSettings.handleRadiusY
      fill = linkSettings.handleColor
    }

    center = initialCenter


    handleEvent(MouseEvent.MousePressed) {
      (mouseEvent: MouseEvent) => {
        mouseEvent.button match {
          case MouseButton.SECONDARY =>
            mouseEvent.clickCount match {
              case 1 =>
                val newInternalPoints = link.internalPoints.filter(internalPoint => internalPoint != center)
                require(newInternalPoints.length == link.internalPoints.length - 1)

                controller.deleteLinkInternalPoint(graph, link, newInternalPoints, center)
                  .foreach(newGraph => notifyGraphChanged(newGraph))

                mouseEvent.consume()

              case _ =>
            }
          case _ =>
        }
      }
    }


    handleEvent(MouseEvent.MouseDragged) {
      (mouseEvent: MouseEvent) => {
        mouseEvent.button match {
          case MouseButton.PRIMARY =>
            val mousePoint = mouseEvent.point
            val delta = mousePoint - dragAnchor //The initial drag anchor is set by the link's click filter

            val newCenter = (center + delta).clip(graph.dimension)

            val newInternalPoints = link.internalPoints.map(internalPoint =>
              if (internalPoint == center) newCenter else internalPoint
            )

            if (controller.canDragLinkInternalPoint(graph, link, newInternalPoints, center, newCenter)) {
              dragAnchor = mousePoint

              val newLink = link.visualCopy(internalPoints = newInternalPoints)

              internalPointHandles = internalPointHandles - center + (newCenter -> this)

              center = newCenter

              notifyGraphChanged(
                graph.replaceLink(newLink)
              )
            }

            mouseEvent.consume()
          case _ =>
        }
      }
    }
  }


  private class LinkLabelConnector extends Line {
    visible = false

    def render(linkSettings: VisualLinkSettings, labelCenter: Point2D, linkJoinPoint: Point2D): Unit = {
      stroke = linkSettings.labelConnectorColor
      strokeDashArray = linkSettings.labelConnectorDashArray.asInstanceOf[List[java.lang.Double]]

      startX = labelCenter.x
      startY = labelCenter.y
      linkLabelConnector.endX = linkJoinPoint.x
      linkLabelConnector.endY = linkJoinPoint.y
    }
  }


  private class LinkLabel extends Text {
    textOrigin = VPos.Top

    handleEvent(MouseEvent.MouseDragged) {
      (mouseEvent: MouseEvent) => {
        mouseEvent.button match {
          case MouseButton.PRIMARY =>
            mouseEvent.clickCount match {
              case 1 =>
                val mousePoint = mouseEvent.point
                val delta = mousePoint - dragAnchor //The initial dragAnchor is set by the link's click filter

                val oldCenter = link.labelCenter.getOrElse(getDefaultLabelCenter)
                val newCenter = (oldCenter + delta).clip(graph.dimension)

                controller.dragLinkLabel(graph, link, oldCenter, newCenter)
                  .foreach(newGraph => {
                    dragAnchor = mousePoint
                    notifyGraphChanged(newGraph)
                  })

                mouseEvent.consume()
            }

          case _ =>
        }
      }
    }

    def render(linkSettings: VisualLinkSettings, labelCenter: Point2D): Unit = {
      fill = linkSettings.fontColor
      font = linkSettings.font

      text = link.text

      val textBounds = label.boundsInLocal.value

      x = labelCenter.x - textBounds.width / 2
      y = labelCenter.y - textBounds.height / 2
    }
  }


  private var controller: BasicController[V, L, G] = _
  private var graph: G = _
  private var _link: L = _


  override def link: L = _link

  private def link_=(newLink: L): Unit = {
    _link = newLink
  }


  private var dragAnchor: Point2D = _

  private var segments: List[LinkSegment] = List()
  private var arrow: LinkArrow = _
  private var internalPointHandles: Map[Point2D, InternalPointHandle] = Map()

  private val linkLabelConnector = new LinkLabelConnector
  children.add(linkLabelConnector)

  private var label: LinkLabel = new LinkLabel
  children.add(label)


  override def setup(controller: GraphCanvasController[V, L, G], graph: G, link: L): Unit = {
    this.controller = controller.asInstanceOf[BasicController[V, L, G]]
    this.graph = graph
    this.link = link

    require(this.controller != null)
    require(this.graph != null)
    require(this.link != null)
  }


  opacity <== when(hover) choose 0.75 otherwise 1


  handleEvent(MouseEvent.Any) {
    (mouseEvent: MouseEvent) => {
      mouseEvent.consume()
    }
  }


  filterEvent(MouseEvent.MousePressed) {
    (mouseEvent: MouseEvent) => {
      mouseEvent.button match {
        case MouseButton.PRIMARY =>
          mouseEvent.clickCount match {
            case 1 =>
              dragAnchor = mouseEvent.point

            case _ =>
          }


        case _ =>
      }
    }
  }


  handleEvent(MouseEvent.MouseEntered) {
    (mouseEvent: MouseEvent) => {
      linkLabelConnector.visible = true
    }
  }

  handleEvent(MouseEvent.MouseExited) {
    (mouseEvent: MouseEvent) => {
      linkLabelConnector.visible = false
    }
  }


  handleEvent(MouseEvent.MousePressed) {
    (mouseEvent: MouseEvent) => {
      mouseEvent.button match {
        case MouseButton.PRIMARY =>
          mouseEvent.clickCount match {
            case 1 =>
              if (mouseEvent.controlDown) {
                controller.setLinkSelectedState(graph, link, !link.selected)
                  .foreach(newGraph => notifyGraphChanged(newGraph))
              } else if (!link.selected) {
                controller.setSelection(graph, Set(), Set(link))
                  .foreach(newGraph => notifyGraphChanged(newGraph))
              }


            case 2 =>
              val selectedLinks = graph.selectedLinks

              if (selectedLinks.size == 1 && graph.selectedVertexes.isEmpty) {
                val selectedLink = selectedLinks.head

                controller.editLink(graph, selectedLink)
                  .foreach(newGraph => notifyGraphChanged(newGraph))
              }


            case _ =>
          }

        case _ =>
      }
    }
  }


  private def getDefaultLabelCenter: Point2D = {
    val sourceVertex = graph.getVertex(sourceVertexId).get
    val targetVertex = graph.getVertex(targetVertexId).get

    new DiagonalBounds(sourceVertex.center, targetVertex.center).centerPoint2D
  }


  override def render(): Unit = {
    val settings =
      if (link.selected) {
        link.selectedSettings
      } else {
        link.settings
      }


    val sourceVertex = graph.getVertex(sourceVertexId).get
    val targetVertex = graph.getVertex(targetVertexId).get

    val defaultLabelCenter = getDefaultLabelCenter

    val labelCenter = link.labelCenter
      .getOrElse(defaultLabelCenter)


    val labelConnectorLinkJoinPoint: Point2D = {
      if (link.internalPoints.isEmpty) {
        defaultLabelCenter
      } else {
        link.internalPoints.head
      }
    }


    segments.foreach(children.remove(_))

    val sourcePoints = sourceVertex.center :: link.internalPoints
    val targetPoints = link.internalPoints ++ List(targetVertex.center)


    segments = (sourcePoints zip targetPoints).zipWithIndex.map { case ((sourcePoint, targetPoint), internalPointIndex) =>
      val newSegment = new LinkSegment(internalPointIndex, settings) {
        startPoint = sourcePoint
        endPoint = targetPoint
      }

      children.add(1, newSegment) //Child 0 is the label connector

      newSegment
    }

    if (graph.directed) {
      if (arrow != null) {
        children.remove(arrow)
      }

      arrow = new LinkArrow(segments.last, settings)

      children.add(1 + segments.size, arrow)
    }


    internalPointHandles = internalPointHandles.filter { case (internalPoint, internalPointHandle) =>
      val keepHandle = link.internalPoints.contains(internalPoint)

      if (!keepHandle) {
        children.remove(internalPointHandle)
      }

      keepHandle
    }



    link.internalPoints.foreach(internalPoint => {
      val handle = internalPointHandles.getOrElse(internalPoint, {
        val newHandle = new InternalPointHandle(internalPoint)
        internalPointHandles += (internalPoint -> newHandle)
        children.add(newHandle)
        newHandle
      })

      handle.render(settings)
    })


    label.render(settings, labelCenter)

    linkLabelConnector.render(settings, labelCenter, labelConnectorLinkJoinPoint)
  }
}
