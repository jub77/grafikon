package net.parostroj.timetable.utils;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.zafarkhaja.semver.Version;
import com.google.common.collect.Iterators;

/**
 * Utitily class for getting information about versions from MANIFEST.MF.
 *
 * @author jub
 */
public class ManifestVersionInfo {

    private static final Logger log = LoggerFactory.getLogger(ManifestVersionInfo.class);

    public static interface VersionData {
        String getTitle();
        Version getVersion();
    }

    private Map<String, VersionData> cachedVersions;

    public Map<String, VersionData> getManifestVersions() {
        if (cachedVersions == null) {
            try {
                cachedVersions = getVersionsImpl();
            } catch (Exception e) {
                // skip - no info is not a problem
                log.warn("Problem extracting versions", e);
                cachedVersions = Collections.emptyMap();
            }
        }
        return cachedVersions;
    }

    private Map<String, VersionData> getVersionsImpl() throws IOException {
        long startTime = System.currentTimeMillis();
        Map<String, VersionData> versions = new TreeMap<>();
        Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(JarFile.MANIFEST_NAME);
        for (URL url : (Iterable<URL>) () -> Iterators.forEnumeration(urls)) {
            Manifest m = new Manifest(url.openStream());
            String title = m.getMainAttributes().getValue("Implementation-Title");
            String versionString = m.getMainAttributes().getValue("Implementation-Version");
            if (title != null && title.startsWith("grafikon")) {
                Version version = Version.valueOf(versionString);
                versions.put(title, new VersionData() {
                    @Override
                    public Version getVersion() {
                        return version;
                    }

                    @Override
                    public String getTitle() {
                        return title;
                    }

                    @Override
                    public String toString() {
                        return String.format("%s: %s", getTitle(), getVersion());
                    }
                });
            }
        }
        log.debug("Analysis of versions finished in {}ms", System.currentTimeMillis() - startTime);
        return versions;
    }
}
