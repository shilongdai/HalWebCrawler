package net.viperfish.crawlerApp.core;

import java.util.Collection;
import net.viperfish.crawler.core.Datasink;
import net.viperfish.crawler.html.CrawledData;
import net.viperfish.crawler.html.HttpCrawlerHandler;
import net.viperfish.crawler.html.HttpFetcher;
import net.viperfish.crawler.html.RestrictionManager;
import net.viperfish.crawlerApp.exceptions.ComponentResolutionException;
import net.viperfish.crawlerApp.exceptions.ModuleLoadingException;
import net.viperfish.crawlerApp.exceptions.ModuleUnloadingException;

public interface CrawlerModule {

	String getName();

	Collection<String> getComponents();

	Collection<String> getTagProcessors();

	Collection<String> getHttpHandlers();

	Collection<String> getDataSinks();

	Collection<String> getRestrictionmanagers();

	Collection<String> getHttpFetchers();

	Component<Datasink<? super CrawledData>> getDatasink(String name)
		throws ComponentResolutionException;

	Component<HttpFetcher> getFetcher(String name) throws ComponentResolutionException;

	Component<RestrictionManager> getRestrictionManager(String name)
		throws ComponentResolutionException;

	Component<HttpCrawlerHandler> getHandler(String name) throws ComponentResolutionException;

	Component<?> getComponent(String name) throws ComponentResolutionException;

	void config();

	void init() throws ModuleLoadingException;

	void cleanup() throws ModuleUnloadingException;
}
