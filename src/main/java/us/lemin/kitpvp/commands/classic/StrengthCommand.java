package us.lemin.kitpvp.commands.classic;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import us.lemin.core.utils.message.CC;
import us.lemin.kitpvp.KitPvPPlugin;
import us.lemin.kitpvp.player.KitProfile;
import us.lemin.kitpvp.server.Buffs;

public class StrengthCommand extends ClassicCommand {
    private final KitPvPPlugin plugin;

    public StrengthCommand(KitPvPPlugin plugin) {
        super("strength", plugin);
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {
        int strengthLevel = 0;
        for (PotionEffect potionEffect : player.getActivePotionEffects()) {
            if (potionEffect.getType() == PotionEffectType.INCREASE_DAMAGE) {
                strengthLevel = potionEffect.getAmplifier();
            }
        }

        if (strengthLevel >= 2) {
            player.sendMessage(CC.RED + "You already have the maximum level of strength.");
            return;
        }

        KitProfile profile = plugin.getPlayerManager().getProfile(player);

        int cost = Buffs.STRENGTH.getPriceByLevel(strengthLevel);

        if (profile.getStatistics().getCredits() < cost) {
            player.sendMessage(CC.RED + "You cannot afford a strength buff.");
            return;
        }
        int finalStrengthLevel = strengthLevel + 1;

        profile.getStatistics().setCredits(profile.getStatistics().getCredits() - cost);
        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 90 * 20, finalStrengthLevel));
        player.sendMessage(CC.PRIMARY + "You have been given " + CC.ACCENT + "Strength " + finalStrengthLevel + CC.PRIMARY + " for " + CC.ACCENT + cost + " credits.");


    }
}
