package de.faz.modules.query.solr;

import de.faz.modules.query.TestMapping;
import net.sf.cglib.proxy.MethodInterceptor;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
@RunWith(MockitoJUnitRunner.class)
public class StandardCallbackFactoryTest {
    @Mock QueryResponse response;
    @Mock SolrDocument document;

    private StandardCallbackFactory factory;

    @Before
    public void setUp() {
        factory = new StandardCallbackFactory();
    }

    @Test
    public void createCallback_createNewMethodInterceptor() {
        assertTrue(factory.createCallbackForDocument(null, null) instanceof MethodInterceptor);
    }

    @Test
    public void createCallback_callCallbackIntercept_verifyThatDocumentFieldIsRetrieved() throws Throwable {
        MethodInterceptor interceptor = (MethodInterceptor) factory.createCallbackForDocument(response, document);
        interceptor.intercept(null, TestMapping.class.getMethod("getField1"), null, null);
        verify(document).getFieldValue("field1");
    }

    @Test
    public void createCallback_callCallbackWithoutMappingAnnotation_verifyThatDocumentIsNotUsed() throws Throwable {
        MethodInterceptor interceptor = (MethodInterceptor) factory.createCallbackForDocument(response, document);
        interceptor.intercept(null, this.getClass().getMethod("setUp"), null, null);
        verifyZeroInteractions(document);
    }
}
