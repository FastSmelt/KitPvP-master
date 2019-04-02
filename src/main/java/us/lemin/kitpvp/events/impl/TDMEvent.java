package us.lemin.kitpvp.events.impl;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import us.lemin.core.utils.StringUtil;
import us.lemin.core.utils.item.ItemBuilder;
import us.lemin.core.utils.message.CC;
import us.lemin.kitpvp.KitPvPPlugin;
import us.lemin.kitpvp.events.Event;
import us.lemin.kitpvp.events.EventStage;
import us.lemin.kitpvp.events.EventType;
import us.lemin.kitpvp.events.ParticipantState;
import us.lemin.kitpvp.player.KitProfile;
import us.lemin.kitpvp.util.TempLocations;

import java.util.*;

public class TDMEvent extends Event implements Listener {

    public TDMEvent(KitPvPPlugin plugin) {
        super(plugin, EventType.TDM, TempLocations.TDM_SPAWN, new ItemBuilder(Material.IRON_HELMET).name(CC.GOLD + "Team Deathmatch").build());
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    private Set<UUID> blueTeam = new HashSet<>();
    private Set<UUID> redTeam = new HashSet<>();

    private void givePvPKit() {
        remainingPlayerIds().forEach(uuid -> {
            setState(uuid, ParticipantState.FIGHTING);

            Player player = plugin.getServer().getPlayer(uuid);

            ItemStack sword = new ItemBuilder(Material.DIAMOND_SWORD).enchant(Enchantment.DAMAGE_ALL, 1).build();
            PotionEffect effect = new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0);
            player.addPotionEffect(effect);
            player.getInventory().setHelmet(new ItemStack(Material.IRON_HELMET));
            player.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
            player.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
            player.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS));
            player.getInventory().addItem(sword);
            while (player.getInventory().firstEmpty() != -1) {
                player.getInventory().setItem(player.getInventory().firstEmpty(), new ItemStack(Material.MUSHROOM_SOUP));
            }
            Scoreboard scoreboard = player.getScoreboard();

            blueTeam.forEach(uuid2 -> {
                Player loopPlayer = plugin.getServer().getPlayer(uuid2);

                scoreboard.getTeam("BlueTDM").addEntry(loopPlayer.getName());
            });
            redTeam.forEach(uuid2 -> {
                Player loopPlayer = plugin.getServer().getPlayer(uuid2);

                scoreboard.getTeam("RedTDM").addEntry(loopPlayer.getName());
            });
        });


    }

    private void splitTeams() {
        remainingPlayerIds().forEach(uuid -> {
                    if (redTeam.size() == 0 || blueTeam.size() >= redTeam.size()) {
                        redTeam.add(uuid);
                    } else {
                        blueTeam.add(uuid);
                    }
                }
        );
    }

    @Override
    public void onStart() {
        setStage(EventStage.FIGHTING);
        splitTeams();
        givePvPKit();
    }

    @Override
    public void onEnd() {
        List<String> winners = new ArrayList<>();
        remainingFighterIds().forEach( uuid -> {
                    Player winnerPlayer = plugin.getServer().getPlayer(uuid);
                    KitProfile profile = plugin.getPlayerManager().getProfile(winnerPlayer);
                    profile.getStatistics().setEventWins(profile.getStatistics().getEventWins() + 1);
                    winners.add(winnerPlayer.getDisplayName());
                });

        String joinedWinners = StringUtil.joinListGrammaticallyWithGuava(winners);
        broadcast(joinedWinners + CC.GREEN + " won the event!");
    }

    @Override
    public void onTick() {

    }

    @Override
    public void onDeath(Player victim) {
        KitProfile profile = plugin.getPlayerManager().getProfile(victim);

        plugin.getPlayerManager().modifyScoreboardTeams(victim);

        if (blueTeam.contains(victim.getUniqueId())) {
            blueTeam.remove(victim.getUniqueId());
        } else {
            redTeam.remove(victim.getUniqueId());
        }

        setState(victim.getUniqueId(), ParticipantState.DEAD);
        leave(victim, profile);

        int blueRemaining = blueTeam.size();
        int redRemaining = redTeam.size();


        if (blueTeam.size() == 0 || redTeam.size() == 0) {
            end();
        } else {
            broadcast(victim.getDisplayName() + CC.RED + " was defeated! Only " + redRemaining + " red players and " + blueRemaining + " blue players remain.");
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


        boolean died = victim.getHealth() - event.getFinalDamage() <= 0;
        if (died) {
            event.setCancelled(true);
            onDeath(victim);
        }
    }
}
