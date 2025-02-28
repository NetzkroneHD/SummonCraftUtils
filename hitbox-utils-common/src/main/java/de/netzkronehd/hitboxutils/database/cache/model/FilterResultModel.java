package de.netzkronehd.hitboxutils.database.cache.model;

public record FilterResultModel(String text, String bannedWord, boolean toSimilar, double similarity, boolean exceedsUpperCaseLimit) {

    public boolean isBanned() {
        return bannedWord != null;
    }

}
