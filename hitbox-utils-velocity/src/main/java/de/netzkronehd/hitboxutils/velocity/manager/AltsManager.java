package de.netzkronehd.hitboxutils.velocity.manager;

import de.netzkronehd.hitboxutils.velocity.HitBoxUtils;
import de.netzkronehd.hitboxutils.velocity.player.HitBoxPlayer;
import lombok.*;

import java.util.*;

@Getter
@Setter
public class AltsManager extends Manager {

    private String altsDisabledMessage;

    @EqualsAndHashCode(callSuper = true)
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AltManagerConfig extends ManagerConfig {

        private boolean allowed;
        private List<String> altsDisabledMessage;

        @Override
        public ManagerConfig loadDefaults() {
            this.allowed = true;
            this.altsDisabledMessage = new ArrayList<>();
            this.altsDisabledMessage.addAll(List.of("§cYou have been kicked.", "§cThere are already accounts with your IPv4-Address online.", ""));
            return this;
        }
    }

    private AltManagerConfig config;

    public AltsManager(HitBoxUtils hitBox) {
        super(hitBox);
        this.config = new AltManagerConfig();
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
        config = getConfigJson(AltManagerConfig.class);

        final StringBuilder sb = new StringBuilder();
        final List<String> lines = config.getAltsDisabledMessage();

        for (int i = 0; i < lines.size(); i++) {
            if ((i + 1) == lines.size()) {
                sb.append(lines.get(i));
            } else sb.append(lines.get(i)).append("\n");
        }
        altsDisabledMessage = sb.toString();
    }

    public List<HitBoxPlayer> getAltAccounts(String ip) {
        final List<HitBoxPlayer> alts = new ArrayList<>();
        for (HitBoxPlayer player : hitBox.getPlayers()) {
            if (!ip.equals(player.getIp())) continue;
            alts.add(player);
        }
        return alts;
    }

    public boolean hasAltAccount(String ip, UUID ignoredUuid) {
        for (HitBoxPlayer player : hitBox.getPlayers()) {
            if (player.getUniqueId().equals(ignoredUuid)) continue;
            if (ip.equals(player.getIp())) return true;
        }
        return false;
    }

    public Map<HitBoxPlayer, Set<HitBoxPlayer>> getAltAccounts() {
        final Map<String, HitBoxPlayer> ipToPlayerMap = new HashMap<>();
        final Map<HitBoxPlayer, Set<HitBoxPlayer>> altAccounts = new HashMap<>();

        for (HitBoxPlayer currentPlayer : hitBox.getPlayers()) {
            final String ip = currentPlayer.getIp();
            if (ipToPlayerMap.containsKey(ip)) {
                HitBoxPlayer existingPlayer = ipToPlayerMap.get(ip);
                final Set<HitBoxPlayer> alts = altAccounts.getOrDefault(existingPlayer, new HashSet<>());
                alts.add(currentPlayer);
                altAccounts.put(existingPlayer, alts);
            } else {
                ipToPlayerMap.put(ip, currentPlayer);
            }
        }
        return altAccounts;
    }

}
