package net.viperfish.crawlerApp.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import net.viperfish.crawler.core.Datasink;
import net.viperfish.crawler.core.ORMLiteDatabase;
import net.viperfish.crawler.html.HttpCrawlerHandler;
import net.viperfish.crawler.html.HttpFetcher;
import net.viperfish.crawler.html.RestrictionManager;
import net.viperfish.crawler.html.Site;
import net.viperfish.crawler.html.SiteDatabase;
import net.viperfish.crawler.html.TagProcessor;
import net.viperfish.crawler.html.crawlChecker.BaseDBCrawlHandler;
import net.viperfish.crawler.html.crawlChecker.BaseInMemCrawlHandler;
import net.viperfish.crawler.html.crawlChecker.NoCrawlChecker;
import net.viperfish.crawler.html.crawlChecker.YesCrawlChecker;
import net.viperfish.crawler.html.dao.SiteDatabaseImpl;
import net.viperfish.crawler.html.engine.ConcurrentHttpFetcher;
import net.viperfish.crawler.html.restrictions.RobotsTxtRestrictionManager;
import net.viperfish.crawler.html.restrictions.UnrestrictiveRestrictionManager;
import net.viperfish.crawler.html.tagProcessors.ALinkTagProcessor;
import net.viperfish.crawler.html.tagProcessors.EmphasizedTagProcessor;
import net.viperfish.crawler.html.tagProcessors.HeaderTagProcessor;
import net.viperfish.crawler.html.tagProcessors.TextSectionProcessor;
import net.viperfish.crawler.html.tagProcessors.TitileTagProcessor;
import net.viperfish.crawlerApp.exceptions.UnsupportedComponentException;
import net.viperfish.crawlerApp.xmlDao.XMLDataSink;

public class BuiltInModule implements CrawlerModule {

	private int fetcherThreadCount;
	private String mysqlConnectionURL;
	private String mysqlUsername;
	private String mysqlPassword;
	private String xmlOutputDir;
	private String userAgent;

	@Override
	public String getName() {
		return "builtins";
	}

	@Override
	public Collection<String> getTagProcessors() {
		List<String> supportedTagProcessor = new LinkedList<>();
		supportedTagProcessor.add("text_chunk");
		supportedTagProcessor.add("a_link");
		supportedTagProcessor.add("emphasized_text");
		supportedTagProcessor.add("headers");
		supportedTagProcessor.add("title");
		return supportedTagProcessor;
	}

	@Override
	public Collection<String> getHttpHandlers() {
		List<String> supportedCrawlHandler = new LinkedList<>();
		supportedCrawlHandler.add("memory_crawl_checker");
		supportedCrawlHandler.add("db_crawl_checker");
		supportedCrawlHandler.add("yes_man_crawl_checker");
		supportedCrawlHandler.add("no_crawl_checker");
		return supportedCrawlHandler;
	}

	@Override
	public Collection<String> getDataSinks() {
		List<String> supportedDatasinks = new LinkedList<>();
		supportedDatasinks.add("xml_output");
		supportedDatasinks.add("mysql_database");
		return supportedDatasinks;
	}

	@Override
	public Collection<String> getRestrictionmanagers() {
		List<String> supportedRestrictionMgr = new LinkedList<>();
		supportedRestrictionMgr.add("robots_txt");
		supportedRestrictionMgr.add("unrestrictive");
		return supportedRestrictionMgr;
	}

	@Override
	public Collection<String> getHttpFetchers() {
		List<String> supportedHttpFetcher = new LinkedList<>();
		supportedHttpFetcher.add("concurrent_iterative_fetcher");
		return supportedHttpFetcher;
	}

	@Override
	public TagProcessor getTagProcessor(String name,
		Map<DependencyType, ResolvedComponent<?>> dependencies) throws Exception {
		switch (name) {
			case "text_chunk": {
				return new TextSectionProcessor();
			}
			case "a_link": {
				return new ALinkTagProcessor();
			}
			case "emphasized_text": {
				return new EmphasizedTagProcessor();
			}
			case "headers": {
				return new HeaderTagProcessor();
			}
			case "title": {
				return new TitileTagProcessor();
			}
			default: {
				throw new UnsupportedComponentException(name);
			}
		}
	}

	@Override
	public HttpCrawlerHandler getHttpHandler(String name,
		Map<DependencyType, ResolvedComponent<?>> dependencies) throws Exception {
		switch (name) {
			case "memory_crawl_checker": {
				return new BaseInMemCrawlHandler();
			}
			case "db_crawl_checker": {
				SiteDatabase siteDB = (SiteDatabase) dependencies.get(DependencyType.DATA_SINK);
				return new BaseDBCrawlHandler(siteDB);
			}
			case "yes_man_crawl_checker": {
				return new YesCrawlChecker();
			}
			case "no_crawl_checker": {
				return new NoCrawlChecker();
			}
			default: {
				throw new UnsupportedComponentException(name);
			}
		}
	}

	@Override
	public HttpFetcher getHttpFetcher(String name,
		Map<DependencyType, ResolvedComponent<?>> dependencies) throws Exception {
		if (name.equals("concurrent_iterative_fetcher")) {
			return new ConcurrentHttpFetcher(fetcherThreadCount);
		}
		throw new UnsupportedComponentException(name);
	}

	@Override
	public Datasink<? super Site> newDatasink(String name,
		Map<DependencyType, ResolvedComponent<?>> dependencies) throws Exception {
		switch (name) {
			case "xml_output": {
				XMLDataSink sink = new XMLDataSink(xmlOutputDir);
				sink.init();
				return sink;
			}
			case "mysql_database": {
				if (ORMLiteDatabase.checkClosed()) {
					ORMLiteDatabase.connect(mysqlConnectionURL, mysqlUsername, mysqlPassword);
				}
				SiteDatabase siteDatabase = new SiteDatabaseImpl();
				siteDatabase.init();
				return siteDatabase;
			}
			default: {
				throw new UnsupportedComponentException(name);
			}
		}
	}

	@Override
	public RestrictionManager getRestrictionManager(String name,
		Map<DependencyType, ResolvedComponent<?>> dependencies) throws Exception {
		switch (name) {
			case "robots_txt": {
				return new RobotsTxtRestrictionManager(userAgent);
			}
			case "unrestrictive": {
				return new UnrestrictiveRestrictionManager();
			}
			default: {
				throw new UnsupportedComponentException(name);
			}
		}
	}

	@Override
	public Map<DependencyType, String> getTagProcessorDependencies(String name) {
		return new HashMap<>();
	}

	@Override
	public Map<DependencyType, String> getHttpHandlerDependencies(String name) {
		if (name.equals("db_crawl_checker")) {
			Map<DependencyType, String> result = new HashMap<>();
			result.put(DependencyType.DATA_SINK, "mysql_database");
			return result;
		}
		return new HashMap<>();
	}

	@Override
	public Map<DependencyType, String> getHttpFetcherDependencies(String name) {
		return new HashMap<>();
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
		Scanner in = new Scanner(System.in);
		System.out.println("Configuring Builtin Modules");
		System.out.println(
			"For any feature that you will not use, feel free to skip over the configuration for that feature");
		System.out.println("Configuring XML Output");
		System.out.print("XML Output Directory:");
		xmlOutputDir = in.nextLine();
		System.out.println("Configuring MySQL Output");
		System.out.print("Connection URL:");
		mysqlConnectionURL = in.nextLine();
		System.out.print("MySQL User:");
		mysqlUsername = in.nextLine();
		System.out.print("MySQL Password:");
		mysqlPassword = in.nextLine();
		System.out.println("Configuring Concurrent Http Fetcher");
		System.out.print("Fetcher Thread Count:");
		String threadCount = in.nextLine();
		fetcherThreadCount = Integer.parseInt(threadCount);
		System.out.print("Fetcher User Agent:");
		userAgent = in.nextLine();
	}

	@Override
	public void init() {
		fetcherThreadCount = 1;
		userAgent = "halbot";
	}

	@Override
	public void cleanup() {
		if (!ORMLiteDatabase.checkClosed()) {
			ORMLiteDatabase.closeConn();
		}
	}
}
