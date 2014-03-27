package de.faz.modules.query;

import de.faz.modules.query.fields.FieldDefinitionGenerator;

import java.util.Objects;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
class TermItem extends QueryItem {

    private final FieldDefinitionGenerator.FieldDefinition field;
    private final ValueItem value;

    public TermItem(final FieldDefinitionGenerator.FieldDefinition fieldDefinition, final ValueItem value) {
        this.field = fieldDefinition;
        this.value = value;
    }

    @Override
    public CharSequence toCharSequence() {
        StringBuffer sb = new StringBuffer();
        sb.append(field.getName()).append(':').append(value.toCharSequence());
        if(field.getBoost() != 1) {
            sb.append('^').append(field.getBoost());
        }
        return sb;
    }

    @Override
    public int hashCode() {
        return Objects.hash(field, value);
    }

    @Override
    public boolean equals(final Object obj) {
        if(obj instanceof TermItem) {
            TermItem termItem = (TermItem)obj;
            return Objects.equals(field, termItem.field)
				&& Objects.equals(value, termItem.value);
        }
        return super.equals(obj);
    }

    @Override
    public boolean contains(final QueryItem item) {
        return false;
    }
}
