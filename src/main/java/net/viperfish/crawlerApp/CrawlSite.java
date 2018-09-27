package net.viperfish.crawlerApp;

import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import net.viperfish.crawler.core.Datasink;
import net.viperfish.crawler.html.HttpFetcher;
import net.viperfish.crawler.html.Site;
import net.viperfish.crawler.html.TagProcessor;
import net.viperfish.crawlerApp.core.CrawlerModuleLoader;
import net.viperfish.crawlerApp.core.ModuleDirJarLoader;
import net.viperfish.crawlerApp.core.ModuleManager;
import net.viperfish.crawlerApp.exceptions.ModuleLoadingException;

public class CrawlSite {

	private static final String SEPERATOR = "----------------------------------------------------------------------";

	public static void main(String argv[]) {
		System.out.println("Setting Up Crawler:");
		CrawlerModuleLoader loader = new ModuleDirJarLoader(Paths.get("./modules"));
		ModuleManager moduleManager = new ModuleManager(loader);
		System.out.println("Loading Modules...");
		try {
			moduleManager.init();
		} catch (ModuleLoadingException e) {
			System.out.println("Module Loading Failed: " + e.getMessage());
			System.out.println("Only Using Builtins");
		}

		// TODO: register the default module here

	}

	private static HttpFetcher getAndConfigHttpFetcher(ModuleManager manager, Scanner in)
		throws Exception {
		Map<Integer, String> fetcherList = col2NumberedMap(manager.getHttpFetchers());
		System.out.println(SEPERATOR);
		String selected = selectFromNumberedList(fetcherList, in);
		System.out.println("Selected:" + selected);
		System.out.println(SEPERATOR);
		manager.configHttpFetcher(selected);
		return manager.getHttpFetcher(selected);
	}

	private static Datasink<? super Site> getAndConfigDatasink(ModuleManager manager, Scanner in)
		throws Exception {
		Map<Integer, String> fetcherList = col2NumberedMap(manager.getDataSinks());
		System.out.println(SEPERATOR);
		String selected = selectFromNumberedList(fetcherList, in);
		System.out.println("Selected:" + selected);
		System.out.println(SEPERATOR);
		manager.configDatasink(selected);
		return manager.newDatasink(selected);
	}

	private static TagProcessor getAndConfigTagProcessor(ModuleManager manager, Scanner in)
		throws Exception {
		Map<Integer, String> fetcherList = col2NumberedMap(manager.getTagProcessors());
		System.out.println(SEPERATOR);
		String selected = selectFromNumberedList(fetcherList, in);
		System.out.println("Selected:" + selected);
		System.out.println(SEPERATOR);
		manager.configTagProcessor(selected);
		return manager.getTagProcessor(selected);
	}

	private static <T> Map<Integer, T> col2NumberedMap(Collection<? extends T> collection) {
		Map<Integer, T> result = new HashMap<>();
		int counter = 0;
		for (T i : collection) {
			result.put(counter++, i);
		}
		return result;
	}

	private static <T> T selectFromNumberedList(Map<Integer, ? extends T> list, Scanner in) {
		for (Entry<Integer, ? extends T> entry : list.entrySet()) {
			System.out.printf("%-4s: %s" + System.lineSeparator(), entry.getKey().toString(),
				entry.getValue().toString());
		}
		T selected = null;
		while (selected == null) {
			System.out.print("Your Selection:");
			String input = in.nextLine();
			if (isNumber(input)) {
				selected = list.get(Integer.parseInt(input));
				if (selected == null) {
					System.out.println("Please select something from the list");
				}
			} else {
				System.out.println("Please Enter a Number");
			}
		}
		return selected;
	}

	private static boolean isNumber(String input) {
		return input.matches("\\d+");
	}
}