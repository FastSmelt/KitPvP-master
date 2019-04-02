package us.lemin.kitpvp.commands.classic;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import us.lemin.core.utils.item.ItemBuilder;
import us.lemin.core.utils.message.CC;
import us.lemin.kitpvp.KitPvPPlugin;

public class ArcherCommand extends ClassicCommand {

    public ArcherCommand(KitPvPPlugin plugin) {
        super("archer", plugin);
        setAliases("bow");
    }

    @Override
    public void execute(Player player, String[] args) {
        if (player.getItemInHand().getType() != Material.DIAMOND_SWORD) {
            player.sendMessage(CC.RED + "You must be holding a diamond sword.");
            return;
        }

        player.getInventory().remove(Material.DIAMOND_SWORD);
        player.getInventory().addItem(new ItemBuilder(Material.BOW).enchant(Enchantment.ARROW_INFINITE, 1).build());
        player.getInventory().setItem(17, new ItemStack(Material.ARROW));
        player.sendMessage(CC.PRIMARY + "You have been given the " + CC.ACCENT + "Archer" + CC.PRIMARY + " kit.");

    }
}
