package com.desiderantes.detectindent;

import org.jspecify.annotations.NullMarked;

/**
 * Data class representing the detected indentation type, amount, and string representation.
 *
 * @param type   The type of indentation (space or tab).
 * @param amount The amount of indentation.
 */
@NullMarked
public record Indent(IndentType type, int amount) {

    /**
     * The string representation of the indentation for a given amount.
     * @return A string you can use for indentation.
     */
    public String indent() {
        return type.value().repeat(amount);
    }
}