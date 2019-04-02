package us.lemin.kitpvp.kit.impl.ffa;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import us.lemin.core.utils.item.ItemBuilder;
import us.lemin.core.utils.message.CC;
import us.lemin.kitpvp.KitPvPPlugin;
import us.lemin.kitpvp.kit.Kit;
import us.lemin.kitpvp.kit.KitContents;
import us.lemin.kitpvp.player.KitProfile;

import java.util.Collections;
import java.util.List;

public class Scorpion extends Kit {
    public Scorpion(KitPvPPlugin plugin) {
        super(plugin, "Scorpion", Material.LEASH, "You pull enemies toward you with your fishing rod.");
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
    public void onFish(PlayerFishEvent event) {
        if (!(event.getCaught() instanceof Player)) {
            return;
        }

        Player scorpion = event.getPlayer();

        if (isInvalidKit(scorpion)) {
            return;
        }

        Player victim = (Player) event.getCaught();
        KitProfile profile = plugin.getPlayerManager().getProfile(victim);
        if (!profile.isControllable()) {
            scorpion.sendMessage(CC.RED + "That player can't be controlled.");
            return;
        }
        pullEntityToLocation(victim, scorpion.getLocation());
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
