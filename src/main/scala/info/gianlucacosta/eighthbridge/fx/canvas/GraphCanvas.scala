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

import info.gianlucacosta.eighthbridge.graphs.point2point.visual.{VisualGraph, VisualLink, VisualVertex}

import scalafx.Includes._
import scalafx.geometry.Point2D
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
](controller: GraphCanvasController[V, L, G], initialGraph: G) extends Pane {
  require(controller != null)
  require(initialGraph != null)

  styleClass.add("graphCanvas")

  /**
    * This property is set whenever a new graph is set- programmatically or via
    * user interaction.
    */
  val graphProperty = new SimpleObjectProperty[G](initialGraph)

  graphProperty.addListener((observable: Observable) => {
    render()
  })

  def graph: G =
    graphProperty()


  def graph_=(newValue: G): Unit = {
    graphProperty() = newValue
  }


  val zoomEnabledProperty = new SimpleBooleanProperty(true)


  def zoomEnabled: Boolean =
    zoomEnabledProperty.get


  def zoomEnabled_=(newValue: Boolean): Unit =
    zoomEnabledProperty.set(newValue)


  val minZoomScaleProperty = new SimpleDoubleProperty(0.2)


  def minZoomScale: Double =
    minZoomScaleProperty.get


  def minZoomScale_=(newValue: Double) =
    minZoomScaleProperty.set(newValue)


  val maxZoomScaleProperty = new SimpleDoubleProperty(Double.PositiveInfinity)


  def maxZoomScale: Double =
    maxZoomScaleProperty.get


  def maxZoomScale_=(newValue: Double) =
    maxZoomScaleProperty.set(newValue)


  val panEnabledProperty = new SimpleBooleanProperty(true)


  def panEnabled: Boolean =
    panEnabledProperty.get


  def panEnabled_=(newValue: Boolean): Unit =
    panEnabledProperty.set(newValue)


  private val backgroundNode: BackgroundNode[V, L, G] = controller.createBackgroundNode()
  require(backgroundNode != null)
  backgroundNode.setGraphChangedListener(newGraph => graph = newGraph)
  children.add(backgroundNode)

  private var vertexNodes: Map[UUID, VertexNode[V, L, G]] = Map()
  private var linkNodes: Map[UUID, LinkNode[V, L, G]] = Map()

  focusTraversable = true

  private var dragAnchor: Point2D = _
  private var panning: Boolean = false


  private var latestRenderedVertexPointers =
    Set[Int]()

  private var latestRenderedLinkPointers =
    Set[Int]()

  private var latestRenderedBindingPointers =
    Set[Int]()


  render()


  private def render(): Unit = {
    val (newLinkNodes, linkNodesToRemove) = linkNodes.partition {
      case (linkId, linkNode) => graph.containsLink(linkId)
    }
    linkNodes = newLinkNodes
    linkNodesToRemove.values.foreach(children.remove)


    val (newVertexNodes, vertexNodesToRemove) = vertexNodes.partition {
      case (vertexId, vertexNode) => graph.containsVertex(vertexId)
    }
    vertexNodes = newVertexNodes
    vertexNodesToRemove.values.foreach(children.remove)


    this.resize(graph.dimension.width, graph.dimension.height)


    clip = new Rectangle {
      width = graph.dimension.width
      height = graph.dimension.height
    }


    graph.bindings.foreach(binding => {
      val link = graph.getLink(binding.linkId).get

      val linkNode = linkNodes.getOrElse(
        binding.linkId, {
          val sourceVertex = graph.getVertex(binding.sourceVertexId).get
          val targetVertex = graph.getVertex(binding.targetVertexId).get

          val newLinkNode = controller.createLinkNode(sourceVertex, targetVertex, link)
          newLinkNode.setGraphChangedListener(newGraph => graph = newGraph)

          children.add(1 + linkNodes.size, newLinkNode)

          linkNodes += (binding.linkId -> newLinkNode)

          newLinkNode
        })

      linkNode.setup(controller, graph, link)
    })


    graph.vertexes.foreach(vertex => {
      val vertexNode = vertexNodes.getOrElse(
        vertex.id, {
          val newVertexNode = controller.createVertexNode(vertex)
          newVertexNode.setGraphChangedListener(newGraph => graph = newGraph)

          children.add(1 + linkNodes.size + vertexNodes.size, newVertexNode)

          vertexNodes += (vertex.id -> newVertexNode)

          newVertexNode
        })

      vertexNode.setup(controller, graph, vertex)
    })


    //This must be the last in order to have full information on vertex nodes and link nodes
    backgroundNode.setup(controller, graph, vertexNodes, linkNodes)

    backgroundNode.render()

    val currentVertexPointers: Set[Int] =
      graph.vertexes.map(System.identityHashCode)


    val currentLinkPointers: Set[Int] =
      graph.links.map(System.identityHashCode)


    val currentBindingPointers: Set[Int] =
      graph.bindings.map(System.identityHashCode)


    val currentLinkVertexPointersOption: Option[Map[UUID, Set[Int]]] =
      if (currentBindingPointers == latestRenderedBindingPointers)
        Some(
          graph.bindings.map(binding => {
            val sourceVertex = graph.getVertex(binding.sourceVertexId).get
            val targetVertex = graph.getVertex(binding.targetVertexId).get

            binding.linkId -> Set(
              sourceVertex,
              targetVertex
            ).map(System.identityHashCode)
          })
            .toMap
        )
      else
        None



    linkNodes.values.foreach(linkNode => {
      val link = linkNode.link

      val linkPointer =
        System.identityHashCode(link)

      val mustRenderLink =
        !latestRenderedLinkPointers.contains(linkPointer) ||
          currentLinkVertexPointersOption.forall(linkVertexPointers => {
            val vertexPointers: Set[Int] =
              linkVertexPointers(link.id)

            !vertexPointers.subsetOf(latestRenderedVertexPointers)
          })


      if (mustRenderLink) {
        linkNode.render()
      }
    })

    vertexNodes.values.foreach(vertexNode => {
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

    latestRenderedVertexPointers =
      currentVertexPointers

    latestRenderedLinkPointers =
      currentLinkPointers

    latestRenderedBindingPointers =
      currentBindingPointers
  }


  handleEvent(KeyEvent.KeyPressed) {
    (keyEvent: KeyEvent) => {
      keyEvent.code match {
        case KeyCode.Delete =>
          controller.deleteSelection(graph)
            .foreach(newGraph => graph = newGraph)

        case _ =>
      }
    }
  }


  handleEvent(ScrollEvent.Scroll) {
    (event: ScrollEvent) => {
      if (zoomEnabled) {
        event.consume()

        val oldScale = scaleX()

        val scaleFactor = math.pow(1.01, event.deltaY / 5)

        val newScale = math.max(
          minZoomScale,

          math.min(
            maxZoomScale,

            oldScale * scaleFactor
          )
        )

        scaleX() = newScale
        scaleY() = newScale

        val zoomFactor = (newScale / oldScale) - 1

        val canvasBounds = this.getBoundsInParent

        val deltaX = event.sceneX - (canvasBounds.width / 2 + canvasBounds.minX)
        val deltaY = event.sceneY - (canvasBounds.height / 2 + canvasBounds.minY)

        translateX() -= zoomFactor * deltaX
        translateY() -= zoomFactor * deltaY

        ()
      }
    }
  }


  filterEvent(MouseEvent.MousePressed) {
    (event: MouseEvent) => {
      if (event.isShiftDown && panEnabled) {
        event.consume()

        dragAnchor = new Point2D(
          event.sceneX,
          event.sceneY
        )

        panning = true
      }
    }
  }


  filterEvent(MouseEvent.MouseDragged) {
    (event: MouseEvent) => {
      if (panning) {
        event.consume()

        val delta = new Point2D(
          event.sceneX - dragAnchor.x,
          event.sceneY - dragAnchor.y
        )

        translateX() += delta.x
        translateY() += delta.y

        dragAnchor = new Point2D(event.sceneX, event.sceneY)
      }
    }
  }


  filterEvent(MouseEvent.MouseReleased) {
    (event: MouseEvent) => {
      if (panning) {
        event.consume()

        dragAnchor = null
        panning = false
      }
    }
  }
}
