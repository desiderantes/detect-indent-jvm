package com.desiderantes.detectindent;


import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static com.desiderantes.detectindent.IndentDetector.detectIndent;
import static org.junit.jupiter.api.Assertions.*;


class IndentTests {

    private static String getFile(@NonNull String path) {

        var url = IndentTests.class.getClassLoader().getResource(path);

        try (InputStream in = url.openStream()) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read file: " + path, e);
        }
    }

    @Test
    @DisplayName("detect the indent of a file with space indent")
    void detectSpaceIndent() {
        var stats = detectIndent(getFile("space.js"));
        assertNotNull(stats);
        assertEquals("    ", stats.indent());
    }

    @Test
    @DisplayName("return indentation stats for spaces")
    void returnSpaceIndentStats() {
        var stats = detectIndent(getFile("space.js"));
        assertEquals(new Indent(IndentType.SPACE, 4), stats);
    }

    @Test
    @DisplayName("return indentation stats for multiple tabs")
    void returnMultipleTabIndentStats() {
        var stats = detectIndent(getFile("tab-four.js"));
        assertEquals(new Indent(IndentType.TAB, 4), stats);
    }

    @Test
    @DisplayName("detect the indent of a file with tab indent")
    void detectTabIndent() {
        var stats = detectIndent(getFile("tab.js"));
        assertNotNull(stats);
        assertEquals("\t", stats.indent());
    }

    @Test
    @DisplayName("return indentation stats for tabs")
    void returnTabIndentStats() {
        var stats = detectIndent(getFile("tab.js"));
        assertEquals(new Indent(IndentType.TAB, 1), stats);

    }

    @Test
    @DisplayName("detect the indent of a file with equal tabs and spaces")
    void detectMixedIndent() {
        var stats = detectIndent(getFile("mixed-tab.js"));
        assertNotNull(stats);
        assertEquals("\t", stats.indent());
    }

    @Test
    @DisplayName("return indentation stats for equal tabs and spaces")
    void returnMixedIndentStats() {
        var indent = detectIndent(getFile("mixed-tab.js"));
        assertEquals(new Indent(IndentType.TAB, 1), indent);

    }

    @Test
    @DisplayName("detect the indent of a file with mostly spaces")
    void detectMostlySpacesIndent() {
        var stats = detectIndent(getFile("mixed-space.js"));
        assertNotNull(stats);

        assertEquals("    ", stats.indent());
    }

    @Test
    @DisplayName("return indentation stats for mostly spaces")
    void returnMostlySpacesIndentStats() {
        var stats = detectIndent(getFile("mixed-space.js"));
        assertEquals(new Indent(IndentType.SPACE, 4), stats);
    }

    @Test
    @DisplayName("detect the indent of a weirdly indented vendor prefixed CSS")
    void detectVendorPrefixedCssIndent() {
        var stats = detectIndent(getFile("vendor-prefixed-css.css"));
        assertNotNull(stats);

        assertEquals("    ", stats.indent());
    }

    @Test
    @DisplayName("return indentation stats for various spaces")
    void returnVendorPrefixedCssIndentStats() {
        var stats = detectIndent(getFile("vendor-prefixed-css.css"));
        assertEquals(new Indent(IndentType.SPACE, 4), stats);
    }

    @Test
    @DisplayName("return indentation stats for no indentation")
    void returnNoIndent() {
        var stats = detectIndent("<ul></ul>");
        assertNull(stats);
    }

    @Test
    @DisplayName("return indentation stats for fifty-fifty indented files with spaces first")
    void returnFiftyFiftySpaceFirstIndentStats() {
        var stats = detectIndent(getFile("fifty-fifty-space-first.js"));
        assertEquals(new Indent(IndentType.SPACE, 4), stats);
    }

    @Test
    @DisplayName("return indentation stats for fifty-fifty indented files with tabs first")
    void returnFiftyFiftyTabFirstIndentStats() {
        var stats = detectIndent(getFile("fifty-fifty-tab-first.js"));
        assertEquals(new Indent(IndentType.TAB, 1), stats);
    }

    @Test
    @DisplayName("return indentation stats for indented files with spaces and tabs last")
    void returnSpaceTabLastIndentStats() {
        var stats = detectIndent(getFile("space-tab-last.js"));
        assertEquals(new Indent(IndentType.TAB, 1), stats);
    }

    @Test
    @DisplayName("detect the indent of a file with single line comments")
    void detectSingleLineCommentsIndent() {
        var stats = detectIndent(getFile("single-space-ignore.js"));
        assertEquals(new Indent(IndentType.SPACE, 4), stats);
    }

    @Test
    @DisplayName("return indentations status for indented files with single spaces only")
    void returnSingleLineCommentsIndentStats() {
        var stats = detectIndent(getFile("single-space-only.js"));
        assertEquals(new Indent(IndentType.SPACE, 1), stats);
    }

    @Test
    @DisplayName("detect the indent of a file with many repeats after a single indent")
    void detectLongRepeatIndent() {
        var stats = detectIndent(getFile("long-repeat.js"));
        assertNotNull(stats);
        assertEquals(4, stats.amount());
    }

    @Test
    @DisplayName("empty file returns null")
    void emptyFileReturnsNull() {
        var stats = detectIndent("");
        assertNull(stats);
    }


}
