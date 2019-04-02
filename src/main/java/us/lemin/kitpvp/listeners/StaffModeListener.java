package us.lemin.kitpvp.listeners;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import us.lemin.core.CorePlugin;
import us.lemin.core.player.CoreProfile;
import us.lemin.kitpvp.KitPvPPlugin;
import us.lemin.kitpvp.player.KitProfile;
import us.lemin.kitpvp.player.PlayerState;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@RequiredArgsConstructor
public class StaffModeListener implements Listener {

    private final KitPvPPlugin plugin;

    @EventHandler
    public void onPlayerPickup(PlayerPickupItemEvent e) {
        Player player = e.getPlayer();
        KitProfile profile = plugin.getPlayerManager().getProfile(player);


        if (profile.getState() == PlayerState.STAFF) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDrop(PlayerDropItemEvent e) {
        Player player = e.getPlayer();
        KitProfile profile = plugin.getPlayerManager().getProfile(player);


        if (profile.getState() == PlayerState.STAFF) {
            e.setCancelled(true);
        }
    }


    @EventHandler
    public void EntityDamageEvent(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        KitProfile profile = plugin.getPlayerManager().getProfile(player);


        if (profile.getState() == PlayerState.STAFF) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Player)) {
            return;
        }
        Player player = event.getPlayer();
        KitProfile profile = plugin.getPlayerManager().getProfile(player);

        if (profile.getState() != PlayerState.STAFF) {
            return;
        }


        Player clicked = (Player) event.getRightClicked();

//        if (clicked.getType() == EntityType.PLAYER) {
//            plugin.getPlayerManager().startExaminationTask(e.getPlayer(), (Player) e.getRightClicked());
//        }
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        KitProfile profile = plugin.getPlayerManager().getProfile(player);

        if (profile.getState() != PlayerState.STAFF) {
            return;
        }

        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {

            if (e.getPlayer().getItemInHand().getType() == Material.RECORD_3) {

                List<Player> playersOnline = new ArrayList<>(Bukkit.getServer().getOnlinePlayers());

                if (playersOnline.size() <= 0) {
                    e.getPlayer().sendMessage(ChatColor.RED + "There are no players online.");
                    e.setCancelled(true);
                    return;
                }

                int random = ThreadLocalRandom.current().nextInt(0, playersOnline.size());

                Player p = playersOnline.get(random);
                CoreProfile coreProfile = CorePlugin.getInstance().getProfileManager().getProfile(p.getUniqueId());

                e.getPlayer().teleport(p);

                e.getPlayer().sendMessage(ChatColor.YELLOW + "Teleported to " + coreProfile.getRank().getColor() + p.getName());
                return;
            }
            if (e.getClickedBlock() != null) {
                if (e.getClickedBlock().getType() == Material.CHEST) {
                    Chest chest = (Chest) e.getClickedBlock().getState();

                    Inventory inv = Bukkit.createInventory(null, chest.getInventory().getSize(), "Fake Chest");
                    inv.setContents(chest.getInventory().getContents());

                    e.getPlayer().openInventory(inv);
                    e.setCancelled(true);
                    e.getPlayer().sendMessage(ChatColor.BLUE + "Fake chest being opened.");
                    return;
                }
            }
        }
    }
}

