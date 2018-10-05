package net.viperfish.crawlerApp.core;

import java.util.Collection;
import net.viperfish.crawler.core.Datasink;
import net.viperfish.crawler.html.HttpCrawlerHandler;
import net.viperfish.crawler.html.HttpFetcher;
import net.viperfish.crawler.html.RestrictionManager;
import net.viperfish.crawler.html.Site;
import net.viperfish.crawlerApp.exceptions.ModuleLoadingException;
import net.viperfish.crawlerApp.exceptions.ModuleUnloadingException;
import net.viperfish.crawlerApp.exceptions.UnsupportedComponentException;

public interface CrawlerModule {

	String getName();

	Collection<String> getComponents();

	Collection<String> getTagProcessors();

	Collection<String> getHttpHandlers();

	Collection<String> getDataSinks();

	Collection<String> getRestrictionmanagers();

	Collection<String> getHttpFetchers();

	Component<Datasink<? extends Site>> getDatasink(String name)
		throws UnsupportedComponentException;

	Component<HttpFetcher> getFetcher(String name) throws UnsupportedComponentException;

	Component<RestrictionManager> getRestrictionManager(String name)
		throws UnsupportedComponentException;

	Component<HttpCrawlerHandler> getHandler(String name) throws UnsupportedComponentException;

	Component<?> getComponent(String name) throws UnsupportedComponentException;

	void config();

	void init() throws ModuleLoadingException;

	void cleanup() throws ModuleUnloadingException;
}
