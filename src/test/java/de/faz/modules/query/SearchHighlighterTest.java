package de.faz.modules.query;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.faz.modules.query.FieldDefinitionGenerator;
import net.sf.cglib.proxy.MethodInterceptor;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

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

    @Test
    public void getQueryExecutor_withQuery_enabledHighlighting() {
        SolrQuery query = mock(SolrQuery.class);
        highlighter.getQueryExecutor().enrich(query);
        verify(query).setHighlight(true);
    }

    @Test
    public void getQueryExecutor_withQueryAndPrefixAndPostfix_setHighlightPrefixAndPostfix() {
        SolrQuery query = mock(SolrQuery.class);
        highlighter.surroundWith("prefix", "postfix");
        highlighter.getQueryExecutor().enrich(query);
        verify(query).setHighlightSimplePre("prefix");
        verify(query).setHighlightSimplePost("postfix");
    }

    @Test
    public void getQueryExecutor_withQueryAndField_setHighlightField() {
        SolrQuery query = mock(SolrQuery.class);
        highlighter.withField(fieldDef.getField1());
        highlighter.getQueryExecutor().enrich(query);
        verify(query).addHighlightField("field1");
    }

    @Test
    public void createCallbackForDocument_withResponseAndDocument_createsProxyThatChecksHighlightingForDoc() throws Throwable {
        Map<String, List<String>> highlightingDoc = new HashMap<>();
        QueryResponse response = prepareQueryResponseMock(highlightingDoc);
        SolrDocument document = prepareSolrDocument();
        MethodInterceptor interceptor = (MethodInterceptor) highlighter.createCallbackForDocument(response, document);
        interceptor.intercept(null, TestMapping.class.getDeclaredMethod("getField1", null), null, null);
        verify(response.getHighlighting()).get("1.234");
    }

    @Test
    public void createCallbackForDocument_withResponseAndDocument_returnHighlightValue() throws Throwable {
        Map<String, List<String>> highlightingDoc = new HashMap<>();
        highlightingDoc.put("field1", Arrays.asList("highlightValue"));
        QueryResponse response = prepareQueryResponseMock(highlightingDoc);
        SolrDocument document = prepareSolrDocument();
        MethodInterceptor interceptor = (MethodInterceptor) highlighter.createCallbackForDocument(response, document);
        assertEquals("highlightValue", interceptor.intercept(null, TestMapping.class.getDeclaredMethod("getField1", null), null, null));
    }
    
    private SolrDocument prepareSolrDocument() {
        SolrDocument document  = mock(SolrDocument.class);
        when(document.getFieldValue("contentId")).thenReturn("1.234");
        return document;
    }

    private QueryResponse prepareQueryResponseMock(final Map<String, List<String>> highlightingDoc) {
        QueryResponse response = mock(QueryResponse.class, RETURNS_DEEP_STUBS);
        when(response.getHighlighting().get("1.234")).thenReturn(highlightingDoc);
        return response;
    }
}
