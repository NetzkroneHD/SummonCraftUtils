package de.netzkronehd.hitboxutils.paper.manager;

import de.netzkronehd.hitboxutils.database.cache.model.FilterResultModel;
import de.netzkronehd.hitboxutils.paper.HitBoxUtils;
import de.netzkronehd.hitboxutils.player.ChatLogEntry;
import de.netzkronehd.hitboxutils.utils.Constants;
import de.netzkronehd.hitboxutils.utils.LevenshteinDistance;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class ChatFilterManager extends Manager {

    private final List<Pattern> blockedPatterns;

    private String filterMessage;
    private long messageTimeout;
    private int minLength;
    private float maxUpperCase;
    private float maxSimilarity;
    private boolean blockMessages;
    private String blockMessage;


    public ChatFilterManager(HitBoxUtils hitBox) {
        super(hitBox);
        blockedPatterns = new ArrayList<>();
    }

    @Override
    public void onLoad() {
        createFile();
        readFile();
    }

    @Override
    public void createFile() {
        if (!file.exists()) {
            cfg.set("upper-case.min-length", 6);
            cfg.set("upper-case.percent", 0.5F);
            cfg.set("max-similarity", 0.80F);
            cfg.set("message-timeout", TimeUnit.SECONDS.toMillis(1));
            cfg.set("banned-word.message", List.of(
                    "&8&l+------------------------------+",
                    "&6&l                   Sanciones",
                    "&8»&7 Acción:&e ChatFilter",
                    "&8»&7 Jugador:&b %PLAYER%",
                    "&8»&7 Staff:&b System",
                    "&8»&7 Razón:&b Found banned word '%WORD%'",
                    "&8»&7 Message:&b %MESSAGE%",
                    "&8»&7 Server:&b %SERVER%",
                    "&8&l+------------------------------+"
            ));
            cfg.set("banned-word.block.block", false);
            cfg.set("banned-word.block.message", "Found the insult '&e%WORD%&7'.");
            cfg.set("banned-word.words", Constants.GLOBAL_INSULTS);
            save();
        }
    }

    @Override
    public void readFile() {
        minLength = cfg.getInt("upper-case.min-length", 6);
        maxUpperCase = (float) cfg.getDouble("upper-case.percent", 0.5);
        maxSimilarity = (float) cfg.getDouble("max-similarity", 0.80F);
        messageTimeout = cfg.getLong("message-timeout", TimeUnit.SECONDS.toMillis(1));
        blockMessages = cfg.getBoolean("banned-word.block.block");
        blockMessage = ChatColor.translateAlternateColorCodes('&', cfg.getString("banned-word.block.message", "Found the insult '&e%WORD%&7'."));

        final StringBuilder sb = new StringBuilder();

        final List<String> messages = cfg.getStringList("banned-word.message");
        for (int i = 0; i < messages.size(); i++) {
            if ((i + 1) == messages.size()) {
                sb.append(messages.get(i));
            } else sb.append(messages.get(i)).append("\n");
        }
        this.filterMessage = sb.toString();

        blockedPatterns.clear();
        for (String word : cfg.getStringList("banned-word.words")) {
            blockedPatterns.add(Pattern.compile(word.trim().toLowerCase()));
        }
    }

    public void createChatLog(UUID uuid, String name, long timestamp, String server, String message) {
        try {
            hitBox.getDatabaseManager().getDatabase().createChatLog(uuid, name, timestamp, server, message);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<ChatLogEntry> getChatLogsFindMessages(UUID uuid, String message) {
        try {
            return hitBox.getDatabaseManager().getDatabase().getChatLogsFindMessages(uuid, message);
        } catch (SQLException e) {
            return Collections.emptyList();
        }
    }

    public List<ChatLogEntry> getChatLogsBetweenTime(UUID uuid, long from, long to) {
        try {
            return hitBox.getDatabaseManager().getDatabase().getChatLogsBetweenTime(uuid, from, to);
        } catch (SQLException e) {
            return Collections.emptyList();
        }
    }

    public ChatLogEntry getChatLog(UUID uuid, long timestamp) {
        try {
            return hitBox.getDatabaseManager().getDatabase().getChatLog(uuid, timestamp);
        } catch (SQLException e) {
            return null;
        }
    }

    public boolean exceedsUpperCaseLimit(String msg) {
        if (msg == null) return false;
        if (msg.length() < minLength) return false;
        int upper = 0;
        for (int i = 0; i < msg.length(); i++) {
            if (Character.isLetter(msg.charAt(i)) && Character.isUpperCase(msg.charAt(i))) {
                upper++;
            }
        }
        return upper / (float) msg.length() > maxUpperCase;
    }

    public boolean exceedsMaxSimilarity(double similarity) {
        return similarity >= maxSimilarity;
    }

    public double getSimilarity(String msg1, String msg2) {
        return LevenshteinDistance.getSimilarity(msg1, msg2);
    }

    public FilterResultModel filterMessage(String text, String compare) {
        if(compare != null && text.trim().equalsIgnoreCase(compare.trim())) {
            return new FilterResultModel(text, null, true, 1, false);
        }
        if (exceedsUpperCaseLimit(text)) {
            return new FilterResultModel(text, null, false, 0, true);
        }
        final String textToCheck = text.trim().toLowerCase();
        for(Pattern p : blockedPatterns) {
            final Matcher matcher = p.matcher(textToCheck);
            if(matcher.find()) {
                return new FilterResultModel(text, textToCheck.substring(matcher.start(), matcher.end()), false, 0, false);
            }
        }
        if(compare == null) return null;
        final double similarity = getSimilarity(textToCheck, compare.toLowerCase().trim());
        if(maxSimilarity > 0.00 && exceedsMaxSimilarity(similarity)) {
            if(similarity >= maxSimilarity) {
                return new FilterResultModel(text, null, true, similarity, false);
            }
        }

        return null;

    }

}
