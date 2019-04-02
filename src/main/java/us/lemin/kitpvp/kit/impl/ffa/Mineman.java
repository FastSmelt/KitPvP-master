package us.lemin.kitpvp.kit.impl.ffa;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import us.lemin.core.utils.item.ItemBuilder;
import us.lemin.core.utils.message.CC;
import us.lemin.kitpvp.KitPvPPlugin;
import us.lemin.kitpvp.kit.Kit;
import us.lemin.kitpvp.kit.KitContents;
import us.lemin.kitpvp.player.KitProfile;
import us.lemin.kitpvp.player.PlayerState;

import java.util.*;
import java.util.concurrent.TimeUnit;


public class Mineman extends Kit {

    public Set<BlockData> blockDataSet = new HashSet<>();

    public Mineman(KitPvPPlugin plugin) {
        super(plugin, "Mineman", Material.DIAMOND_PICKAXE, "Can build and break blocks placed by themself and other Minemen.");
        plugin.getServer().getScheduler().runTaskTimer(plugin, new MinemanTask(), 20 * 3, 20 * 3);
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

        builder.addItem(new ItemBuilder(Material.IRON_SWORD).enchant(Enchantment.DAMAGE_ALL, 1).enchant(Enchantment.DURABILITY, 2).build());
        builder.addItem(new ItemBuilder(Material.DIAMOND_PICKAXE).name(CC.GOLD + "Mineman's Pickaxe").enchant(Enchantment.DIG_SPEED, 5).build());
        builder.addItem(new ItemBuilder(Material.COBBLESTONE).amount(8).build());
        builder.fill(new ItemStack(Material.MUSHROOM_SOUP));
        builder.addArmor(
                new ItemStack(Material.DIAMOND_BOOTS),
                new ItemStack(Material.IRON_LEGGINGS),
                new ItemStack(Material.IRON_CHESTPLATE)
        );

        return builder;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlace(BlockPlaceEvent event) {
        Player mineman = event.getPlayer();

        if (isInvalidKit(mineman)) {
            return;
        }
        if (event.getBlock().getType() != Material.COBBLESTONE) {
            return;
        }

        KitProfile profile = plugin.getPlayerManager().getProfile(mineman);
        if (profile.getState() == PlayerState.SPAWN) {
            event.setCancelled(true);
            return;
        }

        if (plugin.getSpawnCuboid().contains(event.getBlock().getLocation())) {
            event.setCancelled(true);
            return;
        }
        Material material = event.getBlock().getLocation().getBlock().getType();
        if (material == Material.WATER || material == Material.STATIONARY_WATER || material == Material.STATIONARY_LAVA || material == Material.LAVA) {
            event.setCancelled(true);
            return;
        }

        event.setCancelled(false);
        BlockData blockData = new BlockData(mineman, event.getBlock().getLocation());
        blockDataSet.add(blockData);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBreak(BlockBreakEvent event) {
        Player mineman = event.getPlayer();

        if (isInvalidKit(mineman)) {
            return;
        }
        if (event.getBlock().getType() != Material.COBBLESTONE) {
            return;
        }

        KitProfile profile = plugin.getPlayerManager().getProfile(mineman);
        if (profile.getState() == PlayerState.SPAWN) {
            event.setCancelled(true);
            return;
        }
        event.setCancelled(true);
        boolean breakBlock = false;
        Iterator<BlockData> iterator = blockDataSet.iterator();
        while (iterator.hasNext()) {
            BlockData blockData = iterator.next();
            if (blockData.getLocation().equals(event.getBlock().getLocation())) {
                breakBlock = true;
                Player placedBy = blockData.getPlacedBy();
                if (placedBy.isOnline()) {
                    blockData.placedBy.getInventory().addItem(new ItemStack(Material.COBBLESTONE));
                }
                iterator.remove();
                break;
            }
        }

        if (breakBlock) {
            event.getBlock().setType(Material.AIR);
        }
    }

    class BlockData {
        @Getter
        private Player placedBy;
        @Getter
        private long placedAt;
        @Getter
        private Location location;

        BlockData(Player placedBy, org.bukkit.Location blockLocation) {
            this.placedBy = placedBy;
            this.placedAt = System.currentTimeMillis();
            this.location = blockLocation;
        }
    }

    class MinemanTask implements Runnable {


        @Override
        public void run() {
            if (blockDataSet.isEmpty()) {
                return;
            }

            long currentTime = System.currentTimeMillis();

            Iterator<BlockData> iterator = blockDataSet.iterator();
            while (iterator.hasNext()) {
                BlockData blockData = iterator.next();

                long secondsExisted = TimeUnit.MILLISECONDS.toSeconds(currentTime - blockData.getPlacedAt());

                if (secondsExisted >= 15) {
                    plugin.getServer().getWorld("world").getBlockAt(blockData.getLocation()).setType(Material.AIR);
                    Player placedBy = blockData.getPlacedBy();
                    KitProfile profile = plugin.getPlayerManager().getProfile(placedBy);
                    if (placedBy.isOnline() && profile.getCurrentKit() == plugin.getKitManager().getFfaKitByName("Mineman")) {
                        if (!placedBy.getInventory().contains(Material.COBBLESTONE, 8)) {
                            placedBy.getInventory().addItem(new ItemStack(Material.COBBLESTONE));
                        }

                    }
                    iterator.remove();
                }
            }
        }
    }
}

