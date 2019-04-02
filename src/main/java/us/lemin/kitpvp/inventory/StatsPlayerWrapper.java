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
import us.lemin.kitpvp.player.PlayerStatistics;

public class StatsPlayerWrapper extends PlayerInventoryWrapper {
    private final KitPvPPlugin plugin;

    public StatsPlayerWrapper(KitPvPPlugin plugin) {
        super("Stats", 3);
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
        int count = 2;
        int row = 2;

        if (count > 8) {
            row++;
            count = 2;
        }

        PlayerStatistics statistics = plugin.getPlayerManager().getProfile(player).getStatistics();

        ItemStack skeletonSkull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);

        inventoryWrapper.fillBorder(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 1));

        inventoryWrapper.setItem(row, count++, new ItemBuilder(Material.DIAMOND_SWORD).name(CC.PRIMARY + "Kills")
                .lore(CC.ACCENT + statistics.getKills()).build(), new PlayerAction(player1 -> {
                    player1.isOnline();
                    update(player, inventoryWrapper);
        }, false));
        inventoryWrapper.setItem(row, count++, ItemBuilder.from(skeletonSkull).name(CC.PRIMARY + "Deaths")
                .lore(CC.ACCENT + statistics.getDeaths()).build(), new PlayerAction(player1 -> {
            player1.isOnline();
            update(player, inventoryWrapper);
        }, false));
        inventoryWrapper.setItem(row, count++, new ItemBuilder(Material.BLAZE_POWDER).name(CC.PRIMARY + "Kill Streak")
                .lore(CC.ACCENT + statistics.getKillStreak()).build(), new PlayerAction(player1 -> {
            player1.isOnline();
            update(player, inventoryWrapper);
        }, false));
        inventoryWrapper.setItem(row, count++, new ItemBuilder(Material.REDSTONE).name(CC.PRIMARY + "KDR")
                .lore(CC.ACCENT + statistics.getKillDeathRatio()).build(), new PlayerAction(player1 -> {
            player1.isOnline();
            update(player, inventoryWrapper);
        }, false));
        inventoryWrapper.setItem(row, count++, new ItemBuilder(Material.GOLD_INGOT).name(CC.PRIMARY + "Credits")
                .lore(CC.ACCENT + statistics.getCredits()).build(), new PlayerAction(player1 -> {
            player1.isOnline();
            update(player, inventoryWrapper);
        }, false));
        inventoryWrapper.setItem(row, count++, new ItemBuilder(Material.BLAZE_ROD).name(CC.PRIMARY + "Highest Kill Streak")
                .lore(CC.ACCENT + statistics.getHighestKillStreak()).build(), new PlayerAction(player1 -> {
            player1.isOnline();
            update(player, inventoryWrapper);
        }, false));
        inventoryWrapper.setItem(row, count, new ItemBuilder(Material.STICK).name(CC.PRIMARY + "Credits")
                .lore(CC.ACCENT + statistics.getEventWins()).build(), new PlayerAction(player1 -> {
            player1.isOnline();
            update(player, inventoryWrapper);
        }, false));
    }
}
