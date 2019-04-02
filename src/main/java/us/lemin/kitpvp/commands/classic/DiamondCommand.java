package us.lemin.kitpvp.commands.classic;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import us.lemin.core.utils.message.CC;
import us.lemin.kitpvp.KitPvPPlugin;
import us.lemin.kitpvp.player.KitProfile;
import us.lemin.kitpvp.server.Buffs;

public class DiamondCommand extends ClassicCommand {
    private final KitPvPPlugin plugin;

    public DiamondCommand(KitPvPPlugin plugin) {
        super("diamond", plugin);
        this.plugin = plugin;
        setAliases("d");
    }

    @Override
    public void execute(Player player, String[] args) {
        KitProfile profile = plugin.getPlayerManager().getProfile(player);

        int cost = Buffs.DIAMOND.getPriceByLevel(0);

        if (profile.getStatistics().getCredits() < cost) {
            player.sendMessage(CC.RED + "You cannot afford Diamond armor.");
            return;
        }

        profile.getStatistics().setCredits(profile.getStatistics().getCredits() - cost);

        PlayerInventory playerInventory = player.getInventory();

        playerInventory.setHelmet(new ItemStack(Material.DIAMOND_HELMET));
        playerInventory.setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
        playerInventory.setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
        playerInventory.setBoots(new ItemStack(Material.DIAMOND_BOOTS));
        player.sendMessage(CC.PRIMARY + "You have been given a" + CC.ACCENT + " Diamond " + CC.PRIMARY + "set of armor for " + CC.ACCENT + cost + " credits");
    }
}
