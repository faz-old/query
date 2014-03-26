package de.faz.modules.query.util;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
public class SequenceSeparatedListTest {

	private SequenceSeparatedList<String> underTest;

	private final CharSequence separator = ",";

	@Before
	public void setUp() {
		underTest = new SequenceSeparatedList<>(separator);
	}

	@Test
	public void toString_withOneElement_doNotAddSeparator() {
		underTest.add("value1");
		assertEquals("value1", underTest.toString());
	}

	@Test
	public void toString_withTwoElements_addSeparatorToString() {
		underTest.add("value1");
		underTest.add("value2");
		assertEquals("value1,value2", underTest.toString());
	}
}
