package de.faz.modules.query.solr.capabilities;

import de.faz.modules.query.FieldDefinitionGenerator;
import de.faz.modules.query.Query;
import de.faz.modules.query.TestMapping;
import net.sf.cglib.proxy.MethodInterceptor;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.params.HighlightParams;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
@RunWith(MockitoJUnitRunner.class)
public class SolrSearchHighlighterTest {
	private FieldDefinitionGenerator generator;
	private TestMapping fieldDef;

	private SolrSearchHighlighter underTest;

	@Before
	public void setUp() {
		generator = new FieldDefinitionGenerator();
		fieldDef = generator.createFieldDefinition(TestMapping.class);
		underTest = new SolrSearchHighlighter(generator);
	}

	@Test
	public void getQueryExecutor_withQuery_enabledHighlighting() {
		SolrQuery query = mock(SolrQuery.class);
		underTest.getQueryExecutor().enrich(query);
		verify(query).setHighlight(true);
	}

	@Test
	public void getQueryExecutor_withQueryAndPrefixAndPostfix_setHighlightPrefixAndPostfix() {
		SolrQuery query = mock(SolrQuery.class);
		underTest.surroundWith("prefix", "postfix");
		underTest.getQueryExecutor().enrich(query);
		verify(query).setHighlightSimplePre("prefix");
		verify(query).setHighlightSimplePost("postfix");
	}

	@Test
	public void getQueryExecutor_withQueryAndField_setHighlightField() {
		SolrQuery query = mock(SolrQuery.class);
		underTest.withField(fieldDef.getField1());
		underTest.getQueryExecutor().enrich(query);
		verify(query).addHighlightField("field1");
	}

	@Test
	public void getQueryExecutor_withQueryAndHighlightingQuery_setsHighlightingQuery() {
		SolrQuery query = mock(SolrQuery.class);
		Query q = mock(Query.class);
		when(q.toString()).thenReturn("highlightingQuery");

		underTest.withQuery(q).getQueryExecutor().enrich(query);
		verify(query).setParam(HighlightParams.Q, "highlightingQuery");
	}

	@Test
	public void createCallbackForDocument_withResponseAndDocument_createsProxyThatChecksHighlightingForDoc() throws Throwable {
		Map<String, List<String>> highlightingDoc = new HashMap<>();
		QueryResponse response = prepareQueryResponseMock(highlightingDoc);
		SolrDocument document = prepareSolrDocument();
		MethodInterceptor interceptor = (MethodInterceptor) underTest.createCallbackForDocument(response, document);
		interceptor.intercept(null, TestMapping.class.getDeclaredMethod("getField1", null), null, null);
		verify(response.getHighlighting()).get("1.234");
	}

	@Test
	public void createCallbackForDocument_withResponseAndDocument_returnHighlightValue() throws Throwable {
		Map<String, List<String>> highlightingDoc = new HashMap<>();
		highlightingDoc.put("field1", Arrays.asList("highlightValue"));
		QueryResponse response = prepareQueryResponseMock(highlightingDoc);
		SolrDocument document = prepareSolrDocument();
		MethodInterceptor interceptor = (MethodInterceptor) underTest.createCallbackForDocument(response, document);
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
