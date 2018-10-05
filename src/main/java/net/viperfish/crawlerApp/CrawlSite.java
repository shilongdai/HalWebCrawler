package net.viperfish.crawlerApp;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import net.viperfish.crawler.core.Datasink;
import net.viperfish.crawler.html.HttpCrawlerHandler;
import net.viperfish.crawler.html.HttpFetcher;
import net.viperfish.crawler.html.HttpWebCrawler;
import net.viperfish.crawler.html.RestrictionManager;
import net.viperfish.crawler.html.Site;
import net.viperfish.crawler.html.TagProcessor;
import net.viperfish.crawlerApp.core.BuiltInModule;
import net.viperfish.crawlerApp.core.CrawlerModuleLoader;
import net.viperfish.crawlerApp.core.DependencyType;
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
		try {
			moduleManager.register(new BuiltInModule());
		} catch (ModuleLoadingException e) {
			System.out.println("Failed to load Builtins, exitting");
			return;
		}
		moduleManager.config();
		System.out.println(SEPERATOR);
		try (Scanner in = new Scanner(System.in)) {
			System.out.println("Http Fetch Components:");
			HttpFetcher fetcher = getAndConfigHttpFetcher(moduleManager, in);
			System.out.println("Crawler Output Components:");
			Datasink<? super Site> sink = getAndConfigDatasink(moduleManager, in);
			System.out.println("Crawler Callback Handlers:");
			HttpCrawlerHandler handler = getAndConfigHttphandler(moduleManager, in);
			System.out.println("Crawler Restrictions:");
			RestrictionManager restrictionManager = getAndConfigRestrictionManager(moduleManager,
				in);
			List<ResolvedComponent<TagProcessor>> tagProcessors = getAndConfigTagProcessor(
				moduleManager, in);
			System.out.print("Number of Processing Threads:");
			String num = in.nextLine();
			while (!isNumber(num)) {
				System.out.println("Please Provide a Number");
			}
			int threadCount = Integer.parseInt(num);
			System.out.println("Initializing Crawler");
			HttpWebCrawler crawler = new HttpWebCrawler(threadCount, sink, fetcher);
			for (ResolvedComponent<TagProcessor> tp : tagProcessors) {
				crawler.registerProcessor(tp.getName(), tp.getComponent());
			}
			URL u = null;
			while (u == null) {
				System.out.print("Url to Crawl:");
				String urlStr = in.nextLine();
				try {
					u = new URL(urlStr);
				} catch (MalformedURLException e) {
					System.out.println("Please enter a valid URL:" + e.getMessage());
				}
			}
			String limit2Host = null;
			while (limit2Host == null) {
				System.out.print("Limit to Specified Host? [Y/N]:");
				limit2Host = in.nextLine();
				if (!(limit2Host.equalsIgnoreCase("Y") || limit2Host.equalsIgnoreCase("N"))) {
					limit2Host = null;
				}
			}
			if (limit2Host.equalsIgnoreCase("Y")) {
				crawler.limitToHost(true);
			} else {
				crawler.limitToHost(false);
			}
			crawler.submit(u);
			crawler.startProcessing();
			crawler.waitUntiDone();
			System.out.println("Done");
			crawler.shutdown();
			moduleManager.cleanup();
		} catch (Exception e) {
			System.out.println("Critical Failure:" + e.getMessage());
			return;
		}
	}

	private static HttpFetcher getAndConfigHttpFetcher(ModuleManager manager, Scanner in)
		throws Exception {
		if (manager.getSessionState().containsKey(DependencyType.FETCHER)) {
			ResolvedComponent<HttpFetcher> fetcherDep = (ResolvedComponent<HttpFetcher>) manager
				.getSessionState().get(DependencyType.FETCHER);
			System.out.println("Selected Http Fetcher for Dependency:" + fetcherDep.getName());
			return fetcherDep.getComponent();
		}
		Map<Integer, String> fetcherList = col2NumberedMap(manager.getHttpFetchers());
		System.out.println(SEPERATOR);
		String selected = selectFromNumberedList(fetcherList, in);
		System.out.println("Selected:" + selected);
		System.out.println(SEPERATOR);
		ResolvedComponent<HttpFetcher> resolvedFetcher = manager.getHttpFetcher(selected);
		return resolvedFetcher.getComponent();
	}

	private static Datasink<? super Site> getAndConfigDatasink(ModuleManager manager, Scanner in)
		throws Exception {
		if (manager.getSessionState().containsKey(DependencyType.DATA_SINK)) {
			ResolvedComponent<Datasink<? super Site>> datasinkDep = (ResolvedComponent<Datasink<? super Site>>) manager
				.getSessionState().get(DependencyType.DATA_SINK);
			System.out.println("Selected Datasink for Dependency:" + datasinkDep.getName());
			return datasinkDep.getComponent();
		}
		Map<Integer, String> fetcherList = col2NumberedMap(manager.getDataSinks());
		System.out.println(SEPERATOR);
		String selected = selectFromNumberedList(fetcherList, in);
		System.out.println("Selected:" + selected);
		System.out.println(SEPERATOR);
		ResolvedComponent<Datasink<? super Site>> resolvedSink = manager.newDatasink(selected);
		return resolvedSink.getComponent();
	}

	private static List<ResolvedComponent<TagProcessor>> getAndConfigTagProcessor(
		ModuleManager manager, Scanner in)
		throws Exception {
		Set<String> selected = new HashSet<>();
		Map<Integer, String> fetcherList = col2NumberedMap(manager.getTagProcessors());
		for (Entry<Integer, String> entry : fetcherList.entrySet()) {
			System.out.printf("%-4s: %s" + System.lineSeparator(), entry.getKey().toString(),
				entry.getValue());
		}
		System.out.print("Select Via Comma Seperated List:");
		String[] selection = new String[1];
		while (selected.size() != selection.length) {
			selection = in.nextLine().split(",");
			for (String i : selection) {
				if (isNumber(i)) {
					String selectedStr = fetcherList.get(Integer.parseInt(i));
					if (selectedStr != null) {
						selected.add(selectedStr);
					} else {
						System.out.println(i + " is not a valid input");
					}
				} else {
					System.out.println("Please input a number");
				}
			}
		}
		System.out.println(SEPERATOR);
		System.out.println("Selected:" + selected);
		System.out.println(SEPERATOR);
		List<ResolvedComponent<TagProcessor>> result = new LinkedList<>();
		for (String i : selected) {
			result.add(manager.getTagProcessor(i));
		}
		return result;
	}

	private static HttpCrawlerHandler getAndConfigHttphandler(ModuleManager manager, Scanner in)
		throws Exception {
		Map<Integer, String> fetcherList = col2NumberedMap(manager.getHttpHandlers());
		System.out.println(SEPERATOR);
		String selected = selectFromNumberedList(fetcherList, in);
		System.out.println("Selected:" + selected);
		System.out.println(SEPERATOR);
		return manager.getHttpHandler(selected).getComponent();
	}

	private static RestrictionManager getAndConfigRestrictionManager(ModuleManager manager,
		Scanner in) throws Exception {
		if (manager.getSessionState().containsKey(DependencyType.RESTRICTION_MGR)) {
			ResolvedComponent<RestrictionManager> restrictionMgrDep = (ResolvedComponent<RestrictionManager>) manager
				.getSessionState().get(DependencyType.RESTRICTION_MGR);
			System.out.println("Selected Datasink for Dependency:" + restrictionMgrDep.getName());
			return restrictionMgrDep.getComponent();
		}
		Map<Integer, String> fetcherList = col2NumberedMap(manager.getRestrictionmanagers());
		System.out.println(SEPERATOR);
		String selected = selectFromNumberedList(fetcherList, in);
		System.out.println("Selected:" + selected);
		System.out.println(SEPERATOR);
		ResolvedComponent<RestrictionManager> resolvedMgr = manager.getRestrictionManager(selected);
		return resolvedMgr.getComponent();
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