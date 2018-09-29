package net.viperfish.crawlerApp.core;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import net.viperfish.crawler.core.Datasink;
import net.viperfish.crawler.html.HttpFetcher;
import net.viperfish.crawler.html.RestrictionManager;
import net.viperfish.crawler.html.Site;

public class ResolvedComponent<T> {

	private String name;
	private T component;
	private Map<DependencyType, ResolvedComponent<?>> dependencies;

	public ResolvedComponent(String name, T component,
		Map<DependencyType, ResolvedComponent<?>> dependencies) {
		this.name = name;
		this.component = component;
		this.dependencies = dependencies;
	}

	public String getName() {
		return name;
	}

	public T getComponent() {
		return component;
	}

	public boolean dependsOn(DependencyType type) {
		if (dependencies.containsKey(type)) {
			return true;
		}
		for (ResolvedComponent<?> i : dependencies.values()) {
			if (i.dependsOn(type)) {
				return true;
			}
		}
		return false;
	}

	public boolean dependsOn(DependencyType type, String name) {
		if (dependencies.containsKey(type)) {
			if (dependencies.get(type).getName().equals(name)) {
				return true;
			}
		}
		for (ResolvedComponent<?> i : dependencies.values()) {
			if (i.dependsOn(type, name)) {
				return true;
			}
		}
		return false;
	}

	public boolean conflictsWith(ResolvedComponent<?> other) {
		for (Entry<DependencyType, ResolvedComponent<?>> i : dependencies.entrySet()) {
			if (other.dependsOn(i.getKey()) && !other
				.dependsOn(i.getKey(), i.getValue().getName())) {
				return true;
			}
			if (i.getValue().conflictsWith(other)) {
				return true;
			}
		}
		return false;
	}

	public ResolvedComponent<HttpFetcher> getFetcherDependency() {
		return (ResolvedComponent<HttpFetcher>) dependencies.get(DependencyType.FETCHER);
	}

	public ResolvedComponent<Datasink<Site>> getDatasinlDependency() {
		return (ResolvedComponent<Datasink<Site>>) dependencies.get(DependencyType.DATA_SINK);
	}

	public ResolvedComponent<RestrictionManager> getRestrictionManagerDependency() {
		return (ResolvedComponent<RestrictionManager>) dependencies
			.get(DependencyType.RESTRICTION_MGR);
	}

	public Map<DependencyType, ResolvedComponent<?>> getDependencies() {
		return this.dependencies;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		ResolvedComponent<?> that = (ResolvedComponent<?>) o;
		return Objects.equals(name, that.name) &&
			Objects.equals(component, that.component) &&
			Objects.equals(dependencies, that.dependencies);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, component, dependencies);
	}


}
