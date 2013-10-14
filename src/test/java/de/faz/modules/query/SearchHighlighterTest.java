package de.faz.modules.query;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
@RunWith(MockitoJUnitRunner.class)
public class SearchHighlighterTest {

    private SearchHighlighter highlighter;

    private TestMapping fieldDef;

    @Before
    public void setUp() {
        FieldDefinitionGenerator defGenerator = new FieldDefinitionGenerator();
        fieldDef = defGenerator.createFieldDefinition(TestMapping.class);
        highlighter = new SearchHighlighter(defGenerator);
    }

    @Test
    public void withField_withFieldDefinition_addFieldToHighlight() {
        highlighter.withField(fieldDef.getField1());
        Assert.assertEquals(1, highlighter.getFields().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void withField_withoutFieldDefinition_throwsIllegalArgumentException() {
        highlighter.withField(null);
    }

    @Test
    public void surroundWith_withValues_addPrefixAndPostfix() {
        highlighter.surroundWith("<span>", "</span>");
        Assert.assertEquals("<span>", highlighter.getPrefix());
        Assert.assertEquals("</span>", highlighter.getPostfix());
    }

    @Test(expected = IllegalArgumentException.class)
    public void surroundWith_withMissingPrefix_throwsIllegalArgumentException() {
        highlighter.surroundWith(null, "</span>");
    }

    @Test(expected = IllegalArgumentException.class)
    public void surroundWith_withMissingPostfix_throwsIllegalArgumentException() {
        highlighter.surroundWith("<span>", null);
    }

}
