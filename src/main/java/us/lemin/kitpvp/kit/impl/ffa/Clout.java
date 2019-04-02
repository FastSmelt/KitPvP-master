package us.lemin.kitpvp.kit.impl.ffa;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import us.lemin.core.utils.item.ItemBuilder;
import us.lemin.core.utils.message.CC;
import us.lemin.kitpvp.KitPvPPlugin;
import us.lemin.kitpvp.kit.Kit;
import us.lemin.kitpvp.kit.KitContents;

import java.util.Collections;
import java.util.List;

public class Clout extends Kit {

    public Clout(KitPvPPlugin plugin) {
        super(plugin, "Clout", Material.GOLD_SWORD, "Increase the amount of credits you gain while flashing your immense clout!");
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

        builder.addItem(new ItemBuilder(Material.GOLD_SWORD)
                .enchant(Enchantment.DAMAGE_ALL, 2)
                .enchant(Enchantment.DURABILITY, 5)
                .name(CC.GOLD + "Clout Stick")
                .build());
        builder.fill(new ItemStack(Material.MUSHROOM_SOUP));
        builder.addArmor(
                new ItemBuilder(Material.GOLD_BOOTS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).enchant(Enchantment.DURABILITY, 5).build(),
                new ItemBuilder(Material.GOLD_LEGGINGS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).enchant(Enchantment.DURABILITY, 5).build(),
                new ItemBuilder(Material.GOLD_CHESTPLATE).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).enchant(Enchantment.DURABILITY, 5).build(),
                new ItemBuilder(Material.GOLD_HELMET).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).enchant(Enchantment.DURABILITY, 5).build()
        );

        return builder;
    }
}
