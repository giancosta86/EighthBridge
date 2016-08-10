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

import scala.collection.JavaConversions._
import scalafx.Includes._
import scalafx.geometry.{Dimension2D, Point2D, VPos}
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
](val graphCanvas: GraphCanvas[V, L, G], val sourceVertexId: UUID, val targetVertexId: UUID)
  extends Group
    with LinkNode[V, L, G]
    with BasicGraphCanvasNode[V, L, G] {

  protected class LinkSegment(indexOfNewInternalPoint: Int) extends Segment {
    styleClass.add("line")

    strokeLineCap =
      StrokeLineCap.Round

    handleEvent(MouseEvent.MousePressed) {
      (mouseEvent: MouseEvent) => {
        mouseEvent.button match {
          case MouseButton.Secondary =>

            val internalPoint =
              mouseEvent.point

            val newInternalPoints = {
              val (precedingInternalPoints, followingInternalPoints) =
                link.internalPoints.splitAt(indexOfNewInternalPoint)

              precedingInternalPoints ++ (internalPoint :: followingInternalPoints)
            }

            controller.createLinkInternalPoint(graph, link, newInternalPoints, internalPoint)
              .foreach(newGraph =>
                graph = newGraph
              )

            mouseEvent.consume()


          case _ =>
        }
      }
    }
  }

  protected class LinkArrow(segment: LinkSegment) extends Group {
    styleClass.add("arrow")

    protected val leftSegment = new Line() {
      styleClass.add("line")
    }

    protected val rightSegment = new Line() {
      styleClass.add("line")
    }

    children.addAll(
      leftSegment,
      rightSegment
    )

    val angle =
      link.arrow.angle

    val length =
      link.arrow.length


    val startPoint =
      segment.startPoint

    val stopPoint =
      targetAnchorPoint


    val gamma =
      Math.atan2(
        stopPoint.y - startPoint.y,
        stopPoint.x - startPoint.x
      )

    val dPx =
      length * Math.cos(gamma - angle)

    val dPy =
      length * Math.sin(gamma - angle)


    val epsilon =
      Math.PI - gamma - angle

    val dQx =
      length * Math.cos(epsilon)

    val dQy =
      length * Math.sin(epsilon)


    val (leftEndX, leftEndY, rightEndX, rightEndY) =
      if (stopPoint.y <= startPoint.y) {
        (
          stopPoint.x - dPx,
          stopPoint.y - dPy,

          stopPoint.x + dQx,
          stopPoint.y - dQy
          )
      } else {
        (
          stopPoint.x + dQx,
          stopPoint.y - dQy,

          stopPoint.x - dPx,
          stopPoint.y - dPy
          )
      }


    leftSegment.startX =
      stopPoint.x

    leftSegment.startY =
      stopPoint.y

    leftSegment.endX =
      leftEndX

    leftSegment.endY =
      leftEndY


    rightSegment.startX =
      stopPoint.x

    rightSegment.startY =
      stopPoint.y

    rightSegment.endX =
      rightEndX

    rightSegment.endY =
      rightEndY
  }


  protected class InternalPointHandle(initialCenter: Point2D) extends Ellipse {
    styleClass.add("internalPointHandle")

    opacity <==
      when(hover) choose 1 otherwise 0

    def center: Point2D = new Point2D(
      centerX.value,
      centerY.value
    )

    def center_=(newPoint: Point2D) = {
      centerX =
        newPoint.x

      centerY =
        newPoint.y
    }


    def render(): Unit = {
      radiusX =
        link.handleRadius.x

      radiusY =
        link.handleRadius.y
    }

    center =
      initialCenter


    handleEvent(MouseEvent.MousePressed) {
      (mouseEvent: MouseEvent) => {
        mouseEvent.button match {
          case MouseButton.Secondary =>
            mouseEvent.clickCount match {
              case 1 =>
                val newInternalPoints =
                  link.internalPoints.filter(internalPoint => internalPoint != center)

                controller.deleteLinkInternalPoint(graph, link, newInternalPoints, center)
                  .foreach(newGraph =>
                    graph =
                      newGraph
                  )

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
            val mousePoint =
              mouseEvent.point

            val delta =
              mousePoint - dragAnchor //The initial drag anchor is set by the link's click filter

            val newCenter =
              (center + delta).clip(graphCanvas.dimension)

            val newInternalPoints =
              link.internalPoints.map(internalPoint =>
                if (internalPoint == center) newCenter else internalPoint
              )

            if (controller.canDragLinkInternalPoint(graph, link, newInternalPoints, center, newCenter)) {
              dragAnchor =
                mousePoint

              val newLink =
                link.visualCopy(internalPoints = newInternalPoints)

              internalPointHandles =
                internalPointHandles - center + (newCenter -> this)

              center = newCenter

              graph =
                graph.replaceLink(newLink)
            }

            mouseEvent.consume()
          case _ =>
        }
      }
    }
  }


  protected class LinkLabelConnector extends Line {
    styleClass.add("labelConnector")

    visible =
      false

    def render(labelCenter: Point2D, linkJoinPoint: Point2D): Unit = {
      startX =
        labelCenter.x

      startY =
        labelCenter.y

      linkLabelConnector.endX =
        linkJoinPoint.x

      linkLabelConnector.endY =
        linkJoinPoint.y
    }
  }


  protected class LinkLabel extends Text {
    styleClass.add("label")

    textOrigin =
      VPos.Top

    handleEvent(MouseEvent.MouseDragged) {
      (mouseEvent: MouseEvent) => {
        mouseEvent.button match {
          case MouseButton.Primary =>
            mouseEvent.clickCount match {
              case 1 =>
                val mousePoint =
                  mouseEvent.point

                val delta =
                  mousePoint - dragAnchor //The initial dragAnchor is set by the link's click filter

                val oldCenter =
                  link.labelCenter.getOrElse(getDefaultLabelCenter)

                val newCenter =
                  (oldCenter + delta).clip(graphCanvas.dimension)

                controller.dragLinkLabel(graph, link, oldCenter, newCenter)
                  .foreach(newGraph => {
                    dragAnchor =
                      mousePoint

                    graph =
                      newGraph
                  })

                mouseEvent.consume()
            }

          case _ =>
        }
      }
    }

    def render(labelCenter: Point2D): Unit = {
      text =
        link.text

      val textBounds =
        label.boundsInLocal.value

      x =
        labelCenter.x - textBounds.width / 2

      y =
        labelCenter.y - textBounds.height / 2
    }
  }

  private var dragAnchor: Point2D = _

  protected var segments: List[LinkSegment] =
    List()

  protected var arrow: LinkArrow = _

  protected var internalPointHandles: Map[Point2D, InternalPointHandle] =
    Map()


  protected val linkLabelConnector =
    new LinkLabelConnector

  children.add(linkLabelConnector)


  protected var label: LinkLabel =
    new LinkLabel

  children.add(label)


  opacity <==
    when(hover) choose 0.75 otherwise 1


  val sourceVertexNode =
    graphCanvas.vertexNodes(sourceVertexId)


  val targetVertexNode =
    graphCanvas.vertexNodes(targetVertexId)


  targetVertexNode.width.addListener((observable: javafx.beans.Observable) => {
    render()
  })


  targetVertexNode.height.addListener((observable: javafx.beans.Observable) => {
    render()
  })


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
      linkLabelConnector.visible =
        link.text.nonEmpty
    }
  }

  handleEvent(MouseEvent.MouseExited) {
    (mouseEvent: MouseEvent) => {
      linkLabelConnector.visible =
        false
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
                  .foreach(newGraph =>
                    graph =
                      newGraph
                  )
              } else if (!link.selected) {
                controller.setSelection(graph, Set(), Set(link))
                  .foreach(newGraph =>
                    graph =
                      newGraph
                  )
              }


            case 2 =>
              val selectedLinks =
                graph.selectedLinks

              if (selectedLinks.size == 1 && graph.selectedVertexes.isEmpty) {
                val selectedLink =
                  selectedLinks.head

                controller.editLink(graph, selectedLink)
                  .foreach(newGraph =>
                    graph =
                      newGraph
                  )
              }


            case _ =>
          }

        case _ =>
      }
    }
  }

  private var sourceAnchorPoint: Point2D = _
  private var targetAnchorPoint: Point2D = _

  private def getDefaultLabelCenter: Point2D = {
    val sourceVertex =
      graph.getVertex(sourceVertexId).get

    new DiagonalBounds(
      sourceAnchorPoint,
      targetAnchorPoint
    ).centerPoint2D
  }


  override def render(): Unit = {
    super.render()

    val sourceVertex =
      graph.getVertex(sourceVertexId).get

    val targetVertex =
      graph.getVertex(targetVertexId).get


    sourceAnchorPoint =
      link.internalPoints.lastOption.getOrElse(
        {
          val centersDelta =
            sourceVertex.center - targetVertex.center

          val sourceDimension =
            new Dimension2D(
              sourceVertexNode.width(),
              sourceVertexNode.height()
            )


          val intersectionWithSourceVerticalSide =
            if (centersDelta.x != 0)
              math.atan(math.abs(centersDelta.y / centersDelta.x)) <= math.atan(math.abs(sourceDimension.height / sourceDimension.width))
            else
              false

          if (intersectionWithSourceVerticalSide)
            new Point2D(
              sourceVertex.center.x + math.signum(centersDelta.x) * sourceDimension.width / 2,

              sourceVertex.center.y + sourceDimension.width / 2 * centersDelta.y / math.abs(centersDelta.x)
            )
          else
            new Point2D(
              sourceVertex.center.x + sourceDimension.height / 2 * centersDelta.x / math.abs(centersDelta.y),

              sourceVertex.center.y - math.signum(centersDelta.y) * sourceDimension.height / 2
            )
        })

    targetAnchorPoint = {
      val pointsDelta =
        sourceAnchorPoint - targetVertex.center

      val targetDimension =
        new Dimension2D(
          targetVertexNode.width(),
          targetVertexNode.height()
        )


      val intersectionWithTargetVerticalSide =
        if (pointsDelta.x != 0)
          math.atan(math.abs(pointsDelta.y / pointsDelta.x)) <= math.atan(math.abs(targetDimension.height / targetDimension.width))
        else
          false

      if (intersectionWithTargetVerticalSide)
        new Point2D(
          targetVertex.center.x + math.signum(pointsDelta.x) * targetDimension.width / 2,

          targetVertex.center.y + targetDimension.width / 2 * pointsDelta.y / math.abs(pointsDelta.x)
        )
      else
        new Point2D(
          targetVertex.center.x + targetDimension.height / 2 * pointsDelta.x / math.abs(pointsDelta.y),

          targetVertex.center.y + math.signum(pointsDelta.y) * targetDimension.height / 2
        )
    }


    val defaultLabelCenter =
      getDefaultLabelCenter


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




    val sourcePoints =
      sourceVertex.center :: link.internalPoints

    val targetPoints =
      link.internalPoints ++ List(targetAnchorPoint)


    segments = (sourcePoints zip targetPoints).zipWithIndex.map { case ((sourcePoint, targetPoint), internalPointIndex) =>
      val newSegment =
        new LinkSegment(internalPointIndex) {
          startPoint = sourcePoint
          endPoint = targetPoint
        }

      children.add(1, newSegment) //Child 0 is the label connector

      newSegment
    }


    if (controller.renderDirected) {
      if (arrow != null) {
        children.remove(arrow)
      }

      arrow =
        new LinkArrow(segments.last)

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
      val handle =
        internalPointHandles.getOrElse(internalPoint, {
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
