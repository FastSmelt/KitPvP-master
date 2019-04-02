package us.lemin.kitpvp.tasks;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import us.lemin.core.utils.message.CC;
import us.lemin.core.utils.player.PlayerUtil;
import us.lemin.kitpvp.KitPvPPlugin;
import us.lemin.kitpvp.player.KitProfile;
import us.lemin.kitpvp.player.PlayerState;
import us.lemin.kitpvp.server.ServerMode;

public class SwitchModeTask implements Runnable {
    private final KitPvPPlugin plugin;
    private final Server server;
    private int currentIndex;

    public SwitchModeTask(KitPvPPlugin plugin) {
        this.plugin = plugin;
        this.server = plugin.getServer();

    }

    @Override
    public void run() {
        if (plugin.getServerMode() == ServerMode.REGULAR) {
            plugin.setServerMode(ServerMode.CLASSIC);
        } else {
            plugin.setServerMode(ServerMode.REGULAR);
        }
        for (Player players : plugin.getServer().getOnlinePlayers()) {
            KitProfile profile = plugin.getPlayerManager().getProfile(players);
            if (profile.getState() == PlayerState.SPAWN || profile.getState() == PlayerState.FFA) {
                PlayerUtil.clearPlayer(players);
                plugin.getPlayerManager().resetPlayer(players, true);
                profile.setCurrentKit(null);
            }
        }

        plugin.getServer().broadcastMessage(CC.RED + "The server mode was changed, all players have been returned to spawn.");

    }
}
