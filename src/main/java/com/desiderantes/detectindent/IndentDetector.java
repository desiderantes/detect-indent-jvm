/**
 *
 * Translated from JavaScript to Kotlin from the original source code at
 * https://github.com/sindresorhus/detect-indent/.
 * <p>
 * SPDX-License-Identifier: MIT
 *
 * @license MIT
 * @author sindresorhus
 * @author desiderantes
 * <p>
 * The original license is hereby reproduced:
 * <p>
 * MIT License
 * <p>
 * Copyright (c) Sindre Sorhus <sindresorhus@gmail.com> (https://sindresorhus.com)
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.desiderantes.detectindent;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.AbstractMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static java.lang.Math.abs;

/**
 * Utility class for detecting indentation in strings.
 */
@NullMarked
public class IndentDetector {

    private IndentDetector() {
    }

    // Detect either spaces or tabs but not both to properly handle tabs for indentation and spaces for alignment

    private static final Pattern INDENT_REGEX = Pattern.compile("^(?:( )+|\\t+)");

    /**
     * Make a Map that counts how many indents/unindents have occurred for a given size and how many lines follow a given indentation.
     * <p>
     * The key is a concatenation of the indentation type (s = space and t = tab) and the size of the indents/unindents.
     * <p>
     * ```
     * indents = {
     * t3: (1, 0),
     * t4: (1, 5),
     * s5: (1, 0),
     * s12: (1, 0),
     * }
     * ```
     */

    private static Map<Indent, Map.Entry<Integer, Integer>> makeIndentsMap(String string, boolean ignoreSingleSpaces) {
        //We keep insertion order as a way to break ties consistently
        var indents = new LinkedHashMap<Indent, Map.Entry<Integer, Integer>>();

        int previousSize = 0;
        IndentType previousIndentType = null;

        Indent key = null;

        for (var line : string.split("\n")) {
            if (line.isEmpty()) {
                continue;
            }

            var matcher = INDENT_REGEX.matcher(line);
            if (matcher.find()) {
                var indent = matcher.group(0).length();
                IndentType indentType = !(matcher.group(1) == null || matcher.group(1).isEmpty()) ? IndentType.SPACE : IndentType.TAB;

                // Ignore single space unless it's the only indent detected to prevent common false positives
                if (ignoreSingleSpaces && indentType == IndentType.SPACE && indent == 1) {
                    continue;
                }

                if (indentType != previousIndentType) {
                    previousSize = 0;
                }

                previousIndentType = indentType;

                int use = 1;
                int weight = 0;

                int indentDifference = indent - previousSize;
                previousSize = indent;

                // Previous line have same indent?
                if (indentDifference == 0) {
                    // Not a new "use" of the current indent:
                    use = 0;
                    // But do add a bit to it for breaking ties:
                    weight = 1;
                    // We use the key from previous loop
                } else {
                    int absoluteIndentDifference = abs(indentDifference);
                    key = new Indent(indentType, absoluteIndentDifference);
                }
                if (key == null) {
                    continue;
                }
                var entry = indents.getOrDefault(key, new AbstractMap.SimpleImmutableEntry<>(0, 0));
                indents.put(key, new AbstractMap.SimpleEntry<>(entry.getKey() + use, entry.getValue() + weight));
            }
        }

        return indents;
    }

    /**
     * Return the key (e.g. 's4') from the indents Map that represents the most common indent,
     * or return undefined if there are no indents.
     */

    private static @Nullable Indent getMostUsedKey(Map<Indent, Map.Entry<Integer, Integer>> indents) {
        Indent result = null;
        int maxUsed = 0;
        int maxWeight = 0;

        for (var entry : indents.entrySet()) {
            Indent key = entry.getKey();
            Map.Entry<Integer, Integer> value = entry.getValue();
            int usedCount = value.getKey();
            int weight = value.getValue();
            if (usedCount > maxUsed || (usedCount == maxUsed && weight > maxWeight)) {
                maxUsed = usedCount;
                maxWeight = weight;
                result = key;
            }
        }

        return result;
    }

    /**
     * Detect the indentation type and amount in a given string.
     *
     * @param string The input string to analyze for indentation.
     * @return An Indent object containing the type, amount, and string representation of the most common indent.
     */

    public static @Nullable Indent detectIndent(String string) {
        if (string.isEmpty()) {
            return null;
        }

        var indents = makeIndentsMap(string, true);
        if (indents.isEmpty()) {
            indents = makeIndentsMap(string, false);
        }

        return getMostUsedKey(indents);
    }
}

