package us.lemin.kitpvp.commands;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import us.lemin.core.commands.PlayerCommand;
import us.lemin.core.utils.message.CC;
import us.lemin.kitpvp.KitPvPPlugin;
import us.lemin.kitpvp.player.KitProfile;
import us.lemin.kitpvp.player.PlayerState;
import us.lemin.kitpvp.player.PlayerStatistics;
import us.lemin.kitpvp.server.ServerMode;

public class RefillCommand extends PlayerCommand {

    private final KitPvPPlugin plugin;

    public RefillCommand(KitPvPPlugin plugin) {
        super("refill");
        this.plugin = plugin;
        setAliases("resoup");
    }


    @Override
    public void execute(Player player, String[] args) {


        KitProfile profile = plugin.getPlayerManager().getProfile(player);

        if (profile.getState() != PlayerState.FFA) {
            player.sendMessage(CC.RED + "You can't refill right now!");
            return;
        }

        PlayerStatistics playerStatistics = profile.getStatistics();
        int cost = 15;
        if (playerStatistics.getCredits() < cost) {
            player.sendMessage(CC.RED + "You can't afford to refill your inventory.");
            return;
        }

        if (plugin.getServerMode() == ServerMode.CLASSIC) {
            if (profile.getVulnerableTask() != null) {
                return;
            }
            plugin.getPlayerManager().makeVulnerable(player, "refill");
        } else {
            int x = 0;
            while (x < 36) {
                x++;
                player.getInventory().addItem(new ItemStack(Material.MUSHROOM_SOUP));
            }
        }

        playerStatistics.setCredits(playerStatistics.getCredits() - cost);

        player.sendMessage(CC.GREEN + "You have been given an inventory of soup for 25 credits!");
    }
}

