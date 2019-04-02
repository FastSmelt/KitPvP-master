package us.lemin.kitpvp.listeners;

import lombok.RequiredArgsConstructor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import us.lemin.core.utils.message.CC;
import us.lemin.core.utils.timer.Timer;
import us.lemin.kitpvp.KitPvPPlugin;
import us.lemin.kitpvp.events.Event;
import us.lemin.kitpvp.events.EventStage;
import us.lemin.kitpvp.events.ParticipantState;
import us.lemin.kitpvp.kit.Kit;
import us.lemin.kitpvp.kit.impl.ffa.Turtle;
import us.lemin.kitpvp.managers.KitManager;
import us.lemin.kitpvp.player.KitProfile;
import us.lemin.kitpvp.player.PlayerState;
import us.lemin.kitpvp.server.ServerMode;

@RequiredArgsConstructor
public class EntityListener implements Listener {
    private static final double DAMAGE_PER_STRENGTH_LEVEL = 1.5;
    private final KitPvPPlugin plugin;

    @EventHandler
    public void onHit(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player damager = (Player) event.getEntity();
        KitProfile damagerProfile = plugin.getPlayerManager().getProfile(damager);

        if (damagerProfile.getState() != PlayerState.EVENT) {
            return;
        }

        Event activeEvent = damagerProfile.getActiveEvent();

        if (activeEvent.getCurrentStage() != EventStage.FIGHTING) {
            event.setCancelled(true);
        }

        if (activeEvent.getParticipantState(damagerProfile.getId()) != ParticipantState.FIGHTING) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onShoot(ProjectileLaunchEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity().getShooter();

        if (player.getGameMode() != GameMode.SURVIVAL) {
            return;
        }

        KitProfile profile = plugin.getPlayerManager().getProfile(player);

        if (profile.getState() != PlayerState.FFA) {
            return;
        }

        if (event.getEntity() instanceof EnderPearl) {
            Timer timer = profile.getPearlTimer();

            timer.isActive(); // check active
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player victim = (Player) event.getEntity();
        KitProfile victimProfile = plugin.getPlayerManager().getProfile(victim);

        if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
            victim.teleport(plugin.getSpawnLocation());
        } else if (event.getCause() == EntityDamageEvent.DamageCause.FALL && !victimProfile.isFallDamageEnabled()) {
            event.setCancelled(true);
            return;
        }

        if (victimProfile.getState() == PlayerState.SPAWN) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDamageByPlayer(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)
                || (!(event.getDamager() instanceof Player) && (!(event.getDamager() instanceof Arrow)
                || !(((Arrow) event.getDamager()).getShooter() instanceof Player)))) {
            return;
        }

        Player damager = event.getDamager() instanceof Player ? (Player) event.getDamager() : (Player) ((Arrow) event.getDamager()).getShooter();

        KitProfile damagerProfile = plugin.getPlayerManager().getProfile(damager);

        Player victim = (Player) event.getEntity();

        KitProfile victimProfile = plugin.getPlayerManager().getProfile(victim);

        if (victimProfile.getState() == PlayerState.SPAWN) {
            damager.sendMessage(CC.RED + "That player currently has spawn protection.");
            return;
        }

        if (damager.getAllowFlight()) {
            if (damagerProfile.getCurrentKit() != null) {
                if (!damagerProfile.getCurrentKit().getName().equalsIgnoreCase("Thor")) {
                    damager.setAllowFlight(false);
                    damager.setFlying(false);
                }
            } else {
                damager.setAllowFlight(false);
                damager.setFlying(false);
            }
        }

        KitManager kitManager = plugin.getKitManager();

        if (damagerProfile.getState() == PlayerState.SPAWN && victimProfile.getState() == PlayerState.FFA) {
            if (plugin.getServerMode() == ServerMode.REGULAR) {
                if (damagerProfile.getLastKit() != null) {
                    damagerProfile.getLastKit().apply(damager);
                } else {
                    kitManager.getDefaultKit().apply(damager);
                }
            } else {
                plugin.getPlayerManager().giveClassicKit(damager);
            }
            damagerProfile.setState(PlayerState.FFA);
            damager.sendMessage(CC.RED + "You no longer have spawn protection!");
        }

        if (victimProfile.getState() == PlayerState.FFA) {
            victimProfile.getDamageData().put(event.getDamager().getUniqueId(), event.getFinalDamage());
            damagerProfile.setLastAttacked(victim.getUniqueId());
        }

        // todo: implement below into their respective classes //
        Kit turtle = kitManager.getFfaKitByClass(Turtle.class);
        if (victimProfile.isFrozen() || (victimProfile.getCurrentKit() == turtle && victim.isSneaking()) || (damagerProfile.getCurrentKit() == turtle && damager.isSneaking())) {
            victim.damage(event.getFinalDamage());
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDamageStrengthNerf(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getDamager();

        for (PotionEffect effect : player.getActivePotionEffects()) {
            if (effect.getType().equals(PotionEffectType.INCREASE_DAMAGE)) {
                ItemStack heldItem = player.getItemInHand() != null ? player.getItemInHand() : new ItemStack(Material.AIR);

                int sharpnessLevel = heldItem.getEnchantmentLevel(Enchantment.DAMAGE_ALL);
                int strengthLevel = effect.getAmplifier() + 1;

                double totalDamage = event.getDamage();
                double weaponDamage = (totalDamage - 1.25 * sharpnessLevel) / (1.0 + 1.3 * strengthLevel) - 1.0;
                double finalDamage = 1.0 + weaponDamage + 1.25 * sharpnessLevel + (DAMAGE_PER_STRENGTH_LEVEL * 2) * strengthLevel;

                event.setDamage(finalDamage);
                break;
            }
        }
    }

    @EventHandler
    public void onMobDeath(EntityDeathEvent event) {
        event.getDrops().clear();
    }
}
