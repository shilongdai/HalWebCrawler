package net.viperfish.halService.module;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import net.viperfish.halService.exceptions.ModuleLoadingException;

public class ModuleDirJarLoader implements CrawlerModuleLoader {

	private Path path2Dir;

	public ModuleDirJarLoader(Path path2Dir) {
		this.path2Dir = path2Dir;
	}

	@Override
	public List<CrawlerModule> loadModules() throws ModuleLoadingException {
		try {
			List<CrawlerModule> modules = new LinkedList<>();
			Files.walk(path2Dir, FileVisitOption.FOLLOW_LINKS).forEach(new Consumer<Path>() {
				@Override
				public void accept(Path path) {
					try {
						if (Files.isRegularFile(path) && path.getFileName().toString()
							.endsWith(".jar")) {
							JarFile jarFile = new JarFile(path.toFile());
							modules.add(load(path, jarFile));
						}
					} catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			});
			return modules;
		} catch (IOException e) {
			throw new ModuleLoadingException(e);
		}
	}

	private CrawlerModule load(Path path2Jar, JarFile jarFile)
		throws ClassNotFoundException, MalformedURLException, InstantiationException, IllegalAccessException {
		Enumeration<JarEntry> e = jarFile.entries();

		URL[] urls = {new URL("jar:file:" + path2Jar.toString() + "!/")};
		URLClassLoader cl = new URLClassLoader(urls, this.getClass().getClassLoader());
		String moduleClassname = "";
		while (e.hasMoreElements()) {
			JarEntry je = e.nextElement();
			if (je.isDirectory() || !je.getName().endsWith(".class")) {
				continue;
			}
			// -6 because of .class
			String className = je.getName().substring(0, je.getName().length() - 6);
			className = className.replace('/', '.');
			Class c = cl.loadClass(className);
			if (c.getSimpleName().equals("MainModule")) {
				Object result = c.newInstance();
				if (result instanceof CrawlerModule) {
					return (CrawlerModule) result;
				}
			}
		}
		return null;
	}
}
