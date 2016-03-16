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

package info.gianlucacosta.eighthbridge.fx.canvas.basic.editing

import info.gianlucacosta.eighthbridge.graphs.point2point.specific.Named
import info.gianlucacosta.eighthbridge.graphs.point2point.visual.{VisualGraph, VisualLink, VisualVertex}
import info.gianlucacosta.eighthbridge.util.fx.dialogs.InputDialogs

import scalafx.geometry.Point2D

/**
  * Mixin controller that:
  * <ul>
  * <li>Creates a vertex by assigning it a unique name based on a counter</li>
  * <li>Allows the user to edit such name, ensuring the new name is still unique</li>
  * </ul>
  *
  * @tparam V Vertex
  * @tparam L Link
  */
trait VertexNamingController[V <: VisualVertex with Named, L <: VisualLink] extends InteractiveEditingController[V, L] {
  /**
    * The first index used when creating vertexes
    */
  protected val firstIndex = 1

  /**
    * Given a vertex index, returns the vertex name
    *
    * @param vertexIndex
    * @return
    */
  protected def getVertexName(vertexIndex: Int): String = s"V${vertexIndex}"

  /**
    * Actually instantiate the vertex
    *
    * @param center
    * @param vertexName
    * @return
    */
  protected def instantiateVertex(center: Point2D, vertexName: String): V


  override def createVertex(graph: VisualGraph, center: Point2D): Option[VisualGraph] = {
    val lastUsedVertexIndex = Stream.from(firstIndex)
      .takeWhile(vertexIndex => {
        val vertexName = getVertexName(vertexIndex)

        val vertexNameExists = graph.vertexes.exists(_.asInstanceOf[Named].name == vertexName)

        vertexNameExists
      })
      .lastOption
      .getOrElse(firstIndex - 1)

    val vertexIndex = lastUsedVertexIndex + 1

    val vertexName = getVertexName(vertexIndex)

    val newVertex = instantiateVertex(center, vertexName)

    Some(
      graph.addVertex(newVertex)
    )
  }


  override protected def interactiveVertexEditing(graph: VisualGraph, vertex: V): Option[VisualVertex] = {
    val newNameInput = InputDialogs.askForString("Vertex name:", vertex.name, "Edit vertex")

    if (newNameInput.isEmpty) {
      return None
    }


    val newName = newNameInput.get

    if (newName.isEmpty) {
      throw new IllegalArgumentException("The vertex must have a name!")
    }


    val nameAssignedToAnotherVertex =
      graph
        .vertexes
        .exists(otherVertex => otherVertex.asInstanceOf[Named].name == newName && otherVertex.id != vertex.id)

    if (nameAssignedToAnotherVertex) {
      throw new IllegalArgumentException("The vertex name must be unique!")
    }

    val newProblemVertex = vertex.nameCopy(name = newName).asInstanceOf[VisualVertex]

    Some(newProblemVertex)
  }
}
