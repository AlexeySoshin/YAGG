package me.soshin.fetcher;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import me.soshin.model.Edge;
import me.soshin.model.Graph;
import me.soshin.model.Node;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;

@Component("github_events")
public class GitHubFetcher extends Fetcher {


    private final Gson gson = new GsonBuilder().create();

    @Value(value = "classpath:events.json")
    private Resource eventsResource;

    @Override
    public Graph fetch() {

        final Graph g = new Graph();

        try {
            final Events e = getEvents();

            addNodes(g, e);

            addEdges(g, e);

        } catch (final IOException e) {
            e.printStackTrace();
        }

        return g;
    }

    private Events getEvents() throws IOException {
        final String events = StreamUtils.copyToString(this.eventsResource.getInputStream(), UTF_8);

        return this.gson.fromJson(events, Events.class);
    }

    /**
     * @param g
     * @param e
     */
    private void addEdges(final Graph g, final Events e) {
        final Map<String, List<String>> subsribedServices = getSubscribedServices(e);

        for (final String serviceName : e.services.keySet()) {
            final Events.Service service = e.services.get(serviceName);
            for (final String eventName : service.publish) {
                final List<String> subscribedServices = subsribedServices.getOrDefault(eventName, Collections.emptyList());

                for (final String subscribedService : subscribedServices) {
                    g.add(new Edge(serviceName, subscribedService, ""));
                }
            }
        }
    }

    /**
     * @param g
     * @param e
     */
    private void addNodes(final Graph g, final Events e) {
        for (final String key : e.services.keySet()) {
            g.add(new Node(key, key));
        }
    }

    /**
     * @param e
     * @return
     */
    private Map<String, List<String>> getSubscribedServices(final Events e) {
        final Map<String, List<String>> subsribedServices = new HashMap<>();

        for (final String serviceName : e.services.keySet()) {
            final Events.Service service = e.services.get(serviceName);
            for (final JsonElement event : service.subscribe) {
                final String eventName = parseEventName(event);
                addSubscriber(subsribedServices, serviceName, eventName);
            }
        }
        return subsribedServices;
    }

    private void addSubscriber(final Map<String, List<String>> subscribedServices, final String serviceName, final String eventName) {
        // We cannot be more fluent here, because putIfAbsent returns null if key was added just now
        List<String> subscribedToEvent = subscribedServices.get(eventName);
        if (subscribedToEvent == null) {
            subscribedToEvent = new ArrayList<>();
            subscribedServices.put(eventName, subscribedToEvent);
        }
        subscribedToEvent.add(serviceName);
    }

    private String parseEventName(final JsonElement element) {
        final String eventName;
        if (element.isJsonObject()) {
            eventName = element.getAsJsonObject().get("type").getAsString();
        } else {
            eventName = element.getAsString();
        }
        return eventName;
    }

    class Events {

        Map<String, Service> services;

        class Service {
            List<String> publish;
            List<JsonElement> subscribe;

        }

    }

}
