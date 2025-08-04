/**
 *
 * Translated from JavaScript to Kotlin from the original source code at
 * https://github.com/sindresorhus/detect-indent/.
 *
 * SPDX-License-Identifier: MIT
 * @license MIT
 * @author sindresorhus
 * @author desiderantes
 *
 * The original license is hereby reproduced:
 *
 * MIT License
 *
 * Copyright (c) Sindre Sorhus <sindresorhus@gmail.com> (https://sindresorhus.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.desiderantes.detectindent


object IndentDetector {

  // Detect either spaces or tabs but not both to properly handle tabs for indentation and spaces for alignment
  @JvmStatic
  private val INDENT_REGEX = Regex("^(?:( )+|\\t+)", setOf(RegexOption.MULTILINE))

  /**
   * Make a Map that counts how many indents/unindents have occurred for a given size and how many lines follow a given indentation.
   *
   * The key is a concatenation of the indentation type (s = space and t = tab) and the size of the indents/unindents.
   *
   * ```
   * indents = {
   * t3: (1, 0),
   * t4: (1, 5),
   * s5: (1, 0),
   * s12: (1, 0),
   * }
   * ```
   */
  @JvmStatic
  private fun makeIndentsMap(string: String, ignoreSingleSpaces: Boolean): Map<String, Pair<Int, Int>> {
    val indents = mutableMapOf<String, Pair<Int, Int>>()

    var previousSize = 0
    var previousIndentType: IndentType? = null

    var key: String? = null

    for (line in string.split("\n")) {
      if (line.isEmpty()) {
        continue
      }

      val matcher = INDENT_REGEX.find(line)
      if (matcher != null) {
        val indent = matcher.groupValues[0].length
        val indentType = if (!matcher.groupValues.getOrNull(1).isNullOrEmpty()) IndentType.SPACE else IndentType.TAB

        // Ignore single space unless it's the only indent detected to prevent common false positives
        if (ignoreSingleSpaces && indentType == IndentType.SPACE && indent == 1) {
          continue
        }

        if (indentType != previousIndentType) {
          previousSize = 0
        }

        previousIndentType = indentType

        var use = 1
        var weight = 0

        val indentDifference = indent - previousSize
        previousSize = indent

        // Previous line have same indent?
        if (indentDifference == 0) {
          // Not a new "use" of the current indent:
          use = 0
          // But do add a bit to it for breaking ties:
          weight = 1
          // We use the key from previous loop
        } else {
          val absoluteIndentDifference = kotlin.math.abs(indentDifference)
          key = encodeIndentsKey(indentType, absoluteIndentDifference)
        }

        val entry = indents.getOrDefault(key, 0 to 0)
        indents[key!!] = (entry.first + use) to (entry.second + weight)
      }
    }

    return indents
  }

  /**
   * Encode the indent type and amount as a string (e.g. 's4') for use as a compound key in the indents Map.
   */
  @JvmStatic
  private fun encodeIndentsKey(indentType: IndentType, indentAmount: Int): String {
    return "${indentType.typeChar}$indentAmount"
  }

  /**
   * Extract the indent type and amount from a key of the indents Map.
   */
  @JvmStatic
  private fun decodeIndentsKey(indentsKey: String): Pair<IndentType, Int> {
    val type = if (indentsKey[0] == 's') IndentType.SPACE else IndentType.TAB
    val amount = indentsKey.substring(1).toInt()
    return Pair(type, amount)
  }

  /**
   * Return the key (e.g. 's4') from the indents Map that represents the most common indent,
   * or return undefined if there are no indents.
   */
  @JvmStatic
  private fun getMostUsedKey(indents: Map<String, Pair<Int, Int>>): String? {
    var result: String? = null
    var maxUsed = 0
    var maxWeight = 0

    for ((key, value) in indents) {
      val (usedCount, weight) = value
      if (usedCount > maxUsed || (usedCount == maxUsed && weight > maxWeight)) {
        maxUsed = usedCount
        maxWeight = weight
        result = key
      }
    }

    return result
  }

  /**
   * Detect the indentation type and amount in a given string.
   *
   * @param string The input string to analyze for indentation.
   * @return An Indent object containing the type, amount, and string representation of the most common indent.
   * @throws IllegalArgumentException if the input string is empty.
   */
  @JvmStatic
  fun detectIndent(string: String): Indent {
    require(string.isNotEmpty()) {
      "Expected a non-empty string"
    }

    var indents = makeIndentsMap(string, true)
    if (indents.isEmpty()) {
      indents = makeIndentsMap(string, false)
    }

    val keyOfMostUsedIndent = getMostUsedKey(indents)

    var type: IndentType? = null
    var amount = 0
    var indent = ""

    if (keyOfMostUsedIndent != null) {
      val (decodedType, decodedAmount) = decodeIndentsKey(keyOfMostUsedIndent)
      type = decodedType
      amount = decodedAmount
      indent = type.getIndentString(amount)
    }

    return Indent(type, amount, indent)
  }
}

/**
 * Data class representing the detected indentation type, amount, and string representation.
 *
 * @property type The type of indentation (space or tab).
 * @property amount The amount of indentation.
 * @property indent The string representation of the indentation.
 */
@JvmRecord
data class Indent(val type: IndentType?, val amount: Int, val indent: String)

/**
 * Enum class representing the type of indentation.
 *
 * @property value The string representation of the indentation type.
 * @property typeChar The character representing the indentation type ('s' for space, 't' for tab).
 */
enum class IndentType(val value: String, val typeChar: Char) {
  SPACE(" ", 's'),
  TAB("\t", 't');

  /**
   * Get the string representation of the indentation for a given amount.
   *
   * @param amount The amount of indentation.
   * @return The string representation of the indentation.
   */
  fun getIndentString(amount: Int): String {
    return value.repeat(amount)
  }
}
