package net.viperfish.testWebCrawler;

import java.io.IOException;
import java.net.URL;
import net.viperfish.crawler.html.BaseHttpWebCrawler;
import net.viperfish.crawler.html.engine.ConcurrentHttpFetcher;
import net.viperfish.crawler.html.tagProcessors.ALinkTagProcessor;
import net.viperfish.crawler.html.tagProcessors.EmphasizedTagProcessor;
import net.viperfish.crawler.html.tagProcessors.HeaderTagProcessor;
import net.viperfish.crawler.html.tagProcessors.TextSectionProcessor;
import net.viperfish.crawler.html.tagProcessors.TitileTagProcessor;
import xmlDao.XMLDataSink;

public class CrawlSite {

	public static void main(String argv[]) throws IOException {
		XMLDataSink out = new XMLDataSink("out");
		out.init();
		BaseHttpWebCrawler crawler = new BaseHttpWebCrawler(out,
			new ConcurrentHttpFetcher(6));
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
