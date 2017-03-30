package me.soshin;

import com.google.gson.*;
import me.soshin.fetcher.GitHubFetcher;
import org.springframework.util.StreamUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Scrambler {

    private final static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void main(final String[] args) throws IOException {

        final Scrambler s = new Scrambler();

        final GitHubFetcher.Events events = s.doYourJob("./example/data/events.json");

        try (Writer w = new OutputStreamWriter(new FileOutputStream("./example/data/ob_events.json"))) {
            gson.toJson(events, w);

            System.out.println("All went well");
        }
    }

    public GitHubFetcher.Events doYourJob(final String path) throws IOException {

        try (InputStream stream = Files.newInputStream(Paths.get(path))) {
            final String json = StreamUtils.copyToString(stream, UTF_8);
            final GitHubFetcher.Events events = gson.fromJson(json, GitHubFetcher.Events.class);

            final Map<String, GitHubFetcher.Events.Service> services = new HashMap<>();
            events.services.forEach((key, value) -> {
                services.put(garbage(), value);
            });

            scramblePublishers(services);

            scrambleUnused(services);

            events.services = services;

            return events;
        }
    }

    /**
     * If a string is not used, it won't get scrambled
     * Pass over them again, and scramble those
     *
     * @param services
     */
    private void scrambleUnused(final Map<String, GitHubFetcher.Events.Service> services) {

        for (final GitHubFetcher.Events.Service s : services.values()) {
            s.publish = s.publish.parallelStream().map((p) -> {
                if (!isScrambled(p)) {
                    return garbage();
                } else {
                    return p;
                }
            }).collect(Collectors.toList());

            s.subscribe = s.subscribe.parallelStream().map((subscriber) -> {
                String eventName = jsonElementToString(subscriber);

                if (!isScrambled(eventName)) {
                    eventName = garbage();
                }

                if (subscriber.isJsonObject()) {
                    final JsonObject obj = new JsonObject();
                    obj.add("type", new JsonPrimitive(eventName));
                    return obj;
                } else {
                    return new JsonPrimitive(eventName);
                }

            }).collect(Collectors.toList());
        }
    }

    private boolean isScrambled(final String p) {
        return p.length() == 8;
    }

    private void scramblePublishers(final Map<String, GitHubFetcher.Events.Service> services) {
        services.forEach((key, service) -> {
            final List<String> toRemove = new ArrayList<>();
            final List<String> toAdd = new ArrayList<>();
            service.publish.forEach(event -> {
                final String newName = garbage();
                scrambleSubscribers(services, event, newName);

                toRemove.add(event);
                toAdd.add(newName);
            });

            service.publish.removeAll(toRemove);
            service.publish.addAll(toAdd);
        });
    }

    private String jsonElementToString(final JsonElement e) {
        if (e.isJsonObject()) {
            return e.getAsJsonObject().get("type").getAsString();
        } else {
            return e.getAsString();
        }
    }

    private void scrambleSubscribers(final Map<String, GitHubFetcher.Events.Service> services, final String event, final String newName) {
        // Replace all subscribers with garbage
        services.forEach((k, v) -> {
            final ArrayList<JsonElement> toRemove = new ArrayList<>();
            final ArrayList<JsonElement> toAdd = new ArrayList<>();
            v.subscribe.forEach(e -> {

                if (e.isJsonObject()) {
                    final String eventName = e.getAsJsonObject().get("type").getAsString();
                    if (event.equals(eventName)) {
                        toRemove.add(e);
                        final JsonObject obj = new JsonObject();
                        obj.add("type", new JsonPrimitive(newName));
                        toAdd.add(obj);
                    }
                } else {
                    if (event.equals(e.getAsString())) {
                        toRemove.add(e);
                        toAdd.add(new JsonPrimitive(newName));
                    }
                }
            });

            v.subscribe.removeAll(toRemove);
            v.subscribe.addAll(toAdd);
        });
    }

    private static String garbage() {
        return UUID.randomUUID().toString().split("-")[0];
    }
}
