package net.teujaem.jpalib.jpa.scanner;

import jakarta.persistence.Entity;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.security.CodeSource;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class JpaEntityScanner {

    private JpaEntityScanner() {
    }

    public static Class<?>[] scan(
            Class<?> applicationClass
    ) {

        Set<Class<?>> entities =
                new HashSet<>();

        String basePackage =
                applicationClass.getPackageName();

        String packagePath =
                basePackage.replace('.', '/');

        ClassLoader classLoader =
                applicationClass.getClassLoader();

        if (classLoader == null) {

            classLoader =
                    ClassLoader.getSystemClassLoader();
        }

        try {

            CodeSource codeSource =
                    applicationClass
                            .getProtectionDomain()
                            .getCodeSource();

            if (codeSource != null) {

                File location =
                        new File(
                                codeSource
                                        .getLocation()
                                        .toURI()
                        );

                if (location.isFile()
                        && location.getName().endsWith(".jar")) {

                    scanJar(
                            location,
                            packagePath,
                            classLoader,
                            entities
                    );

                } else if (location.isDirectory()) {

                    File packageDirectory =
                            new File(
                                    location,
                                    packagePath
                            );

                    if (packageDirectory.exists()) {

                        scanDirectory(
                                packageDirectory,
                                basePackage,
                                classLoader,
                                entities
                        );
                    }
                }
            }

        } catch (Exception e) {

            throw new IllegalStateException(
                    "JPA Entity 스캔에 실패했습니다: "
                            + basePackage,
                    e
            );
        }

        try {

            Enumeration<URL> resources =
                    classLoader.getResources(
                            packagePath
                    );

            while (resources.hasMoreElements()) {

                URL resource =
                        resources.nextElement();

                if ("file".equals(
                        resource.getProtocol()
                )) {

                    File directory =
                            new File(
                                    resource.toURI()
                            );

                    scanDirectory(
                            directory,
                            basePackage,
                            classLoader,
                            entities
                    );

                } else if ("jar".equals(
                        resource.getProtocol()
                )) {

                    JarURLConnection connection =
                            (JarURLConnection)
                                    resource.openConnection();

                    try (
                            JarFile jar =
                                    connection.getJarFile()
                    ) {

                        scanJar(
                                jar,
                                packagePath,
                                classLoader,
                                entities
                        );
                    }
                }
            }

        } catch (Exception ignored) {
        }

        return entities.toArray(
                new Class<?>[0]
        );
    }

    private static void scanDirectory(
            File directory,
            String packageName,
            ClassLoader classLoader,
            Set<Class<?>> entities
    ) {

        File[] files =
                directory.listFiles();

        if (files == null) {
            return;
        }

        for (File file : files) {

            if (file.isDirectory()) {

                scanDirectory(
                        file,
                        packageName
                                + "."
                                + file.getName(),
                        classLoader,
                        entities
                );

                continue;
            }

            String fileName =
                    file.getName();

            if (!fileName.endsWith(
                    ".class"
            )) {

                continue;
            }

            String className =
                    packageName
                            + "."
                            + fileName.substring(
                                    0,
                                    fileName.length() - 6
                            );

            loadEntity(
                    className,
                    classLoader,
                    entities
            );
        }
    }


    private static void scanJar(
            File file,
            String packagePath,
            ClassLoader classLoader,
            Set<Class<?>> entities
    ) throws IOException {

        try (
                JarFile jar =
                        new JarFile(file)
        ) {

            scanJar(
                    jar,
                    packagePath,
                    classLoader,
                    entities
            );
        }
    }

    private static void scanJar(
            JarFile jar,
            String packagePath,
            ClassLoader classLoader,
            Set<Class<?>> entities
    ) {

        Enumeration<JarEntry> entries =
                jar.entries();

        while (entries.hasMoreElements()) {

            JarEntry entry =
                    entries.nextElement();

            if (entry.isDirectory()) {
                continue;
            }

            String name =
                    entry.getName();

            if (!name.endsWith(".class")) {
                continue;
            }

            if (!name.startsWith(
                    packagePath + "/"
            )) {

                continue;
            }

            /*
             * inner class 제외
             */
            if (name.contains("$")) {
                continue;
            }

            String className =
                    name.substring(
                            0,
                            name.length() - 6
                    ).replace(
                            '/',
                            '.'
                    );

            loadEntity(
                    className,
                    classLoader,
                    entities
            );
        }
    }


    private static void loadEntity(
            String className,
            ClassLoader classLoader,
            Set<Class<?>> entities
    ) {

        try {

            Class<?> clazz =
                    Class.forName(
                            className,
                            false,
                            classLoader
                    );

            if (clazz.isAnnotationPresent(
                    Entity.class
            )) {

                entities.add(clazz);
            }

        } catch (ClassNotFoundException ignored) {

        } catch (LinkageError ignored) {

        } catch (Throwable ignored) {

        }
    }
}