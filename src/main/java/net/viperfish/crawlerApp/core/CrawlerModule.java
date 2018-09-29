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

/**
 * A module containing components for the HalCrawler web crawler library. It can be seen as a
 * provider for providing implementations. Implementations does not have to be thread safe.
 */
public interface CrawlerModule {

	/**
	 * gets the name of the module.
	 *
	 * @return the name of the module.
	 */
	String getName();

	/**
	 * gets the names of all the supported TagProcessor.
	 *
	 * @return the names of TagProcessors.
	 */
	Collection<String> getTagProcessors();

	/**
	 * gets the names of all the supported HttpCrawlerHandler
	 *
	 * @return the name of all the supported HttpCrawlerHandler
	 */
	Collection<String> getHttpHandlers();

	/**
	 * gets the names of all the supported Datasink.
	 *
	 * @return the name of all supported Datasinks.
	 */
	Collection<String> getDataSinks();

	/**
	 * gets the name of all the supported RestrictionManagers.
	 *
	 * @return the names of all supported RestrictionManagers.
	 */
	Collection<String> getRestrictionManagers();

	/**
	 * gets the names of all the supported HttpFetcher.
	 *
	 * @return the names of all supported HttpFetcher.
	 */
	Collection<String> getHttpFetchers();

	/**
	 * gets an instance of a TagProcessor by its name.
	 *
	 * @param name the name of the TagProcessor to request
	 * @param dependencies the dependencies for the requested TagProcessor
	 * @return an instance of the requested TagProcessor
	 * @throws Exception if failed to acquire the TagProcessor
	 * @throws UnsupportedComponentException if the component is not supported.
	 */
	TagProcessor getTagProcessor(String name,
		Map<DependencyType, ResolvedComponent<?>> dependencies) throws Exception;

	/**
	 * gets an instance of a HttpCrawlerHandler by its name.
	 *
	 * @param name the name of the HttpCrawlerHandler to request
	 * @param dependencies the dependencies for the requested HttpCrawlerHandler
	 * @return an instance of the requested HttpCrawlerHandler
	 * @throws Exception if failed to acquire the HttpCrawlerHandler
	 * @throws UnsupportedComponentException if the component is not supported.
	 */
	HttpCrawlerHandler getHttpHandler(String name,
		Map<DependencyType, ResolvedComponent<?>> dependencies) throws Exception;

	/**
	 * gets an instance of a HttpFetcher by its name.
	 *
	 * @param name the name of the HttpFetcher to request
	 * @param dependencies the dependencies for the requested HttpFetcher
	 * @return an instance of the requested HttpFetcher
	 * @throws Exception if failed to acquire the HttpFetcher
	 * @throws UnsupportedComponentException if the component is not supported.
	 */
	HttpFetcher getHttpFetcher(String name, Map<DependencyType, ResolvedComponent<?>> dependencies)
		throws Exception;

	/**
	 * gets an instance of a Datasink by its name.
	 *
	 * @param name the name of the Datasink to request
	 * @param dependencies the dependencies for the requested Datasink
	 * @return an instance of the requested Datasink
	 * @throws Exception if failed to acquire the Datasink
	 * @throws UnsupportedComponentException if the component is not supported.
	 */
	Datasink<? super Site> newDatasink(String name,
		Map<DependencyType, ResolvedComponent<?>> dependencies) throws Exception;

	/**
	 * gets an instance of a RestrictionManager by its name.
	 *
	 * @param name the name of the RestrictionManager to request
	 * @param dependencies the dependencies for the requested RestrictionManageer
	 * @return an instance of the requested RestrictionManager
	 * @throws Exception if failed to acquire the RestrictionManager
	 * @throws UnsupportedComponentException if the component is not supported.
	 */
	RestrictionManager getRestrictionManager(String name,
		Map<DependencyType, ResolvedComponent<?>> dependencies) throws Exception;

	/**
	 * declares all the dependencies for a specified TagProcessor.
	 *
	 * @param name the name of the TagProcessor
	 * @return the dependencies required by the TagProcessor
	 * @throws UnsupportedComponentException if no such component exists.
	 */
	Map<DependencyType, String> getTagProcessorDependencies(String name)
		throws UnsupportedComponentException;

	/**
	 * declares all the dependencies for a specified HttpCrawlerHandler.
	 *
	 * @param name the name of the HttpCrawlerHandler
	 * @return the dependencies required by the HttpCrawlerHandler
	 * @throws UnsupportedComponentException if no such component exists.
	 */
	Map<DependencyType, String> getHttpHandlerDependencies(String name)
		throws UnsupportedComponentException;

	/**
	 * declares all the dependencies for a specified HttpFetcher.
	 *
	 * @param name the name of the HttpFetcher
	 * @return the dependencies required by the HttpFetcher
	 * @throws UnsupportedComponentException if no such component exists.
	 */
	Map<DependencyType, String> getHttpFetcherDependencies(String name)
		throws UnsupportedComponentException;

	/**
	 * declares all the dependencies for a specified Datasink.
	 *
	 * @param name the name of the Datasink
	 * @return the dependencies required by the Datasink
	 * @throws UnsupportedComponentException if no such component exists.
	 */
	Map<DependencyType, String> getDatasinkDependencies(String name)
		throws UnsupportedComponentException;

	/**
	 * declares all the dependencies for a specified RestrictionManager.
	 *
	 * @param name the name of the RestrictionManager
	 * @return the dependencies required by the RestrictionManager
	 * @throws UnsupportedComponentException if no such component exists.
	 */
	Map<DependencyType, String> getRestrictionManagerDependencies(String name)
		throws UnsupportedComponentException;

	/**
	 * configures the module. It can prompt use input. As of now, it should use the console.
	 */
	void config();

	/**
	 * initializes the module.
	 *
	 * @throws ModuleLoadingException if failed to initialize
	 */
	void init() throws ModuleLoadingException;

	/**
	 * close all resources used by the module.
	 *
	 * @throws ModuleUnloadingException if fail to close the resources.
	 */
	void cleanup() throws ModuleUnloadingException;
}
