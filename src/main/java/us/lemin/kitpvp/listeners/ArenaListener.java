package us.lemin.kitpvp.listeners;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import us.lemin.core.utils.message.CC;
import us.lemin.kitpvp.KitPvPPlugin;
import us.lemin.kitpvp.managers.ArenaManager;
import us.lemin.kitpvp.player.KitProfile;
import us.lemin.kitpvp.player.PlayerState;

import java.util.Arrays;

@RequiredArgsConstructor
public class ArenaListener implements Listener {
    private final KitPvPPlugin plugin;
    private final ArenaManager arenaManager;

    @EventHandler
    public void onLeave(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            return;
        }

        if (player.getItemInHand() == null) {
            return;
        }

        if (player.getItemInHand().getType() == Material.REDSTONE) {
            plugin.getPlayerManager().resetPlayer(player, true);
        }
    }

    @EventHandler
    public void onRequest(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) {
            return;
        }

        Player victim = (Player) event.getEntity();
        Player damager = (Player) event.getDamager();

        if (!arenaManager.canRequest(damager) || !arenaManager.canRequest(victim)) {
            return;
        }

        if (damager.getItemInHand() == null) {
            return;
        }

        event.setCancelled(true);

        KitProfile damagerProfile = plugin.getPlayerManager().getProfile(damager);
        KitProfile victimProfile = plugin.getPlayerManager().getProfile(victim);

        if (damager.getItemInHand().getType() == Material.BLAZE_ROD) {
            if (damagerProfile.getRequestedDuels().contains(victim.getUniqueId())) {
                damagerProfile.getRequestedDuels().clear();
                victimProfile.getRequestedDuels().clear();
                plugin.getArenaManager().startMatch(damager, victim);
                damagerProfile.setState(PlayerState.DUEL);
                victimProfile.setState(PlayerState.DUEL);
                return;
            }
            if (victimProfile.getRequestedDuels().contains(damager.getUniqueId())) {
                damager.sendMessage(CC.RED + "You have already sent " + victim.getName() + " a duel request.");
            } else {
                victimProfile.getRequestedDuels().add(damager.getUniqueId());
                damager.sendMessage(CC.GREEN + "You have successfully sent a duel request to " + victim.getName() + ".");
                victim.sendMessage(CC.GREEN + "You have received a duel request from " + damager.getName() + ".");
            }
        }
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) {
            return;
        }

        Player victim = (Player) event.getEntity();
        Player damager = (Player) event.getDamager();

        KitProfile damagerProfile = plugin.getPlayerManager().getProfile(damager);
        KitProfile victimProfile = plugin.getPlayerManager().getProfile(victim);

        if (damagerProfile.getState() != PlayerState.DUEL || victimProfile.getState() != PlayerState.DUEL) {
            return;
        }
        if (victim.getHealth() - event.getFinalDamage() <= 0) {
            int soups = (int) Arrays.stream(damager.getInventory().getContents()).filter(item -> item != null && item.getType() == Material.MUSHROOM_SOUP).count();
            Arrays.asList(damager.getUniqueId(), victim.getUniqueId()).forEach(uuid -> {
                Player player = plugin.getServer().getPlayer(uuid);
                player.getInventory().setArmorContents(null);
                player.getInventory().clear();
                player.setHealth(20);
                player.getInventory().setHeldItemSlot(2);
                player.sendMessage(damager.getDisplayName() + CC.PRIMARY + " won the match with " + CC.ACCENT + soups + CC.PRIMARY + " soups.");
                plugin.getPlayerManager().giveArenaItems(player);
                for (Player players : plugin.getServer().getOnlinePlayers()) {
                    player.showPlayer(players);
                }

            });
            event.setCancelled(true);
            damagerProfile.getStatistics().setDuelWins(damagerProfile.getStatistics().getDuelWins() + 1);
            victimProfile.getStatistics().setDuelLosses(victimProfile.getStatistics().getDuelLosses() + 1);
        }
    }
}
