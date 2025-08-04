package com.desiderantes.detectindent


import com.desiderantes.detectindent.IndentDetector.detectIndent
import io.kotest.core.spec.style.FunSpec
import kotlin.test.assertEquals
import kotlin.test.assertTrue

fun getFile(path: String): String {
	return IndentTests::class.java.classLoader.getResource(path)!!.readText()
}

class IndentTests : FunSpec({
	test("detect the indent of a file with space indent") {
		assertEquals("    ", detectIndent(getFile("space.js")).indent)
	}

	test("return indentation stats for spaces") {
		val stats = detectIndent(getFile("space.js"))
		assertEquals(
			Indent(
				amount = 4,
				indent = "    ",
				type = IndentType.SPACE
			), stats
		)
	}

	test("return indentation stats for multiple tabs") {
		val stats = detectIndent(getFile("tab-four.js"))
		assertEquals(
			Indent(
				amount = 4,
				indent = "\t\t\t\t",
				type = IndentType.TAB
			), stats
		)
	}

	test("detect the indent of a file with tab indent") {
		assertEquals("\t", detectIndent(getFile("tab.js")).indent)
	}

	test("return indentation stats for tabs") {
		val stats = detectIndent(getFile("tab.js"))
		assertEquals(
			Indent(
				amount = 1,
				indent = "\t",
				type = IndentType.TAB
			), stats
		)

	}

	test("detect the indent of a file with equal tabs and spaces") {
		assertEquals("\t", detectIndent(getFile("mixed-tab.js")).indent)
	}

	test("return indentation stats for equal tabs and spaces") {
		val indent = detectIndent(getFile("mixed-tab.js"))
		assertEquals(
			Indent(
				amount = 1,
				indent = "\t",
				type = IndentType.TAB
			), indent
		)

	}

	test("detect the indent of a file with mostly spaces") {
		val stats = detectIndent(getFile("mixed-space.js"))
		assertEquals("    ", stats.indent)
	}

	test("return indentation stats for mostly spaces") {
		val stats = detectIndent(getFile("mixed-space.js"))
		assertEquals(
			Indent(
				amount = 4,
				indent = "    ",
				type = IndentType.SPACE
			), stats
		)
	}

	test("detect the indent of a weirdly indented vendor prefixed CSS") {
		val stats = detectIndent(getFile("vendor-prefixed-css.css"))
		assertEquals("    ", stats.indent)
	}

	test("return indentation stats for various spaces") {
		val stats = detectIndent(getFile("vendor-prefixed-css.css"))
		assertEquals(
			Indent(
				amount = 4,
				indent = "    ",
				type = IndentType.SPACE
			), stats
		)
	}

	test("return `0` when there is no indentation") {
		assertTrue { detectIndent("<ul></ul>").amount == 0 }
	}

	test("return indentation stats for no indentation") {
		val stats = detectIndent("<ul></ul>")
		assertEquals(
			Indent(
				amount = 0,
				indent = "",
				type = null
			), stats
		)
	}

	test("return indentation stats for fifty-fifty indented files with spaces first") {
		val stats = detectIndent(getFile("fifty-fifty-space-first.js"))
		assertEquals(
			Indent(
				amount = 4,
				indent = "    ",
				type = IndentType.SPACE
			), stats
		)
	}

	test("return indentation stats for fifty-fifty indented files with tabs first") {
		val stats = detectIndent(getFile("fifty-fifty-tab-first.js"))
		assertEquals(
			Indent(
				amount = 1,
				indent = "	",
				type = IndentType.TAB
			), stats
		)
	}

	test("return indentation stats for indented files with spaces and tabs last") {
		val stats = detectIndent(getFile("space-tab-last.js"))
		assertEquals(
			Indent(
				amount = 1,
				indent = "	",
				type = IndentType.TAB
			), stats
		)
	}

	test("detect the indent of a file with single line comments") {
		val stats = detectIndent(getFile("single-space-ignore.js"))
		assertEquals(
			Indent(
				amount = 4,
				indent = "    ",
				type = IndentType.SPACE
			), stats
		)
	}

	test("return indentations status for indented files with single spaces only") {
		val stats = detectIndent(getFile("single-space-only.js"))
		assertEquals(
			Indent(
				amount = 1,
				indent = " ",
				type = IndentType.SPACE
			), stats
		)
	}

	test("detect the indent of a file with many repeats after a single indent") {
		val stats = detectIndent(getFile("long-repeat.js"))
		assertEquals(4, stats.amount)
	}

})