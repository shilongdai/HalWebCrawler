package net.viperfish.testWebCrawler;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import net.viperfish.crawler.base.BaseHttpWebCrawler;
import net.viperfish.crawler.core.Site;
import net.viperfish.crawler.core.SiteDatabase;
import net.viperfish.crawler.crawlChecker.URLCrawlChecker;
import net.viperfish.crawler.dao.AnchorDatabase;
import net.viperfish.crawler.dao.EmphasizedTextDatabase;
import net.viperfish.crawler.dao.HeaderDatabase;
import net.viperfish.crawler.dao.ORMLiteDatabase;
import net.viperfish.crawler.dao.SiteDatabaseImpl;
import net.viperfish.crawler.dao.TextContentDatabase;
import net.viperfish.crawler.engines.ConcurrentHttpFetcher;
import net.viperfish.crawler.html.tagProcessors.ALinkTagProcessor;
import net.viperfish.crawler.html.tagProcessors.EmphasizedTagProcessor;
import net.viperfish.crawler.html.tagProcessors.HeaderTagProcessor;
import net.viperfish.crawler.html.tagProcessors.TextAllTagProcessor;
import net.viperfish.crawler.html.tagProcessors.TextOwnTagsProcessor;
import net.viperfish.crawler.html.tagProcessors.TitileTagProcessor;

public class CrawlSite {

	public static void main(String argv[]) throws SQLException, IOException {
		ORMLiteDatabase.connect(argv[1], argv[2], argv[3]);
		SiteDatabase db = (SiteDatabase) new SiteDatabaseImpl(new HeaderDatabase().connect(),
			new TextContentDatabase().connect(), new EmphasizedTextDatabase().connect()).connect();
		BaseHttpWebCrawler crawler = new BaseHttpWebCrawler(db, new AnchorDatabase().connect(),
			new ConcurrentHttpFetcher(1));
		crawler.setCrawlChecker(new URLCrawlChecker(db));
		crawler.limitToHost(true);
		crawler.registerProcessor("a", new ALinkTagProcessor());
		crawler.registerProcessor("title", new TitileTagProcessor());
		crawler.registerProcessor("p", new TextAllTagProcessor());
		crawler.registerProcessor("div", new TextOwnTagsProcessor());
		crawler.registerProcessor("blockquote", new TextOwnTagsProcessor());
		crawler.registerProcessor("ul", new TextAllTagProcessor());
		crawler.registerProcessor("ol", new TextAllTagProcessor());
		crawler.registerProcessor("pre", new TextOwnTagsProcessor());
		crawler.registerProcessor("dl", new TextAllTagProcessor());
		crawler.registerProcessor("table", new TextAllTagProcessor());
		crawler.registerProcessor("h1", new HeaderTagProcessor());
		crawler.registerProcessor("h2", new HeaderTagProcessor());
		crawler.registerProcessor("h3", new HeaderTagProcessor());
		crawler.registerProcessor("h4", new HeaderTagProcessor());
		crawler.registerProcessor("h5", new HeaderTagProcessor());
		crawler.registerProcessor("h6", new HeaderTagProcessor());
		crawler.registerProcessor("h7", new HeaderTagProcessor());
		crawler.registerProcessor("em", new EmphasizedTagProcessor());
		crawler.registerProcessor("b", new EmphasizedTagProcessor());
		crawler.registerProcessor("strong", new EmphasizedTagProcessor());
		crawler.submit(new URL(argv[0]));
		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {
			return;
		}
		while (!crawler.isIdle()) {
			Long crawledID;
			try {
				crawledID = crawler.getResults().take();
			} catch (InterruptedException e) {
				return;
			}
			Site crawled = db.find(crawledID);
			if (crawled == null) {
				System.out.println("Crawled ID Not in DB:" + crawledID);
				continue;
			}
			System.out.println("Crawled:" + crawled.getUrl());
		}
		crawler.shutdown();
		ORMLiteDatabase.closeConn();
	}

}
