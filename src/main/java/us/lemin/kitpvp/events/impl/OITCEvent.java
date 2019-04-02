package us.lemin.kitpvp.events.impl;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import us.lemin.core.utils.item.ItemBuilder;
import us.lemin.core.utils.message.CC;
import us.lemin.kitpvp.KitPvPPlugin;
import us.lemin.kitpvp.events.Event;
import us.lemin.kitpvp.events.EventStage;
import us.lemin.kitpvp.events.EventType;
import us.lemin.kitpvp.events.ParticipantState;
import us.lemin.kitpvp.player.KitProfile;
import us.lemin.kitpvp.util.TempLocations;

import java.util.UUID;

public class OITCEvent extends Event implements Listener {


    public OITCEvent(KitPvPPlugin plugin) {
        super(plugin, EventType.OITC, TempLocations.OITC_SPAWN, new ItemBuilder(Material.BOW).name(CC.GOLD + "OITC").build());
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    private void setup() {
        remainingPlayerIds().forEach(uuid -> {
            Player player = plugin.getServer().getPlayer(uuid);
            player.teleport(TempLocations.OITC_SPAWN);
            giveItems(player);
        });
    }

    private void giveItems(Player player) {
        ItemStack sword = new ItemBuilder(Material.STONE_SWORD).name(CC.GOLD + "Sword").enchant(Enchantment.DAMAGE_ALL, 1).enchant(Enchantment.DURABILITY, 3).build();
        ItemStack bow = new ItemBuilder(Material.BOW).name(CC.GOLD + "Bow").build();
        player.getInventory().addItem(sword, bow, new ItemStack(Material.ARROW, 1));
    }

    @Override
    public void onStart() {
        setup();
    }

    @Override
    public void onEnd() {
        UUID winnerId = remainingPlayerIds().get(0);
        Player winnerPlayer = plugin.getServer().getPlayer(winnerId);
        KitProfile profile = plugin.getPlayerManager().getProfile(winnerPlayer);
        profile.getStatistics().setEventWins(profile.getStatistics().getEventWins() + 1);

        broadcast(winnerPlayer.getDisplayName() + CC.GREEN + " won the event!");
    }

    @Override
    public void onTick() {
        // NO-OP
    }

    @Override
    public void onDeath(Player victim) {
        KitProfile profile = plugin.getPlayerManager().getProfile(victim);

        setState(victim.getUniqueId(), ParticipantState.DEAD);
        leave(victim, profile);

        int remaining = remainingFighterIds().size();

        if (remaining != 1) {
            broadcast(victim.getDisplayName() + CC.RED + " was defeated! Only " + remaining + "/" + MAX_PLAYERS + " remain.");
        } else {
            end();
        }
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)
                || (!(event.getDamager() instanceof Player) && (!(event.getDamager() instanceof Arrow)
                || !(((Arrow) event.getDamager()).getShooter() instanceof Player)))) {
            return;
        }

        Player victim = (Player) event.getEntity();


        if (!isEventApplicable(victim) || currentStage != EventStage.FIGHTING
                || getParticipantState(victim.getUniqueId()) != ParticipantState.FIGHTING) {
            return;
        }

        Player damager = (Player) event.getDamager();


        boolean died = victim.getHealth() - event.getFinalDamage() <= 0;
        if (died) {
            event.setCancelled(true);
            onDeath(victim);
            damager.getInventory().addItem(new ItemStack(Material.ARROW, 1));
        }
    }
}
