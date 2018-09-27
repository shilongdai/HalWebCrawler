package net.viperfish.crawlerApp.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.xml.ws.spi.http.HttpHandler;
import net.viperfish.crawler.core.Datasink;
import net.viperfish.crawler.html.HttpFetcher;
import net.viperfish.crawler.html.RestrictionManager;
import net.viperfish.crawler.html.Site;
import net.viperfish.crawler.html.TagProcessor;
import net.viperfish.crawlerApp.exceptions.ModuleLoadingException;
import net.viperfish.crawlerApp.exceptions.ModuleUnloadingException;

public class ModuleManager implements CrawlerModule {

	private CrawlerModuleLoader loader;
	private Map<String, CrawlerModule> modules;

	public ModuleManager(CrawlerModuleLoader loader) {
		this.loader = loader;
		modules = new HashMap<>();
	}

	public void register(CrawlerModule module) throws ModuleLoadingException {
		module.init();
		modules.put(module.getName(), module);
	}

	public void unregister(String moduleName) throws ModuleUnloadingException {
		CrawlerModule m = modules.remove(moduleName);
		m.cleanup();
	}

	@Override
	public String getName() {
		return "module_manager";
	}

	@Override
	public Collection<String> getTagProcessors() {
		List<String> result = new LinkedList<>();
		for (CrawlerModule m : modules.values()) {
			result.addAll(m.getTagProcessors());
		}
		return result;
	}

	@Override
	public Collection<String> getHttpHandlers() {
		List<String> result = new LinkedList<>();
		for (CrawlerModule m : modules.values()) {
			result.addAll(m.getHttpHandlers());
		}
		return result;
	}

	@Override
	public Collection<String> getDataSinks() {
		List<String> result = new LinkedList<>();
		for (CrawlerModule m : modules.values()) {
			result.addAll(m.getDataSinks());
		}
		return result;
	}

	@Override
	public Collection<String> getRestrictionManagers() {
		List<String> result = new LinkedList<>();
		for (CrawlerModule m : modules.values()) {
			result.addAll(m.getRestrictionManagers());
		}
		return result;
	}

	@Override
	public Collection<String> getHttpFetchers() {
		List<String> result = new LinkedList<>();
		for (CrawlerModule m : modules.values()) {
			result.addAll(m.getHttpFetchers());
		}
		return result;
	}

	@Override
	public TagProcessor getTagProcessor(String name) throws Exception {
		for (CrawlerModule m : modules.values()) {
			TagProcessor result = m.getTagProcessor(name);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

	@Override
	public HttpHandler getHttpHandler(String name) throws Exception {
		for (CrawlerModule m : modules.values()) {
			HttpHandler result = m.getHttpHandler(name);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

	@Override
	public HttpFetcher getHttpFetcher(String name) throws Exception {
		for (CrawlerModule m : modules.values()) {
			HttpFetcher result = m.getHttpFetcher(name);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

	@Override
	public Datasink<? super Site> newDatasink(String name) throws Exception {
		for (CrawlerModule m : modules.values()) {
			Datasink<? super Site> result = m.newDatasink(name);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

	@Override
	public RestrictionManager getRestrictionManager(String name) throws Exception {
		for (CrawlerModule m : modules.values()) {
			RestrictionManager result = m.getRestrictionManager(name);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

	@Override
	public void configTagProcessor(String name) {
		for (CrawlerModule m : modules.values()) {
			m.configTagProcessor(name);
		}
	}

	@Override
	public void configHttpHandler(String name) {
		for (CrawlerModule m : modules.values()) {
			m.configHttpHandler(name);
		}
	}

	@Override
	public void configHttpFetcher(String name) {
		for (CrawlerModule m : modules.values()) {
			m.configHttpFetcher(name);
		}
	}

	@Override
	public void configDatasink(String name) {
		for (CrawlerModule m : modules.values()) {
			m.configTagProcessor(name);
		}
	}

	@Override
	public void configRestrictionManager(String name) {
		for (CrawlerModule m : modules.values()) {
			m.configRestrictionManager(name);
		}
	}

	@Override
	public void init() throws ModuleLoadingException {
		for (CrawlerModule m : loader.loadModules()) {
			register(m);
		}
	}

	@Override
	public void cleanup() throws ModuleUnloadingException {
		for (String m : modules.keySet()) {
			unregister(m);
		}
	}
}
