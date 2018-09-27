package net.viperfish.crawlerApp.core;

import java.util.List;
import net.viperfish.crawlerApp.exceptions.ModuleLoadingException;

public interface CrawlerModuleLoader {

	List<CrawlerModule> loadModules() throws ModuleLoadingException;
}
