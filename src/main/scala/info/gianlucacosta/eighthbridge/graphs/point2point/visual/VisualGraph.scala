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

package info.gianlucacosta.eighthbridge.graphs.point2point.visual

import java.util.UUID

import info.gianlucacosta.eighthbridge.graphs.Graph
import info.gianlucacosta.eighthbridge.graphs.point2point.{ArcBinding, DirectedGraph}
import info.gianlucacosta.helios.fx.geometry.extensions.GeometryExtensions._

import scalafx.geometry.{Bounds, Dimension2D, Point2D}

/**
  * Graph dedicated to rendering; it is especially useful in combination with GraphCanvas.
  *
  * Since such a graph is designed to be interactively drawn by users, it is necessarily
  * based on arc bindings - however, its "directed" property enables renderers to draw it
  * with edges instead of arcs.
  */
trait VisualGraph[V <: VisualVertex[V], L <: VisualLink[L], G <: VisualGraph[V, L, G]] extends DirectedGraph[V, L, G] { this: G =>
  def directed: Boolean

  def dimension: Dimension2D

  def selectionBounds: Bounds

  def visualCopy(directed: Boolean = directed, dimension: Dimension2D = dimension, selectionBounds: Bounds = selectionBounds): G

  @transient
  lazy val selectedVertexes: Set[V] =
    vertexes.filter(vertex => vertex.selected)


  @transient
  lazy val selectedLinks: Set[L] =
    links.filter(link => link.selected)


  @transient
  lazy val selectAll =
    setSelection(vertexes, links)


  @transient
  lazy val deselectAll =
    setSelection(Set(), Set())


  @transient
  lazy val selectionEmpty: Boolean =
    selectedVertexes.isEmpty && selectedLinks.isEmpty


  def setSelection(selectionVertexes: Set[V] = Set(), selectionLinks: Set[L] = Set()): G  =
    replaceVertexes(
      vertexes.map(vertex =>
        vertex.visualCopy(selected = selectionVertexes.contains(vertex))
      )
    )
      .replaceLinks(
        links.map(link =>
          link.visualCopy(selected = selectionLinks.contains(link))
        )
      )



  def moveSelectedVertexesBy(delta: Point2D): G =
    replaceVertexes(selectedVertexes.map(vertex => {
      val newCenter = (vertex.center + delta).clip(dimension)

      vertex.visualCopy(center = newCenter)
    }))
}
