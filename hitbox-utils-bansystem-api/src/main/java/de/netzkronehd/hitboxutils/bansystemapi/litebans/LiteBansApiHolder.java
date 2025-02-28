package de.netzkronehd.hitboxutils.bansystemapi.litebans;


public class LiteBansApiHolder {

    private LiteBansApiHolder() {
    }

    private static LiteBansApi api;

    static {
        try {
            api = new LiteBansApi();
        } catch (Throwable ignored) {
            api = null;
        }
    }

    public static LiteBansApi getApi() {
        return api;
    }
}
