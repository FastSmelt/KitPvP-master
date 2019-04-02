package us.lemin.kitpvp.managers;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import us.lemin.core.CorePlugin;
import us.lemin.core.player.CoreProfile;
import us.lemin.core.player.rank.Rank;
import us.lemin.core.utils.item.ItemBuilder;
import us.lemin.core.utils.message.CC;
import us.lemin.core.utils.player.PlayerUtil;
import us.lemin.kitpvp.KitPvPPlugin;
import us.lemin.kitpvp.kit.impl.ffa.Classic;
import us.lemin.kitpvp.player.KitProfile;
import us.lemin.kitpvp.player.PlayerState;
import us.lemin.kitpvp.server.ServerMode;
import us.lemin.kitpvp.tasks.VulnerableTask;
import us.lemin.kitpvp.util.ItemHotbars;
import us.lemin.kitpvp.util.TempLocations;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.IntStream;

@RequiredArgsConstructor
public class PlayerManager {
    private final KitPvPPlugin plugin;
    private final Map<UUID, KitProfile> profiles = new HashMap<>();

    public void createProfile(UUID id, String name) {
        KitProfile profile = new KitProfile(name, id);
        profiles.put(id, profile);
    }

    public KitProfile getProfile(Player player) {
        return profiles.get(player.getUniqueId());
    }

    public void removeProfile(Player player) {
        profiles.remove(player.getUniqueId());
    }

    public void saveAllProfiles() {
        for (KitProfile profile : profiles.values()) {
            profile.save(false);
        }
    }

    public void giveArenaItems(Player player) {
        PlayerUtil.clearPlayer(player);
        KitProfile profile = getProfile(player);

        ItemHotbars.ARENA_ITEMS.apply(player);
        player.updateInventory();
        profile.setState(PlayerState.ARENA);
        profile.setActiveMatch(null);
        player.teleport(TempLocations.ARENA_SPAWN);
    }

    public void giveStaffModeItems(Player player) {
        KitProfile profile = plugin.getPlayerManager().getProfile(player);

        player.setGameMode(GameMode.CREATIVE);

        profile.setState(PlayerState.STAFF);

        ItemHotbars.STAFFMODE_ITEMS.apply(player);
    }

    public void startExaminationTask(Player p, Player personToBeExamined) {
        Inventory inv = Bukkit.createInventory(null, 54, ChatColor.RED + "Examining " + ChatColor.BOLD + personToBeExamined.getName());

        IntStream.range(0, 36).forEach(i -> {
            ItemStack is = personToBeExamined.getInventory().getItem(i);
            inv.setItem(i, is);
        });

        inv.setItem(36, personToBeExamined.getInventory().getHelmet());
        inv.setItem(37, personToBeExamined.getInventory().getChestplate());
        inv.setItem(38, personToBeExamined.getInventory().getLeggings());
        inv.setItem(39, personToBeExamined.getInventory().getBoots());

        inv.setItem(40, personToBeExamined.getItemInHand());


        IntStream.range(0, 3).forEach(i -> inv.setItem(41 + i, new ItemStack(Material.THIN_GLASS, 1)));

        p.openInventory(inv);
    }

    public void giveSpawnItems(Player player) {
        KitProfile profile = getProfile(player);

        PlayerUtil.clearPlayer(player);
        profile.setCurrentKit(null);
        if (plugin.getServerMode() == ServerMode.REGULAR) {
            ItemHotbars.SPAWN_ITEMS.apply(player);


            if (profile.getLastKit() != null) {
                player.getInventory().setItem(2, new ItemBuilder(Material.WATCH).name(CC.YELLOW + "Last Kit " + CC.SECONDARY + "(" + profile.getLastKit().getName() + ")").build());
                player.updateInventory();
            }
        } else {
            ItemHotbars.CLASSIC_ITEMS.apply(player);
        }

    }

    public void loseSpawnProtection(Player player) {
        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        KitProfile profile = getProfile(player);

        profile.setState(PlayerState.FFA);

        CoreProfile coreProfile = CorePlugin.getInstance().getProfileManager().getProfile(player.getUniqueId());

        if (coreProfile.hasDonor()) {
            player.setFlying(false);
            player.setAllowFlight(false);

            profile.setFallDamageEnabled(false);

            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                if (player.isOnline()) {
                    profile.setFallDamageEnabled(true);
                }
            }, 20L * 5);
        }

        player.sendMessage(CC.RED + "You no longer have spawn protection!");
    }

    public void acquireSpawnProtection(Player player) {
        KitProfile profile = getProfile(player);

        profile.setState(PlayerState.SPAWN);
        profile.setFallDamageEnabled(true);

        CoreProfile coreProfile = CorePlugin.getInstance().getProfileManager().getProfile(player.getUniqueId());

        if (coreProfile.hasDonor()) {
            player.setAllowFlight(true);
            player.setFlying(true);
        } else {
            player.setAllowFlight(false);
            player.setFlying(false);
        }

        player.sendMessage(CC.GREEN + "You have acquired spawn protection.");
    }

    public void resetPlayer(Player player, boolean teleport) {
        KitProfile profile = getProfile(player);

        profile.setState(PlayerState.SPAWN);
        profile.setFallDamageEnabled(true);

        CoreProfile coreProfile = CorePlugin.getInstance().getProfileManager().getProfile(player.getUniqueId());

        if (coreProfile.hasDonor()) {
            player.setAllowFlight(true);
            player.setFlying(true);
        } else {
            player.setAllowFlight(false);
            player.setFlying(false);
        }

        player.setWalkSpeed(0.2F);
        player.setHealth(20);

        giveSpawnItems(player);

        if (teleport) {
            player.teleport(plugin.getSpawnLocation());
        }
    }

    public void makeVulnerable(Player player, String statusChange) {
        KitProfile profile = getProfile(player);

        VulnerableTask vulnerableTask = new VulnerableTask(player, statusChange, profile);
        profile.setVulnerableTask(vulnerableTask);
        plugin.getServer().getScheduler().runTaskLater(plugin, vulnerableTask, 100);
        player.sendMessage(CC.RED + "You are vulnerable for 5 seconds!");
    }

    public void giveClassicKit(Player player) {
        plugin.getKitManager().getFfaKitByClass(Classic.class).apply(player);
    }

    public void modifyScoreboardTeams(Player player) {
        KitProfile profile = getProfile(player);
        CoreProfile coreProfile = CorePlugin.getInstance().getProfileManager().getProfile(player.getUniqueId());

        boolean staffMode = profile.getState() == PlayerState.STAFF;
        Rank rank = coreProfile.getRank();
        if (staffMode) {
            plugin.getServer().getOnlinePlayers().forEach(players -> {
                players.getScoreboard().getTeam(rank.getName()).removeEntry(player.getName());

            });
            Set<UUID> staffSet = CorePlugin.getInstance().getStaffManager().getStaffIds();
            staffSet.forEach(uuid -> {
                Player players = plugin.getServer().getPlayer(uuid);
                Scoreboard playersScoreboard = players.getScoreboard();

                Team seeInvis = player.getScoreboard().getTeam("seeInvis");
                seeInvis.addEntry(players.getName());

                Team loopSeeInvis = playersScoreboard.getTeam("seeInvis");
                loopSeeInvis.addEntry(player.getName());
            });
        } else {
            plugin.getServer().getOnlinePlayers().forEach(players -> {
                Team seeInvis = players.getScoreboard().getTeam("seeInvis");
                if (seeInvis.getEntries().contains(player.getName())) {
                    seeInvis.removeEntry(player.getName());
                }
                players.getScoreboard().getTeam(rank.getName()).addEntry(player.getName());
            });
        }
    }
}
