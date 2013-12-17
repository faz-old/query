package de.faz.modules.query;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class StringValueTest {

	@Test
	public void test_toCharSequence_blankAndMinus() {
		// Regression Test for FTK-550, FTK-551
		StringValue value = new StringValue("Joachim Müller-Jung");
		assertEquals("Joachim\\ Müller\\-Jung", value.toCharSequence());
	}

	@Test
	public void test_toCharSequence_multipleBlanksAndMinus() {
		StringValue value = new StringValue("*und    Joachim Müller-Jung,");
		assertEquals("*und\\ \\ \\ \\ Joachim\\ Müller\\-Jung,", value.toCharSequence());
	}

	@Test
	public void test_toCharSequence_MultipleChars() {
		StringValue value = new StringValue("Test-! +  Joachim+Müller-Jung^");
		assertEquals("Test\\-\\!\\ \\+\\ \\ Joachim\\+Müller\\-Jung\\^", value.toCharSequence());
	}

	@Test
	public void test_toCharSequence_braces() {
		StringValue value = new StringValue("{[(Joachim Müller-Jung])})");
		assertEquals("\\{\\[\\(Joachim\\ Müller\\-Jung\\]\\)\\}\\)", value.toCharSequence());
	}

	@Test
	public void test_toCharSequence_AndOr() {
		StringValue value = new StringValue("Klaus Kleber || Joachim Müller-Jung && Gundula Gause");
		assertEquals("Klaus\\ Kleber\\ \\||\\ Joachim\\ Müller\\-Jung\\ \\&&\\ Gundula\\ Gause", value.toCharSequence());
	}


	@Test
	public void test_toCharSequence_InvalidAnd() {
		StringValue value = new StringValue("Joachim &&& Müller-Jung");
		assertEquals("Joachim\\ \\&&&\\ Müller\\-Jung", value.toCharSequence());
	}

	@Test
	public void test_toCharSequence_LotsOfBlanks() {
		StringValue value = new StringValue("   Joachim   Müller  -  Jung   ");
		assertEquals("\\ \\ \\ Joachim\\ \\ \\ Müller\\ \\ \\-\\ \\ Jung\\ \\ \\ ", value.toCharSequence());
	}

	@Test
	public void test_toCharSequence_TildeAndColon() {
		StringValue value = new StringValue("^Joachim ~Müller:Jung");
		assertEquals("\\^Joachim\\ \\~Müller\\:Jung", value.toCharSequence());
	}

	@Test
	public void test_toCharSequence_OtherSpecialChars() {
		StringValue value = new StringValue("Werner D’Inka");
		assertEquals("Werner\\ D’Inka", value.toCharSequence());
	}

	@Test
	public void test_toCharSequence_OtherSpecialChars2() {
		StringValue value = new StringValue("Werner D'Inka");
		assertEquals("Werner\\ D'Inka", value.toCharSequence());
	}

}
