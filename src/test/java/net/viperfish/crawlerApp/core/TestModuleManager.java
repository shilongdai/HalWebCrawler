package net.viperfish.crawlerApp.core;

import net.viperfish.crawler.core.Datasink;
import net.viperfish.crawler.html.HttpCrawlerHandler;
import net.viperfish.crawler.html.HttpFetcher;
import net.viperfish.crawler.html.RestrictionManager;
import net.viperfish.crawler.html.Site;
import net.viperfish.crawler.html.TagProcessor;
import net.viperfish.crawler.html.crawlChecker.BaseInMemCrawlHandler;
import net.viperfish.crawler.html.engine.ConcurrentHttpFetcher;
import net.viperfish.crawler.html.restrictions.UnrestrictiveRestrictionManager;
import net.viperfish.crawler.html.tagProcessors.TextSectionProcessor;
import net.viperfish.crawlerApp.exceptions.ModuleLoadingException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestModuleManager {

	private static ModuleManager manager;

	@BeforeClass
	public static void init() throws ModuleLoadingException {
		manager = new ModuleManager(new StubModuleLoader());
		manager.register(new StubModule());
	}

	@Test
	public void testGetTagProcessor() throws Exception {
		ResolvedComponent<TagProcessor> textSection = manager.getTagProcessor("text_section");
		Assert.assertTrue(textSection.getComponent() instanceof TextSectionProcessor);
		Assert.assertEquals("text_section", textSection.getName());
		Assert.assertEquals(null, textSection.getDatasinkDependency());
		Assert.assertEquals(null, textSection.getFetcherDependency());
		Assert.assertEquals(null, textSection.getRestrictionManagerDependency());
	}

	@Test
	public void testGetHttpHandler() throws Exception {
		ResolvedComponent<HttpCrawlerHandler> textSection = manager
			.getHttpHandler("inMem_crawlChecker");
		Assert.assertTrue(textSection.getComponent() instanceof BaseInMemCrawlHandler);
		Assert.assertEquals("inMem_crawlChecker", textSection.getName());
		Assert.assertEquals(null, textSection.getDatasinkDependency());
		Assert.assertEquals(null, textSection.getFetcherDependency());
		Assert.assertEquals(null, textSection.getRestrictionManagerDependency());
	}

	@Test
	public void testGetDatasink() throws Exception {
		ResolvedComponent<Datasink<? super Site>> textSection = manager.newDatasink("xml_sink");
		Assert.assertTrue(textSection.getComponent() instanceof Datasink);
		Assert.assertEquals("xml_sink", textSection.getName());
		Assert.assertEquals(null, textSection.getDatasinkDependency());
		Assert.assertEquals(null, textSection.getFetcherDependency());
		Assert.assertEquals(null, textSection.getRestrictionManagerDependency());
	}

	@Test
	public void testGetRestrictionManager() throws Exception {
		ResolvedComponent<RestrictionManager> textSection = manager
			.getRestrictionManager("yesManager");
		Assert.assertTrue(textSection.getComponent() instanceof UnrestrictiveRestrictionManager);
		Assert.assertEquals("yesManager", textSection.getName());
		Assert.assertEquals(null, textSection.getDatasinkDependency());
		Assert.assertEquals(null, textSection.getFetcherDependency());
		Assert.assertEquals(null, textSection.getRestrictionManagerDependency());
	}

	@Test
	public void testGetHttpFetcherWithDeps() throws Exception {
		ResolvedComponent<HttpFetcher> fetcher = manager.getHttpFetcher("concurrentFetcher");

		Assert.assertTrue(fetcher.getComponent() instanceof ConcurrentHttpFetcher);
		Assert.assertEquals("concurrentFetcher", fetcher.getName());
		Assert.assertEquals(null, fetcher.getDatasinkDependency());
		Assert.assertEquals(null, fetcher.getFetcherDependency());

		ResolvedComponent<RestrictionManager> managerDep = fetcher
			.getRestrictionManagerDependency();
		Assert.assertEquals("yesManager", managerDep.getName());
		Assert.assertTrue(managerDep.getComponent() instanceof UnrestrictiveRestrictionManager);
		Assert.assertEquals(null, managerDep.getDatasinkDependency());
		Assert.assertEquals(null, managerDep.getFetcherDependency());
		Assert.assertEquals(null, managerDep.getRestrictionManagerDependency());
	}
}
