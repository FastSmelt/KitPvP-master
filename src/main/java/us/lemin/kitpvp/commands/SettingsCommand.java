package us.lemin.kitpvp.commands;

import org.bukkit.entity.Player;
import us.lemin.core.commands.PlayerCommand;
import us.lemin.kitpvp.KitPvPPlugin;
import us.lemin.kitpvp.inventory.SettingsPlayerWrapper;

public class SettingsCommand extends PlayerCommand {
    private final KitPvPPlugin plugin;

    public SettingsCommand(KitPvPPlugin plugin) {
        super("settings");
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {
        plugin.getInventoryManager().getPlayerWrapper(SettingsPlayerWrapper.class).open(player);
    }
}
