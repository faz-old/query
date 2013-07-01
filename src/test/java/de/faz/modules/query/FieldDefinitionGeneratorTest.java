package de.faz.modules.query;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
public class FieldDefinitionGeneratorTest {

    FieldDefinitionGenerator generator;

    @Before
    public void setUp() {
        generator = new FieldDefinitionGenerator();
    }

    @Test
    public void enhance_withInheritedMapping_getFieldnameFromSuperClass() {
        InheritedMapping mapping = generator.createFieldDefinition(InheritedMapping.class);
        assertEquals("field2", generator.getFieldNameOf(mapping.getField2()));
    }

    @Test
    public void enhance_withNativeMethod_returnsFieldnameOfNativeMethod() {
        InheritedMapping mapping = generator.createFieldDefinition(InheritedMapping.class);
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