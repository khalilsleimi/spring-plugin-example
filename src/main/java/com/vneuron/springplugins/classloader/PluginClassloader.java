package com.vneuron.springplugins.classloader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

public class PluginClassloader extends ClassLoader {

    private final String pluginsFolder;
    private List<JarFile> jars;

    public PluginClassloader(String pluginsFolder, ClassLoader parent) {
        super(parent);
        this.pluginsFolder = pluginsFolder;

        init();
    }

    /**
     * Init method: populates this.jars with the jar files in specified pluginsFolder
     */
    public void init() {
        File[] jarFiles = new File(pluginsFolder).listFiles((dir, name) -> name.endsWith(".jar"));
        if (jarFiles == null) {
            jars = Collections.emptyList();
            return;
        }

        this.jars = Arrays.stream(jarFiles).map(jarFile -> {
            try {
                return new JarFile(jarFile);
            } catch (IOException e) {
                // we've just listed them, they're here
                return null;
            }
        }).collect(Collectors.toList());
    }

    /**
     * Find a class in one of the Jars - use the first one to define the class and return it (or replace old one)
     */
    @Override
    protected Class<?> findClass(final String name) throws ClassNotFoundException {
        String className = name.replace('.', '/').concat(".class");
        List<URL> resourceUrl = getResourceUrl(className);

        if (!resourceUrl.isEmpty()) {
            URL url = resourceUrl.iterator().next();
            byte[] bytes = getBytes(url);
            Class<?> clazz = defineClass(name, bytes, 0, bytes.length);
            resolveClass(clazz);
            return clazz;
        } else {
            throw new ClassNotFoundException();
        }
    }

    @Override
    protected URL findResource(final String name) {
        List<URL> resourceUrls = getResourceUrl(name);
        return resourceUrls.isEmpty() ? null : resourceUrls.iterator().next();
    }

    @Override
    protected Enumeration<URL> findResources(final String name) {
        List<URL> urls = getResourceUrl(name);
        return Collections.enumeration(urls);
    }

    private byte[] getBytes(final URL classUrl) {
        try (InputStream cu = classUrl.openStream()) {
            return cu.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    private List<URL> getResourceUrl(String className) {
        List<URL> urls = new ArrayList<>();
        for (JarFile jar : jars) {
            ZipEntry entry = jar.getEntry(className);
            if (entry != null) {
                urls.add(createUrl(entry.getName(), jar));
            }
        }

        return urls;
    }

    private URL createUrl(final String className, final JarFile jarFile) {
        try {
            return new URL("jar", null, "file:" + jarFile.getName() + "!/" + className);
        } catch (MalformedURLException e) {
            throw new RuntimeException();
        }
    }
}