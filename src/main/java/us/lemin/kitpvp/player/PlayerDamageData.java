package us.lemin.kitpvp.player;

import org.bukkit.Bukkit;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerDamageData {
    private final Map<UUID, Double> attackerDamage = new HashMap<>();

    public void put(UUID attackerId, double dmg) {
        attackerDamage.put(attackerId, attackerDamage.getOrDefault(attackerId, 0.0) + dmg);
    }

    public void clear() {
        attackerDamage.clear();
    }

    public double total() {
        double total = 0.0;

        for (Map.Entry<UUID, Double> entry : attackerDamage.entrySet()) {
            if (Bukkit.getPlayer(entry.getKey()) != null) {
                total += entry.getValue();
            }
        }

        return total;
    }

    public Map<UUID, Double> sortedMap() {
        return attackerDamage.entrySet().stream()
                .filter(entry -> Bukkit.getPlayer(entry.getKey()) != null)
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }
}
