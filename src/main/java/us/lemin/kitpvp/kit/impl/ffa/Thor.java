package us.lemin.kitpvp.kit.impl.ffa;

import net.minecraft.server.v1_8_R3.EntityLightning;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityWeather;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import us.lemin.core.utils.item.ItemBuilder;
import us.lemin.core.utils.message.CC;
import us.lemin.kitpvp.KitPvPPlugin;
import us.lemin.kitpvp.kit.Kit;
import us.lemin.kitpvp.kit.KitContents;
import us.lemin.kitpvp.player.KitProfile;
import us.lemin.kitpvp.player.PlayerState;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Thor extends Kit {

    public Thor(KitPvPPlugin plugin) {
        super(plugin, "Thor", Material.IRON_AXE, "Call lightning strikes upon your foes as the god of thunder.");
        registerCooldownTimer("thor", 15);
    }

    @Override
    protected void onEquip(Player player) {

    }

    @Override
    protected List<PotionEffect> effects() {
        return Collections.singletonList(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0));
    }

    @Override
    protected KitContents.Builder contentsBuilder() {
        KitContents.Builder builder = KitContents.newBuilder();

        builder.addItem(new ItemBuilder(Material.IRON_AXE).enchant(Enchantment.DAMAGE_ALL, 2).enchant(Enchantment.DURABILITY, 3).name(CC.GOLD + "Stormbreaker").build());
        builder.fill(new ItemStack(Material.MUSHROOM_SOUP));
        builder.addArmor(
                new ItemBuilder(Material.GOLD_BOOTS).enchant(Enchantment.DURABILITY, 4).build(),
                new ItemStack(Material.IRON_LEGGINGS),
                new ItemStack(Material.IRON_CHESTPLATE),
                new ItemBuilder(Material.GOLD_HELMET).enchant(Enchantment.DURABILITY, 4).build()
        );

        return builder;
    }

    @EventHandler
    public void onThor(PlayerInteractEvent event) {
        if (!event.hasItem() || event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            return;
        }

        if (event.getItem().getType() != Material.IRON_AXE) {
            return;
        }

        Player thor = event.getPlayer();

        KitProfile kitProfile = plugin.getPlayerManager().getProfile(thor);

        if (kitProfile.getState() != PlayerState.FFA) {
            return;
        }

        if (isInvalidKit(thor) || isOnCooldown(thor, "thor")) {
            return;
        }

        List<Player> nearbyEntities = thor.getNearbyEntities(7.0, 7.0, 7.0)
                .stream()
                .filter(Player.class::isInstance)
                .map(Player.class::cast)
                .collect(Collectors.toList());

        if (nearbyEntities.size() > 0) {
            for (Player players : nearbyEntities) {
                players.sendMessage(CC.RED + "Thor's storm is upon you...");
            }
        }

        thor.setAllowFlight(true);
        thor.setFlying(true);
        thor.sendMessage(CC.GREEN + "Stormbreaker has given you the ability to fly for a short time.");

        new BukkitRunnable() {
            int count = 0;

            @Override
            public void run() {
                if (count++ == 3 || !thor.isOnline()) {
                    thor.setAllowFlight(false);
                    thor.setFlying(false);
                    thor.setSprinting(true);
                    cancel();
                    return;
                }

                List<Player> nearbyEntities = thor.getNearbyEntities(7.0, 7.0, 7.0)
                        .stream()
                        .filter(Player.class::isInstance)
                        .map(Player.class::cast)
                        .collect(Collectors.toList());


                if (nearbyEntities.size() > 0) {
                    for (Player victims : nearbyEntities) {

                        Location location = victims.getLocation();
                        EntityLightning lightning = new EntityLightning(((CraftWorld) location.getWorld()).getHandle(), location.getX(), location.getY(), location.getZ());
                        PacketPlayOutSpawnEntityWeather weatherPacket = new PacketPlayOutSpawnEntityWeather(lightning);

                        for (Player players : Bukkit.getOnlinePlayers()) {
                            ((CraftPlayer) players).getHandle().playerConnection.sendPacket(weatherPacket);
                            players.playSound(players.getLocation(), Sound.AMBIENCE_THUNDER, 64.0f, 64.0f);
                        }

                        KitProfile victimProfile = plugin.getPlayerManager().getProfile(victims);
                        if (victimProfile.getState() == PlayerState.FFA) {
                            victimProfile.getDamageData().put(thor.getUniqueId(), 5);
                            org.bukkit.util.Vector up = new org.bukkit.util.Vector(0, .6, 0);
                            victims.damage(4);
                            /*victims.setVelocity(up);*/
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 20);
        kitProfile.setFallDamageEnabled(false);

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (thor.isOnline()) {
                kitProfile.setFallDamageEnabled(true);
            }
        }, 20L * 5);
    }
}
