package us.lemin.kitpvp.commands;

import org.bukkit.entity.Player;
import us.lemin.core.commands.PlayerCommand;
import us.lemin.core.utils.message.CC;
import us.lemin.kitpvp.KitPvPPlugin;
import us.lemin.kitpvp.player.KitProfile;
import us.lemin.kitpvp.player.PlayerState;
import us.lemin.kitpvp.player.PlayerStatistics;
import us.lemin.kitpvp.server.ServerMode;

public class RepairCommand extends PlayerCommand {

    private final KitPvPPlugin plugin;

    public RepairCommand(KitPvPPlugin plugin) {
        super("repair");
        this.plugin = plugin;
        setAliases("fix");
    }

    @Override
    public void execute(Player player, String[] args) {
        KitProfile profile = plugin.getPlayerManager().getProfile(player);

        if (profile.getState() != PlayerState.FFA) {
            player.sendMessage(CC.RED + "You can't repair right now!");
            return;
        }

        PlayerStatistics playerStatistics = profile.getStatistics();
        int cost = 25;
        if (profile.getStatistics().getCredits() < cost) {
            player.sendMessage(CC.RED + "You can't afford to repair your armor.");
            return;
        }

        if (plugin.getServerMode() == ServerMode.CLASSIC) {
            if (profile.getVulnerableTask() != null) {
                return;
            }
            plugin.getPlayerManager().makeVulnerable(player, "repair");
        } else {
            profile.getCurrentKit().repairKit(player);
        }

        playerStatistics.setCredits(playerStatistics.getCredits() - cost);

        player.sendMessage(CC.GREEN + "You have been given a new set of armor for 50 credits!");
    }
}

