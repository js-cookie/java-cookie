package com.github.jscookie.javacookie.test.integration.qunit;

import java.util.ArrayList;
import java.util.List;

public class QUnitResults {
	private int failed;
	private int passed;
	private int runtime;
	private int total;
	private List<Test> tests = new ArrayList<Test>();

	public int getFailed() {
		return failed;
	}

	public int getPassed() {
		return passed;
	}

	public int getRuntime() {
		return runtime;
	}

	public List<Test> getTests() {
		return tests;
	}

	public int getTotal() {
		return total;
	}

	public static class Test {
		private String actual = "";
		private String expected = "";
		private String name = "";
		private boolean result = false;
		private String source;

		public String getActual() {
			return actual;
		}

		public String getExpected() {
			return expected;
		}

		public String getName() {
			return name;
		}

		public boolean getResult() {
			return result;
		}

		public String getSource() {
			return source;
		}
	}
}
