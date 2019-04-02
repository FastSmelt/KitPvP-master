package us.lemin.kitpvp.kit.impl.ffa;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import us.lemin.core.utils.item.ItemBuilder;
import us.lemin.core.utils.message.CC;
import us.lemin.kitpvp.KitPvPPlugin;
import us.lemin.kitpvp.kit.Kit;
import us.lemin.kitpvp.kit.KitContents;
import us.lemin.kitpvp.player.KitProfile;
import us.lemin.kitpvp.player.PlayerState;

import java.util.Collections;
import java.util.List;

public class Ninja extends Kit {

    public Ninja(KitPvPPlugin plugin) {
        super(plugin, "Ninja", Material.IRON_SWORD, "Teleports behind the last damaged player when sneaking.");
        registerCooldownTimer("ninja", 15);
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
                new ItemStack(Material.DIAMOND_BOOTS),
                new ItemStack(Material.IRON_LEGGINGS),
                new ItemStack(Material.IRON_CHESTPLATE)
        );

        return builder;
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        Player ninja = event.getPlayer();
        Player victim = plugin.getServer().getPlayer(plugin.getPlayerManager().getProfile(ninja).getLastAttacked());

        if (ninja.isSneaking()) {
            return;
        }

        if (victim == null) {
            return;
        }

        KitProfile kitProfile = plugin.getPlayerManager().getProfile(ninja);

        if (kitProfile.getState() != PlayerState.FFA) {
            return;
        }

        if (isInvalidKit(ninja)) {
            return;
        }

        if (ninja.getLocation().distanceSquared(victim.getLocation()) >= 400) {
            ninja.sendMessage(CC.RED + "Target is too far away.");
            return;
        }

        if (isOnCooldown(ninja, "ninja")) {
            return;
        }
        ninja.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 0));
        ninja.teleport(victim);
    }
}

