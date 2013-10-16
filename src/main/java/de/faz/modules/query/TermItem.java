package de.faz.modules.query;

import de.faz.modules.query.fields.FieldDefinitionGenerator;

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
        int result = field.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if(obj instanceof TermItem) {
            TermItem termItem = (TermItem)obj;
            return field.equals(termItem.field) && value.equals(termItem.value);
        }
        return super.equals(obj);
    }

    @Override
    public boolean contains(final QueryItem item) {
        return false;
    }
}
