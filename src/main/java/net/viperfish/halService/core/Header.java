package net.viperfish.halService.core;

import java.util.Objects;

/**
 * A POJO class for representing a header in the html text. It is associated with the "Header" table
 * in the database. This class is not originally designed for thread safety.
 */
public class Header {

	private int size;
	private String content;

	/**
	 * creates a new Header with a siteID of default IDs of -1 and a size of 0.
	 */
	public Header() {
		size = 0;
		content = "";
	}


	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Header header = (Header) o;
		return size == header.size &&
			Objects.equals(content, header.content);
	}

	@Override
	public int hashCode() {
		return Objects.hash(size, content);
	}
}
