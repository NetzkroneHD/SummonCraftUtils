package de.netzkronehd.hitboxutils.bungee.manager;

import de.netzkronehd.hitboxutils.bungee.HitBoxUtils;
import de.netzkronehd.hitboxutils.bungee.player.HitBoxPlayer;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class AltsManager extends Manager {

    private boolean allowed;
    private String altsDisabledMessage;

    public AltsManager(HitBoxUtils hitBox) {
        super(hitBox);
    }

    @Override
    public void onLoad() {
        allowed = true;
        createFile();
        readFile();
    }

    @Override
    public void createFile() {
        if (!file.exists()) {
            cfg.set("alts-are-disabled-message", List.of("§cYou have been kicked.", "§cThere are already accounts with your IPv4-Address online.", ""));
            save();
        }
    }

    @Override
    public void readFile() {
        final StringBuilder sb = new StringBuilder();

        final List<String> lines = cfg.getStringList("alts-are-disabled-message");

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
