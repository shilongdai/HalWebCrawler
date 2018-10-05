package net.viperfish.crawlerApp.core;

public abstract class BaseComponent<T> implements Component<T> {

	private String name;

	protected BaseComponent(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}
}
