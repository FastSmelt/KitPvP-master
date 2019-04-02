package us.lemin.kitpvp.handlers;

import org.bukkit.entity.Player;
import us.lemin.core.utils.message.CC;
import us.lemin.kitpvp.KitPvPPlugin;
import us.lemin.kitpvp.player.KitProfile;
import us.lemin.kitpvp.server.KillStreakReward;
import us.lemin.kitpvp.server.ServerMode;

public class KillstreakHandler {
    private final KitPvPPlugin plugin;

    public KillstreakHandler(KitPvPPlugin plugin) {
        this.plugin = plugin;
    }

    public void checkDeath(Player victim, int killstreak) {
        if (killstreak >= 10) {
            plugin.getServer().broadcastMessage(CC.ACCENT + victim.getName() + CC.PRIMARY + "'s killstreak of " + CC.ACCENT + killstreak + CC.PRIMARY + " has been ended!");
        }
    }

    public void checkDeath(Player killer, Player victim, int killstreak) {
        if (killstreak >= 10) {
            plugin.getServer().broadcastMessage(CC.ACCENT + killer.getName() + CC.PRIMARY + " has ended " + CC.ACCENT + victim.getName() + CC.PRIMARY + "'s killstreak of " + CC.ACCENT + killstreak + CC.PRIMARY + "!");
        }
    }

    public void checkKill(Player player, int killstreak) {
        boolean isDivisibleBy5 = killstreak % 5 == 0;
        ServerMode servermode = plugin.getServerMode();
        if (isDivisibleBy5) {
            plugin.getServer().broadcastMessage(CC.ACCENT + player.getName() + CC.PRIMARY + " is on a " + CC.ACCENT + killstreak + CC.PRIMARY + " kill streak!");
            reward(player, killstreak);
        }
    }

    private void reward(Player player, int killstreak) {
        KitProfile profile = plugin.getPlayerManager().getProfile(player);
        switch (killstreak) {
            case 5:
                player.sendMessage(CC.GREEN + "You have received Strength II for 30 seconds as a reward.");
                KillStreakReward.STRENGTH.apply(player);
                break;
            case 10:
                player.sendMessage(CC.GREEN + "Your armor has been repaired as a reward.");
                profile.getCurrentKit().repairKit(player);
                break;
            case 15:
                player.sendMessage(CC.GREEN + "You have received 100 Credits as a reward.");
                KillStreakReward.CREDITS.apply(player);
                break;
        }
    }
}
