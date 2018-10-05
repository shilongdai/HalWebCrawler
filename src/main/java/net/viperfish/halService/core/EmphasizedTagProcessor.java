package net.viperfish.halService.core;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.viperfish.crawler.html.TagProcessor;
import org.jsoup.nodes.Element;

public class EmphasizedTagProcessor implements TagProcessor {

	@Override
	public Map<TagDataType, List<TagData>> processTag(Element tag, Site site) {
		Map<TagDataType, List<TagData>> result = new HashMap<>();

		List<TagData> tags = new LinkedList<>();
		TagData tgData = new TagData();
		switch (tag.tagName()) {
			case "b": {
				tgData.set("method", EmphasizedType.BOLD);
				break;
			}
			case "em": {
				tgData.set("method", EmphasizedType.ITALIC);
				break;
			}
			case "strong": {
				tgData.set("method", EmphasizedType.BOLD);
				break;
			}
			default: {
				tgData.set("method", EmphasizedType.BOLD);
			}
		}
		tgData.set("content", tag.text());
		tags.add(tgData);

		result.put(TagDataType.HTML_EMPHASIZED_TEXT, tags);
		return result;
	}

	@Override
	public boolean match(Element e) {
		boolean notEmpty = e.ownText() != null && !e.ownText().isEmpty();
		boolean isEmphasized =
			e.tagName().equalsIgnoreCase("b") || e.tagName().equalsIgnoreCase("em") || e.tagName()
				.equalsIgnoreCase("strong");
		return notEmpty && isEmphasized;
	}

}
