package de.faz.modules.query.polopoly;

/**
 * @author Andreas Kaubisch <a.kaubisch@faz.de>
 * @since $rev$
 */

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polopoly.application.Application;
import com.polopoly.search.solr.QueryDecorator;
import com.polopoly.search.solr.SolrClientImpl;
import com.polopoly.search.solr.SolrSearchClient;

import de.faz.modules.query.SearchContext;
import de.faz.modules.query.SearchDecorator;
import de.faz.modules.query.polopoly.filter.NeedIndexingFilter;
import de.faz.modules.query.solr.SolrSearchContextFactory;

import javax.annotation.Nullable;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
public class PolopolySearchContextFactory {

	private static final Logger LOG = LoggerFactory.getLogger(SolrSearchContextFactory.class);

	private SolrSearchClient client;

	public PolopolySearchContextFactory(Application application) {
		client = (SolrSearchClient)application.getApplicationComponent(SolrSearchClient.DEFAULT_COMPOUND_NAME);
	}

	public SearchContext createContext() {
		return createContext(client);
	}

	public static SearchContext createContext(SolrSearchClient client) {
		SearchContext context;
		if(client != null && client.getServiceControl() instanceof SolrClientImpl) {
			SolrClientImpl solrClient = (SolrClientImpl)client.getServiceControl();
			context = SolrSearchContextFactory.createSearchContext(solrClient.getSolrServer());
			appendSearchDecoratorsTo(solrClient, context);
		} else {
			context = SolrSearchContextFactory.createSearchContext(null);
		}

		return context;
	}

	private static void appendSearchDecoratorsTo(SolrClientImpl client, SearchContext context) {
		for(QueryDecorator decorator : client.getQueryDecorators()) {
			if(decorator instanceof SearchDecorator) {
				SearchDecorator instance = createDecoratorFromClass((Class<? extends SearchDecorator>) decorator.getClass(), context);
				if(instance != null) {
					context.addSearchDecorator(instance);
				}
			}
		}

		context.addSearchDecorator(new NeedIndexingFilter(context));
	}

	@Nullable
	private static SearchDecorator createDecoratorFromClass(Class<? extends SearchDecorator> decoratorClass, SearchContext context) {
		SearchDecorator instance = null;
		try {
			Constructor<? extends SearchDecorator> constructor = decoratorClass.getConstructor(SearchContext.class);
			instance = constructor.newInstance(context);
		} catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
			LOG.info("unable to create new instance of class {}", decoratorClass.getName());
		}

		return instance;
	}

	private static QueryDecorator[] getDecorators(SolrClientImpl client) {
		List<QueryDecorator> decoratorList = new ArrayList<>(client.getQueryDecorators());
		decoratorList.add(new QueryDecorator() {
			@Override
			public SolrQuery decorate(final SolrQuery solrQuery) {
				return solrQuery.addFilterQuery("visibleOnline:true", "-p.needsIndexing_b:true");
			}
		});

		return decoratorList.toArray(new QueryDecorator[0]);
	}
}
