package de.exceptionflug.haunted.util;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.security.CodeSource;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Utility for scanning classes without external dependencies.
 * - scanOwnArtifact: scans only the JAR or class directory of the given anchor class.
 * - Loads classes without initializing them (no <clinit>).
 */
public final class ClassScanUtil {

    private ClassScanUtil() { }

    /** Scans the JAR (or class directory) of the given anchor class for all classes in basePackage. */
    public static Set<Class<?>> scanOwnArtifact(Class<?> anchor, String basePackage) {
        Set<Class<?>> out = new LinkedHashSet<>();
        String pkgPath = basePackage.replace('.', '/') + "/";
        ClassLoader cl = anchor.getClassLoader();

        File location = resolveLocation(anchor);
        if (location == null) return out;

        if (location.isFile() && location.getName().endsWith(".jar")) {
            try (JarFile jar = new JarFile(location)) {
                scanJar(jar, pkgPath, cl, out);
            } catch (Exception ignored) { }
        } else if (location.isDirectory()) {
            // Development environment: compiled class directory
            File root = new File(location, pkgPath);
            scanDir(root, basePackage, cl, out);
        }
        return out;
    }

    /** Optionally scans all classpath resources for basePackage (slower, broader). */
    public static Set<Class<?>> scanClasspathResources(Class<?> anchor, String basePackage) {
        Set<Class<?>> out = new LinkedHashSet<>();
        String pkgPath = basePackage.replace('.', '/');
        ClassLoader cl = anchor.getClassLoader();
        try {
            Enumeration<URL> urls = cl.getResources(pkgPath);
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                String protocol = url.getProtocol();
                if ("file".equals(protocol)) {
                    String dir = decode(url.getPath());
                    scanDir(new File(dir), basePackage, cl, out);
                } else if ("jar".equals(protocol)) {
                    JarURLConnection con = (JarURLConnection) url.openConnection();
                    try (JarFile jar = con.getJarFile()) {
                        scanJar(jar, pkgPath + "/", cl, out);
                    }
                }
            }
        } catch (Exception ignored) { }
        return out;
    }

    // ---------- internal helpers ----------

    private static void scanJar(JarFile jar, String pkgPrefix, ClassLoader cl, Set<Class<?>> out) {
        jar.stream()
                .filter(e -> isClassInPackage(e, pkgPrefix))
                .forEach(e -> {
                    String name = e.getName().substring(0, e.getName().length() - 6).replace('/', '.');
                    load(name, cl, out);
                });
    }

    private static boolean isClassInPackage(JarEntry e, String pkgPrefix) {
        return !e.isDirectory() && e.getName().startsWith(pkgPrefix) && e.getName().endsWith(".class");
    }

    private static void scanDir(File dir, String pkg, ClassLoader cl, Set<Class<?>> out) {
        if (dir == null || !dir.exists()) return;
        File[] files = dir.listFiles();
        if (files == null) return;
        for (File f : files) {
            if (f.isDirectory()) {
                scanDir(f, pkg + "." + f.getName(), cl, out);
            } else if (f.getName().endsWith(".class")) {
                String cls = pkg + '.' + f.getName().substring(0, f.getName().length() - 6);
                load(cls, cl, out);
            }
        }
    }

    private static void load(String clsName, ClassLoader cl, Set<Class<?>> out) {
        try {
            Class<?> c = Class.forName(clsName, false, cl); // load only, do not initialize
            out.add(c);
        } catch (ClassNotFoundException | NoClassDefFoundError ignored) { }
    }

    private static File resolveLocation(Class<?> anchor) {
        try {
            CodeSource cs = anchor.getProtectionDomain().getCodeSource();
            return cs != null ? new File(cs.getLocation().toURI()) : null;
        } catch (URISyntaxException e) {
            return null;
        }
    }

    private static String decode(String s) {
        try { return URLDecoder.decode(s, "UTF-8"); }
        catch (UnsupportedEncodingException e) { return s; }
    }
}
