package de.faz.modules.query;

import static org.junit.Assert.assertEquals;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.Validate;
import org.junit.Before;
import org.junit.Test;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
public class FieldDefinitionGeneratorTest {

    FieldDefinitionGenerator generator;

    @Before
    public void setUp() {
        generator = new FieldDefinitionGenerator();
    }

    @Test
    public void enhance_withInheritedMapping_getFieldnameFromSuperClass() {
        final InheritedMapping mapping = generator.createFieldDefinition(InheritedMapping.class);
        assertEquals("field2", generator.getFieldNameOf(mapping.getField2()));
    }

    @Test
    public void enhance_withInheritedMappingWithNonDefaultConstructor_getFieldnameFromSuperClass() {
        final InheritedMapping mapping = generator.createFieldDefinition(InheritedMappingWithNonDefaultConstructor.class);
        assertEquals("field2", generator.getFieldNameOf(mapping.getField2()));
    }

    @Test
    public void enhance_withNativeMethod_returnsFieldnameOfNativeMethod() {
        final InheritedMapping mapping = generator.createFieldDefinition(InheritedMapping.class);
        assertEquals("field4", generator.getFieldNameOf(mapping.getField4()));
    }

}

class InheritedMapping extends TestMapping {

    @MapToField("field3")
    public String getField3() {
        return null;
    }

    @MapToField("field4")
    public boolean getField4() {
        return false;
    }
}

class InheritedMappingWithNonDefaultConstructor extends InheritedMapping {

    public InheritedMappingWithNonDefaultConstructor(@Nonnull final ObjectWithNoDefaultConstructor object) {
        Validate.notNull(object, "Object must not be null.");
    }
}

class ObjectWithNoDefaultConstructor {

    public ObjectWithNoDefaultConstructor(@Nullable final Object object) {
    }
}
