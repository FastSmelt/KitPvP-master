package us.lemin.kitpvp.kit.impl.ffa;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import us.lemin.core.utils.item.ItemBuilder;
import us.lemin.kitpvp.KitPvPPlugin;
import us.lemin.kitpvp.kit.Kit;
import us.lemin.kitpvp.kit.KitContents;

import java.util.Collections;
import java.util.List;

public class Archer extends Kit {
    public Archer(KitPvPPlugin plugin) {
        super(plugin, "Archer", Material.BOW, "Shoot players.");
    }

    @Override
    protected void onEquip(Player player) {
        // NO-OP
    }

    @Override
    protected List<PotionEffect> effects() {
        return Collections.singletonList(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
    }

    @Override
    protected KitContents.Builder contentsBuilder() {
        KitContents.Builder builder = KitContents.newBuilder();

        builder.addItem(new ItemBuilder(Material.IRON_SWORD).enchant(Enchantment.DAMAGE_ALL, 1).enchant(Enchantment.DURABILITY, 3).build());
        builder.addItem(new ItemBuilder(Material.BOW)
                .enchant(Enchantment.ARROW_DAMAGE, 3)
                .enchant(Enchantment.ARROW_INFINITE, 1).build());
        builder.addItem(new ItemStack(Material.ENDER_PEARL, 16));
        builder.setItem(35, new ItemStack(Material.ARROW));
        builder.fill(new ItemStack(Material.MUSHROOM_SOUP));
        builder.addArmor(
                new ItemBuilder(Material.LEATHER_BOOTS).enchant(Enchantment.PROTECTION_FALL, 4).build(),
                new ItemStack(Material.IRON_LEGGINGS),
                new ItemStack(Material.IRON_CHESTPLATE),
                new ItemStack(Material.LEATHER_HELMET)
        );

        return builder;
    }
}
