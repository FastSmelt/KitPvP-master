package us.lemin.kitpvp.events.impl;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import us.lemin.core.utils.item.ItemBuilder;
import us.lemin.core.utils.message.CC;
import us.lemin.kitpvp.KitPvPPlugin;
import us.lemin.kitpvp.events.Event;
import us.lemin.kitpvp.events.EventStage;
import us.lemin.kitpvp.events.EventType;
import us.lemin.kitpvp.events.ParticipantState;
import us.lemin.kitpvp.player.KitProfile;
import us.lemin.kitpvp.util.TempLocations;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class BracketsEvent extends Event implements Listener {
    private static final Random random = ThreadLocalRandom.current();

    public BracketsEvent(KitPvPPlugin plugin) {
        super(plugin, EventType.BRACKETS, TempLocations.BRACKETS_SPAWN, new ItemBuilder(Material.DIAMOND_SWORD).name(CC.GOLD + "Brackets").build());
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    private UUID getRandomPlayerId() {
        List<UUID> remaining = remainingPlayerIds();
        return remaining.get(random.nextInt(remaining.size()));
    }

    private UUID getRandomPlayerIdExcludingId(UUID excludedId) {
        List<UUID> remaining = remainingPlayerIds();
        UUID found = remaining.get(random.nextInt(remaining.size()));
        return found.equals(excludedId) ? getRandomPlayerIdExcludingId(excludedId) : found;
    }

    private void givePvPKit(Player player) {
        ItemStack sword = new ItemBuilder(Material.DIAMOND_SWORD).enchant(Enchantment.DAMAGE_ALL, 1).build();
        PotionEffect effect = new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0);
        player.addPotionEffect(effect);
        player.getInventory().setHelmet(new ItemStack(Material.IRON_HELMET));
        player.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
        player.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
        player.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS));
        player.getInventory().addItem(sword);
        int x = 0;
        while (x < 17) {
            x++;
            player.getInventory().addItem(new ItemStack(Material.MUSHROOM_SOUP));
        }
    }

    public void matchup() {
        UUID first = getRandomPlayerId();
        UUID second = getRandomPlayerIdExcludingId(first);

        setState(first, ParticipantState.FIGHTING);
        setState(second, ParticipantState.FIGHTING);

        Player firstPlayer = plugin.getServer().getPlayer(first);
        Player secondPlayer = plugin.getServer().getPlayer(second);

        firstPlayer.getInventory().clear();
        firstPlayer.setAllowFlight(false);
        givePvPKit(firstPlayer);
        secondPlayer.getInventory().clear();
        secondPlayer.setAllowFlight(false);
        givePvPKit(secondPlayer);

        firstPlayer.teleport(TempLocations.BRACKETS_SPAWN_A);
        secondPlayer.teleport(TempLocations.BRACKETS_SPAWN_B);

        broadcast(CC.PRIMARY + "Starting a new match between " + CC.SECONDARY + firstPlayer.getName()
                + CC.PRIMARY + " and " + CC.SECONDARY + secondPlayer.getName() + CC.PRIMARY + "!");
        setStage(EventStage.INTERMISSION);
    }

    @Override
    public void onStart() {
        matchup();
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
        switch (currentStage) {
            case INTERMISSION:
                int remaining = 5 - duration;

                if (remaining != 0) {
                    String time = remaining + CC.PRIMARY + (remaining == 1 ? " second!" : " seconds!");
                    broadcast(CC.PRIMARY + "The match will start in " + CC.SECONDARY + time);
                } else {
                    broadcast(CC.GREEN + "The match has started!");
                    setStage(EventStage.FIGHTING);
                }
                break;
        }
    }

    @Override
    public void onDeath(Player victim) {
        for (UUID id : remainingFighterIds()) {
            Player player = plugin.getServer().getPlayer(id);
            player.setHealth(20);
            resetToLobby(player);
        }
        victim.setHealth(20);
        setState(victim.getUniqueId(), ParticipantState.DEAD);

        remainingFighterIds().forEach(winner -> setState(winner, ParticipantState.ADVANCING));


        if (remainingPlayerIds().size() <= 1 && advancingPlayerIds().size() != 0) {
            advancingPlayerIds().forEach(advancing -> setState(advancing, ParticipantState.WAITING));
            round++;
        }

        int remaining = remainingPlayerIds().size() + advancingPlayerIds().size();

        if (remaining != 1) {
            broadcast(victim.getDisplayName() + CC.RED + " was defeated! Only " + remaining + "/" + MAX_PLAYERS + " remain.");
            matchup();
        } else {
            end();
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (!isEventApplicable(player) || getParticipantState(player.getUniqueId()) != ParticipantState.FIGHTING) {
            return;
        }

        Location to = event.getTo();
        Location from = event.getFrom();

        if (currentStage == EventStage.INTERMISSION
                && (to.getBlockX() != from.getBlockX() || to.getBlockZ() != from.getBlockZ())) {
            from.setX(from.getBlockX() + 0.5);
            from.setZ(from.getBlockZ() + 0.5);
            event.setTo(from);
        }
    }

    @EventHandler
    public void onHit(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();

        if (!isEventApplicable(player) || currentStage != EventStage.FIGHTING
                || getParticipantState(player.getUniqueId()) != ParticipantState.FIGHTING) {
            return;
        }

        if (player.getHealth() - event.getFinalDamage() <= 0) {
            event.setCancelled(true);
            onDeath(player);
            player.getInventory().clear();
        }
    }
}
