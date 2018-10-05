package net.viperfish.crawlerApp.core;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import net.viperfish.crawler.core.Datasink;
import net.viperfish.crawler.html.HttpCrawlerHandler;
import net.viperfish.crawler.html.HttpFetcher;
import net.viperfish.crawler.html.RestrictionManager;
import net.viperfish.crawler.html.Site;
import net.viperfish.crawler.html.TagProcessor;
import net.viperfish.crawler.html.crawlChecker.BaseInMemCrawlHandler;
import net.viperfish.crawler.html.engine.ConcurrentHttpFetcher;
import net.viperfish.crawler.html.restrictions.UnrestrictiveRestrictionManager;
import net.viperfish.crawler.html.tagProcessors.ALinkTagProcessor;
import net.viperfish.crawler.html.tagProcessors.TextSectionProcessor;
import net.viperfish.crawlerApp.exceptions.UnsupportedComponentException;
import net.viperfish.crawlerApp.xmlDao.XMLDataSink;

public class StubModule implements CrawlerModule {

	private String xmlSinkDir;

	@Override
	public String getName() {
		return "test_module";
	}

	@Override
	public Collection<String> getTagProcessors() {
		return Arrays.asList("text_section", "link");
	}

	@Override
	public Collection<String> getHttpHandlers() {
		return Arrays.asList("inMem_crawlChecker");
	}

	@Override
	public Collection<String> getDataSinks() {
		return Arrays.asList("xml_sink");
	}

	@Override
	public Collection<String> getRestrictionmanagers() {
		return Arrays.asList("yesManager");
	}

	@Override
	public Collection<String> getHttpFetchers() {
		return Arrays.asList("concurrentFetcher");
	}

	@Override
	public TagProcessor getTagProcessor(String name,
		Map<DependencyType, ResolvedComponent<?>> dependencies) throws Exception {
		if (name.equals("text_section")) {
			return new TextSectionProcessor();
		}
		if (name.equals("link")) {
			return new ALinkTagProcessor();
		}
		throw new UnsupportedComponentException(name);
	}

	@Override
	public HttpCrawlerHandler getHttpHandler(String name,
		Map<DependencyType, ResolvedComponent<?>> dependencies) throws Exception {
		if (name.equals("inMem_crawlChecker")) {
			return new BaseInMemCrawlHandler();
		}
		throw new UnsupportedComponentException(name);
	}

	@Override
	public HttpFetcher getHttpFetcher(String name,
		Map<DependencyType, ResolvedComponent<?>> dependencies) throws Exception {
		if (name.equals("concurrentFetcher")) {
			return new ConcurrentHttpFetcher(1,
				(RestrictionManager) dependencies.get(DependencyType.RESTRICTION_MGR)
					.getComponent());
		}
		throw new UnsupportedComponentException(name);
	}

	@Override
	public Datasink<? super Site> newDatasink(String name,
		Map<DependencyType, ResolvedComponent<?>> dependencies) throws Exception {
		if (name.equals("xml_sink")) {
			return new XMLDataSink(xmlSinkDir);
		}
		throw new UnsupportedComponentException(name);
	}

	@Override
	public RestrictionManager getRestrictionManager(String name,
		Map<DependencyType, ResolvedComponent<?>> dependencies) throws Exception {
		if (name.equals("yesManager")) {
			return new UnrestrictiveRestrictionManager();
		}
		throw new UnsupportedComponentException(name);
	}

	@Override
	public Map<DependencyType, String> getTagProcessorDependencies(String name) {
		return new HashMap<>();
	}

	@Override
	public Map<DependencyType, String> getHttpHandlerDependencies(String name) {
		return new HashMap<>();
	}

	@Override
	public Map<DependencyType, String> getHttpFetcherDependencies(String name) {
		Map<DependencyType, String> result = new HashMap<>();
		result.put(DependencyType.RESTRICTION_MGR, "yesManager");
		return result;
	}

	@Override
	public Map<DependencyType, String> getDatasinkDependencies(String name) {
		return new HashMap<>();
	}

	@Override
	public Map<DependencyType, String> getRestrictionManagerDependencies(String name) {
		return new HashMap<>();
	}

	@Override
	public void config() {

	}


	@Override
	public void init() {
		xmlSinkDir = "out";
	}

	@Override
	public void cleanup() {

	}
}
