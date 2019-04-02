package us.lemin.kitpvp.kit.impl.ffa;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import us.lemin.core.utils.StringUtil;
import us.lemin.core.utils.item.ItemBuilder;
import us.lemin.core.utils.message.CC;
import us.lemin.kitpvp.KitPvPPlugin;
import us.lemin.kitpvp.kit.Kit;
import us.lemin.kitpvp.kit.KitContents;
import us.lemin.kitpvp.player.KitProfile;
import us.lemin.kitpvp.player.PlayerState;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Thanos extends Kit {
    public Thanos(KitPvPPlugin plugin) {
        super(plugin, "Thanos", Material.EMERALD, "You can dust away the whole server with the snap of your finger.");
        registerCooldownTimer("thanos", 30);
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
        builder.addItem(new ItemBuilder(Material.EMERALD).name("Time Stone").build());
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
    public void onSnap(PlayerInteractEvent event) {
        if (!event.hasItem() || event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            return;
        }

        if (event.getItem().getType() != Material.EMERALD) {
            return;
        }


        Player thanos = event.getPlayer();

        if (isInvalidKit(thanos)) {
            return;
        }

        KitProfile kitProfile = plugin.getPlayerManager().getProfile(thanos);

        if (kitProfile.getState() != PlayerState.FFA) {
            return;
        }

        List<Player> nearbyEntities = thanos.getNearbyEntities(10.0, 10.0, 10.0)
                .stream()
                .filter(Player.class::isInstance)
                .map(Player.class::cast)
                .collect(Collectors.toList());

        nearbyEntities.remove(thanos);



        if (nearbyEntities.size() > 0) {
            if (isOnCooldown(thanos, "thanos")) {
                return;
            }

            for (Player victims : nearbyEntities) {
                KitProfile victimProfile = plugin.getPlayerManager().getProfile(victims);
                if (victimProfile.getState() == PlayerState.FFA) {
                    victims.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 5, 2));
                    victims.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * 10, 2));
                    victims.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 5, 1));
                    victims.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 5, 2));
                    victims.sendMessage(CC.RED + "I don't feel so good...");
                }
            }
        } else {
            thanos.sendMessage(CC.RED + "There aren't any players to snap.");
            return;
        }

        String names = StringUtil.joinListGrammaticallyWithGuava(nearbyEntities.stream().map(Player::getName).collect(Collectors.toList()));
        thanos.sendMessage(ChatColor.GREEN + "You snapped " + names + ".");
    }
}
