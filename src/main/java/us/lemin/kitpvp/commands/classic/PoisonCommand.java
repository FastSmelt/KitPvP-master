package us.lemin.kitpvp.commands.classic;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import us.lemin.core.utils.item.ItemBuilder;
import us.lemin.core.utils.message.CC;
import us.lemin.kitpvp.KitPvPPlugin;
import us.lemin.kitpvp.player.KitProfile;
import us.lemin.kitpvp.server.Buffs;

public class PoisonCommand extends ClassicCommand {
    private final KitPvPPlugin plugin;

    public PoisonCommand(KitPvPPlugin plugin) {
        super("poison", plugin);
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {

        KitProfile profile = plugin.getPlayerManager().getProfile(player);


        int cost = Buffs.POISON.getPriceByLevel(0);

        if (profile.getStatistics().getCredits() < cost) {
            player.sendMessage(CC.RED + "You cannot afford to buy a poison potion.");
            return;
        }

        profile.getStatistics().setCredits(profile.getStatistics().getCredits() - cost);

        PlayerInventory playerInventory = player.getInventory();

        int firstEmpty = playerInventory.firstEmpty();

        ItemStack poisonPotion = new ItemBuilder(Material.POTION).durability(16388).build();

        if (firstEmpty == -1) {
            playerInventory.setItem(2, poisonPotion);
        } else {
            playerInventory.setItem(firstEmpty, poisonPotion);
        }

        player.sendMessage(CC.PRIMARY + "You have been given a " + CC.ACCENT + "Poison potion for " + CC.ACCENT + cost + " credits.");

    }
}
