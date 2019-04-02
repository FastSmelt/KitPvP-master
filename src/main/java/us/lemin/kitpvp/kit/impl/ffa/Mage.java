package us.lemin.kitpvp.kit.impl.ffa;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
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

public class Mage extends Kit {

    private Map<UUID, Location> savedLocations;

    public Mage(KitPvPPlugin plugin) {
        super(plugin, "Mage", Material.ENDER_PORTAL_FRAME, "Teleport to a saved location within 30 blocks.");
        registerCooldownTimer("mage", 15);
        savedLocations = new HashMap<>();
        plugin.getServer().getScheduler().runTaskTimer(plugin, new ParticleTask(), 10, 10);
    }

    @Override
    protected void onEquip(Player player) {

    }

    @Override
    protected List<PotionEffect> effects() {
        return Collections.singletonList(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0));
    }

    @Override
    protected KitContents.Builder contentsBuilder() {
        KitContents.Builder builder = KitContents.newBuilder();

        builder.addItem(new ItemBuilder(Material.STICK).enchant(Enchantment.DAMAGE_ALL, 6).name(CC.GOLD + "Mage Wand").build());
        builder.addItem(new ItemBuilder(Material.ENDER_PORTAL_FRAME).name(CC.GOLD + "Teleporter Pad")
                .lore(CC.YELLOW + "Right click to set a location")
                .lore(CC.YELLOW + "Sneak to teleport to the set location.")
                .build());
        builder.fill(new ItemStack(Material.MUSHROOM_SOUP));
        builder.addArmor(
                new ItemBuilder(Material.GOLD_BOOTS).enchant(Enchantment.DURABILITY, 5).build(),
                new ItemStack(Material.IRON_LEGGINGS),
                new ItemStack(Material.IRON_CHESTPLATE),
                new ItemBuilder(Material.GOLD_HELMET).enchant(Enchantment.DURABILITY, 5).build()
        );

        return builder;
    }

    @EventHandler
    public void onTeleport(PlayerToggleSneakEvent event) {
        Player mage = event.getPlayer();

        if (isInvalidKit(mage)) {
            return;
        }

        if (mage.isSneaking()) {
            return;
        }

        if (savedLocations.get(mage.getUniqueId()) == null) {
            mage.sendMessage(CC.RED + "You don't have a saved location to teleport to.");
            return;
        }

        KitProfile kitProfile = plugin.getPlayerManager().getProfile(mage);

        if (kitProfile.getState() != PlayerState.FFA) {
            return;
        }

        int allowedDistance = 40 * 40;
        if (savedLocations.get(mage.getUniqueId()).distanceSquared(mage.getLocation()) >= allowedDistance) {
            mage.sendMessage(CC.RED + "Your saved location is too far away.");
            return;
        }

        if (isOnCooldown(mage, "mage")) {
            return;
        }

        mage.teleport(savedLocations.get(mage.getUniqueId()));
        mage.sendMessage(CC.GREEN + "You have teleported to your saved location.");

    }

    @EventHandler
    public void onMage(PlayerInteractEvent event) {
        if (!event.hasItem() || event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR) {
            return;
        }

        if (event.getItem().getType() != Material.ENDER_PORTAL_FRAME) {
            return;
        }

        Player mage = event.getPlayer();

        KitProfile kitProfile = plugin.getPlayerManager().getProfile(mage);

        if (kitProfile.getState() != PlayerState.FFA) {
            return;
        }

        if (isInvalidKit(mage)) {
            return;
        }

        mage.sendMessage(CC.GREEN + "You have set a location to teleport back to by sneaking.");
        savedLocations.remove(mage.getUniqueId());
        savedLocations.put(mage.getUniqueId(), mage.getLocation());
    }

    class ParticleTask implements Runnable {

        private int intensity = 50;

        @Override
        public void run() {
            if (savedLocations.isEmpty()) {
                return;
            }

            Iterator<UUID> iterator = savedLocations.keySet().iterator();
            while (iterator.hasNext()) {
                UUID uuid = iterator.next();

                Player player = plugin.getServer().getPlayer(uuid);

                if (!player.isOnline()) {
                    iterator.remove();
                }

                KitProfile profile = plugin.getPlayerManager().getProfile(player);

                if (profile.getCurrentKit() != plugin.getKitManager().getFfaKitByName("mage")) {
                    iterator.remove();
                }

                int x = 0;
                while (x < intensity) {
                    player.playEffect(savedLocations.get(uuid), Effect.WITCH_MAGIC, 1);
                    x++;
                }
            }
        }
    }

}
