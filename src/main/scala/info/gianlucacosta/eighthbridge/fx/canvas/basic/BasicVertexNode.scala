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

import info.gianlucacosta.eighthbridge.fx.canvas._
import info.gianlucacosta.eighthbridge.graphs.point2point.visual.VisualGraph

import scala.collection.JavaConversions._
import scalafx.Includes._
import scalafx.beans.property.ReadOnlyDoubleProperty
import scalafx.geometry.Dimension2D
import scalafx.scene.control.Label
import scalafx.scene.layout.VBox
import scalafx.scene.shape.Rectangle
import scalafx.scene.text.TextAlignment
import scalafx.scene.{Group, Scene}


object BasicVertexNode {
  val DefaultPadding: Double =
    10

  /**
    * Case class whose instances can be passed to <i>getDimensions()</i>
    *
    * @param text         The vertex text
    * @param padding      The vertex padding
    * @param styleClasses The CSS classes that should be applied to the vertex (in addition to the "vertex" predefined one)
    */
  case class DimensionQuery(
                             text: String,
                             padding: Double = DefaultPadding,
                             styleClasses: List[String] = List()
                           )


  /**
    * Given a list of stylesheets and a list of query objects,
    * returns the list of Dimension2D for BasicVertexNode objects,
    * instantiated in a dedicated invisible scene
    *
    * @param sceneStylesheets
    * @param queries
    * @return The list of dimensions, in the same order as the input queries
    */
  def getDimensions(sceneStylesheets: List[String], queries: List[DimensionQuery]): List[Dimension2D] = {
    val layoutBox =
      new VBox

    val scene =
      new Scene(layoutBox) {
        stylesheets.setAll(sceneStylesheets: _*)
      }


    val labels =
      queries.map(query =>
        new Label {
          text = query.text
        }
      )


    val groupNodes =
      labels.zip(queries).map {
        case (label, query) =>
          new Group {
            children.add(label)

            styleClass.setAll("vertex")
            styleClass.addAll(query.styleClasses)
          }
            .delegate
      }

    layoutBox.children.setAll(groupNodes: _*)

    layoutBox.applyCss()

    labels.zip(queries).map {
      case (label, query) =>
        val padding =
          query.padding

        new Dimension2D(
          label.delegate.prefWidth(-1)
            + 2 * padding,

          label.delegate.prefHeight(-1)
            + 2 * padding
        )
    }
  }
}


/**
  * Default, interactive implementation of VertexNode
  */
class BasicVertexNode[
V <: BasicVertex[V],
L <: BasicLink[L],
G <: VisualGraph[V, L, G]
](
   val graphCanvas: GraphCanvas[V, L, G],
   padding: Double = BasicVertexNode.DefaultPadding
 )
  extends Group
    with BasicVertexNodeMixin[V, L, G] {
  protected val label = new Label {
    styleClass.add("label")

    textAlignment =
      TextAlignment.Center


    layoutX <==
      centerX - width / 2


    layoutY <==
      centerY - height / 2
  }

  protected val body = new Rectangle {
    styleClass.add("body")

    width <==
      label.width + 2 * padding


    height <==
      label.height + 2 * padding


    layoutX <==
      label.layoutX - padding


    layoutY <==
      label.layoutY - padding
  }


  children.addAll(body, label)


  override def render(): Unit = {
    super.render()

    label.text =
      vertex.text
  }


  override def width: ReadOnlyDoubleProperty =
    body.width


  override def height: ReadOnlyDoubleProperty =
    body.height
}

