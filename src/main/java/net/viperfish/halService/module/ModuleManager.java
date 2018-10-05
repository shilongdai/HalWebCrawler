package net.viperfish.halService.module;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.viperfish.crawler.core.Datasink;
import net.viperfish.crawler.html.HttpCrawlerHandler;
import net.viperfish.crawler.html.HttpFetcher;
import net.viperfish.crawler.html.RestrictionManager;
import net.viperfish.crawler.html.TagProcessor;
import net.viperfish.halService.exceptions.ConflictingDependenciesException;
import net.viperfish.halService.exceptions.DependencyException;
import net.viperfish.halService.exceptions.ModuleLoadingException;
import net.viperfish.halService.exceptions.ModuleUnloadingException;
import net.viperfish.halService.exceptions.UnsupportedComponentException;

// TODO: Add resolution for circular dependencies

public class ModuleManager implements CrawlerModule {

	private CrawlerModuleLoader loader;
	private Map<String, CrawlerModule> modules;
	private Map<DependencyType, ResolvedComponent<?>> currentSession;
	private boolean inSession;

	public ModuleManager(CrawlerModuleLoader loader) {
		this.loader = loader;
		modules = new HashMap<>();
		currentSession = new HashMap<>();
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
	public TagProcessor getTagProcessor(String name,
		Map<DependencyType, ResolvedComponent<?>> dependencies)
		throws Exception {
		for (CrawlerModule m : modules.values()) {
			if (m.getTagProcessors().contains(name)) {
				TagProcessor result = m.getTagProcessor(name, dependencies);
				return result;
			}
		}
		throw new UnsupportedComponentException(name);
	}

	@Override
	public HttpCrawlerHandler getHttpHandler(String name,
		Map<DependencyType, ResolvedComponent<?>> dependencies)
		throws Exception {
		for (CrawlerModule m : modules.values()) {
			if (m.getHttpHandlers().contains(name)) {
				HttpCrawlerHandler result = m.getHttpHandler(name, dependencies);
				return result;
			}
		}
		throw new UnsupportedComponentException(name);
	}

	@Override
	public HttpFetcher getHttpFetcher(String name,
		Map<DependencyType, ResolvedComponent<?>> dependencies)
		throws Exception {
		for (CrawlerModule m : modules.values()) {
			if (m.getHttpFetchers().contains(name)) {
				HttpFetcher result = m.getHttpFetcher(name, dependencies);
				return result;
			}
		}
		throw new UnsupportedComponentException(name);
	}

	@Override
	public Datasink<? super Site> newDatasink(String name,
		Map<DependencyType, ResolvedComponent<?>> dependencies)
		throws Exception {
		for (CrawlerModule m : modules.values()) {
			if (m.getDataSinks().contains(name)) {
				Datasink<? super Site> result = m.newDatasink(name, dependencies);
				return result;
			}
		}
		throw new UnsupportedComponentException(name);
	}

	@Override
	public RestrictionManager getRestrictionManager(String name,
		Map<DependencyType, ResolvedComponent<?>> dependencies) throws Exception {
		for (CrawlerModule m : modules.values()) {
			if (m.getRestrictionManagers().contains(name)) {
				RestrictionManager result = m.getRestrictionManager(name, dependencies);
				return result;
			}
		}
		throw new UnsupportedComponentException(name);
	}

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
		System.out.println(modules);
	}

	@Override
	public void cleanup() throws ModuleUnloadingException {
		for (String m : modules.keySet()) {
			unregister(m);
		}
	}

	@Override
	public Map<DependencyType, String> getTagProcessorDependencies(String name)
		throws UnsupportedComponentException {
		for (CrawlerModule m : modules.values()) {
			if (m.getTagProcessors().contains(name)) {
				return m.getTagProcessorDependencies(name);
			}
		}
		throw new UnsupportedComponentException(name);
	}

	@Override
	public Map<DependencyType, String> getHttpHandlerDependencies(String name)
		throws UnsupportedComponentException {
		for (CrawlerModule m : modules.values()) {
			if (m.getHttpHandlers().contains(name)) {
				return m.getHttpHandlerDependencies(name);
			}
		}
		throw new UnsupportedComponentException(name);
	}

	@Override
	public Map<DependencyType, String> getHttpFetcherDependencies(String name)
		throws UnsupportedComponentException {
		for (CrawlerModule m : modules.values()) {
			if (m.getHttpFetchers().contains(name)) {
				return m.getHttpFetcherDependencies(name);
			}
		}
		throw new UnsupportedComponentException(name);
	}

	@Override
	public Map<DependencyType, String> getDatasinkDependencies(String name)
		throws UnsupportedComponentException {
		for (CrawlerModule m : modules.values()) {
			if (m.getDataSinks().contains(name)) {
				return m.getDatasinkDependencies(name);
			}
		}
		throw new UnsupportedComponentException(name);
	}

	@Override
	public Map<DependencyType, String> getRestrictionManagerDependencies(String name)
		throws UnsupportedComponentException {
		for (CrawlerModule m : modules.values()) {
			if (m.getRestrictionManagers().contains(name)) {
				return m.getRestrictionManagerDependencies(name);
			}
		}
		throw new UnsupportedComponentException(name);
	}

	public void beginSession() {
		if (!inSession) {
			currentSession = new HashMap<>();
			inSession = true;
		}

	}

	public void endSession() {
		currentSession.clear();
		inSession = false;
	}

	public Map<DependencyType, ResolvedComponent<?>> getSessionState() {
		return this.currentSession;
	}

	public ResolvedComponent<TagProcessor> getTagProcessor(String name) throws Exception {
		Map<DependencyType, ResolvedComponent<?>> resolvedDependencies = resolveRoot(
			this.getTagProcessorDependencies(name));
		TagProcessor result = this.getTagProcessor(name, resolvedDependencies);
		return new ResolvedComponent<>(name, result, resolvedDependencies);
	}

	public ResolvedComponent<HttpCrawlerHandler> getHttpHandler(String name) throws Exception {
		Map<DependencyType, ResolvedComponent<?>> resolvedDependencies = resolveRoot(
			this.getHttpHandlerDependencies(name));
		HttpCrawlerHandler result = this.getHttpHandler(name, resolvedDependencies);
		return new ResolvedComponent<>(name, result, resolvedDependencies);
	}

	public ResolvedComponent<HttpFetcher> getHttpFetcher(String name) throws Exception {
		Map<DependencyType, ResolvedComponent<?>> resolvedDependencies = resolveRoot(
			this.getHttpFetcherDependencies(name));
		HttpFetcher result = this.getHttpFetcher(name, resolvedDependencies);
		return new ResolvedComponent<>(name, result, resolvedDependencies);
	}

	public ResolvedComponent<Datasink<? super Site>> newDatasink(String name)
		throws Exception {
		Map<DependencyType, ResolvedComponent<?>> resolvedDependencies = resolveRoot(
			this.getDatasinkDependencies(name));
		Datasink<? super Site> result = this.newDatasink(name, resolvedDependencies);
		return new ResolvedComponent<>(name, result, resolvedDependencies);
	}

	public ResolvedComponent<RestrictionManager> getRestrictionManager(String name)
		throws Exception {
		Map<DependencyType, ResolvedComponent<?>> resolvedDependencies = resolveRoot(
			this.getRestrictionManagerDependencies(name));
		RestrictionManager restrictionManager = this
			.getRestrictionManager(name, resolvedDependencies);
		return new ResolvedComponent<>(name, restrictionManager, resolvedDependencies);
	}

	private Map<DependencyType, ResolvedComponent<?>> resolveRoot(
		Map<DependencyType, String> dependencies) throws DependencyException {
		Map<DependencyType, ResolvedComponent<?>> resolvedDependencies = new HashMap<>();
		for (Entry<DependencyType, String> entry : dependencies.entrySet()) {
			resolveDependencies(resolvedDependencies, entry.getKey(),
				entry.getValue());
		}
		return resolvedDependencies;
	}

	private void resolveDependencies(Map<DependencyType, ResolvedComponent<?>> resolved,
		DependencyType type, String name)
		throws DependencyException {
		if (resolved.containsKey(type)) {
			if (!resolved.get(type).getName().equals(name)) {
				throw new ConflictingDependenciesException(
					name + " AND " + resolved.get(type).getName());
			}
			return;
		}
		if (currentSession.containsKey(type)) {
			ResolvedComponent<?> sessionComp = currentSession.get(type);
			if (!sessionComp.getName().equals(name)) {
				throw new ConflictingDependenciesException(
					name + " AND " + sessionComp.getName());
			}
			resolved.put(type, sessionComp);
		}
		try {
			Map<DependencyType, ResolvedComponent<?>> childResolved = new HashMap<>();
			switch (type) {
				case FETCHER: {
					Map<DependencyType, String> fetcherDependencies = this
						.getHttpFetcherDependencies(name);
					for (Entry<DependencyType, String> e : fetcherDependencies.entrySet()) {
						resolveDependencies(childResolved, e.getKey(),
							e.getValue());
					}
					HttpFetcher resolvedFetcher = getHttpFetcher(name, childResolved);
					ResolvedComponent<HttpFetcher> result = new ResolvedComponent<HttpFetcher>(name,
						resolvedFetcher, childResolved);
					resolved.put(DependencyType.FETCHER,
						result);
					currentSession.put(DependencyType.FETCHER, result);
					break;
				}
				case DATA_SINK: {
					Map<DependencyType, String> sinkDependencies = this
						.getDatasinkDependencies(name);
					for (Entry<DependencyType, String> e : sinkDependencies.entrySet()) {
						resolveDependencies(childResolved, e.getKey(), e.getValue());
					}
					Datasink<? super Site> resolvedSink = newDatasink(name, childResolved);
					ResolvedComponent<Datasink<? super Site>> result = new ResolvedComponent<>(name,
						resolvedSink,
						childResolved);
					resolved.put(DependencyType.DATA_SINK,
						result);
					currentSession.put(DependencyType.DATA_SINK, result);
					break;
				}
				case RESTRICTION_MGR: {
					Map<DependencyType, String> restrictionManagerDependencies = this
						.getRestrictionManagerDependencies(name);
					for (Entry<DependencyType, String> e : restrictionManagerDependencies
						.entrySet()) {
						resolveDependencies(childResolved, e.getKey(),
							e.getValue());
					}
					RestrictionManager restrictionManager = getRestrictionManager(name,
						childResolved);
					ResolvedComponent<RestrictionManager> result = new ResolvedComponent<RestrictionManager>(
						name, restrictionManager,
						childResolved);
					resolved.put(DependencyType.RESTRICTION_MGR, result);
					currentSession.put(DependencyType.RESTRICTION_MGR, result);
					break;
				}
			}
		} catch (Exception e) {
			throw new DependencyException(e);
		}
	}

}
