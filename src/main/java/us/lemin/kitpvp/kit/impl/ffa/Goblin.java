package us.lemin.kitpvp.kit.impl.ffa;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
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

import java.util.Collections;
import java.util.List;

public class Goblin extends Kit {

    public Goblin(KitPvPPlugin plugin) {
        super(plugin, "Goblin", Material.FLOWER_POT_ITEM, "Steal soups or potions from your enemy.");
        registerCooldownTimer("goblin", 30);
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

        builder.addItem(new ItemBuilder(Material.IRON_SWORD).enchant(Enchantment.DAMAGE_ALL, 1).enchant(Enchantment.DURABILITY, 3).name(CC.GOLD + "Goblin's Dagger").build());
        builder.addItem(new ItemBuilder(Material.FLOWER_POT_ITEM).name(CC.GOLD + "Treasure Bag").build());
        builder.fill(new ItemStack(Material.MUSHROOM_SOUP));
        builder.addArmor(
                new ItemBuilder(Material.GOLD_BOOTS).enchant(Enchantment.DURABILITY, 4).build(),
                new ItemStack(Material.IRON_LEGGINGS),
                new ItemStack(Material.IRON_CHESTPLATE),
                new ItemBuilder(Material.CHAINMAIL_HELMET).enchant(Enchantment.DURABILITY, 4).build()
        );

        return builder;
    }

    @EventHandler
    public void onGoblin(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Player)) {
            return;
        }

        if (event.getPlayer().getItemInHand().getType() != Material.FLOWER_POT_ITEM) {
            return;
        }

        Player goblin = event.getPlayer();
        Player victim = (Player) event.getRightClicked();
        KitProfile kitProfile = plugin.getPlayerManager().getProfile(goblin);
        KitProfile victimProfile = plugin.getPlayerManager().getProfile(victim);

        if (kitProfile.getState() != PlayerState.FFA || victimProfile.getState() != PlayerState.FFA) {
            return;
        }

        if (isInvalidKit(goblin) || isOnCooldown(goblin, "goblin")) {
            return;
        }
        int x = 0;
        ItemStack healthPotion = new ItemBuilder(Material.POTION).amount(1).durability(16421).build();
        ItemStack soup = new ItemStack(Material.MUSHROOM_SOUP);
        while (x <= 3) {
            if (victimProfile.getCurrentKit().getName().equalsIgnoreCase("potpvp")) {
                if (victim.getInventory().contains(healthPotion)) {
                    victim.getInventory().removeItem(healthPotion);
                    goblin.getInventory().addItem(new ItemBuilder(Material.MUSHROOM_SOUP).amount(1).build());

                }
                x++;
            } else {
                if (victim.getInventory().contains(Material.MUSHROOM_SOUP)) {
                    victim.getInventory().removeItem(new ItemBuilder(Material.MUSHROOM_SOUP).amount(1).build());
                    goblin.getInventory().addItem(new ItemBuilder(Material.MUSHROOM_SOUP).amount(1).build());
                }
                x++;
            }

        }
        victim.sendMessage(CC.RED + "A Goblin has stolen items from you!");
        goblin.sendMessage(CC.GREEN + "You have stolen items from " + victim.getName() + "!");

    }
}
