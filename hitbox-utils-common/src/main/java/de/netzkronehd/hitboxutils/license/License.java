package de.netzkronehd.hitboxutils.license;

import lombok.extern.java.Log;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Log
public class License {

    private static final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();

    public static void checkLicense() {

        try {

            final HttpRequest httpRequest = HttpRequest.newBuilder(URI.create("http://134.255.234.210/project/hitboxutils")).GET().build();
            final HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if(response.body() == null) return;
            final String body = response.body().trim();

            if (body.contains("true")) {
                log.severe("####################################################");
                log.severe("Could not find license! Please contact NetzkroneHD on Discord.");
                log.severe("Reason: " + body);
                log.severe("####################################################");
                throw new UnsupportedOperationException();
            }
        } catch (IOException | InterruptedException ignored) {}
    }

}
