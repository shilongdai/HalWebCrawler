package net.viperfish.testWebCrawler;

import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import net.viperfish.crawler.html.CrawlChecker;
import net.viperfish.crawler.html.Site;

public class InMemoryCrawlChecker implements CrawlChecker {

	private ConcurrentMap<URL, Boolean> tracker;
	private ConcurrentMap<String, Boolean> hashTracker;

	public InMemoryCrawlChecker() {
		tracker = new ConcurrentHashMap<>();
		hashTracker = new ConcurrentHashMap<>();
	}

	@Override
	public boolean shouldCrawl(URL url, Site site) {
		return shouldCrawl(url) && !hashTracker.containsKey(site.getChecksum());
	}

	@Override
	public boolean shouldCrawl(URL url) {
		return !tracker.containsKey(url);
	}

	@Override
	public boolean lock(URL url, Site s) {
		return tracker.putIfAbsent(url, true) == null
			&& hashTracker.putIfAbsent(s.getChecksum(), true) == null;
	}
}
