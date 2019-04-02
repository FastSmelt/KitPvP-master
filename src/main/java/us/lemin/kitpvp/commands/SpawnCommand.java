package us.lemin.kitpvp.commands;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import us.lemin.core.commands.PlayerCommand;
import us.lemin.core.utils.message.CC;
import us.lemin.kitpvp.KitPvPPlugin;
import us.lemin.kitpvp.player.KitProfile;
import us.lemin.kitpvp.player.PlayerState;

import java.util.List;
import java.util.stream.Collectors;

public class SpawnCommand extends PlayerCommand {
    private final KitPvPPlugin plugin;

    public SpawnCommand(KitPvPPlugin plugin) {
        super("spawn");
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {
        KitProfile profile = plugin.getPlayerManager().getProfile(player);

        if (profile.isAwaitingTeleport()) {
            player.sendMessage(CC.RED + "You're already waiting to go to spawn.");
            return;
        }

        if (profile.getState() == PlayerState.SPAWN) {
            sendToSpawn(player, false);
        } else if (profile.getState() == PlayerState.ARENA || profile.getState() == PlayerState.DUEL || profile.getState() == PlayerState.EVENT){
            player.sendMessage(CC.RED + "You can't use that command right now.");
        } else {
            List<Player> nearbyPlayers = player.getNearbyEntities(32.0, 32.0, 32.0).stream()
                    .filter(Player.class::isInstance)
                    .map(Player.class::cast)
                    .collect(Collectors.toList());
            boolean wait = false;

            for (Player nearbyPlayer : nearbyPlayers) {
                KitProfile nearbyProfile = plugin.getPlayerManager().getProfile(nearbyPlayer);

                if (nearbyProfile.getState() == PlayerState.FFA) {
                    wait = true;
                    break;
                }
            }

            if (!wait) {
                sendToSpawn(player, true);
            } else {
                profile.setAwaitingTeleport(true);
                player.sendMessage(CC.PRIMARY + "Players are nearby! You will be teleported in 5 seconds.");

                new BukkitRunnable() {
                    private int count = 6;

                    @Override
                    public void run() {
                        if (!player.isOnline()) {
                            cancel();
                        } else if (!profile.isAwaitingTeleport()) {
                            player.sendMessage(CC.RED + "You moved! The teleportation has been cancelled.");
                            cancel();
                        } else if (--count == 0) {
                            sendToSpawn(player, true);
                            cancel();
                        } else {
                            player.sendMessage(CC.PRIMARY + count + "...");
                        }
                    }
                }.runTaskTimer(plugin, 0L, 20L);
            }
        }
        profile.setLastAttacked(null);
    }

    private void sendToSpawn(Player player, boolean withProtection) {
        if (withProtection) {
            plugin.getPlayerManager().acquireSpawnProtection(player);
        }

        player.teleport(plugin.getSpawnLocation());
        player.sendMessage(CC.GREEN + "Teleported to spawn.");
    }
}
