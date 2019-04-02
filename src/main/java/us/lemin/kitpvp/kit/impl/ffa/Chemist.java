package us.lemin.kitpvp.kit.impl.ffa;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import us.lemin.core.utils.item.ItemBuilder;
import us.lemin.kitpvp.KitPvPPlugin;
import us.lemin.kitpvp.kit.Kit;
import us.lemin.kitpvp.kit.KitContents;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Chemist extends Kit {

    private ItemStack harm;
    private ItemStack slow;
    private ItemStack poison;
    private List<ItemStack> items;


    public Chemist(KitPvPPlugin plugin) {
        super(plugin, "Chemist", new ItemBuilder(Material.POTION).durability(16428).build(), "You receive debuff potions after killing another player.");
        harm = new ItemBuilder(Material.POTION).durability(16428).amount(2).build();
        slow = new ItemBuilder(Material.POTION).durability(16426).amount(2).build();
        poison = new ItemBuilder(Material.POTION).durability(16388).amount(2).build();
        items = Arrays.asList(harm, slow, poison);
    }

    @Override
    protected void onEquip(Player player) {
        int x = 0;
        for (ItemStack itemStack : items) {
            player.getInventory().setItem(++x, itemStack);
        }
    }

    @Override
    protected List<PotionEffect> effects() {
        return Collections.singletonList(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0));
    }

    @Override
    protected KitContents.Builder contentsBuilder() {
        KitContents.Builder builder = KitContents.newBuilder();

        builder.addItem(new ItemStack(Material.DIAMOND_SWORD));
        builder.fill(new ItemStack(Material.MUSHROOM_SOUP));
        builder.addArmor(
                new ItemStack(Material.IRON_BOOTS),
                new ItemBuilder(Material.GOLD_LEGGINGS).enchant(Enchantment.DURABILITY, 4).build(),
                new ItemStack(Material.IRON_CHESTPLATE),
                new ItemStack(Material.IRON_HELMET)
        );

        return builder;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player)) {
            return;
        }

        Player chemist = (Player) event.getDamager();
        Player damaged = (Player) event.getEntity();

        if (isInvalidKit(chemist)) {
            return;
        }

        boolean died = damaged.getHealth() - event.getFinalDamage() <= 0;
        if (died) {
            items.forEach(itemStack -> chemist.getInventory().addItem(itemStack));
        }
    }
}
