package us.lemin.kitpvp.kit.impl.ffa;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import us.lemin.core.utils.item.ItemBuilder;
import us.lemin.kitpvp.KitPvPPlugin;
import us.lemin.kitpvp.kit.Kit;
import us.lemin.kitpvp.kit.KitContents;
import us.lemin.kitpvp.player.KitProfile;
import us.lemin.kitpvp.player.PlayerState;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Vampire extends Kit {
    public Vampire(KitPvPPlugin plugin) {
        super(plugin, "Vampire", Material.GHAST_TEAR, "Suck the blood from your opponents.");
    }

    @Override
    protected void onEquip(Player player) {
        // NO-OP
    }

    @Override
    protected List<PotionEffect> effects() {
        return Collections.singletonList(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0));
    }

    @Override
    protected KitContents.Builder contentsBuilder() {
        KitContents.Builder builder = KitContents.newBuilder();

        builder.addItem(new ItemBuilder(Material.IRON_SWORD).enchant(Enchantment.DAMAGE_ALL, 1).enchant(Enchantment.DURABILITY, 2).build());
        builder.fill(new ItemStack(Material.MUSHROOM_SOUP));
        builder.addArmor(
                new ItemBuilder(Material.GOLD_BOOTS).enchant(Enchantment.DURABILITY, 3).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build(),
                new ItemBuilder(Material.GOLD_LEGGINGS).enchant(Enchantment.DURABILITY, 3).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build(),
                new ItemBuilder(Material.CHAINMAIL_CHESTPLATE).enchant(Enchantment.DURABILITY, 3).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 3).build(),
                new ItemBuilder(Material.IRON_HELMET).build()
        );

        return builder;
    }

    private int randomPercent() {
        return ThreadLocalRandom.current().nextInt(0, 101);
    }

    @EventHandler
    public void onDrain(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player)) {
            return;
        }

        Player vampire = (Player) event.getDamager();
        Player victim = (Player) event.getEntity();

        if (isInvalidKit(vampire)) {
            return;
        }

        KitProfile victimProfile = plugin.getPlayerManager().getProfile(victim);

        if (victimProfile.getState() != PlayerState.FFA) {
            return;
        }

        if (randomPercent() <= 25) {
            double health = vampire.getHealth() + 2;

            vampire.setHealth(health > 20.0 ? 20.0 : health);
            victim.damage(2, vampire);
        }
    }
}
