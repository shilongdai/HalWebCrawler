package net.viperfish.crawlerApp.core;

import java.util.Collection;
import javax.xml.ws.spi.http.HttpHandler;
import net.viperfish.crawler.core.Datasink;
import net.viperfish.crawler.html.HttpFetcher;
import net.viperfish.crawler.html.RestrictionManager;
import net.viperfish.crawler.html.Site;
import net.viperfish.crawler.html.TagProcessor;
import net.viperfish.crawlerApp.exceptions.ModuleLoadingException;
import net.viperfish.crawlerApp.exceptions.ModuleUnloadingException;

public interface CrawlerModule {

	String getName();

	Collection<String> getTagProcessors();

	Collection<String> getHttpHandlers();

	Collection<String> getDataSinks();

	Collection<String> getRestrictionManagers();

	Collection<String> getHttpFetchers();

	TagProcessor getTagProcessor(String name) throws Exception;

	HttpHandler getHttpHandler(String name) throws Exception;

	HttpFetcher getHttpFetcher(String name) throws Exception;

	Datasink<? super Site> newDatasink(String name) throws Exception;

	RestrictionManager getRestrictionManager(String name) throws Exception;

	void configTagProcessor(String name);

	void configHttpHandler(String name);

	void configHttpFetcher(String name);

	void configDatasink(String name);

	void configRestrictionManager(String name);

	void init() throws ModuleLoadingException;

	void cleanup() throws ModuleUnloadingException;
}
