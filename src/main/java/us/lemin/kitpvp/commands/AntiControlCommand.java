package us.lemin.kitpvp.commands;

import org.bukkit.entity.Player;
import us.lemin.core.commands.PlayerCommand;
import us.lemin.core.utils.message.CC;
import us.lemin.kitpvp.KitPvPPlugin;
import us.lemin.kitpvp.player.KitProfile;
import us.lemin.kitpvp.player.PlayerState;
import us.lemin.kitpvp.player.PlayerStatistics;

public class AntiControlCommand extends PlayerCommand {

    private final KitPvPPlugin plugin;

    public AntiControlCommand(KitPvPPlugin plugin) {
        super("anticontrol");
        this.plugin = plugin;
        setAliases("ac");
    }

    @Override
    public void execute(Player player, String[] args) {
        KitProfile profile = plugin.getPlayerManager().getProfile(player);

        if (profile.getState() != PlayerState.FFA) {
            player.sendMessage(CC.RED + "You can't use this command right now!");
            return;
        }

        PlayerStatistics playerStatistics = profile.getStatistics();
        int cost = 50;
        if (profile.getStatistics().getCredits() < cost) {
            player.sendMessage(CC.RED + "You can't afford to be immune from control.");
            return;
        }

        playerStatistics.setCredits(playerStatistics.getCredits() - cost);

        profile.setControllable(false);

        player.sendMessage(CC.GREEN + "You have been given immunity to control abilities for 50 credits!");
    }
}
