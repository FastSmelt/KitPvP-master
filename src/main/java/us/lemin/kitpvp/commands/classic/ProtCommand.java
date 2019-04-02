package us.lemin.kitpvp.commands.classic;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import us.lemin.core.utils.message.CC;
import us.lemin.kitpvp.KitPvPPlugin;
import us.lemin.kitpvp.player.KitProfile;
import us.lemin.kitpvp.server.Buffs;

import java.util.Arrays;
import java.util.Objects;

public class ProtCommand extends ClassicCommand {
    private final KitPvPPlugin plugin;

    public ProtCommand(KitPvPPlugin plugin) {
        super("protection", plugin);
        this.setAliases("prot");
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {
        if (player.getInventory().getChestplate() == null) {
            player.sendMessage(CC.RED + "You must have a chestplate in order to upgrade your armor.");
            return;
        }

        int protectionLevel = player.getInventory().getChestplate().getEnchantments().get(Enchantment.PROTECTION_ENVIRONMENTAL) == null ?
                0 : player.getInventory().getChestplate().getEnchantments().get(Enchantment.PROTECTION_ENVIRONMENTAL);

        if (protectionLevel >= 2) {
            player.sendMessage(CC.RED + "Your armor is already too powerful.");
            return;
        }

        KitProfile profile = plugin.getPlayerManager().getProfile(player);

        int cost = Buffs.PROTECTION.getPriceByLevel(protectionLevel);

        if (profile.getStatistics().getCredits() < cost) {
            player.sendMessage(CC.RED + "You cannot afford to upgrade your armor.");
            return;
        }
        PlayerInventory playerInventory = player.getInventory();
        Arrays.stream(playerInventory.getArmorContents()).filter(Objects::nonNull).forEach
                (itemStack -> itemStack.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, protectionLevel + 1));

        final int finalProtectionLevel = protectionLevel + 1;
        profile.getStatistics().setCredits(profile.getStatistics().getCredits() - cost);
        player.sendMessage(CC.PRIMARY + "Your armor has been enchanted with Protection " + CC.ACCENT + finalProtectionLevel + CC.PRIMARY + " for " + CC.ACCENT + cost + " credits.");

    }
}
