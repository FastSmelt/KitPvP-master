package us.lemin.kitpvp.commands;

import org.bukkit.entity.Player;
import us.lemin.core.commands.PlayerCommand;
import us.lemin.core.utils.message.CC;
import us.lemin.kitpvp.KitPvPPlugin;
import us.lemin.kitpvp.player.KitProfile;
import us.lemin.kitpvp.player.PlayerStatistics;

public class StatisticsCommand extends PlayerCommand {
    private final KitPvPPlugin plugin;

    public StatisticsCommand(KitPvPPlugin plugin) {
        super("statistics");
        setAliases("stats");
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {
        Player target = args.length < 1 || plugin.getServer().getPlayer(args[0]) == null ? player : plugin.getServer().getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(CC.RED + "Player not found."); // TODO: offline stats lookup
        } else {
            KitProfile targetProfile = plugin.getPlayerManager().getProfile(target);
            PlayerStatistics targetStats = targetProfile.getStatistics();
            player.sendMessage(CC.SEPARATOR);
            player.sendMessage(CC.SECONDARY + "Statistics of " + target.getDisplayName());
            player.sendMessage(CC.PRIMARY + "Kills: " + CC.SECONDARY + targetStats.getKills());
            player.sendMessage(CC.PRIMARY + "Deaths: " + CC.SECONDARY + targetStats.getDeaths());
            player.sendMessage(CC.PRIMARY + "Kill Streak: " + CC.SECONDARY + targetStats.getKillStreak());
            player.sendMessage(CC.PRIMARY + "KDR: " + CC.SECONDARY + targetStats.getKillDeathRatio());
            player.sendMessage(CC.PRIMARY + "Credits: " + CC.SECONDARY + targetStats.getCredits());
            player.sendMessage(CC.PRIMARY + "Highest Kill Streak: " + CC.SECONDARY + targetStats.getHighestKillStreak());
            player.sendMessage(CC.PRIMARY + "Event Wins: " + CC.SECONDARY + targetStats.getEventWins());
            player.sendMessage(CC.SEPARATOR);
        }
    }
}
