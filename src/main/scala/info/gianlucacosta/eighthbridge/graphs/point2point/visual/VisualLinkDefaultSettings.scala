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

import scalafx.scene.paint.Color

object VisualLinkDefaultSettings extends VisualLinkSettings(
  lineColor = Color.valueOf("#9fecf3"),
  lineSize = 5,
  fontName = "Arial",
  fontSize = 14,
  fontColor = Color.Black,

  arrowRelativePosition = 0.75,
  arrowAngle = math.Pi / 6,
  arrowLength = 15,

  handleRadiusX = 6,
  handleRadiusY = 6,
  handleColor = Color.valueOf("#9fecf3"),

  labelConnectorColor = Color.Maroon,
  labelConnectorDashArray = List(15.0, 10.0)
)
