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

import java.util.Arrays;
import java.util.List;

public class Flash extends Kit {
    public Flash(KitPvPPlugin plugin) {
        super(plugin, "Flash", Material.SUGAR, "Zoom around the map at the speed of sound.");
    }

    @Override
    protected void onEquip(Player player) {

    }

    @Override
    protected List<PotionEffect> effects() {
        return Arrays.asList(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2), new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
    }

    @Override
    protected KitContents.Builder contentsBuilder() {
        KitContents.Builder builder = KitContents.newBuilder();
        builder.addItem(new ItemBuilder(Material.DIAMOND_SWORD).enchant(Enchantment.DAMAGE_ALL, 3).build());
        builder.fill(new ItemStack(Material.MUSHROOM_SOUP));
        return builder;
    }
}
