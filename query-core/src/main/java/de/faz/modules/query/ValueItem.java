package de.faz.modules.query;

import java.util.Objects;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
public abstract class ValueItem {
	abstract CharSequence toCharSequence();

	@Override
	public String toString() {
		return toCharSequence().toString();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof ValueItem ? Objects.equals(toCharSequence(), ((ValueItem) obj).toCharSequence()) : super.equals(obj);
	}

	@Override
	public int hashCode() {
		return Objects.hash(toCharSequence());
	}
}
