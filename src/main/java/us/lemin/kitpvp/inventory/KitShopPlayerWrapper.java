package us.lemin.kitpvp.inventory;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import us.lemin.core.CorePlugin;
import us.lemin.core.api.inventoryapi.InventoryWrapper;
import us.lemin.core.api.inventoryapi.PlayerAction;
import us.lemin.core.api.inventoryapi.PlayerInventoryWrapper;
import us.lemin.core.player.CoreProfile;
import us.lemin.core.utils.item.ItemBuilder;
import us.lemin.core.utils.message.CC;
import us.lemin.kitpvp.KitPvPPlugin;
import us.lemin.kitpvp.kit.Kit;

import java.util.List;

public class KitShopPlayerWrapper extends PlayerInventoryWrapper {
    private final KitPvPPlugin plugin;

    public KitShopPlayerWrapper(KitPvPPlugin plugin) {
        super("Kit Shop", 6);
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
        CoreProfile coreProfile = CorePlugin.getInstance().getProfileManager().getProfile(player.getUniqueId());

        for (Kit kit : plugin.getKitManager().getKits()) {
            boolean kitOwned = coreProfile.hasDonor() ? coreProfile.hasDonor() : plugin.getPlayerManager().getProfile(player).getPurchasedKits().contains(kit.getName());
            String owned =  kitOwned ? CC.GREEN + " Owned" : CC.RED + " Unowned";
            List<String> oldLore = kit.getIcon().getItemMeta().getLore();
            ItemBuilder kitItem = ItemBuilder.from(kit.getIcon().clone());
            kitItem.name(kit.getIcon().getItemMeta().getDisplayName() + owned);
            if (!kitOwned) {
                kitItem.lore(oldLore.get(0), CC.GREEN + "Click to purchase for 500 credits.");
            }
            inventoryWrapper.addItem(kitItem.build(), new PlayerAction(kit::purchaseKit, true));
        }
    }
}
