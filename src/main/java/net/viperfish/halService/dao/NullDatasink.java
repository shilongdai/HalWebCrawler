package net.viperfish.halService.dao;

import net.viperfish.crawler.core.Datasink;

public class NullDatasink implements Datasink<Site> {

	private boolean isClosed = false;

	@Override
	public void write(Site data) {
		System.out.println("Crawled:" + data.getUrl().toExternalForm());
	}

	@Override
	public boolean isClosed() {
		return isClosed;
	}

	@Override
	public void close() {
		isClosed = true;
	}

	@Override
	public void init() {

	}
}
