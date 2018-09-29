package net.viperfish.crawlerApp.core;

import java.util.Collection;
import java.util.Map;
import net.viperfish.crawler.core.Datasink;
import net.viperfish.crawler.html.HttpCrawlerHandler;
import net.viperfish.crawler.html.HttpFetcher;
import net.viperfish.crawler.html.RestrictionManager;
import net.viperfish.crawler.html.Site;
import net.viperfish.crawler.html.TagProcessor;
import net.viperfish.crawlerApp.exceptions.ModuleLoadingException;
import net.viperfish.crawlerApp.exceptions.ModuleUnloadingException;
import net.viperfish.crawlerApp.exceptions.UnsupportedComponentException;

public interface CrawlerModule {

	String getName();

	Collection<String> getTagProcessors();

	Collection<String> getHttpHandlers();

	Collection<String> getDataSinks();

	Collection<String> getRestrictionManagers();

	Collection<String> getHttpFetchers();

	TagProcessor getTagProcessor(String name,
		Map<DependencyType, ResolvedComponent<?>> dependencies) throws Exception;

	HttpCrawlerHandler getHttpHandler(String name,
		Map<DependencyType, ResolvedComponent<?>> dependencies) throws Exception;

	HttpFetcher getHttpFetcher(String name, Map<DependencyType, ResolvedComponent<?>> dependencies)
		throws Exception;

	Datasink<? super Site> newDatasink(String name,
		Map<DependencyType, ResolvedComponent<?>> dependencies) throws Exception;

	RestrictionManager getRestrictionManager(String name,
		Map<DependencyType, ResolvedComponent<?>> dependencies) throws Exception;

	Map<DependencyType, String> getTagProcessorDependencies(String name)
		throws UnsupportedComponentException;

	Map<DependencyType, String> getHttpHandlerDependencies(String name)
		throws UnsupportedComponentException;

	Map<DependencyType, String> getHttpFetcherDependencies(String name)
		throws UnsupportedComponentException;

	Map<DependencyType, String> getDatasinkDependencies(String name)
		throws UnsupportedComponentException;

	Map<DependencyType, String> getRestrictionManagerDependencies(String name)
		throws UnsupportedComponentException;

	void config();

	void init() throws ModuleLoadingException;

	void cleanup() throws ModuleUnloadingException;
}
