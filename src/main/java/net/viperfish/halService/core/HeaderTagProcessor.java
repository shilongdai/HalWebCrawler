package net.viperfish.halService.core;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.viperfish.crawler.html.TagProcessor;
import org.jsoup.nodes.Element;

public final class HeaderTagProcessor implements TagProcessor {

	public HeaderTagProcessor() {
	}

	@Override
	public Map<TagDataType, List<TagData>> processTag(Element tag, Site site) {
		Map<TagDataType, List<TagData>> result = new HashMap<>();
		result.put(TagDataType.HTML_HEADER_CONTENT, new LinkedList<>());

		TagData td = new TagData(TagDataType.HTML_HEADER_CONTENT);
		td.set("headerText", tag.text());
		if (tag.tagName().length() == 2) {
			td.set("size", tag.tagName().substring(1, 2));
		} else {
			td.set("size", "1");
		}

		result.get(TagDataType.HTML_HEADER_CONTENT).add(td);
		return result;
	}

	@Override
	public boolean match(Element e) {
		boolean notEmpty = e.text() != null && !e.text().isEmpty();
		boolean isHeader = e.tagName().startsWith("h") && e.tagName().length() == 2;
		return notEmpty && isHeader;
	}
}
