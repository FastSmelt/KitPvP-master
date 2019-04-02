package us.lemin.kitpvp.tasks;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Wool;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import us.lemin.core.utils.item.ItemBuilder;
import us.lemin.kitpvp.player.KitProfile;

import java.util.Collection;

public class VulnerableTask implements Runnable {

    public final Player player;
    public final String statusChange;
    private final Collection<PotionEffect> potionEffects;
    private final Wool woolColor = new Wool(DyeColor.RED);
    private final KitProfile profile;
    private final double healthSnapshot;
    private ItemStack[] inventoryContents;
    private ItemStack[] armorContents;


    public VulnerableTask(Player player, String statusChange, KitProfile profile) {
        this.player = player;
        this.profile = profile;
        this.statusChange = statusChange.toLowerCase();
        this.inventoryContents = player.getInventory().getContents();
        this.potionEffects = player.getActivePotionEffects();
        this.armorContents = player.getInventory().getArmorContents();
        this.healthSnapshot = player.getHealth();

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.getActivePotionEffects().clear();
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 100, 1));
        player.setHealth(0.1);
        player.getInventory().setHelmet(woolColor.toItemStack());
    }

    @Override
    public void run() {
        if (!player.isOnline()) {
            return;
        }

        if (profile.isDiedWhileVulnerable()) {
            profile.setDiedWhileVulnerable(false);
            return;
        }

        switch (statusChange) {
            case "repair":
                ItemStack sword = new ItemBuilder(Material.DIAMOND_SWORD).enchant(Enchantment.DAMAGE_ALL, 1).build();
                player.getInventory().setHelmet(new ItemStack(Material.IRON_HELMET));
                player.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
                player.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
                player.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS));
                player.getInventory().addItem(sword);
                int x = 0;
                while (x < 36) {
                    x++;
                    player.getInventory().addItem(new ItemStack(Material.MUSHROOM_SOUP));
                }
                break;
            case "hotbar":
                player.getInventory().setArmorContents(armorContents);
                player.getInventory().setContents(inventoryContents);
                potionEffects.forEach(player::addPotionEffect);
                x = 0;
                while (x < 9) {
                    x++;
                    player.getInventory().addItem(new ItemStack(Material.MUSHROOM_SOUP));
                }
                break;
            case "refill":
                player.getInventory().setArmorContents(armorContents);
                player.getInventory().setContents(inventoryContents);
                potionEffects.forEach(player::addPotionEffect);
                x = 0;
                while (x < 36) {
                    x++;
                    player.getInventory().addItem(new ItemStack(Material.MUSHROOM_SOUP));
                }
                break;
        }

        player.setHealth(healthSnapshot);
        profile.setVulnerableTask(null);
    }
}
