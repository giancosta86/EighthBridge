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

import info.gianlucacosta.eighthbridge.graphs.point2point.visual.VisualGraph

import scalafx.scene.Node

/**
  * Generic JavaFX node rendering a graph component
  */
trait GraphCanvasNode extends Node {
  private var graphChangedListener: Option[VisualGraphChangedListener] = None


  def addGraphChangedListener(listener: VisualGraphChangedListener): Unit = {
    require(listener != null)
    require(graphChangedListener.isEmpty)

    graphChangedListener = Some(listener)
  }


  /**
    * To be called by the JavaFX node whenever - after user
    * interaction - the graph in the GraphCanvas has to be replaced
    *
    * @param newGraph
    */
  protected def notifyGraphChanged(newGraph: VisualGraph): Unit = {
    require(newGraph != null)
    graphChangedListener.foreach(_ (newGraph))
  }


  /**
    * Used by GraphCanvas to render the node whenever a new underlying graph is set
    */
  def render(): Unit
}
