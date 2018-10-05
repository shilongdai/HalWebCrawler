package net.viperfish.halService.module;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import net.viperfish.crawler.core.Datasink;
import net.viperfish.crawler.html.HttpFetcher;
import net.viperfish.crawler.html.RestrictionManager;

/**
 * A component of a modular system with its name and dependencies.
 *
 * @param <T> the type of the compoenent.
 */
public class ResolvedComponent<T> {

	private String name;
	private T component;
	private Map<DependencyType, ResolvedComponent<?>> dependencies;

	/**
	 * creates a new {@link ResolvedComponent} with name, object, and dependencies.
	 *
	 * @param name the name of the component
	 * @param component the component object
	 * @param dependencies the dependencies used by this component
	 */
	public ResolvedComponent(String name, T component,
		Map<DependencyType, ResolvedComponent<?>> dependencies) {
		this.name = name;
		this.component = component;
		this.dependencies = dependencies;
	}

	/**
	 * gets the name of the component
	 *
	 * @return the name of the component.
	 */
	public String getName() {
		return name;
	}

	/**
	 * gets the actual component.
	 *
	 * @return the actual component
	 */
	public T getComponent() {
		return component;
	}

	/**
	 * tests if this component requires a specific type of dependency.
	 *
	 * @param type the type of the dependency.
	 * @return true if it requires the type of dependency, false otherwise.
	 */
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

	/**
	 * tests if this component depends on another component with a specified type and name.
	 *
	 * @param type the type of the other component
	 * @param name the name of the other component
	 * @return true if depends on, false otherwise.
	 */
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

	/**
	 * checks if this component conflicts in dependency with the other components. A dependency
	 * conflict occurs when there are two or more distinct requirements for a specific type of
	 * dependency.
	 *
	 * @param other the other component.
	 * @return true if conflicts, false otherwise.
	 */
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

	/**
	 * gets the HttpFetcher dependency of this component.
	 *
	 * @return the HttpFetcher dependency, or null if this component does not depend on HttpFetcher.
	 */
	public ResolvedComponent<HttpFetcher> getFetcherDependency() {
		return (ResolvedComponent<HttpFetcher>) dependencies.get(DependencyType.FETCHER);
	}

	/**
	 * gets the Datasink dependency of this component.
	 *
	 * @return the Datasink dependency, or null if this component does not depend on Datasink.
	 */
	public ResolvedComponent<Datasink<Site>> getDatasinkDependency() {
		return (ResolvedComponent<Datasink<Site>>) dependencies.get(DependencyType.DATA_SINK);
	}

	/**
	 * gets the RestrictionManager dependency of this component.
	 *
	 * @return the RestrictionManager dependency, or null if this component does not depend on
	 * RestrictionManager.
	 */
	public ResolvedComponent<RestrictionManager> getRestrictionManagerDependency() {
		return (ResolvedComponent<RestrictionManager>) dependencies
			.get(DependencyType.RESTRICTION_MGR);
	}

	/**
	 * gets all the dependencies of this component
	 *
	 * @return all the dependencies.
	 */
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
