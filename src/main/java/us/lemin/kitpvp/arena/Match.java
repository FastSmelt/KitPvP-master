package us.lemin.kitpvp.arena;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import us.lemin.kitpvp.KitPvPPlugin;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Match {
    private final KitPvPPlugin plugin;
    @Getter
    @Setter
    private UUID firstPlayerId;
    @Getter
    @Setter
    private UUID secondPlayerId;
    private long startTime;

    public Match(Player firstPlayer, Player secondPlayer, KitPvPPlugin plugin) {
        this.plugin = plugin;
        this.firstPlayerId = firstPlayer.getUniqueId();
        this.secondPlayerId = secondPlayer.getUniqueId();
        this.startTime = System.currentTimeMillis();
    }

    public String formatTimeMillisToClock() {
        long millis = System.currentTimeMillis() - startTime;
        return millis / 1000L <= 0 ? "0:00" : String.format("%01d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }


}
