package de.miraculixx.mchallenge.loader;

import com.google.gson.Gson;
import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class MChallengeLoader implements PluginLoader {
    @Override
    public void classloader(@NotNull PluginClasspathBuilder classpathBuilder) {
        MavenLibraryResolver resolver = new MavenLibraryResolver();
        PluginLibraries pluginLibraries = load();
        pluginLibraries.asDependencies().forEach(resolver::addDependency);
        pluginLibraries.asRepositories().forEach(resolver::addRepository);
        classpathBuilder.addLibrary(resolver);
    }

    private PluginLibraries load() {
        try (var in = getClass().getResourceAsStream("/paper-libraries.json")) {
            assert in != null;
            return new Gson().fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), PluginLibraries.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (AssertionError e) {
            throw new RuntimeException("Failed to load paper-libraries.json", e);
        }
    }

    private record PluginLibraries(Map<String, String> repositories, List<String> dependencies) {
        public Stream<Dependency> asDependencies() {
            return dependencies.stream()
                .map(d -> new Dependency(new DefaultArtifact(d), null));
        }

        public Stream<RemoteRepository> asRepositories() {
            return repositories.entrySet().stream()
                    .map(e -> {
                        var url = e.getValue();
                        if (url.matches("https://repo.maven.apache.org/maven2/")) url = "https://maven-central.storage-download.googleapis.com/maven2";
                        return new RemoteRepository.Builder(e.getKey(), "default", url).build();
                    });
        }
    }
}
