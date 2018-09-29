package net.viperfish.crawlerApp.core;

import java.util.LinkedList;
import java.util.List;

public class StubModuleLoader implements CrawlerModuleLoader {

	@Override
	public List<CrawlerModule> loadModules() {
		return new LinkedList<>();
	}
}
