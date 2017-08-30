package me.soshin;

import com.google.gson.JsonElement;
import me.soshin.fetcher.GitHubFetcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

@RunWith(BlockJUnit4ClassRunner.class)
public class ScramblerTest {

    @Test
    public void testScramble() throws IOException {

        final Scrambler s = new Scrambler();

        final GitHubFetcher.Events events = s.doYourJob("./src/test/resources/ob_events.json");

        for (final GitHubFetcher.Events.Service service : events.services.values()) {

            for (final String publishEvent : service.publish) {
                assertEquals(publishEvent.length(), 8);
            }

            for (final JsonElement subscribeEvent : service.subscribe) {
                final String eventName;
                if (subscribeEvent.isJsonPrimitive()) {
                    eventName = subscribeEvent.getAsString();
                } else {
                    eventName = subscribeEvent.getAsJsonObject().get("type").getAsString();
                }

                assertEquals(String.format("%s should be scrambled", eventName), eventName.length(), 8);
            }
        }
    }

}