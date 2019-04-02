package us.lemin.kitpvp.commands.classic;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import us.lemin.core.utils.message.CC;
import us.lemin.kitpvp.KitPvPPlugin;

public class GoldCommand extends ClassicCommand {

    public GoldCommand(KitPvPPlugin plugin) {
        super("gold", plugin);
    }

    @Override
    public void execute(Player player, String[] args) {


        PlayerInventory playerInventory = player.getInventory();

        playerInventory.setHelmet(new ItemStack(Material.GOLD_HELMET));
        playerInventory.setChestplate(new ItemStack(Material.GOLD_CHESTPLATE));
        playerInventory.setLeggings(new ItemStack(Material.GOLD_LEGGINGS));
        playerInventory.setBoots(new ItemStack(Material.GOLD_BOOTS));
        player.sendMessage(CC.PRIMARY + "You have been given a " + CC.ACCENT + " Gold " + CC.PRIMARY + "set of armor.");


    }
}
