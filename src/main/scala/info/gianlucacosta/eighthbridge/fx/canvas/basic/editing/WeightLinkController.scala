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

import info.gianlucacosta.eighthbridge.fx.canvas.basic.{BasicLink, BasicVertex}
import info.gianlucacosta.eighthbridge.graphs.point2point.specific.Weighted
import info.gianlucacosta.eighthbridge.graphs.point2point.visual.VisualGraph
import info.gianlucacosta.helios.fx.dialogs.InputDialogs

/**
  * Mixin controller providing editing support for weighted links
  *
  * @tparam V Vertex
  * @tparam L Link
  */
trait WeightLinkController[V <: BasicVertex[V], L <: BasicLink[L] with Weighted[L], G <: VisualGraph[V, L, G]]
  extends InteractiveEditingController[V, L, G] {
  override protected def interactiveLinkEditing(graph: G, link: L): Option[L] = {
    val newWeightOption =
      InputDialogs.askForDouble(
        "Weight:",
        link.weight,
        link.minWeight,
        link.maxWeight,
        "Edit link"
      )

    newWeightOption.map(newWeight => {
      link.weightCopy(newWeight)
    })
  }
}
