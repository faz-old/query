package de.faz.modules.query.solr;

import java.lang.reflect.Method;

import de.faz.modules.query.solr.internal.SolrResponseCallbackFactory;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;

import de.faz.modules.query.MapToField;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
public class StandardCallbackFactory implements SolrResponseCallbackFactory {
    @Override
    public Callback createCallbackForDocument(final QueryResponse response, final SolrDocument document) {
        return new MethodInterceptor() {
            @Override
            public Object intercept(final Object o, final Method method, final Object[] objects, final MethodProxy methodProxy)
                    throws Throwable {
                MapToField mapping = method.getAnnotation(MapToField.class);
                if (mapping != null) {
                    return document.getFieldValue(mapping.value());
                }
                return null;
            }
        };
    }
}
