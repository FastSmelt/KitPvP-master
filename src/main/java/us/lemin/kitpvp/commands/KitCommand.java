package us.lemin.kitpvp.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.lemin.core.commands.PlayerCommand;
import us.lemin.core.utils.message.CC;
import us.lemin.kitpvp.KitPvPPlugin;
import us.lemin.kitpvp.inventory.KitSelectorPlayerWrapper;
import us.lemin.kitpvp.kit.Kit;
import us.lemin.kitpvp.player.KitProfile;
import us.lemin.kitpvp.player.PlayerState;
import us.lemin.kitpvp.server.ServerMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KitCommand extends PlayerCommand {
    private final KitPvPPlugin plugin;

    public KitCommand(KitPvPPlugin plugin) {
        super("kit");
        this.plugin = plugin;
        setAliases("kits");
    }

    @Override
    public void execute(Player player, String[] args) {
        KitProfile profile = plugin.getPlayerManager().getProfile(player);

        if (profile.getState() != PlayerState.SPAWN) {
            player.sendMessage(CC.RED + "You can't choose a kit right now!");
            return;
        }

        if (plugin.getServerMode() == ServerMode.CLASSIC) {
            plugin.getPlayerManager().giveClassicKit(player);
            return;
        }

        if (args.length < 1) {
            plugin.getInventoryManager().getPlayerWrapper(KitSelectorPlayerWrapper.class).open(player);
            return;
        }

        Kit kit = plugin.getKitManager().getFfaKitByName(args[0]);

        if (kit == null) {
            plugin.getInventoryManager().getPlayerWrapper(KitSelectorPlayerWrapper.class).open(player);
            return;
        }

        kit.apply(player);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        List<String> kits = new ArrayList<>();
        if (args.length >= 1) {
            for (Kit kit : plugin.getKitManager().getKits()) {
                if (kit.getName().startsWith(args[0])) {
                    return Collections.singletonList(kit.getName());
                }
                kits.add(kit.getName());
            }
        }
        return kits;
    }
}
