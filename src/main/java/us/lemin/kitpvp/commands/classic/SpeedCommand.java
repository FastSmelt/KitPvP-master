package us.lemin.kitpvp.commands.classic;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import us.lemin.core.utils.message.CC;
import us.lemin.kitpvp.KitPvPPlugin;
import us.lemin.kitpvp.player.KitProfile;
import us.lemin.kitpvp.server.Buffs;

public class SpeedCommand extends ClassicCommand {
    private final KitPvPPlugin plugin;

    public SpeedCommand(KitPvPPlugin plugin) {
        super("speed", plugin);
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {
        int speedLevel = 0;
        for (PotionEffect potionEffect : player.getActivePotionEffects()) {
            if (potionEffect.getType() == PotionEffectType.SPEED) {
                speedLevel = potionEffect.getAmplifier();
            }
        }

        if (speedLevel >= 2) {
            player.sendMessage(CC.RED + "You already have the maximum level of speed.");
            return;
        }

        KitProfile profile = plugin.getPlayerManager().getProfile(player);

        int cost = Buffs.SPEED.getPriceByLevel(speedLevel);

        if (profile.getStatistics().getCredits() < cost) {
            player.sendMessage(CC.RED + "You cannot afford a speed buff.");
            return;
        }
        final int finalSpeedLevel = speedLevel + 1;
        profile.getStatistics().setCredits(profile.getStatistics().getCredits() - cost);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 90 * 20, finalSpeedLevel));
        player.sendMessage(CC.PRIMARY + "You have been given " + CC.ACCENT + "Speed " + finalSpeedLevel + CC.PRIMARY + " for " + CC.ACCENT + cost + " credits.");


    }
}
