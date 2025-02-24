package io.github.jayhan94;

import java.net.URISyntaxException;
import java.nio.file.Path;

public class NativeLibInfo {
    private static final String libraryName = "parquet_arrow";

    private NativeLibInfo() {}

    private static OsName getOsName() {
        String os = System.getProperty("os.name").toLowerCase().replace(' ', '_');
        if (os.contains("win")) {
            return OsName.Windows;
        } else if (os.startsWith("mac") || os.contains("os_x")) {
            return OsName.Osx;
        } else {
            return OsName.Linux;
        }
    }

    public static String getLibraryFileName() {
        String prefix = "lib";
        if (getOsName() == OsName.Windows) {
            prefix = "";
        }
        return prefix + libraryName + "." + getExtension();
    }

    /**
     * @return the absolute path in the jar file for the native library
     */
    public static String getResourceName() {
        return "/" + getLibraryFileName();
    }

    private static String getExtension() {
        OsName osName = getOsName();
        if (osName == OsName.Linux) {
            return "so";
        } else if (osName == OsName.Osx) {
            return "dylib";
        } else if (osName == OsName.Windows) {
            return "dll";
        }
        throw new IllegalStateException("Cannot determine the extension for " + osName);
    }

    public static Path libPath() {
        try {
            return Path.of(NativeLibInfo.class.getResource(getResourceName()).toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private enum OsName {
        Windows, Osx, Linux
    }
}
