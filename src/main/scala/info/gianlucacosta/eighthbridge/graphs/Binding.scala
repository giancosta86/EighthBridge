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

package info.gianlucacosta.eighthbridge.graphs

import java.util.UUID

/**
  * Generic binding - that is, a connection between a link and any number of vertexes
  */
trait Binding extends GraphComponent {
  /**
    * The set of ids of the attached vertexes
    */
  val vertexIds: Set[UUID]

  /**
    * The sorted list containing the ids of the attached vertexes.
    * The actual sort order depends on the specific binding implementation.
    */
  val sortedVertexIds: Seq[UUID]

  /**
    * The id of the attached link
    */
  val linkId: UUID
}
