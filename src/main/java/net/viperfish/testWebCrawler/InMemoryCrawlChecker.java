package net.viperfish.testWebCrawler;

import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import net.viperfish.crawler.html.CrawlChecker;
import net.viperfish.crawler.html.Site;

public class InMemoryCrawlChecker implements CrawlChecker {

	private ConcurrentMap<URL, Boolean> tracker;

	public InMemoryCrawlChecker() {
		tracker = new ConcurrentHashMap<>();
	}

	@Override
	public boolean shouldCrawl(URL url, Site site) {
		return shouldCrawl(url);
	}

	@Override
	public boolean shouldCrawl(URL url) {
		return !tracker.containsKey(url);
	}

	@Override
	public boolean lock(URL url, Site s) {
		return tracker.putIfAbsent(url, true) == null;
	}
}
