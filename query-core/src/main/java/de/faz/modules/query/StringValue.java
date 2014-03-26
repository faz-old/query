package de.faz.modules.query;

import java.util.regex.Pattern;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
class StringValue extends ValueItem {

	private final static String[] ILLEGAL_CHARACTERS = new String[] { " ", "+", "-", "&&", "||", "!", "(", ")", "{", "}", "[", "]", "^", "~", ":" };

	final CharSequence value;

	public StringValue(final CharSequence value) {
		this.value = value;
	}

	@Override
	public CharSequence toCharSequence() {
		String stringValue = value.toString();
		for (String character : ILLEGAL_CHARACTERS) {
			stringValue = stringValue.replaceAll(Pattern.quote(character), "\\\\" + character);
		}
		// escape the quotation mark (") in special cases
		stringValue = stringValue.replaceAll("=([^\\\\\"]*[\\\\\"][^\\\\\"]*[\\\\\"])*[^\\\\\"]*$", "\\\\\"");
		return stringValue;
	}
}
