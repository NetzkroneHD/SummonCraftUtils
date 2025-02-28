package de.netzkronehd.hitboxutils.velocity.manager;

import de.netzkronehd.hitboxutils.player.ChatLogEntry;
import de.netzkronehd.hitboxutils.utils.Constants;
import de.netzkronehd.hitboxutils.velocity.HitBoxUtils;
import lombok.*;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.netzkronehd.hitboxutils.velocity.message.MessageColor.translateAlternateColorCodes;

@Getter
public class ChatManager extends Manager {

    private final List<Pattern> blockedPatterns;
    private String filterMessage;
    private String blockMessage;

    private ChatManagerConfig config;

    public ChatManager(HitBoxUtils hitBox) {
        super(hitBox);
        blockedPatterns = new ArrayList<>();
        config = new ChatManagerConfig();
    }

    @Override
    public void onLoad() {
        createFile();
        readFile();
    }

    @Override
    public void createFile() {
        if (!file.exists()) {
            config.loadDefaults();
            save(config);
        }
    }

    @Override
    public void readFile() {
        config = getConfigJson(ChatManagerConfig.class);

        blockMessage = translateAlternateColorCodes('&', config.getBlockMessage());

        final StringBuilder sb = new StringBuilder();

        final List<String> messages = config.getFilterMessage();
        for (int i = 0; i < messages.size(); i++) {
            if ((i + 1) == messages.size()) {
                sb.append(messages.get(i));
            } else sb.append(messages.get(i)).append("\n");
        }
        this.filterMessage = sb.toString();

        blockedPatterns.clear();
        for (String word : config.getBlockedPatterns()) {
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
        if (msg.length() < config.minLength) return false;
        int upper = 0;
        for (int i = 0; i < msg.length(); i++) {
            if (Character.isLetter(msg.charAt(i)) && Character.isUpperCase(msg.charAt(i))) {
                upper++;
            }
        }
        return upper / (float) msg.length() > config.maxUpperCase;
    }

    public boolean exceedsMaxSimilarity(String msg1, String msg2) {
        if (config.maxSimilarity <= 0) return false;
        return getSimilarity(msg1, msg2) >= config.maxSimilarity;
    }

    public double getSimilarity(String msg1, String msg2) {
        if (msg1 == null) return 0;
        if (msg2 == null) return 0;
        final double maxLength = Double.max(msg1.length(), msg2.length());
        if (maxLength > 0) {
            return (maxLength - LevenshteinDistance.getDefaultInstance().apply(msg1.toLowerCase(), msg2.toLowerCase())) / maxLength;
        }
        return 1.0;
    }

    public FilterResult filterMessage(String text, String compare) {
        if(compare != null && text.trim().equalsIgnoreCase(compare.trim())) {
            return new FilterResult(text, null, true, 1, false);
        }
        if (exceedsUpperCaseLimit(text)) {
            return new FilterResult(text, null, false, 0, true);
        }
        final String textToCheck = text.trim().toLowerCase();
        for(Pattern p : blockedPatterns) {
            final Matcher matcher = p.matcher(textToCheck);
            if(matcher.find()) {
                return new FilterResult(text, textToCheck.substring(matcher.start(), matcher.end()), false, 0, false);
            }
        }
        if(config.maxSimilarity > 0.00 && compare != null && exceedsMaxSimilarity(textToCheck, compare)) {
            final double similarity = getSimilarity(textToCheck, compare.toLowerCase().trim());
            if(similarity >= config.maxSimilarity) {
                return new FilterResult(text, null, false, similarity, false);
            }
        }

        return null;

    }

    public record FilterResult(String text, String bannedWord, boolean toSimilar, double similarity, boolean exceedsUpperCaseLimit) {

        public boolean isBanned() {
            return bannedWord != null;
        }

    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChatManagerConfig extends ManagerConfig {

        private List<String> blockedPatterns;
        private List<String> filterMessage;
        private long messageTimeout;
        private int minLength;
        private float maxUpperCase;
        private float maxSimilarity;
        private boolean blockMessages;
        private String blockMessage;

        @Override
        public ChatManagerConfig loadDefaults() {
            blockedPatterns = new ArrayList<>(Constants.GLOBAL_INSULTS.size());
            blockedPatterns.addAll(Constants.GLOBAL_INSULTS);
            filterMessage = new ArrayList<>();
            filterMessage.addAll(List.of(
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
            minLength = 6;
            maxUpperCase = 0.5F;
            maxSimilarity = 0.8F;
            messageTimeout = TimeUnit.SECONDS.toMillis(1);
            blockMessages = false;
            blockMessage = "Found the insult '&e%WORD%&7'.";
            return this;
        }
    }

}
