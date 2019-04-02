package us.lemin.kitpvp.inventory;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import us.lemin.core.api.inventoryapi.InventoryWrapper;
import us.lemin.core.api.inventoryapi.PlayerAction;
import us.lemin.core.api.inventoryapi.PlayerInventoryWrapper;
import us.lemin.kitpvp.KitPvPPlugin;

public class EventPlayerWrapper extends PlayerInventoryWrapper {
    private final KitPvPPlugin plugin;

    public EventPlayerWrapper(KitPvPPlugin plugin) {
        super("Events", 3);
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

        plugin.getEventManager().getAvailableEvents().forEach((eventType, event) ->
                inventoryWrapper.addItem(event.getIcon(), new PlayerAction(player1 -> player1.performCommand("event host " + eventType.getName()), true)));
    }
}
