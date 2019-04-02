package us.lemin.kitpvp.commands.admin;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import us.lemin.core.commands.PlayerCommand;
import us.lemin.core.player.rank.Rank;
import us.lemin.core.utils.message.CC;
import us.lemin.kitpvp.KitPvPPlugin;
import us.lemin.kitpvp.util.structure.Cuboid;
import us.lemin.kitpvp.util.structure.RegionData;

public class EditRegionCommand extends PlayerCommand {
    private final KitPvPPlugin plugin;

    public EditRegionCommand(KitPvPPlugin plugin) {
        super("editregion", Rank.ADMIN);
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 1) {
            player.sendMessage(usageMessage);
            return;
        }

        switch (args[0].toLowerCase()) {
            case "start":
                if (plugin.getRegionManager().isEditingRegion(player)) {
                    player.sendMessage(CC.RED + "You're already editing a region!");
                    return;
                }

                plugin.getRegionManager().startEditingRegion(player);
                player.sendMessage(CC.GREEN + "Begun editing region. Use the wand to select points.");
                player.getInventory().clear();
                player.getInventory().setItem(1, new ItemStack(Material.GOLD_AXE));
                break;
            case "stop":
                if (!plugin.getRegionManager().isEditingRegion(player)) {
                    player.sendMessage(CC.RED + "You aren't editing a region!");
                    return;
                }

                plugin.getRegionManager().stopEditingRegion(player);
                player.sendMessage(CC.RED + "Stopped editing region.");
                break;
            case "finish":
                if (!plugin.getRegionManager().isEditingRegion(player)) {
                    player.sendMessage(CC.RED + "You aren't editing a region!");
                    return;
                }

                if (!plugin.getRegionManager().isDataValid(player)) {
                    player.sendMessage(CC.RED + "You must set both points with the wand before you can finish!");
                    return;
                }

                if (args.length < 2) {
                    player.sendMessage(CC.RED + "You must define a name for this region! (spawn|koth)");
                    return;
                }

                RegionData data = plugin.getRegionManager().getData(player);
                Cuboid cuboid = new Cuboid(data.getA(), data.getB());

                switch (args[1].toLowerCase()) {
                    case "spawn":
                        plugin.setSpawnCuboid(cuboid);
                        plugin.getLocationConfig().set("spawn-cuboid", cuboid);
                        plugin.getLocationConfig().save();
                        break;
                    case "koth":
                        plugin.setKothCuboid(cuboid);
                        plugin.getLocationConfig().set("koth-cuboid", cuboid);
                        plugin.getLocationConfig().save();
                        break;
                }


                plugin.getRegionManager().stopEditingRegion(player);

                player.sendMessage(CC.GREEN + "Finished editing region.");
                break;
        }
    }
}
