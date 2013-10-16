package de.faz.modules.query;

import com.google.common.base.Joiner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
class OperatorValue extends ValueItem {

	private final String operator;
	private final CharSequence[] values;

	public OperatorValue(final String operator, final CharSequence[] values) {
		this.operator = operator;
		List<CharSequence> valueList = new ArrayList<>(values.length);
		for (CharSequence value : values) {
			valueList.add(new StringValue(value).toCharSequence());
		}
		valueList.removeAll(Collections.singleton(null));
		this.values = valueList.toArray(new CharSequence[valueList.size()]);
	}

	@Override
	public CharSequence toCharSequence() {
		StringBuffer sb = new StringBuffer();
		sb.append('(');
		try {
			Joiner.on(operator).appendTo(sb, values);
		} catch (IOException e) {
			return "";
		}
		sb.append(')');
		return sb;
	}

	@Override
	public int hashCode() {
		int result = operator.hashCode();
		result = 31 * result + Arrays.hashCode(values);
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof OperatorValue) {
			OperatorValue opValue = (OperatorValue) obj;
			boolean containsAllValues = Arrays.asList(values).containsAll(Arrays.asList(opValue.values));
			return containsAllValues && operator.equals(opValue.operator);
		}
		return super.equals(obj);
	}
}
