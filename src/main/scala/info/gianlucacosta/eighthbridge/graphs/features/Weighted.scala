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

package info.gianlucacosta.eighthbridge.graphs.point2point.specific

import info.gianlucacosta.eighthbridge.graphs.Link
import info.gianlucacosta.eighthbridge.util.Numbers

/**
  * Object having a weight
  */
trait Weighted[T <: Weighted[T]] { this: T =>
  def minWeight: Double

  def maxWeight: Double

  def weight: Double


  /**
    * Ensures the weight is in the range [minWeight; maxWeight], throwing an IllegalArgumentException in case of errors
    */
  protected def checkWeight(): Unit = {
    if (weight < minWeight || weight > maxWeight) {
      throw new IllegalArgumentException(s"Weight must be in [${Numbers.smartString(minWeight)}; ${Numbers.smartString(maxWeight)}")
    }
  }

  /**
    * Copies the current object, giving it a new weight.
    *
    * If you implement this trait as a "case class", you can implement this method just by using the Scala-provided copy() method.
    *
    * @param weight The new weight
    * @return The resulting new object
    */
  def weightCopy(weight: Double): T
}
