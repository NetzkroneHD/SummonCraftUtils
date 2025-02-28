package de.netzkronehd.hitboxutils.bansystemapi;

public class BanSystemApiHolder {

    private static final BanSystemApi api = loadBanSystem();

    public static BanSystemApi loadBanSystem() {
        final BanSystemApi liteBansApi = BanSystemApi.getLiteBansApi();
        if (liteBansApi != null) {
            return liteBansApi;
        }
        return BanSystemApi.getAdvancedBansApi();
    }

    public static boolean isSupported() {
        return api != null;
    }

    public static BanSystemApi getApi() {
        return api;
    }
}
