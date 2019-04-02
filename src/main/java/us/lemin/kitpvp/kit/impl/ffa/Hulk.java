package us.lemin.kitpvp.kit.impl.ffa;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import us.lemin.core.utils.StringUtil;
import us.lemin.core.utils.item.ItemBuilder;
import us.lemin.core.utils.message.CC;
import us.lemin.kitpvp.KitPvPPlugin;
import us.lemin.kitpvp.kit.Kit;
import us.lemin.kitpvp.kit.KitContents;
import us.lemin.kitpvp.player.KitProfile;
import us.lemin.kitpvp.player.PlayerState;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Hulk extends Kit {
    public Hulk(KitPvPPlugin plugin) {
        super(plugin, "Hulk", Material.CACTUS, "You hulk smash everyone whenever you sneak.");
        registerCooldownTimer("hulk", 15);
    }

    @Override
    protected void onEquip(Player player) {
        // NO-OP
    }

    @Override
    protected List<PotionEffect> effects() {
        return Arrays.asList(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0), new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0));
    }

    @Override
    protected KitContents.Builder contentsBuilder() {
        KitContents.Builder builder = KitContents.newBuilder();

        builder.addItem(new ItemBuilder(Material.CACTUS).name(CC.GOLD + "Hulk Fist").enchant(Enchantment.DAMAGE_ALL, 4).build());
        builder.addItem(new ItemStack(Material.FISHING_ROD));
        builder.fill(new ItemStack(Material.MUSHROOM_SOUP));
        builder.addArmor(
                new ItemBuilder(Material.CHAINMAIL_BOOTS).enchant(Enchantment.DURABILITY, 5).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build(),
                new ItemStack(Material.IRON_LEGGINGS),
                new ItemStack(Material.IRON_CHESTPLATE),
                new ItemBuilder(Material.GOLD_HELMET).enchant(Enchantment.DURABILITY, 5).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build()
        );

        return builder;
    }


    @EventHandler
    public void onGroundSlam(PlayerToggleSneakEvent event) {

        Player hulk = event.getPlayer();

        if (isInvalidKit(hulk)) {
            return;
        }

        if (hulk.isSneaking()) {
            return;
        }

        KitProfile kitProfile = plugin.getPlayerManager().getProfile(hulk);

        if (kitProfile.getState() != PlayerState.FFA) {
            return;
        }

        List<Player> nearbyEntities = hulk.getNearbyEntities(7.0, 7.0, 7.0)
                .stream()
                .filter(Player.class::isInstance)
                .map(Player.class::cast)
                .collect(Collectors.toList());

        if (nearbyEntities.size() == 0) {
            hulk.sendMessage(CC.RED + "There aren't any players nearby to smash.");
            return;
        }

        if (isOnCooldown(hulk, "hulk")) {
            return;
        }

        for (Player victims : nearbyEntities) {
            KitProfile victimProfile = plugin.getPlayerManager().getProfile(victims);
            if (victimProfile.getState() == PlayerState.FFA) {
                Location increasedY = victims.getLocation();
                increasedY.setY(victims.getLocation().getY() + 5);

                pullEntityToLocation(victims, increasedY);
                victims.sendMessage(CC.RED + "You got smashed by " + hulk.getName());
            }

        }
        String names = StringUtil.joinListGrammaticallyWithGuava(nearbyEntities.stream().map(Player::getName).collect(Collectors.toList()));
        hulk.sendMessage(ChatColor.GREEN + "You smashed " + names + ".");
    }

    private void pullEntityToLocation(Entity e, Location loc) {
        Location entityLoc = e.getLocation();
        entityLoc.setY(entityLoc.getY() + 0.5D);
        e.teleport(entityLoc);
        double g = -0.08D;
        if (loc.getWorld() != entityLoc.getWorld())
            return;
        double d = loc.distance(entityLoc);
        double v_x = (1.0D + 0.07000000000000001D * d) * (loc.getX() - entityLoc.getX()) / d;
        double v_y = (1.0D + 0.03D * d) * (loc.getY() - entityLoc.getY()) / d - 0.5D * g * d;
        double v_z = (1.0D + 0.07000000000000001D * d) * (loc.getZ() - entityLoc.getZ()) / d;
        Vector v = e.getVelocity();
        v.setX(v_x);
        v.setY(v_y);
        v.setZ(v_z);
        e.setVelocity(v);
        e.setFallDistance(0f);
    }
}
