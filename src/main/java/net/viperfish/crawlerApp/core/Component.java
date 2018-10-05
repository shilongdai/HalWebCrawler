package net.viperfish.crawlerApp.core;

import java.util.List;
import java.util.Map;

public interface Component<T> {

	String getName();

	T getObject();

	void init(Map<String, Component<?>> dependencies) throws Exception;

	void config();

	boolean isConfigured();

	boolean isInitialized();

	List<String> declareDependencies();

	void cleanup() throws Exception;
}
