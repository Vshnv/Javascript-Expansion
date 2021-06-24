package com.extendedclip.papi.expansion.javascript.evaluator;

import io.github.slimjar.app.builder.ApplicationBuilder;
import io.github.slimjar.injector.loader.Injectable;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;

import javax.script.ScriptEngine;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.StandardOpenOption;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public final class NashornScriptEvaluatorFactory implements ScriptEvaluatorFactory {
    private static final String[] LIBRARIES = {
            "nashorn-core-15.1.isolated-jar"
    };
    private static final URL SELF_JAR_URL = NashornScriptEvaluatorFactory.class.getProtectionDomain()
            .getCodeSource().getLocation();
    private final NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
    private final ScriptEngine engine = factory.getScriptEngine();

    private NashornScriptEvaluatorFactory() {

    }

    @Override
    public ScriptEvaluator create(final Map<String, Object> bindings) {
        return new NashornScriptEvaluator(bindings, factory, engine);
    }

    public static ScriptEvaluatorFactory create() throws URISyntaxException, ReflectiveOperationException, NoSuchAlgorithmException, IOException {
/*
        final Collection<URL> libraryURLs = extractLibraries();
*/
        final ClassLoader bukkitClassLoader = NashornScriptEvaluatorFactory.class.getClassLoader().getParent();
        final Injectable injectable = Injectables.createInjectable(bukkitClassLoader);
        /*for (final URL libraryURL : libraryURLs) {
            injectable.inject(libraryURL);
        }*/
        System.out.println(ApplicationBuilder.class);
        ApplicationBuilder.injecting("JSExpansion", injectable).build();
        return new NashornScriptEvaluatorFactory();
    }

    private static Collection<URL> extractLibraries() throws IOException, URISyntaxException, NoSuchAlgorithmException, ReflectiveOperationException {
        final Collection<URL> extracted = new ArrayList<>();
        final File selfFile = new File(SELF_JAR_URL.toURI());
        final JarFile jarFile = new JarFile(selfFile);
        for (final String library : LIBRARIES) {
            final File extractedFile = getExtractionFile(library, selfFile);
            if (extractedFile.exists()) {
                extracted.add(extractedFile.toURI().toURL());
                continue;
            }
            final ZipEntry entry = jarFile.getEntry(library);
            if (entry == null) {
                continue;
            }
            extractedFile.getParentFile().mkdirs();
            extractedFile.createNewFile();
            try (final InputStream stream = jarFile.getInputStream(entry);
                 final ReadableByteChannel inChannel = Channels.newChannel(stream);
                 final FileChannel outChannel = FileChannel.open(extractedFile.toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
                outChannel.transferFrom(inChannel, 0, entry.getSize());
            }
            extracted.add(extractedFile.toURI().toURL());
        }
        return extracted;
    }

    private static File getExtractionFile(final String name, final File selfFile) {
        return new File(selfFile.getParentFile(), "libraries/" + name.replace("isolated-jar", "jar"));
    }
}
