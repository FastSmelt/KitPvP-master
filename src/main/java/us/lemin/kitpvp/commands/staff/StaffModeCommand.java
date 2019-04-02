package us.lemin.kitpvp.commands.staff;

import org.bukkit.entity.Player;
import us.lemin.core.CorePlugin;
import us.lemin.core.commands.PlayerCommand;
import us.lemin.core.player.CoreProfile;
import us.lemin.core.player.rank.Rank;
import us.lemin.core.utils.message.CC;
import us.lemin.core.utils.player.PlayerUtil;
import us.lemin.kitpvp.KitPvPPlugin;
import us.lemin.kitpvp.managers.PlayerManager;
import us.lemin.kitpvp.player.KitProfile;
import us.lemin.kitpvp.player.PlayerState;

public class StaffModeCommand extends PlayerCommand {

    private final KitPvPPlugin plugin;

    public StaffModeCommand(KitPvPPlugin plugin) {
        super("staffmode", Rank.TRIAL_MOD);

        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] strings) {
        CorePlugin corePlugin = CorePlugin.getInstance();

        KitProfile profile = plugin.getPlayerManager().getProfile(player);
        CoreProfile coreProfile = corePlugin.getProfileManager().getProfile(player.getUniqueId());
        PlayerManager playerManager = plugin.getPlayerManager();
        boolean vanished = coreProfile.isVanished();
        boolean staffMode = profile.getState() == PlayerState.STAFF;

        if (staffMode) {
            if (vanished) {
                coreProfile.setVanished(false);
                corePlugin.getStaffManager().vanishPlayer(player);
                plugin.getServer().getOnlinePlayers().forEach(online -> corePlugin.getStaffManager().hideVanishedStaffFromPlayer(online));
            }
            playerManager.resetPlayer(player, true);
            playerManager.modifyScoreboardTeams(player);
        } else {
            if (!vanished) {
                PlayerUtil.clearPlayer(player);
                coreProfile.setVanished(true);
                corePlugin.getStaffManager().vanishPlayer(player);
                plugin.getServer().getOnlinePlayers().forEach(online -> corePlugin.getStaffManager().hideVanishedStaffFromPlayer(online));
            }
            playerManager.giveStaffModeItems(player);
            playerManager.modifyScoreboardTeams(player);
        }
        player.sendMessage(!staffMode ? CC.GREEN + "Your staffmode has been enabled." : CC.RED + "Your staffmode has been disabled.");
    }
}
