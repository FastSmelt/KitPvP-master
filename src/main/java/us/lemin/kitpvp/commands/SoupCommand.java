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

public class SoupCommand extends PlayerCommand {

    private final KitPvPPlugin plugin;

    public SoupCommand(KitPvPPlugin plugin) {
        super("soup");
        this.plugin = plugin;
        setAliases("s");
    }

    @Override
    public void execute(Player player, String[] args) {
        KitProfile profile = plugin.getPlayerManager().getProfile(player);

        if (profile.getState() != PlayerState.FFA) {
            player.sendMessage(CC.RED + "You can't refill right now!");
            return;
        }
        PlayerStatistics playerStatistics = profile.getStatistics();

        int cost = 10;
        if (profile.getStatistics().getCredits() < cost) {
            player.sendMessage(CC.RED + "You can't afford a hotbar of soup.");
            return;
        }

        if (plugin.getServerMode() == ServerMode.CLASSIC) {
            if (profile.getVulnerableTask() != null) {
                return;
            }
            plugin.getPlayerManager().makeVulnerable(player, "hotbar");
        } else {
            int x = 0;
            while (x < 10) {
                x++;
                player.getInventory().addItem(new ItemStack(Material.MUSHROOM_SOUP));
            }
        }

        playerStatistics.setCredits(playerStatistics.getCredits() - cost);

        player.sendMessage(CC.GREEN + "You have been given a hotbar of soup for 10 credits!");
    }
}
