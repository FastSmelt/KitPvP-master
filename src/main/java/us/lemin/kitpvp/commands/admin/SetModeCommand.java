package us.lemin.kitpvp.commands.admin;

import org.bukkit.entity.Player;
import us.lemin.core.commands.PlayerCommand;
import us.lemin.core.player.rank.Rank;
import us.lemin.core.utils.message.CC;
import us.lemin.core.utils.player.PlayerUtil;
import us.lemin.kitpvp.KitPvPPlugin;
import us.lemin.kitpvp.player.KitProfile;
import us.lemin.kitpvp.player.PlayerState;
import us.lemin.kitpvp.server.ServerMode;

public class SetModeCommand extends PlayerCommand {
    private final KitPvPPlugin plugin;

    public SetModeCommand(KitPvPPlugin plugin) {
        super("setmode", Rank.ADMIN);
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 1) {
            player.sendMessage(usageMessage);
            return;
        }

        switch (args[0].toLowerCase()) {
            case "classic":
                plugin.setServerMode(ServerMode.CLASSIC);
                break;
            case "regular":
                plugin.setServerMode(ServerMode.REGULAR);
                break;
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
