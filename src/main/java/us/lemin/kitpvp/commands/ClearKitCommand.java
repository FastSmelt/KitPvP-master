package us.lemin.kitpvp.commands;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.potion.PotionEffect;
import us.lemin.core.commands.PlayerCommand;
import us.lemin.core.utils.message.CC;
import us.lemin.kitpvp.KitPvPPlugin;
import us.lemin.kitpvp.player.KitProfile;
import us.lemin.kitpvp.player.PlayerState;

public class ClearKitCommand extends PlayerCommand {
    private final KitPvPPlugin plugin;

    public ClearKitCommand(KitPvPPlugin plugin) {
        super("clearkit");
        this.plugin = plugin;
        setAliases("removekit", "deletekit", "ci", "ck");
    }

    @Override
    public void execute(Player player, String[] args) {
        KitProfile profile = plugin.getPlayerManager().getProfile(player);

        if (profile.getState() != PlayerState.SPAWN) {
            player.sendMessage(CC.RED + "You can't clear your kit while not in spawn!");
            return;
        }

        profile.setCurrentKit(null);

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        for (ItemStack armor : player.getInventory().getArmorContents()) {
            if (armor instanceof Repairable) {
                armor.setDurability((short) 0);
            }
        }

        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }

        plugin.getPlayerManager().giveSpawnItems(player);
        player.sendMessage(CC.GREEN + "Kit cleared!");
    }
}
