package us.lemin.kitpvp.commands.toggle;

import org.bukkit.entity.Player;
import us.lemin.core.commands.PlayerCommand;
import us.lemin.core.utils.message.CC;
import us.lemin.kitpvp.KitPvPPlugin;
import us.lemin.kitpvp.player.KitProfile;

public class ToggleScoreboardCommand extends PlayerCommand {
    private final KitPvPPlugin plugin;

    public ToggleScoreboardCommand(KitPvPPlugin plugin) {
        super("togglescoreboard");
        this.plugin = plugin;
        setAliases("scoreboard", "tsb", "sb");
    }

    @Override
    public void execute(Player player, String[] args) {
        KitProfile profile = plugin.getPlayerManager().getProfile(player);
        boolean enabled = !profile.isScoreboardEnabled();

        profile.setScoreboardEnabled(enabled);
        player.sendMessage(enabled ? CC.GREEN + "Scoreboard enabled." : CC.RED + "Scoreboard disabled.");
    }
}
