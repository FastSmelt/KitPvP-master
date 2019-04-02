package us.lemin.kitpvp.server;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import us.lemin.kitpvp.KitPvPPlugin;
import us.lemin.kitpvp.player.KitProfile;

import java.util.function.Consumer;

public enum KillStreakReward {
    /*
     * the strength is an example, use this to make killstreaks
     *
     */


    STRENGTH("Strength I", "Gives player Strength I for 30 seconds.", player -> {
        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 30, 1));
    }),
    CREDITS("100 Credits", "Gives player 100 Credits.", player -> {
        KitProfile profile = KitPvPPlugin.getInstance().getPlayerManager().getProfile(player);
        profile.getStatistics().setCredits(profile.getStatistics().getCredits() + 100);
    });


    private String name;
    private String description;
    private Consumer<Player> playerConsumer;


    KillStreakReward(String name, String description, Consumer<Player> playerConsumer) {
        this.name = name;
        this.description = description;
        this.playerConsumer = playerConsumer;
    }

    public static KillStreakReward getByName(String name) {
        for (KillStreakReward killStreakReward : values()) {
            if (killStreakReward.name().equalsIgnoreCase(name)) {
                return killStreakReward;
            }
        }

        return null;
    }

    public void apply(Player player) {
        playerConsumer.accept(player);
    }
}
