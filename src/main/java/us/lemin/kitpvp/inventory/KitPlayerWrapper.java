package us.lemin.kitpvp.inventory;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import us.lemin.core.api.inventoryapi.InventoryWrapper;
import us.lemin.core.api.inventoryapi.PlayerAction;
import us.lemin.core.api.inventoryapi.PlayerInventoryWrapper;
import us.lemin.core.utils.item.ItemBuilder;
import us.lemin.core.utils.message.CC;
import us.lemin.kitpvp.KitPvPPlugin;

public class KitPlayerWrapper extends PlayerInventoryWrapper {
    private final KitPvPPlugin plugin;

    public KitPlayerWrapper(KitPvPPlugin plugin) {
        super("Kit", 3);
        this.plugin = plugin;
    }

    @Override
    public void init(Player player, InventoryWrapper inventoryWrapper) {
        format(player, inventoryWrapper);
    }

    @Override
    public void update(Player player, InventoryWrapper inventoryWrapper) {
        format(player, inventoryWrapper);
    }

    private void format(Player player, InventoryWrapper inventoryWrapper) {
        inventoryWrapper.fillBorder(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 1));

        inventoryWrapper.setItem(2, 2, new ItemBuilder(Material.CHEST).name(CC.GOLD + "Choose a Kit").build(), new PlayerAction(actionPlayer -> {
            actionPlayer.getOpenInventory().close();
            plugin.getInventoryManager().getPlayerWrapper(KitSelectorPlayerWrapper.class).open(actionPlayer);
        }));
        inventoryWrapper.setItem(2, 8, new ItemBuilder(Material.ENDER_CHEST).name(CC.GOLD + "Purchase a Kit").build(), new PlayerAction(actionPlayer -> {
            actionPlayer.getOpenInventory().close();
            plugin.getInventoryManager().getPlayerWrapper(KitShopPlayerWrapper.class).open(actionPlayer);
        }));
    }
}
