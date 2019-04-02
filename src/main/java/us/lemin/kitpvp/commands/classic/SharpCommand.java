package us.lemin.kitpvp.commands.classic;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import us.lemin.core.utils.message.CC;
import us.lemin.kitpvp.KitPvPPlugin;
import us.lemin.kitpvp.player.KitProfile;
import us.lemin.kitpvp.server.Buffs;

public class SharpCommand extends ClassicCommand {
    private final KitPvPPlugin plugin;

    public SharpCommand(KitPvPPlugin plugin) {
        super("sharpness", plugin);
        this.plugin = plugin;
        setAliases("sharp");
    }

    @Override
    public void execute(Player player, String[] args) {
        if (player.getItemInHand() == null || player.getItemInHand().getType() != Material.DIAMOND_SWORD) {
            player.sendMessage(CC.RED + "You must be holding a diamond sword.");
            return;
        }
        int sharpnessLevel = player.getItemInHand().getEnchantments().get(Enchantment.DAMAGE_ALL) == null ?
                0 : player.getItemInHand().getEnchantments().get(Enchantment.DAMAGE_ALL);

        if (sharpnessLevel >= 3) {
            player.sendMessage(CC.RED + "Your sword is already too powerful.");
            return;
        }

        KitProfile profile = plugin.getPlayerManager().getProfile(player);


        int cost = Buffs.SHARPNESS.getPriceByLevel(sharpnessLevel);

        if (profile.getStatistics().getCredits() < cost) {
            player.sendMessage(CC.RED + "You cannot afford to upgrade your sword.");
            return;
        }

        profile.getStatistics().setCredits(profile.getStatistics().getCredits() - cost);
        player.getItemInHand().addEnchantment(Enchantment.DAMAGE_ALL, sharpnessLevel + 1);
        player.sendMessage(CC.PRIMARY + "Your sword has been enchanted with Sharpness " + CC.ACCENT + ++sharpnessLevel + CC.PRIMARY + " for " + CC.ACCENT + cost + " credits.");

    }
}
