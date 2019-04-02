package us.lemin.kitpvp.commands;

import org.bukkit.entity.Player;
import us.lemin.core.commands.PlayerCommand;
import us.lemin.core.utils.message.CC;
import us.lemin.kitpvp.KitPvPPlugin;
import us.lemin.kitpvp.inventory.ShopWrapper;
import us.lemin.kitpvp.player.KitProfile;
import us.lemin.kitpvp.player.PlayerState;


public class ShopCommand extends PlayerCommand {
    private final KitPvPPlugin plugin;

    public ShopCommand(KitPvPPlugin plugin) {
        super("shop");
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {
        KitProfile profile = plugin.getPlayerManager().getProfile(player);
        if (profile.getState() == PlayerState.SPAWN || profile.getState() == PlayerState.FFA) {
            plugin.getInventoryManager().getWrapper(ShopWrapper.class).open(player);
        } else {
            player.sendMessage(CC.RED + "You can't use the shop right now!");
        }


    }
}
