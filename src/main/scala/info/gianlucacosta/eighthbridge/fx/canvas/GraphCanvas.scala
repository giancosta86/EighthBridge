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

package info.gianlucacosta.eighthbridge.fx.canvas

import java.util.UUID
import javafx.beans.Observable
import javafx.beans.property.{SimpleBooleanProperty, SimpleDoubleProperty, SimpleObjectProperty}

import info.gianlucacosta.eighthbridge.graphs.point2point.ArcBinding
import info.gianlucacosta.eighthbridge.graphs.point2point.visual.{VisualGraph, VisualLink, VisualVertex}

import scalafx.Includes._
import scalafx.geometry.{Dimension2D, Point2D}
import scalafx.scene.input.{KeyCode, KeyEvent, MouseEvent, ScrollEvent}
import scalafx.scene.layout.Pane
import scalafx.scene.shape.Rectangle

/**
  * JavaFX component rendering a graph.
  *
  * By plugging a controller, the canvas can flexibly:
  * <ul>
  * <li>Render graph components using different JavaFX nodes</li>
  * <li>Support a given set of operations - for example: graph creation via user interaction</li>
  * </ul>
  *
  * @param controller   The controller telling the canvas how to render components and how to react to user input
  * @param initialGraph The initial graph shown by the canvas
  */
class GraphCanvas[
V <: VisualVertex[V],
L <: VisualLink[L],
G <: VisualGraph[V, L, G]
](val controller: GraphCanvasController[V, L, G], initialGraph: G) extends Pane {
  require(controller != null)
  require(initialGraph != null)

  styleClass.add("graphCanvas")

  /**
    * This property is set whenever a new graph is set - programmatically or via
    * user interaction.
    */
  val graphProperty =
    new SimpleObjectProperty[G](initialGraph)

  graphProperty.addListener((observable: Observable) => {
    //The canvas can only be updated by changing the graph
    render()
  })


  def graph: G =
    graphProperty()


  def graph_=(newValue: G): Unit =
    graphProperty() =
      newValue


  val zoomEnabledProperty =
    new SimpleBooleanProperty(true)


  def zoomEnabled: Boolean =
    zoomEnabledProperty.get


  def zoomEnabled_=(newValue: Boolean): Unit =
    zoomEnabledProperty.set(newValue)


  val minZoomScaleProperty =
    new SimpleDoubleProperty(0.2)


  def minZoomScale: Double =
    minZoomScaleProperty.get


  def minZoomScale_=(newValue: Double) =
    minZoomScaleProperty.set(newValue)


  val maxZoomScaleProperty =
    new SimpleDoubleProperty(Double.PositiveInfinity)


  def maxZoomScale: Double =
    maxZoomScaleProperty.get


  def maxZoomScale_=(newValue: Double) =
    maxZoomScaleProperty.set(newValue)


  val panEnabledProperty =
    new SimpleBooleanProperty(true)


  def panEnabled: Boolean =
    panEnabledProperty.get


  def panEnabled_=(newValue: Boolean): Unit =
    panEnabledProperty.set(newValue)


  clip = new Rectangle {
    width <==
      GraphCanvas.this.width

    height <==
      GraphCanvas.this.height
  }


  val backgroundNode: BackgroundNode[V, L, G] =
    controller.createBackgroundNode(this)

  children.add(backgroundNode)


  private var _vertexNodes: Map[UUID, VertexNode[V, L, G]] =
    Map()


  private var _linkNodes: Map[UUID, LinkNode[V, L, G]] =
    Map()


  def vertexNodes: Map[UUID, VertexNode[V, L, G]] =
    _vertexNodes


  def linkNodes: Map[UUID, LinkNode[V, L, G]] =
    _linkNodes


  focusTraversable =
    true


  private var dragAnchor: Point2D = _


  private var panning: Boolean =
    false


  private var latestRenderedVertexPointers =
    Set[Int]()

  private var latestRenderedLinkPointers =
    Set[Int]()

  private var latestRenderedBindingPointers =
    Set[Int]()


  private var _dimension: Dimension2D =
    new Dimension2D(
      width(),
      height()
    )


  def dimension: Dimension2D =
    _dimension


  width.addListener((observable: javafx.beans.Observable) => {
    _dimension =
      new Dimension2D(
        width(),
        height()
      )
  })


  height.addListener((observable: javafx.beans.Observable) => {
    _dimension =
      new Dimension2D(
        width(),
        height()
      )
  })


  render()


  private def render(): Unit = {
    purgeDanglingVertexNodes()
    purgeDanglingLinkNodes()

    updateVertexNodes()
    updateLinkNodes()


    vertexNodes
      .values
      .foreach(_.toFront())


    val currentVertexPointers: Set[Int] =
      graph.vertexes.map(System.identityHashCode)


    val currentLinkPointers: Set[Int] =
      graph.links.map(System.identityHashCode)


    val currentBindingPointers: Set[Int] =
      graph.bindings.map(System.identityHashCode)


    val currentLinkToVertexPointersOption: Option[Map[UUID, Set[Int]]] =
      getLinkToVertexPointers(currentBindingPointers)


    renderVertexes()

    resizeCanvas()

    renderLinks(currentLinkToVertexPointersOption)

    backgroundNode.render()


    latestRenderedVertexPointers =
      currentVertexPointers

    latestRenderedLinkPointers =
      currentLinkPointers

    latestRenderedBindingPointers =
      currentBindingPointers
  }


  private def purgeDanglingVertexNodes(): Unit = {
    val (newVertexNodes, vertexNodesToRemove) =
      _vertexNodes.partition {
        case (vertexId, vertexNode) =>
          graph.containsVertex(vertexId)
      }

    _vertexNodes =
      newVertexNodes

    vertexNodesToRemove
      .values
      .foreach(children.remove)
  }


  private def purgeDanglingLinkNodes(): Unit = {
    val (newLinkNodes, linkNodesToRemove) =
      _linkNodes.partition {
        case (linkId, linkNode) =>
          graph.containsLink(linkId)
      }

    _linkNodes =
      newLinkNodes

    linkNodesToRemove.values.foreach(children.remove)
  }


  private def updateVertexNodes(): Unit = {
    graph.vertexes.foreach(vertex => {
      val vertexNode =
        _vertexNodes.getOrElse(
          vertex.id,
          createVertexNode(vertex)
        )

      vertexNode.vertex =
        vertex
    })
  }


  private def createVertexNode(vertex: V): VertexNode[V, L, G] = {
    val newVertexNode =
      controller.createVertexNode(this, vertex)

    newVertexNode.width.addListener((observable: javafx.beans.Observable) => {
      resizeCanvas()
    })


    newVertexNode.height.addListener((observable: javafx.beans.Observable) => {
      resizeCanvas()
    })


    children.add(
      newVertexNode
    )

    _vertexNodes +=
      (vertex.id -> newVertexNode)

    newVertexNode
  }


  private def updateLinkNodes(): Unit = {
    graph.bindings.foreach(binding => {
      val link =
        graph.getLink(binding.linkId).get

      val linkNode =
        _linkNodes.getOrElse(
          binding.linkId,
          createLinkNode(link, binding)
        )

      linkNode.link =
        link
    })
  }


  private def createLinkNode(link: L, binding: ArcBinding): LinkNode[V, L, G] = {
    val sourceVertex =
      graph.getVertex(binding.sourceVertexId).get

    val targetVertex =
      graph.getVertex(binding.targetVertexId).get

    val newLinkNode =
      controller.createLinkNode(this, sourceVertex, targetVertex, link)

    children.add(
      newLinkNode
    )

    _linkNodes +=
      (binding.linkId -> newLinkNode)

    newLinkNode
  }


  private def getLinkToVertexPointers(currentBindingPointers: Set[Int]): Option[Map[UUID, Set[Int]]] = {
    if (currentBindingPointers == latestRenderedBindingPointers)
      Some(
        graph.bindings.map(binding => {
          val sourceVertex =
            graph.getVertex(binding.sourceVertexId).get

          val targetVertex =
            graph.getVertex(binding.targetVertexId).get

          binding.linkId -> Set(
            sourceVertex,
            targetVertex
          ).map(System.identityHashCode)
        })
          .toMap
      )
    else
      None
  }


  private def renderVertexes(): Unit = {
    _vertexNodes.values.foreach(vertexNode => {
      val vertex =
        vertexNode.vertex

      val vertexPointer =
        System.identityHashCode(vertex)

      val mustRenderVertex =
        !latestRenderedVertexPointers.contains(vertexPointer)

      if (mustRenderVertex) {
        vertexNode.render()
      }
    })
  }


  private def resizeCanvas(): Unit = {
    val newDimension =
      controller.getCanvasDimension(this)

    this.resize(
      newDimension.width,
      newDimension.height
    )
  }


  def renderLinks(currentLinkToVertexPointersOption: Option[Map[UUID, Set[Int]]]): Unit = {
    _linkNodes.values.foreach(linkNode => {
      val link =
        linkNode.link

      val linkPointer =
        System.identityHashCode(link)

      val mustRenderLink =
        !latestRenderedLinkPointers.contains(linkPointer) ||
          currentLinkToVertexPointersOption.forall(linkVertexPointers => {
            val vertexPointers: Set[Int] =
              linkVertexPointers(link.id)

            !vertexPointers.subsetOf(latestRenderedVertexPointers)
          })


      if (mustRenderLink) {
        linkNode.render()
      }
    })
  }


  handleEvent(KeyEvent.KeyPressed) {
    (keyEvent: KeyEvent) => {
      keyEvent.code match {
        case KeyCode.Delete =>
          controller.deleteSelection(this, graph)
            .foreach(newGraph =>
              graph = newGraph
            )

        case _ =>
      }
    }
  }


  handleEvent(ScrollEvent.Scroll) {
    (event: ScrollEvent) => {
      if (zoomEnabled) {
        event.consume()

        val oldScale =
          scaleX()

        val scaleFactor =
          math.pow(1.01, event.deltaY / 5)

        val newScale = math.max(
          minZoomScale,

          math.min(
            maxZoomScale,

            oldScale * scaleFactor
          )
        )

        scaleX() =
          newScale

        scaleY() =
          newScale

        val zoomFactor =
          (newScale / oldScale) - 1

        val canvasBounds =
          this.getBoundsInParent


        val deltaX =
          event.sceneX - (canvasBounds.width / 2 + canvasBounds.minX)

        val deltaY =
          event.sceneY - (canvasBounds.height / 2 + canvasBounds.minY)

        translateX() -=
          zoomFactor * deltaX

        translateY() -=
          zoomFactor * deltaY

        ()
      }
    }
  }


  filterEvent(MouseEvent.MousePressed) {
    (event: MouseEvent) => {
      if (event.isShiftDown && panEnabled) {
        event.consume()

        dragAnchor =
          new Point2D(
            event.sceneX,
            event.sceneY
          )

        panning =
          true
      }
    }
  }


  filterEvent(MouseEvent.MouseDragged) {
    (event: MouseEvent) => {
      if (panning) {
        event.consume()

        val delta =
          new Point2D(
            event.sceneX - dragAnchor.x,
            event.sceneY - dragAnchor.y
          )

        translateX() +=
          delta.x

        translateY() +=
          delta.y

        dragAnchor =
          new Point2D(event.sceneX, event.sceneY)
      }
    }
  }


  filterEvent(MouseEvent.MouseReleased) {
    (event: MouseEvent) => {
      if (panning) {
        event.consume()

        dragAnchor =
          null

        panning =
          false
      }
    }
  }
}
