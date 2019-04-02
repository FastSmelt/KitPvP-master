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

public class PotPvP extends Kit {
    public PotPvP(KitPvPPlugin plugin) {
        super(plugin, "PotPvP", new ItemBuilder(Material.POTION).durability(16421).build(), "The standard PvP kit - potion style.");
    }

    @Override
    protected void onEquip(Player player) {
        // NO-OP
    }

    @Override
    public List<PotionEffect> effects() {
        return Collections.singletonList(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
    }

    @Override
    protected KitContents.Builder contentsBuilder() {
        KitContents.Builder builder = KitContents.newBuilder();

        builder.addItem(new ItemBuilder(Material.DIAMOND_SWORD).enchant(Enchantment.DAMAGE_ALL, 1).build());
        builder.addItem(new ItemStack(Material.ENDER_PEARL, 16));
        builder.fill(new ItemBuilder(Material.POTION).durability(16421).build());
        builder.addArmor(
                new ItemBuilder(Material.IRON_BOOTS)
                        .enchant(Enchantment.PROTECTION_FALL, 4).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build(),
                new ItemBuilder(Material.IRON_LEGGINGS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build(),
                new ItemBuilder(Material.IRON_CHESTPLATE).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build(),
                new ItemBuilder(Material.IRON_HELMET).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build()
        );

        return builder;
    }
}
