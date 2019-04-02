package us.lemin.kitpvp.commands.classic;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import us.lemin.core.utils.message.CC;
import us.lemin.kitpvp.KitPvPPlugin;

public class LeatherCommand extends ClassicCommand {

    public LeatherCommand(KitPvPPlugin plugin) {
        super("leather", plugin);
    }

    @Override
    public void execute(Player player, String[] args) {
        PlayerInventory playerInventory = player.getInventory();

        playerInventory.setHelmet(new ItemStack(Material.LEATHER_BOOTS));
        playerInventory.setChestplate(new ItemStack(Material.LEATHER_BOOTS));
        playerInventory.setLeggings(new ItemStack(Material.LEATHER_BOOTS));
        playerInventory.setBoots(new ItemStack(Material.LEATHER_BOOTS));
        player.sendMessage(CC.PRIMARY + "You have been given a " + CC.ACCENT + " Leather " + CC.PRIMARY + "set of armor.");


    }
}