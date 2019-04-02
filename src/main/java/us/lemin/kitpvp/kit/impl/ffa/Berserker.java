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
import us.lemin.core.utils.message.CC;
import us.lemin.core.utils.player.PlayerUtil;
import us.lemin.kitpvp.KitPvPPlugin;
import us.lemin.kitpvp.kit.Kit;
import us.lemin.kitpvp.kit.KitContents;

import java.util.Collections;
import java.util.List;

public class Berserker extends Kit {
    public Berserker(KitPvPPlugin plugin) {
        super(plugin, "Berserker", Material.BLAZE_POWDER, "You receive Strength II for 30 seconds after killing another player.");
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

        builder.addItem(new ItemBuilder(Material.DIAMOND_SWORD).name(CC.GOLD + "Berserker Sword").build());
        builder.fill(new ItemStack(Material.MUSHROOM_SOUP));
        builder.addArmor(
                new ItemStack(Material.IRON_BOOTS),
                new ItemBuilder(Material.GOLD_LEGGINGS).enchant(Enchantment.DURABILITY, 4).build(),
                new ItemStack(Material.IRON_CHESTPLATE),
                new ItemStack(Material.IRON_HELMET)
        );

        return builder;
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player)) {
            return;
        }

        Player berserker = (Player) event.getDamager();
        Player damaged = (Player) event.getEntity();

        if (isInvalidKit(berserker)) {
            return;
        }

        if (damaged.getHealth() - event.getFinalDamage() <= 0) {
            if (PlayerUtil.hasEffect(berserker, PotionEffectType.INCREASE_DAMAGE)) {
                berserker.getActivePotionEffects().stream().filter(potionEffect -> potionEffect.getType().equals(PotionEffectType.INCREASE_DAMAGE)).map(PotionEffect::getType).forEach(berserker::removePotionEffect);
            }
            berserker.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 30, 1));
            berserker.sendMessage(CC.GREEN + "You have received strength for killing a player!");
        }
    }
}
