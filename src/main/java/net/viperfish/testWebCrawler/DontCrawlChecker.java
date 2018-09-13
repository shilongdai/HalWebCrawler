package net.viperfish.testWebCrawler;

import java.net.URL;
import net.viperfish.crawler.html.CrawlChecker;
import net.viperfish.crawler.html.Site;

public class DontCrawlChecker implements CrawlChecker {

	@Override
	public boolean shouldCrawl(URL url, Site site) {
		return false;
	}

	@Override
	public boolean shouldCrawl(URL url) {
		return false;
	}

	@Override
	public boolean lock(URL url, Site s) {
		return false;
	}
}
