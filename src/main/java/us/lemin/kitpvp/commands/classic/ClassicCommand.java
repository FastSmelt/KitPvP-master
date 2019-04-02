package us.lemin.kitpvp.commands.classic;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.lemin.core.commands.PlayerCommand;
import us.lemin.core.player.rank.Rank;
import us.lemin.core.utils.message.CC;
import us.lemin.kitpvp.KitPvPPlugin;
import us.lemin.kitpvp.server.ServerMode;

public abstract class ClassicCommand extends PlayerCommand {
    private final KitPvPPlugin plugin;

    protected ClassicCommand(String name, KitPvPPlugin plugin) {
        super(name, Rank.MEMBER);
        this.plugin = plugin;
    }

    @Override
    protected final void execute(CommandSender sender, String[] args) {
        if (plugin.getServerMode() == ServerMode.CLASSIC) {
            execute((Player) sender, args);
        } else {
            sender.sendMessage(CC.RED + "This command can only be used during Classic mode.");
        }
    }

    public abstract void execute(Player player, String[] args);
}
