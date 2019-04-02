package us.lemin.kitpvp.listeners;

import lombok.RequiredArgsConstructor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
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
import us.lemin.core.utils.timer.Timer;
import us.lemin.kitpvp.KitPvPPlugin;
import us.lemin.kitpvp.arena.Match;
import us.lemin.kitpvp.events.Event;
import us.lemin.kitpvp.handlers.KillstreakHandler;
import us.lemin.kitpvp.inventory.KitSelectorPlayerWrapper;
import us.lemin.kitpvp.inventory.KitShopPlayerWrapper;
import us.lemin.kitpvp.inventory.SettingsPlayerWrapper;
import us.lemin.kitpvp.kit.Kit;
import us.lemin.kitpvp.player.KitProfile;
import us.lemin.kitpvp.player.PlayerDamageData;
import us.lemin.kitpvp.player.PlayerState;
import us.lemin.kitpvp.util.MathUtil;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class PlayerListener implements Listener {
    private final KitPvPPlugin plugin;
    private final KillstreakHandler killstreakHandler;

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        if (event.getLoginResult() == AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            plugin.getPlayerManager().createProfile(event.getUniqueId(), event.getName());
        }
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        KitProfile profile = plugin.getPlayerManager().getProfile(player);

        if (profile == null) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, CC.RED + "Your data failed to load for KitPvP. Try logging in again.");
        } else if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
            plugin.getPlayerManager().removeProfile(player);
        }
    }

    /*
        @EventHandler
        public void onJoinTeam(PlayerJoinEvent event) {
            KitProfile profile = plugin.getPlayerManager().getProfile(event.getPlayer());
            TeamManager teamManager = plugin.getTeamManager();
            if (profile.getLastKnownTeam() == null) {
                return;
            }

            boolean teamLoaded = teamManager.getTeamByName(profile.getLastKnownTeam()) != null;
            System.out.println(event.getPlayer().getName() + "'s team is loaded: " + teamLoaded);
            us.lemin.kitpvp.team.Team loadedTeam = teamLoaded ? teamManager.getTeamByName(profile.getLastKnownTeam()) : teamManager.registerTeam(profile.getLastKnownTeam());
            if (loadedTeam == null) return;
            if (!loadedTeam.getMembers().containsKey(event.getPlayer().getUniqueId())) {
                System.out.println(loadedTeam.getName() + " has been unregistered because " + event.getPlayer().getName() + " is not a member.");
                teamManager.unregisterTeam(loadedTeam.getName());
                profile.setLastKnownTeam(null);
            }
        }
    */
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        PlayerUtil.clearPlayer(player);

        plugin.getPlayerManager().giveSpawnItems(player);

        player.teleport(plugin.getSpawnLocation());

        player.sendMessage(CC.SEPARATOR);
        player.sendMessage(CC.PRIMARY + "Welcome to " + CC.SECONDARY + "Okolie KitPvP" + CC.PRIMARY + "!");
        player.sendMessage(CC.SEPARATOR);

        CoreProfile coreProfile = CorePlugin.getInstance().getProfileManager().getProfile(player.getUniqueId());

        if (coreProfile.hasDonor()) {
            player.setAllowFlight(true);
            player.setFlying(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onJoinHighest(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        CoreProfile playerCoreProfile = CorePlugin.getInstance().getProfileManager().getProfile(player.getUniqueId());
        Scoreboard scoreboard = player.getScoreboard();

        for (Rank rank : Rank.values()) {
            Team team = scoreboard.registerNewTeam(rank.getName());
            team.setPrefix(rank.getColor());
        }

        for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
            CoreProfile coreProfile = CorePlugin.getInstance().getProfileManager().getProfile(onlinePlayer.getUniqueId());
            Team team = scoreboard.getTeam(coreProfile.getRank().getName());
            team.addEntry(onlinePlayer.getName());
            onlinePlayer.getScoreboard().getTeam(playerCoreProfile.getRank().getName()).addEntry(player.getName());
        }

        Team blueTDM = scoreboard.registerNewTeam("BlueTDM");
        Team redTDM = scoreboard.registerNewTeam("RedTDM");
        blueTDM.setPrefix(CC.BLUE);
        blueTDM.setAllowFriendlyFire(false);
        redTDM.setPrefix(CC.RED);
        redTDM.setAllowFriendlyFire(false);


    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        KitProfile profile = plugin.getPlayerManager().getProfile(player);
        CoreProfile coreProfile = CorePlugin.getInstance().getProfileManager().getProfile(player.getUniqueId());
        if (profile == null) {
            return;
        }
        /*if (profile.getLastKnownTeam() != null) {
            us.lemin.kitpvp.team.Team team = teamManager.getTeamByPlayer(player);
            if (team == null) {
                return;
            }
            System.out.println(team.getOnlinePlayers().count() + " COUNT");
            if (team.getOnlinePlayers().count() <= 1) {
                System.out.println("TEAM UNREGISTERED");
                teamManager.unregisterTeam(team.getName());
            }
        }*/
        switch (profile.getState()) {
            case FFA:
                List<Player> nearbyPlayers = player.getNearbyEntities(32.0, 32.0, 32.0).stream()
                        .filter(Player.class::isInstance)
                        .map(Player.class::cast)
                        .collect(Collectors.toList());
                boolean kill = false;

                for (Player nearbyPlayer : nearbyPlayers) {
                    KitProfile nearbyProfile = plugin.getPlayerManager().getProfile(nearbyPlayer);

                    if (nearbyProfile.getState() == PlayerState.FFA) {
                        kill = true;
                        break;
                    }
                }

                if (kill) {
                    player.setHealth(0.0);
                }
                break;
            case EVENT:
                Event activeEvent = profile.getActiveEvent();
                activeEvent.leave(player, profile);
                break;
            case DUEL:
                Match match = profile.getActiveMatch();
                if (match == null) {
                    break;
                }
                Player opponent;
                if (profile.getActiveMatch().getFirstPlayerId() != event.getPlayer().getUniqueId()) {
                    opponent = plugin.getServer().getPlayer(profile.getActiveMatch().getFirstPlayerId());
                } else {
                    opponent = plugin.getServer().getPlayer(profile.getActiveMatch().getSecondPlayerId());
                }
                plugin.getPlayerManager().giveArenaItems(opponent);
                break;
        }


        profile.save(false);
        plugin.getPlayerManager().removeProfile(player);

        if (coreProfile == null) {
            return;
        }

        for (Player players : plugin.getServer().getOnlinePlayers()) {
            players.getScoreboard().getTeam(coreProfile.getRank().getName()).removeEntry(player.getName());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPearl(PlayerInteractEvent event) {
        if (!event.hasItem() || event.getItem().getType() != Material.ENDER_PEARL
                || event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        KitProfile profile = plugin.getPlayerManager().getProfile(player);

        if (profile.getState() != PlayerState.FFA) {
            event.setCancelled(true);
            player.updateInventory();
            return;
        }

        Timer timer = profile.getPearlTimer();

        if (timer.isActive(false)) {
            event.setCancelled(true);
            player.updateInventory();
            player.sendMessage(CC.PRIMARY + "You can't throw pearls for another " + CC.SECONDARY + timer.formattedExpiration() + CC.PRIMARY + ".");
        }
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        if (!event.hasItem() || event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        KitProfile profile = plugin.getPlayerManager().getProfile(player);

        switch (profile.getState()) {
            case SPAWN:
                if (player.getGameMode() != GameMode.CREATIVE) {
                    event.setCancelled(true);
                    player.updateInventory();
                }


                if (profile.getCurrentKit() != null) {
                    return;
                }

                switch (event.getItem().getType()) {
                    case CHEST:
                        plugin.getInventoryManager().getPlayerWrapper(KitSelectorPlayerWrapper.class).open(player);
                        break;
                    case WATCH:
                        Kit kit = profile.getLastKit();

                        if (kit != null) {
                            kit.apply(player);
                        }
                        break;
                    case PAPER:
                        player.performCommand("stats");
                        break;
                    case DIAMOND_SWORD:
                        plugin.getPlayerManager().giveArenaItems(player);
                        break;
                    case ENDER_CHEST:
                        plugin.getInventoryManager().getPlayerWrapper(KitShopPlayerWrapper.class).open(player);
                        break;
                    case EYE_OF_ENDER:
                        plugin.getInventoryManager().getPlayerWrapper(SettingsPlayerWrapper.class).open(player);
                        break;
                    case DIAMOND:
                        plugin.getPlayerManager().giveClassicKit(player);
                        break;
                }
                break;
            case EVENT:
                event.setCancelled(true);

                switch (event.getItem().getType()) {
                    case INK_SACK:
                        Event activeEvent = profile.getActiveEvent();

                        if (activeEvent != null) {
                            activeEvent.leave(player, profile);
                        }
                        break;
                }
                break;
        }
    }

    @EventHandler
    public void onSoup(PlayerInteractEvent event) {
        if (!event.hasItem() || event.getItem().getType() != Material.MUSHROOM_SOUP
                || event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();

        if (!player.isDead() && player.getHealth() > 0.0 && player.getHealth() <= 19.0) {
            event.setCancelled(true);
            double health = player.getHealth() + 7.0;

            player.setHealth(health > 20.0 ? 20.0 : health);
            player.getItemInHand().setType(Material.BOWL);
            player.updateInventory();
        }
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = ((Player) event.getEntity()).getPlayer();
            KitProfile profile = plugin.getPlayerManager().getProfile(player);
            if (event.getFoodLevel() < 20 || !profile.isFrozen()) {
                event.setFoodLevel(20);
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() != GameMode.CREATIVE) {
            if (event.getItemDrop().getItemStack().getType() != Material.BOWL) {
                event.setCancelled(true);
                return;
            }

            event.getItemDrop().remove();
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        KitProfile profile = plugin.getPlayerManager().getProfile(player);

        if (!profile.isAwaitingTeleport()) {
            return;
        }

        Location to = event.getTo();
        Location from = event.getFrom();

        if (MathUtil.isWithin(to.getX(), from.getX(), 0.1) && MathUtil.isWithin(to.getZ(), from.getZ(), 0.1)) {
            return;
        }

        profile.setAwaitingTeleport(false);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        event.getDrops().clear();

        Player player = event.getEntity();
        System.out.println(player.getName() + " has died.");
        KitProfile profile = plugin.getPlayerManager().getProfile(player);
        PlayerDamageData damageData = profile.getDamageData();
        double totalDamage = damageData.total();
        Map<UUID, Double> sortedDamage = damageData.sortedMap();
        boolean killer = true;
        boolean killstreakHandled = false;
        profile.setLastAttacked(null);
        System.out.println(player.getName() + " has died." + "INFO:" + "damage data size: " + damageData.sortedMap().size() + " kit: " + profile.getCurrentKit());

        for (Map.Entry<UUID, Double> entry : sortedDamage.entrySet()) {
            UUID damagerId = entry.getKey();
            Player damager = plugin.getServer().getPlayer(damagerId);
            KitProfile damagerProfile = plugin.getPlayerManager().getProfile(damager);
            double damage = entry.getValue();
            double percent = damage / totalDamage;

            if (!killer && percent < 0.15) {
                continue;
            }

            int worth = killer ? damagerProfile.getWorth() : (int) (damagerProfile.getWorth() * percent);

            if (damagerProfile.getCurrentKit() == null) {
                return;
            }

            if (damagerProfile.getCurrentKit().getName().equalsIgnoreCase("Clout")) {
                worth = worth * 2;
            } else if (damagerProfile.getCurrentKit().getName().equalsIgnoreCase("Ninja")) {
                damagerProfile.setLastAttacked(null);
            }

            String strPercent = String.format("%.1f", percent * 100);


            damagerProfile.getStatistics().setCredits(damagerProfile.getStatistics().getCredits() + worth);

            if (killer) {
                System.out.println("killer is " + damager);
                killer = false;
                damagerProfile.getStatistics().handleKill();
                plugin.getKillstreakHandler().checkDeath(damager, player, profile.getStatistics().getKillStreak());
                System.out.println("checked death");
                killstreakHandled = true;
                plugin.getKillstreakHandler().checkKill(damager, damagerProfile.getStatistics().getKillStreak());
                System.out.println("checked kill");
                damager.sendMessage(CC.PRIMARY + "You killed " + CC.SECONDARY + player.getDisplayName()
                        + CC.PRIMARY + " and received " + CC.SECONDARY + worth + CC.PRIMARY + " credits "
                        + CC.GRAY + "(" + strPercent + "% of damage)" + CC.PRIMARY + ".");

                for (int i = 0; i < 8; i++) {
                    damager.getInventory().addItem(damagerProfile.getCurrentKit().getName().equals("PotPvP") ?
                            new ItemBuilder(Material.POTION).durability(16421).build() :
                            new ItemStack(Material.MUSHROOM_SOUP));
                }

                player.sendMessage(CC.PRIMARY + "You were slain by " + CC.SECONDARY + damager.getDisplayName() + CC.PRIMARY + ".");
            } else {
                damager.sendMessage(CC.PRIMARY + "You got an assist on " + CC.SECONDARY + player.getDisplayName()
                        + CC.PRIMARY + " and received " + CC.SECONDARY + worth + CC.PRIMARY + " credits "
                        + CC.GRAY + "(" + strPercent + "% of damage)" + CC.PRIMARY + ".");
            }
        }
        if (profile.getVulnerableTask() != null) {
            int refundAmount = 0;
            switch (profile.getVulnerableTask().statusChange.toLowerCase()) {
                case "refill":
                    refundAmount = 25;
                    break;
                case "repair":
                    refundAmount = 50;
                    break;
                case "hotbar":
                    refundAmount = 10;
                    break;
            }
            profile.getStatistics().setCredits(profile.getStatistics().getCredits() + refundAmount);
            player.sendMessage(CC.ACCENT + "You received a refund of " + CC.PRIMARY + refundAmount + CC.ACCENT + " for dying while vulnerable.");
            profile.setDiedWhileVulnerable(true);
            profile.setVulnerableTask(null);
        }
        profile.setControllable(true);
        profile.setCurrentKit(null);
        damageData.clear();
        if (!killstreakHandled) {
            killstreakHandler.checkDeath(player, profile.getStatistics().getKillStreak());
        }

        System.out.println("death was handled for " + player.getName());
        profile.getStatistics().handleDeath();

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline()) {
                player.spigot().respawn();
            }
        }, 16L);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        plugin.getPlayerManager().acquireSpawnProtection(player);
        plugin.getPlayerManager().giveSpawnItems(player);
    }

    @EventHandler
    public void onSoupSignCreate(SignChangeEvent event) {
        Player player = event.getPlayer();
        CoreProfile profile = CorePlugin.getInstance().getProfileManager().getProfile(player.getUniqueId());
        if (!profile.hasRank(Rank.ADMIN)) {
            return;
        }

        if (event.getLine(0) == null || !event.getLine(0).equalsIgnoreCase("soup sign")) {
            player.sendMessage(CC.RED + "You can't create that type of sign.");
            event.setCancelled(true);
            return;
        }

        event.setLine(0, "");
        event.setLine(1, "[Soup Sign]");
        event.setLine(2, "Right click");
        event.setLine(3, "for soup");

    }

    @EventHandler
    public void onSoupSignClick(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) {
            return;
        }
        Material clickedBlockType = event.getClickedBlock().getType();
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (clickedBlockType == Material.SIGN
                    || clickedBlockType == Material.SIGN_POST
                    || clickedBlockType == Material.WALL_SIGN) {

                Sign sign = (Sign) event.getClickedBlock().getState();

                if (!sign.getLine(1).equalsIgnoreCase("[Soup Sign]")) {
                    return;
                }
                Player player = event.getPlayer();

                Inventory soupSign = plugin.getServer().createInventory(player, 9, "Soup Sign");
                int x = 0;
                while (x <= 8) {
                    soupSign.setItem(x++, new ItemStack(Material.MUSHROOM_SOUP));
                }
                player.openInventory(soupSign);

            }
        }

    }
}
