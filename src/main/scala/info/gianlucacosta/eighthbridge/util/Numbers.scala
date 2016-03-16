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

package info.gianlucacosta.eighthbridge.util

import java.text.DecimalFormat

/**
  * Number-related utilities
  */
case object Numbers {
  /**
    * Tests if a Double actually represents a Long value
    *
    * @param value The value to test
    * @return true if the fractional part of the number is 0
    */
  def isLong(value: Double): Boolean = {
    val roundedValue = math.round(value)

    value - roundedValue == 0
  }


  /**
    * Prints a double in a user-friendly way:
    * <ul>
    * <li>If the fraction digits are all 0, do not print them</li>
    * <li>Otherwise, print at most the given number of fraction digits</li>
    * </ul>
    *
    * @param value             The value to print
    * @param maxFractionDigits The maximum number of fraction digits
    * @return A user-friendly string representation
    */
  def smartString(value: Double, maxFractionDigits: Int = 2): String = {
    val formatter = new DecimalFormat {
      setMaximumFractionDigits(maxFractionDigits)
    }

    formatter.format(value)
  }
}
