package de.faz.modules.query;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
public class TestMapping implements Mapping {
    @MapToField("field1")
    public String getField1() {
        return "field1Value";
    }

    @MapToField("field1")
    @BoostResult(2)
    public String getBoostedField1() {
        return "field1Value";
    }

    @MapToField("field2")
    public String getField2() {
        return "field2Value";
    }
}
