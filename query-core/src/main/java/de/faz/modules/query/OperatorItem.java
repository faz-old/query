package de.faz.modules.query;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
class OperatorItem extends QueryItem {
	private final String operator;
	private final QueryItem value;

	public OperatorItem(final String operator, final QueryItem value) {
		this.operator = operator;
		this.value = value;
	}

	@Override
	public CharSequence toCharSequence() {
		StringBuffer sb = new StringBuffer();
		CharSequence valueSequence = value.toCharSequence();
		if (valueSequence.length() > 0) {
			boolean hasBracket = valueSequence.charAt(0) == '(';
			sb.append(operator);
			if (!hasBracket) {
				sb.append('(');
			}
			sb.append(valueSequence);
			if (!hasBracket) {
				sb.append(')');
			}
		}
		return sb;
	}

	@Override
	public int hashCode() {
		int result = operator.hashCode();
		result = 31 * result + value.hashCode();
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof OperatorItem) {
			OperatorItem objItem = (OperatorItem) obj;
			return operator.equals(objItem.operator) && value.equals(objItem.value);
		}
		return super.equals(obj);
	}

	@Override
	public boolean contains(final QueryItem item) {
		return value.equals(item);
	}
}
