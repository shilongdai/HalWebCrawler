package net.viperfish.testWebCrawler;

import java.io.IOException;
import java.net.URL;
import net.viperfish.crawler.html.HttpWebCrawler;
import net.viperfish.crawler.html.RobotsTxtRestrictionManager;
import net.viperfish.crawler.html.engine.ConcurrentHttpFetcher;
import net.viperfish.crawler.html.tagProcessors.ALinkTagProcessor;
import net.viperfish.crawler.html.tagProcessors.EmphasizedTagProcessor;
import net.viperfish.crawler.html.tagProcessors.HeaderTagProcessor;
import net.viperfish.crawler.html.tagProcessors.TextSectionProcessor;
import net.viperfish.crawler.html.tagProcessors.TitileTagProcessor;
import xmlDao.XMLDataSink;

public class CrawlSite {

	public static void main(String argv[]) throws IOException, InterruptedException {
		argv = new String[]{"https://www.cryptopp.com"};
		XMLDataSink out = new XMLDataSink("out");
		RobotsTxtRestrictionManager restrictionManager = new RobotsTxtRestrictionManager("halbot");
		out.init();
		HttpWebCrawler crawler = new HttpWebCrawler(out,
			new ConcurrentHttpFetcher(6, restrictionManager));
		crawler.setCrawlChecker(new InMemoryCrawlChecker());
		crawler.limitToHost(true);
		crawler.registerProcessor("link", new ALinkTagProcessor());
		crawler.registerProcessor("title", new TitileTagProcessor());
		crawler.registerProcessor("textChunk", new TextSectionProcessor());
		crawler.registerProcessor("header", new HeaderTagProcessor());
		crawler.registerProcessor("emphasized", new EmphasizedTagProcessor());
		crawler.submit(new URL(argv[0]));
		crawler.startProcessing();
		crawler.waitUntiDone();
		crawler.shutdown();
	}

}
