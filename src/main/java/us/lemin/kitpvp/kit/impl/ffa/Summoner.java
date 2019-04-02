package us.lemin.kitpvp.kit.impl.ffa;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerInteractEvent;
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
import java.util.concurrent.ThreadLocalRandom;

public class Summoner extends Kit {

    public Summoner(KitPvPPlugin plugin) {
        super(plugin, "Summoner", Material.EYE_OF_ENDER, "You summon a zombie and silverfish to attack your enemies.");
        registerCooldownTimer("summoner", 30);
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
        builder.addItem(new ItemBuilder(Material.EYE_OF_ENDER).name(CC.GOLD + "Summoner's Stone").build());
        builder.fill(new ItemStack(Material.MUSHROOM_SOUP));
        builder.addArmor(
                new ItemBuilder(Material.CHAINMAIL_BOOTS).enchant(Enchantment.DURABILITY, 5).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build(),
                new ItemStack(Material.IRON_LEGGINGS),
                new ItemStack(Material.IRON_CHESTPLATE),
                new ItemBuilder(Material.GOLD_HELMET).enchant(Enchantment.DURABILITY, 5).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build()
        );

        return builder;
    }

    private int randomPercent() {
        return ThreadLocalRandom.current().nextInt(0, 101);
    }

    @EventHandler
    public void onSpawn(PlayerInteractEvent event) {
        if (!event.hasItem() || event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            return;
        }

        if (event.getItem().getType() != Material.EYE_OF_ENDER) {
            return;
        }

        Player summoner = event.getPlayer();

        if (isInvalidKit(summoner)) {
            return;
        }

        Player mobTarget = null;
        double lastDistance = Double.MAX_VALUE;
        for (Player targets : summoner.getWorld().getPlayers()) {
            KitProfile targetProfile = plugin.getPlayerManager().getProfile(targets);
            if (targets == summoner || targetProfile.getCurrentKit() == plugin.getKitManager().getFfaKitByName("Summoner") || targetProfile.getState() == PlayerState.SPAWN)
                continue;

            double distance = summoner.getLocation().distance(targets.getLocation());
            if (distance < lastDistance) {
                lastDistance = distance;
                mobTarget = targets;
            }
        }

        if (mobTarget == null) {
            summoner.sendMessage(CC.RED + "No suitable targets for your summoned monsters.");
            return;
        }

        if (isOnCooldown(summoner, "summoner")) {
            return;
        }
        Giant giant = null;
        if (randomPercent() <= 1) {
            giant = (Giant) summoner.getWorld().spawnEntity(summoner.getLocation(), EntityType.GIANT);
            giant.setMaxHealth(80);
            giant.setHealth(80);
            giant.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1));
            giant.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 3));
            giant.setTarget(mobTarget);
        }
        Zombie zombie = (Zombie) summoner.getWorld().spawnEntity(summoner.getLocation(), EntityType.ZOMBIE);
        Silverfish silverfish = (Silverfish) summoner.getWorld().spawnEntity(summoner.getLocation(), EntityType.SILVERFISH);
        zombie.setMaxHealth(40);
        zombie.setHealth(40);
        zombie.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0));
        zombie.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1));
        zombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 3));
        zombie.setTarget(mobTarget);
        silverfish.setMaxHealth(40);
        silverfish.setHealth(40);
        silverfish.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1));
        silverfish.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 3));
        silverfish.setTarget(mobTarget);
        Giant finalGiant = giant;
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (finalGiant != null) {
                if (!finalGiant.isDead()) {
                    finalGiant.remove();
                }
            }
            if (!zombie.isDead()) {
                zombie.remove();
            }
            if (!silverfish.isDead()) {
                silverfish.remove();
            }
        }, 20 * 20);
    }


    @EventHandler
    public void onTarget(EntityTargetEvent event) {
        if (event.getTarget() instanceof Player) {
            Player player = ((Player) event.getTarget()).getPlayer();
            KitProfile profile = plugin.getPlayerManager().getProfile(player);
            if (profile.getCurrentKit() == plugin.getKitManager().getFfaKitByName("summoner") || profile.getState() == PlayerState.SPAWN) {
                event.setCancelled(true);
            }
        }
    }
}
