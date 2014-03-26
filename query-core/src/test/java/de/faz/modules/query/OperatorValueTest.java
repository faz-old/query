package de.faz.modules.query;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
public class OperatorValueTest {

	private OperatorValue underTest;

	@Before
	public void setUp() {
		underTest = new OperatorValue(" OP ", new CharSequence[]{"value1", "value2"});
	}

	@Test
	public void toCharSequence_withOperatorAndValues_returnCombinedCharSequence() {
		assertEquals("(value1 OP value2)", underTest.toCharSequence().toString());
	}

	@Test
	public void equals_withSameValuesAndOperator_isEquals() {
		assertEquals(
			new OperatorValue(" OP ", new CharSequence[] {"value1", "value2"})
			, underTest
		);
	}

	@Test
	public void equals_withSameValuesButOtherOperator_isNotEquals() {
		assertNotEquals(new OperatorValue(" NOP ", new CharSequence[] { "value1", "value2"}), underTest);
	}

	@Test
	public void equals_withDifferentValuesAndSameOperator_isNotEquals() {
		assertNotEquals(new OperatorValue(" OP ", new CharSequence[] { "value1", "value3"}), underTest);
	}
}
