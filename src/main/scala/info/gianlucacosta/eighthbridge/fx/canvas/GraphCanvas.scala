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
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.{InvalidationListener, Observable}

import info.gianlucacosta.eighthbridge.graphs.point2point.visual.{VisualGraph, VisualVertex, VisualLink}

import scalafx.Includes._
import scalafx.scene.input.{KeyCode, KeyEvent}
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
class GraphCanvas[V <: VisualVertex[V], L <: VisualLink[L], G <: VisualGraph[V, L, G]](controller: GraphCanvasController[V, L, G], initialGraph: G) extends Pane {
  require(controller != null)
  require(initialGraph != null)

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


  private val backgroundNode: BackgroundNode[V, L, G] = controller.createBackgroundNode()
  require(backgroundNode != null)
  backgroundNode.addGraphChangedListener(newGraph => graph = newGraph)
  children.add(backgroundNode)

  private var vertexNodes: Map[UUID, VertexNode[V, L, G]] = Map()
  private var linkNodes: Map[UUID, LinkNode[V, L, G]] = Map()

  focusTraversable = true

  render()


  private def render(): Unit = {
    require(graph != null)

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
          newLinkNode.addGraphChangedListener(newGraph => graph = newGraph)

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
          newVertexNode.addGraphChangedListener(newGraph => graph = newGraph)

          children.add(1 + linkNodes.size + vertexNodes.size, newVertexNode)

          vertexNodes += (vertex.id -> newVertexNode)

          newVertexNode
        })

      vertexNode.setup(controller, graph, vertex)
    })


    //This must be the last in order to have full information on vertex nodes and link nodes
    backgroundNode.setup(controller, graph, vertexNodes, linkNodes)


    backgroundNode.render()
    linkNodes.values.foreach(_.render())
    vertexNodes.values.foreach(_.render())
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
}
