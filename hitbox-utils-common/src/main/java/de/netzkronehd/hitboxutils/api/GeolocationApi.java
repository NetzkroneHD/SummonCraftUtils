package de.netzkronehd.hitboxutils.api;

import com.google.gson.Gson;
import lombok.Data;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;

import static java.net.http.HttpResponse.BodyHandlers.ofString;

public class GeolocationApi {

    private static final HashMap<String, GeolocationInfo> cache = new HashMap<>();
    private static final Gson gson = new Gson();

    private final HttpClient httpClient = HttpClient.newBuilder().build();

    /**
     * @see #getGeolocation(String)
     */
    public GeolocationInfo getGeolocation(InetSocketAddress address) throws IOException, InterruptedException {
        return getGeolocation(address.getHostName());
    }


    /**
     * @param ip The raw Ip-Address (127.0.0.1)
     * @return {@link GeolocationInfo} (default language is english)
     */
    public GeolocationInfo getGeolocation(String ip) throws IOException, InterruptedException {
        return getGeolocation(ip, "en");
    }

    /**
     * @see #getGeolocation(String, String)
     */
    public GeolocationInfo getGeolocation(InetSocketAddress address, String languageCode) throws IOException, InterruptedException {
        return getGeolocation(address.getHostName(), languageCode);
    }

    /**
     * @param ip           The raw Ip-Address (127.0.0.1)
     * @param languageCode de Deutsch (German)
     *                     en English (default)
     *                     es Español (Spanish)
     *                     fr Français (French)
     *                     ja 日本語 (Japanese)
     *                     pt-BR Español - Argentina (Spanish)
     *                     ru Русский (Russian)
     *                     zh-CN 中国 (Chinese)
     * @return {@link GeolocationInfo}
     */
    public GeolocationInfo getGeolocation(String ip, String languageCode) throws IOException, InterruptedException {
        final GeolocationInfo geolocationInfo = cache.get(ip);
        if (geolocationInfo != null) {
            if (geolocationInfo.getCountryCode().equalsIgnoreCase(languageCode)) {
                return geolocationInfo;
            }
        }

        final String url = String.format("http://ip-api.com/json/%s?lang=%s", ip, languageCode);
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        final HttpResponse<String> response = httpClient.send(request, ofString());
        final GeolocationInfo info = gson.fromJson(response.body(), GeolocationInfo.class);

        info.setIp(ip);
        cache.put(ip, info);
        return info;
    }

    public boolean isCached(String ip) {
        return cache.containsKey(ip);
    }

    @Data
    public static class GeolocationInfo {
        private String ip;
        private final String status, country, countryCode, region, regionName, city, isp;
        private final boolean proxy;

        protected void setIp(String ip) {
            this.ip = ip;
        }

    }

}
