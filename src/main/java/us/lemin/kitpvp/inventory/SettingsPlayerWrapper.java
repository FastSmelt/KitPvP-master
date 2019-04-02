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
import us.lemin.kitpvp.player.KitProfile;

public class SettingsPlayerWrapper extends PlayerInventoryWrapper {
    private final KitPvPPlugin plugin;

    public SettingsPlayerWrapper(KitPvPPlugin plugin) {
        super("Settings", 3);
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

        KitProfile profile = plugin.getPlayerManager().getProfile(player);
        CoreProfile coreProfile = CorePlugin.getInstance().getProfileManager().getProfile(player.getUniqueId());
        String[] scoreboard = getSettingLore(profile.isScoreboardEnabled());
        String[] sounds = getSettingLore(coreProfile.isPlayingSounds());
        String[] messages = getSettingLore(coreProfile.isMessaging());
        String[] chat = getSettingLore(coreProfile.isGlobalChatEnabled());

        inventoryWrapper.fillBorder(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 1));

        inventoryWrapper.setItem(row, count++, new ItemBuilder(Material.SIGN).name(CC.PRIMARY + "Toggle Scoreboard")
                .lore(scoreboard).build(), new PlayerAction(player2 -> {
            player2.performCommand("togglescoreboard");
            update(player, inventoryWrapper);
        }, false));
        count++;
        inventoryWrapper.setItem(row, count++, new ItemBuilder(Material.REDSTONE).name(CC.PRIMARY + "Toggle Sounds").lore(sounds).build(), new PlayerAction(player2 -> {
            player2.performCommand("togglesounds");
            update(player, inventoryWrapper);
        }, false));
        count++;
        inventoryWrapper.setItem(row, count++, new ItemBuilder(Material.WATCH).name(CC.PRIMARY + "Toggle Messages").lore(messages).build(), new PlayerAction(player2 -> {
            player2.performCommand("togglemessages");
            update(player, inventoryWrapper);
        }, false));
        count++;
        inventoryWrapper.setItem(row, count++, new ItemBuilder(Material.LEVER).name(CC.PRIMARY + "Toggle Global Chat").lore(chat).build(), new PlayerAction(player2 -> {
            player2.performCommand("toggleglobalchat");
            update(player, inventoryWrapper);
        }, false));
    }

    private String[] getSettingLore(boolean value) {
        String one = value ? CC.GREEN + "Enabled" : CC.GRAY + "Enabled";
        String two = !value ? CC.RED + "Disabled" : CC.GRAY + "Disabled";
        return new String[]{one, two};
    }
}
