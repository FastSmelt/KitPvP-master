package us.lemin.kitpvp.commands.classic;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import us.lemin.core.utils.message.CC;
import us.lemin.kitpvp.KitPvPPlugin;
import us.lemin.kitpvp.player.KitProfile;
import us.lemin.kitpvp.server.Buffs;

public class KnockbackCommand extends ClassicCommand {
    private final KitPvPPlugin plugin;

    public KnockbackCommand(KitPvPPlugin plugin) {
        super("knockback", plugin);
        this.plugin = plugin;
        setAliases("knock");
    }

    @Override
    public void execute(Player player, String[] args) {
        if (player.getItemInHand() == null || player.getItemInHand().getType() != Material.DIAMOND_SWORD) {
            player.sendMessage(CC.RED + "You must be holding a diamond sword.");
            return;
        }
        int knockbackLevel = player.getItemInHand().getEnchantments().get(Enchantment.KNOCKBACK) == null ?
                0 : player.getItemInHand().getEnchantments().get(Enchantment.KNOCKBACK);

        if (knockbackLevel >= 2) {
            player.sendMessage(CC.RED + "Your sword is already too powerful.");
            return;
        }

        KitProfile profile = plugin.getPlayerManager().getProfile(player);


        int cost = Buffs.KNOCKBACK.getPriceByLevel(knockbackLevel);

        if (profile.getStatistics().getCredits() < cost) {
            player.sendMessage(CC.RED + "You cannot afford to upgrade your sword.");
            return;
        }

        profile.getStatistics().setCredits(profile.getStatistics().getCredits() - cost);
        player.getItemInHand().addEnchantment(Enchantment.KNOCKBACK, knockbackLevel + 1);
        player.sendMessage(CC.PRIMARY + "Your sword has been enchanted with Knockback " + CC.ACCENT + ++knockbackLevel + CC.PRIMARY + " for " + CC.ACCENT + cost + " credits.");

    }
}
