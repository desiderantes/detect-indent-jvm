package com.desiderantes.detectindent;

import org.jspecify.annotations.NullMarked;

/**
 * Enum class representing the type of indentation.
 */
@NullMarked
enum IndentType {
    SPACE(" ", 's'),
    TAB("\t", 't');

    private final String value;
    private final char typeChar;

    IndentType(String value, char typeChar) {
        this.value = value;
        this.typeChar = typeChar;
    }

    /**
     * @return The string representation of the indentation type.
     */
    public String value() {
        return value;
    }

    /**
     * @return The character representing the indentation type ('s' for space, 't' for tab).
     */
    public char typeChar() {
        return typeChar;
    }
}
