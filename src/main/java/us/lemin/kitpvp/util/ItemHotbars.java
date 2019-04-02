package us.lemin.kitpvp.util;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import us.lemin.core.utils.item.ItemBuilder;
import us.lemin.core.utils.message.CC;

@RequiredArgsConstructor
public enum ItemHotbars {
    SPAWN_ITEMS(
            new ItemStack[]{
                    new ItemBuilder(Material.CHEST).name(CC.GOLD + "Kit Selector" + CC.GRAY + " (Right Click)").build(),
                    new ItemBuilder(Material.IRON_FENCE).name(CC.GREEN + "Event Selector" + CC.GRAY + " (Right Click)").build(),
                    null,
                    null,
                    new ItemBuilder(Material.PAPER).name(CC.GRAY + "Your Stats" + CC.GRAY + " (Right Click)").build(),
                    null,
                    new ItemBuilder(Material.ENDER_CHEST).name(CC.GOLD + "Kit Shop" + CC.GRAY + " (Right Click)").build(),
                    new ItemBuilder(Material.DIAMOND_SWORD).name(CC.GRAY + "Duel Arena" + CC.GRAY + " (Right Click)").build(),
                    new ItemBuilder(Material.EYE_OF_ENDER).name(CC.GOLD + "Settings" + CC.GRAY + " (Right Click)").build(),
            }
    ),
    CLASSIC_ITEMS(
            new ItemStack[]{
                    new ItemBuilder(Material.DIAMOND).name(CC.GOLD + "Classic Kit").build(),
                    null,
                    null,
                    null,
                    new ItemBuilder(Material.PAPER).name(CC.GRAY + "Your Stats").build(),
                    null,
                    new ItemBuilder(Material.ENDER_CHEST).name(CC.GOLD + "Kit Shop").build(),
                    new ItemBuilder(Material.DIAMOND_SWORD).name(CC.GRAY + "Duel Arena").build(),
                    new ItemBuilder(Material.EYE_OF_ENDER).name(CC.GOLD + "Settings").build(),
            }
    ),
    ARENA_ITEMS(
            new ItemStack[]{
                    new ItemBuilder(Material.BLAZE_ROD).name(CC.GOLD + "Duel Request").build(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    new ItemBuilder(Material.REDSTONE).name(CC.RED + "Exit Arena").build(),
            }),

    STAFFMODE_ITEMS(new ItemStack[] {
            new ItemBuilder(Material.COMPASS).name(CC.RED + "Teleport Compass").build(),
            new ItemBuilder(Material.BOOK).name(CC.RED + "Inspection Book").build(),
            null,
            null,
            null,
            null,
            null,
            new ItemBuilder(Material.CHEST).name(CC.RED + "Staff Menu").build(),
            new ItemBuilder(Material.RECORD_3).name(CC.RED + "Random Teleport").build()
    });

    private final ItemStack[] hotbar;

    public void apply(Player player) {
        for (int i = 0; i < hotbar.length; i++) {
            ItemStack item = hotbar[i];
            player.getInventory().setItem(i, item == null ? new ItemStack(Material.AIR) : item.clone());
        }

        player.updateInventory();
    }
}
