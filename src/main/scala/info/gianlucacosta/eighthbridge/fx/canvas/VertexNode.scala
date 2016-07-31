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

import info.gianlucacosta.eighthbridge.graphs.point2point.visual.{VisualGraph, VisualLink, VisualVertex}
import info.gianlucacosta.helios.fx.styles.PseudoClasses

import scala.collection.JavaConversions._
import scalafx.beans.property.ReadOnlyDoubleProperty

/**
  * JavaFX node rendering a VisualVertex
  */
trait VertexNode[
V <: VisualVertex[V],
L <: VisualLink[L],
G <: VisualGraph[V, L, G]
] extends GraphCanvasNode[V, L, G] {
  private var _vertex: V = _

  /**
    * The underlying vertex, updated as rendering is performed
    *
    * @return
    */
  def vertex: V =
    _vertex


  private[canvas] def vertex_=(newVertex: V): Unit =
    _vertex = newVertex


  def width: ReadOnlyDoubleProperty

  def height: ReadOnlyDoubleProperty


  override def render(): Unit = {
    styleClass.setAll("vertex")
    styleClass.addAll(vertex.styleClasses)


    this.pseudoClassStateChanged(
      PseudoClasses.Selected,
      vertex.selected
    )
  }
}
