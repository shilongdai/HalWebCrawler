package net.viperfish.crawlerApp.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.viperfish.crawler.core.Datasink;
import net.viperfish.crawler.html.CrawledData;
import net.viperfish.crawler.html.HttpCrawlerHandler;
import net.viperfish.crawler.html.HttpFetcher;
import net.viperfish.crawler.html.RestrictionManager;
import net.viperfish.crawlerApp.exceptions.ComponentResolutionException;
import net.viperfish.crawlerApp.exceptions.ConflictingDependenciesException;
import net.viperfish.crawlerApp.exceptions.DependencyException;
import net.viperfish.crawlerApp.exceptions.ModuleLoadingException;
import net.viperfish.crawlerApp.exceptions.ModuleUnloadingException;
import net.viperfish.crawlerApp.exceptions.UnsupportedComponentException;

// TODO: Add resolution for circular dependencies

public class ModuleManager implements CrawlerModule {

	private static String SINGLETON_DATASINk = "__datasink";
	private static String SINGLETON_FETCHER = "__fetcher";

	private CrawlerModuleLoader loader;
	private Map<String, CrawlerModule> modules;
	private Map<String, Component<?>> sessionTracker;
	private boolean inSession;

	public ModuleManager(CrawlerModuleLoader loader) {
		this.loader = loader;
		modules = new HashMap<>();
		sessionTracker = new HashMap<>();
		inSession = false;
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
	public Collection<String> getRestrictionmanagers() {
		List<String> result = new LinkedList<>();
		for (CrawlerModule m : modules.values()) {
			result.addAll(m.getRestrictionmanagers());
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
	public Collection<String> getComponents() {
		List<String> result = new LinkedList<>();
		for (CrawlerModule m : modules.values()) {
			result.addAll(m.getComponents());
		}
		return result;
	}

	@Override
	public Component<Datasink<? super CrawledData>> getDatasink(String name)
		throws ComponentResolutionException {
		for (CrawlerModule m : this.modules.values()) {
			if (m.getDataSinks().contains(name)) {
				Component<Datasink<? super CrawledData>> result = m.getDatasink(name);
				resolveRoot(result);
			}
		}
		throw new UnsupportedComponentException(name);
	}

	@Override
	public Component<HttpFetcher> getFetcher(String name) throws ComponentResolutionException {
		for (CrawlerModule m : this.modules.values()) {
			if (m.getHttpFetchers().contains(name)) {
				Component<HttpFetcher> component = m.getFetcher(name);
				resolveRoot(component);
				return component;
			}
		}
		throw new UnsupportedComponentException(name);
	}

	@Override
	public Component<RestrictionManager> getRestrictionManager(String name)
		throws ComponentResolutionException {
		for (CrawlerModule m : this.modules.values()) {
			if (m.getRestrictionmanagers().contains(name)) {
				Component<RestrictionManager> component = m.getRestrictionManager(name);
				resolveRoot(component);
				return component;
			}
		}
		throw new UnsupportedComponentException(name);
	}

	@Override
	public Component<HttpCrawlerHandler> getHandler(String name)
		throws ComponentResolutionException {
		for (CrawlerModule m : this.modules.values()) {
			if (m.getHttpHandlers().contains(name)) {
				Component<HttpCrawlerHandler> component = m.getHandler(name);
				resolveRoot(component);
				return component;
			}
		}
		throw new UnsupportedComponentException(name);
	}

	@Override
	public Component<?> getComponent(String name) throws ComponentResolutionException {
		for (CrawlerModule m : this.modules.values()) {
			if (m.getComponents().contains(name)) {
				return m.getComponent(name);
			}
		}
		throw new UnsupportedComponentException(name);
	}

	@Override
	public void config() {
		for (CrawlerModule m : modules.values()) {
			m.config();
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

	public void beginSession() {
		if (!inSession) {
			sessionTracker = new HashMap<>();
			inSession = true;
		}

	}

	public void endSession() {
		sessionTracker.clear();
		inSession = false;
	}

	public Map<String, Component<?>> getSessionComponents() {
		return new HashMap<>(sessionTracker);
	}

	private void resolveRoot(Component<?> component) throws ComponentResolutionException {
		for (String name : component.declareDependencies()) {
			resolveDependencies(component.getName());
		}
	}

	private Component<?> resolveDependencies(String name)
		throws ComponentResolutionException {
		try {
			Component<?> component = this.getComponent(name);
			List<String> compNames = component.declareDependencies();
			for (String c : compNames) {
				if (!sessionTracker.containsKey(name)) {
					Component<?> dep = resolveDependencies(c);
					sessionTracker.put(dep.getName(), dep);
				} else {
					Component<?> existingDependency = sessionTracker.get(name);
					if (!existingDependency.getName().equals(name)) {
						throw new ConflictingDependenciesException(name);
					}
				}
			}
			return component;
		} catch (UnsupportedComponentException e) {
			throw new DependencyException(e);
		}
	}

}
