package net.viperfish.halService.module;

import java.util.List;
import net.viperfish.halService.exceptions.ModuleLoadingException;

/**
 * A loader that loads {@link CrawlerModule}.
 */
public interface CrawlerModuleLoader {

	/**
	 * loads all the modules loadable by this loader.
	 *
	 * @return loaded modules.
	 * @throws ModuleLoadingException if failed to load modules.
	 */
	List<CrawlerModule> loadModules() throws ModuleLoadingException;
}
