package de.faz.modules.query;

import java.util.Objects;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
public abstract class QueryItem {
	public abstract CharSequence toCharSequence();
	public abstract boolean contains(QueryItem item);

	@Override
	public String toString() {
		return toCharSequence().toString();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof QueryItem) {
			return Objects.equals(toCharSequence(), ((QueryItem) obj).toCharSequence());
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return Objects.hash(toCharSequence());
	}
}
