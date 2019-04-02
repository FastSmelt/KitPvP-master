package us.lemin.kitpvp.commands.classic;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import us.lemin.core.utils.message.CC;
import us.lemin.kitpvp.KitPvPPlugin;
import us.lemin.kitpvp.player.KitProfile;
import us.lemin.kitpvp.server.Buffs;

public class PowerCommand extends ClassicCommand {
    private final KitPvPPlugin plugin;

    public PowerCommand(KitPvPPlugin plugin) {
        super("power", plugin);
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {
        if (player.getItemInHand() == null || player.getItemInHand().getType() != Material.BOW) {
            player.sendMessage(CC.RED + "You must be holding a bow.");
            return;
        }

        int powerLevel = player.getItemInHand().getEnchantments().get(Enchantment.ARROW_DAMAGE) == null ?
                0 : player.getItemInHand().getEnchantments().get(Enchantment.ARROW_DAMAGE);

        if (powerLevel >= 3) {
            player.sendMessage(CC.RED + "Your bow is already too powerful.");
            return;
        }

        KitProfile profile = plugin.getPlayerManager().getProfile(player);


        int cost = Buffs.POWER.getPriceByLevel(powerLevel);

        if (profile.getStatistics().getCredits() < cost) {
            player.sendMessage(CC.RED + "You cannot afford to upgrade your bow.");
            return;
        }

        profile.getStatistics().setCredits(profile.getStatistics().getCredits() - cost);
        player.getItemInHand().addEnchantment(Enchantment.ARROW_DAMAGE, powerLevel + 1);
        player.sendMessage(CC.PRIMARY + "Your sword has been enchanted with Power " + CC.ACCENT + ++powerLevel + CC.PRIMARY + " for " + CC.ACCENT + cost + " credits.");

    }
}