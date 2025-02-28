package de.netzkronehd.hitboxutils.bansystemapi.advancedbans;

import de.netzkronehd.hitboxutils.bansystemapi.BanSystemApi;

public class AdvancedBansApiHolder {

    private AdvancedBansApiHolder() {
    }

    private static BanSystemApi api;

    static {
        try {
            api = new AdvancedBansApi();
        } catch (Throwable ignored) {
            api = null;
        }
    }

    public static BanSystemApi getApi() {
        return api;
    }
}
