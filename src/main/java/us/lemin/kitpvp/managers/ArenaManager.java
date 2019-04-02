package us.lemin.kitpvp.managers;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import us.lemin.core.utils.item.ItemBuilder;
import us.lemin.core.utils.message.CC;
import us.lemin.kitpvp.KitPvPPlugin;
import us.lemin.kitpvp.arena.Match;
import us.lemin.kitpvp.player.KitProfile;
import us.lemin.kitpvp.player.PlayerState;
import us.lemin.kitpvp.util.TempLocations;

import java.util.UUID;

@RequiredArgsConstructor
public class ArenaManager {
    private final KitPvPPlugin plugin;

    public void startMatch(Player firstPlayer, Player secondPlayer) {
        Match match = new Match(firstPlayer, secondPlayer, plugin);
        registerMatchesInProfiles(match, match.getFirstPlayerId(), match.getSecondPlayerId());
        firstPlayer.sendMessage(CC.PRIMARY + "You have started a match with " + CC.SECONDARY + secondPlayer.getName() + ".");
        secondPlayer.sendMessage(CC.PRIMARY + "You have started a match with " + CC.SECONDARY + firstPlayer.getName() + ".");
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            firstPlayer.hidePlayer(player);
            secondPlayer.hidePlayer(player);
        }
        firstPlayer.teleport(TempLocations.ARENA_SPAWN_A);
        secondPlayer.teleport(TempLocations.ARENA_SPAWN_B);
        firstPlayer.showPlayer(secondPlayer);
        secondPlayer.showPlayer(firstPlayer);

        givePvPKit(firstPlayer);
        givePvPKit(secondPlayer);
    }

    public boolean canRequest(Player player) {
        KitProfile profile = plugin.getPlayerManager().getProfile(player);
        return profile.getState() == PlayerState.ARENA && profile.getActiveMatch() == null;
    }

    private void givePvPKit(Player player) {
        player.getInventory().clear();
        ItemStack sword = new ItemBuilder(Material.DIAMOND_SWORD).enchant(Enchantment.DAMAGE_ALL, 1).build();
        PotionEffect effect = new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0);
        player.addPotionEffect(effect);
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
    }

    private void registerMatchesInProfiles(Match match, UUID firstPlayer, UUID secondPlayer) {
        KitProfile firstProfile = plugin.getPlayerManager().getProfile(plugin.getServer().getPlayer(firstPlayer));
        KitProfile secondProfile = plugin.getPlayerManager().getProfile(plugin.getServer().getPlayer(secondPlayer));
        firstProfile.setActiveMatch(match);
        secondProfile.setActiveMatch(match);
    }

}
