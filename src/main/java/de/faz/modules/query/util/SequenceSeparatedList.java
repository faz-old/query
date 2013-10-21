package de.faz.modules.query.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ListIterator;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
public class SequenceSeparatedList<T> extends ArrayList<T> {

	private final CharSequence separator;

	public SequenceSeparatedList(final Collection<? extends T> c, final CharSequence separator) {
		super(c);
		this.separator = separator;
	}

	public SequenceSeparatedList(final CharSequence separator) {
		this.separator = separator;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		ListIterator<T> it = listIterator();
		while(it.hasNext()) {
			sb.append(it.next().toString());
			if(it.hasNext()) {
				sb.append(separator);
			}
		}
		return sb.toString();
	}
}
