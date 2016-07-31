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

import scalafx.scene.Node

/**
  * Generic JavaFX node rendering a graph element
  */
trait GraphCanvasNode[
V <: VisualVertex[V],
L <: VisualLink[L],
G <: VisualGraph[V, L, G]
] extends Node {
  /**
    * The graph canvas owning this UI node
    *
    * @return
    */
  def graphCanvas: GraphCanvas[V, L, G]


  /**
    * The controller of the owning graph canvas
    *
    * @return
    */
  def controller: GraphCanvasController[V, L, G] =
    graphCanvas.controller


  /**
    * The current graph within the graph canvas
    *
    * @return
    */
  def graph: G =
    graphCanvas.graph


  /**
    * Simple way to update the graph contained the graph canvas - thus triggering the rendering process.
    * When migrating from older versions of EighthBridge, use this in lieu of notifyGraphChanged()
    *
    * @param newGraph
    */
  def graph_=(newGraph: G): Unit =
    graphCanvas.graph = newGraph


  /**
    * Used by GraphCanvas to draw the node whenever rendering is performed
    */
  def render(): Unit
}
