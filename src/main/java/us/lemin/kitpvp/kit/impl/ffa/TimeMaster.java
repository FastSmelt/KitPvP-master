package us.lemin.kitpvp.kit.impl.ffa;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import us.lemin.core.utils.StringUtil;
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

public class TimeMaster extends Kit {
    public TimeMaster(KitPvPPlugin plugin) {
        super(plugin, "Time-master", Material.WATCH, "Freeze those around you in time.");
        registerCooldownTimer("time-master", 25);
    }

    @Override
    protected void onEquip(Player player) {
        // NO-OP
    }

    @Override
    protected List<PotionEffect> effects() {
        return Collections.singletonList(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0));
    }

    @Override
    protected KitContents.Builder contentsBuilder() {
        KitContents.Builder builder = KitContents.newBuilder();

        builder.addItem(new ItemBuilder(Material.DIAMOND_SWORD).build());
        builder.addItem(new ItemBuilder(Material.WATCH).name(ChatColor.GOLD + "Hourglass").build());
        builder.fill(new ItemStack(Material.MUSHROOM_SOUP));
        builder.addArmor(
                new ItemStack(Material.DIAMOND_BOOTS),
                new ItemStack(Material.IRON_LEGGINGS),
                new ItemStack(Material.IRON_CHESTPLATE),
                new ItemStack(Material.GOLD_HELMET)
        );

        return builder;
    }

    @EventHandler
    public void onTimeMaster(PlayerInteractEvent event) {
        if (!event.hasItem() || event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            return;
        }

        if (event.getItem().getType() != Material.WATCH) {
            return;
        }

        Player timemaster = event.getPlayer();

        KitProfile kitProfile = plugin.getPlayerManager().getProfile(timemaster);

        if (kitProfile.getState() != PlayerState.FFA) {
            return;
        }

        List<Player> nearbyEntities = timemaster.getNearbyEntities(7.0, 7.0, 7.0)
                .stream()
                .filter(Player.class::isInstance)
                .map(Player.class::cast)
                .collect(Collectors.toList());

        if (nearbyEntities.size() == 0) {
            timemaster.sendMessage(CC.RED + "There aren't any players nearby to freeze.");
            return;
        }

        if (isInvalidKit(timemaster) || isOnCooldown(timemaster, "time-master")) {
            return;
        }


        StringBuilder frozenPlayers = new StringBuilder();
        for (Player victims : nearbyEntities) {
            KitProfile victimProfile = plugin.getPlayerManager().getProfile(victims);
            if (victimProfile.getState() == PlayerState.FFA && !victimProfile.isFrozen()) {
                frozenPlayers.append(victims.getName() + ", ");
                FreezeSnapshot freezeSnapshot = new FreezeSnapshot(victims.isSprinting(), victims.getFoodLevel(), victims.getWalkSpeed());
                freezeSnapshot.init(victims);
                victimProfile.setFrozen(true);
                victims.sendMessage(ChatColor.RED + "You were frozen by " + timemaster.getName() + ", the Time-master.");
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    if (!victims.isOnline()) {
                        return;
                    }
                    freezeSnapshot.applySnapshot(victims);
                    victimProfile.setFrozen(false);
                }, 60);
            }
        }
        String names = StringUtil.joinListGrammaticallyWithGuava(nearbyEntities.stream().map(Player::getName).collect(Collectors.toList()));
        timemaster.sendMessage(ChatColor.GREEN + "You froze " + names + ".");
    }

    private class FreezeSnapshot {
        private boolean sprinting;
        private int foodLevel;
        private float walkSpeed;
        private PotionEffect nausea = new PotionEffect(PotionEffectType.CONFUSION, 150, 0);
        private PotionEffect jumpBoost = new PotionEffect(PotionEffectType.JUMP, 60, -5);
        private PotionEffect weakness = new PotionEffect(PotionEffectType.WEAKNESS, 60, 5);
        private PotionEffect minerFatigue = new PotionEffect(PotionEffectType.SLOW_DIGGING, 60, 3);

        FreezeSnapshot(boolean sprinting, int foodLevel, float walkSpeed) {
            this.sprinting = sprinting;
            this.foodLevel = foodLevel;
            this.walkSpeed = walkSpeed;
        }

        void init(Player player) {
            player.setSprinting(false);
            player.setFoodLevel(0);
            player.setWalkSpeed(0);
            player.addPotionEffect(jumpBoost);
            player.addPotionEffect(nausea);
            player.addPotionEffect(weakness);
            player.addPotionEffect(minerFatigue);
        }

        private void applySnapshot(Player player) {
            player.setSprinting(sprinting);
            player.setFoodLevel(foodLevel);
            player.setWalkSpeed(walkSpeed);
        }

    }
}
