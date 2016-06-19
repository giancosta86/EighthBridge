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
import info.gianlucacosta.helios.fx.geometry.{DiagonalBounds, Segment}
import info.gianlucacosta.helios.fx.styles.PseudoClasses

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
class BasicLinkNode[
V <: BasicVertex[V],
L <: BasicLink[L],
G <: VisualGraph[V, L, G]
](val sourceVertexId: UUID, val targetVertexId: UUID) extends Group with LinkNode[V, L, G] {

  protected class LinkSegment(indexOfNewInternalPoint: Int) extends Segment {
    styleClass.add("line")

    strokeLineCap = StrokeLineCap.Round

    handleEvent(MouseEvent.MousePressed) {
      (mouseEvent: MouseEvent) => {
        mouseEvent.button match {
          case MouseButton.Secondary =>

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

  protected class LinkArrow(segment: LinkSegment) extends Group {
    styleClass.add("line")

    protected val leftSegment = new Line()
    protected val rightSegment = new Line()

    children.addAll(leftSegment, rightSegment)

    val relativePosition = link.arrow.relativePosition
    val angle = link.arrow.angle
    val length = link.arrow.length

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
  }


  protected class InternalPointHandle(initialCenter: Point2D) extends Ellipse {
    styleClass.add("internalPointHandle")

    opacity <== when(hover) choose 1 otherwise 0

    def center: Point2D = new Point2D(
      centerX.value,
      centerY.value
    )

    def center_=(newPoint: Point2D) = {
      centerX = newPoint.x
      centerY = newPoint.y
    }


    def render(): Unit = {
      radiusX = link.handleRadius.x
      radiusY = link.handleRadius.y
    }

    center = initialCenter


    handleEvent(MouseEvent.MousePressed) {
      (mouseEvent: MouseEvent) => {
        mouseEvent.button match {
          case MouseButton.Secondary =>
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
          case MouseButton.Primary =>
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


  protected class LinkLabelConnector extends Line {
    styleClass.add("labelConnector")

    visible = false

    def render(labelCenter: Point2D, linkJoinPoint: Point2D): Unit = {
      startX = labelCenter.x
      startY = labelCenter.y
      linkLabelConnector.endX = linkJoinPoint.x
      linkLabelConnector.endY = linkJoinPoint.y
    }
  }


  protected class LinkLabel extends Text {
    styleClass.add("label")

    textOrigin = VPos.Top

    handleEvent(MouseEvent.MouseDragged) {
      (mouseEvent: MouseEvent) => {
        mouseEvent.button match {
          case MouseButton.Primary =>
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

    def render(labelCenter: Point2D): Unit = {
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

  protected var segments: List[LinkSegment] = List()
  protected var arrow: LinkArrow = _
  protected var internalPointHandles: Map[Point2D, InternalPointHandle] = Map()

  protected val linkLabelConnector = new LinkLabelConnector
  children.add(linkLabelConnector)

  protected var label: LinkLabel = new LinkLabel
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
        case MouseButton.Primary =>
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
        case MouseButton.Primary =>
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
    styleClass.setAll("link")

    if (link.styleClass.nonEmpty) {
      styleClass.add(link.styleClass)
    }

    this.pseudoClassStateChanged(PseudoClasses.Selected, link.selected)


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
      val newSegment = new LinkSegment(internalPointIndex) {
        startPoint = sourcePoint
        endPoint = targetPoint
      }

      children.add(1, newSegment) //Child 0 is the label connector

      newSegment
    }

    if (graph.renderDirected) {
      if (arrow != null) {
        children.remove(arrow)
      }

      arrow = new LinkArrow(segments.last)

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

      handle.render()
    })


    label.render(labelCenter)

    linkLabelConnector.render(labelCenter, labelConnectorLinkJoinPoint)
  }
}
