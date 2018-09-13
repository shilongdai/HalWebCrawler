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

	public static void main(String argv[]) throws IOException, InterruptedException {
		XMLDataSink out = new XMLDataSink("out");
		out.init();
		BaseHttpWebCrawler crawler = new BaseHttpWebCrawler(out,
			new ConcurrentHttpFetcher(1));
		crawler.setCrawlChecker(new InMemoryCrawlChecker());
		crawler.limitToHost(true);
		crawler.registerProcessor("a", new ALinkTagProcessor());
		crawler.registerProcessor("title", new TitileTagProcessor());
		crawler.registerProcessor("p", new TextSectionProcessor());
		crawler.registerProcessor("div", new TextSectionProcessor());
		crawler.registerProcessor("blockquote", new TextSectionProcessor());
		crawler.registerProcessor("pre", new TextSectionProcessor());
		crawler.registerProcessor("dl", new TextSectionProcessor());
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
		crawler.startCrawl();
		crawler.waitUntiDone();
		crawler.shutdown();
	}

}
