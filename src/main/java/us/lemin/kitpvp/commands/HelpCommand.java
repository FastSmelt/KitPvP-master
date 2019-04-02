package us.lemin.kitpvp.commands;

import org.bukkit.entity.Player;
import us.lemin.core.commands.PlayerCommand;
import us.lemin.core.utils.message.CC;
import us.lemin.kitpvp.server.Buffs;

public class HelpCommand extends PlayerCommand {
    public HelpCommand() {
        super("help");
        setUsage(CC.SECONDARY + "/help event" + CC.GRAY + " - " + CC.PRIMARY + "view all event commands",
                CC.SECONDARY + "/help core" + CC.GRAY + " - " + CC.PRIMARY + "view all core commands",
                CC.SECONDARY + "/help regular" + CC.GRAY + " - " + CC.PRIMARY + "view all regular mode commands",
                CC.SECONDARY + "/help classic" + CC.GRAY + " - " + CC.PRIMARY + "view all classic mode commands"
        );
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 1) {
            player.sendMessage(usageMessage);
            return;
        }
        String arg = args[0].toLowerCase();
        switch (arg) {
            case "event":
                player.sendMessage(CC.BOARD_SEPARATOR);
                player.performCommand("event");
                break;
            case "core":
                player.sendMessage(CC.BOARD_SEPARATOR);
                player.sendMessage(CC.SECONDARY + "/list" + CC.GRAY + " - " + CC.PRIMARY + "view all online players and their ranks");
                player.sendMessage(CC.SECONDARY + "/message <player> <message>" + CC.GRAY + " - " + CC.PRIMARY + "message another player");
                player.sendMessage(CC.SECONDARY + "/reply <message>" + CC.GRAY + " - " + CC.PRIMARY + "reply to the person who last messaged you");
                player.sendMessage(CC.SECONDARY + "/(un)ignore <player>" + CC.GRAY + " - " + CC.PRIMARY + "ignore or unignore a offline/online player");
                player.sendMessage(CC.SECONDARY + "/helpop <request>" + CC.GRAY + " - " + CC.PRIMARY + "message all online staff for assistance");
                player.sendMessage(CC.SECONDARY + "/ping <player>" + CC.GRAY + " - " + CC.PRIMARY + "view your own or another player's ping");
                player.sendMessage(CC.SECONDARY + "/report <player>" + CC.GRAY + " - " + CC.PRIMARY + "report another player for breaking the rules");
                player.sendMessage(CC.SECONDARY + "/color <color>" + CC.GRAY + " - " + CC.PRIMARY + "change the color of your prefix and name");
                player.sendMessage(CC.SECONDARY + "/togglesounds" + CC.GRAY + " - " + CC.PRIMARY + "toggles sounds from messages or mentions");
                player.sendMessage(CC.SECONDARY + "/togglemessages" + CC.GRAY + " - " + CC.PRIMARY + "toggles private messages");
                player.sendMessage(CC.SECONDARY + "/togglechat" + CC.GRAY + " - " + CC.PRIMARY + "toggles global chat");
                player.sendMessage(CC.SECONDARY + "/links" + CC.GRAY + " - " + CC.PRIMARY + "view the server's social media links");
                break;
            case "regular":
                player.sendMessage(CC.BOARD_SEPARATOR);
                player.sendMessage(CC.SECONDARY + "/spawn" + CC.GRAY + " - " + CC.PRIMARY + "returns you to spawn");
                player.sendMessage(CC.SECONDARY + "/kit <kit name>" + CC.GRAY + " - " + CC.PRIMARY + "opens the kit selector or applies a kit");
                player.sendMessage(CC.SECONDARY + "/kitshop" + CC.GRAY + " - " + CC.PRIMARY + "opens the kit shop");
                player.sendMessage(CC.SECONDARY + "/shop" + CC.GRAY + " - " + CC.PRIMARY + "opens the pvp shop");
                player.sendMessage(CC.SECONDARY + "/resetstats" + CC.GRAY + " - " + CC.PRIMARY + "resets your stats");
                player.sendMessage(CC.SECONDARY + "/clearkit" + CC.GRAY + " - " + CC.PRIMARY + "clears your kit and returns hotbar items");
                player.sendMessage(CC.SECONDARY + "/stats <player>" + CC.GRAY + " - " + CC.PRIMARY + "view your own or another player's stats");
                player.sendMessage(CC.SECONDARY + "/soup" + CC.GRAY + " - " + CC.PRIMARY + "purchases a hotbar of soup for 10 credits");
                player.sendMessage(CC.SECONDARY + "/refill" + CC.GRAY + " - " + CC.PRIMARY + "purchases an inventory of soup for 25 credits");
                player.sendMessage(CC.SECONDARY + "/repair" + CC.GRAY + " - " + CC.PRIMARY + "repairs your armor for 50 credits");
                player.sendMessage(CC.SECONDARY + "/anticontrol" + CC.GRAY + " - " + CC.PRIMARY + "prevents control from other players for 50 credits");
                player.sendMessage(CC.SECONDARY + "/togglescoreboard" + CC.GRAY + " - " + CC.PRIMARY + "toggles the scoreboard");
                break;
            case "classic":
                player.sendMessage(CC.BOARD_SEPARATOR);
                player.sendMessage(CC.SECONDARY + "/spawn" + CC.GRAY + " - " + CC.PRIMARY + "returns you to spawn");
                player.sendMessage(CC.SECONDARY + "/kit" + CC.GRAY + " - " + CC.PRIMARY + "applies the classic kit");
                player.sendMessage(CC.SECONDARY + "/strength" + CC.GRAY + " - " + CC.PRIMARY + "applies strength for " + Buffs.STRENGTH.getPriceByLevel(0) + "/" + Buffs.STRENGTH.getPriceByLevel(1) + " credits");
                player.sendMessage(CC.SECONDARY + "/speed" + CC.GRAY + " - " + CC.PRIMARY + "applies speed for " + Buffs.SPEED.getPriceByLevel(0) + "/" + Buffs.SPEED.getPriceByLevel(1) + " credits");
                player.sendMessage(CC.SECONDARY + "/poison" + CC.GRAY + " - " + CC.PRIMARY + "places a splash poison potion in the first empty or second slot of your inventory for " + Buffs.POISON.getPriceByLevel(0) + " credits");
                player.sendMessage(CC.SECONDARY + "/archer" + CC.GRAY + " - " + CC.PRIMARY + "replaces your sword with a bow and arrow");
                player.sendMessage(CC.SECONDARY + "/power" + CC.GRAY + " - " + CC.PRIMARY + "applies power to your bow for " + Buffs.POWER.getPriceByLevel(0) + "/" + Buffs.POWER.getPriceByLevel(1) + "/" + Buffs.POWER.getPriceByLevel(2) + " credits");
                player.sendMessage(CC.SECONDARY + "/punch" + CC.GRAY + " - " + CC.PRIMARY + "applies punch to your bow for " + Buffs.PUNCH.getPriceByLevel(0) + "/" + Buffs.PUNCH.getPriceByLevel(1) + " credits");
                player.sendMessage(CC.SECONDARY + "/sharpness" + CC.GRAY + " - " + CC.PRIMARY + "applies sharpness to your sword for " + Buffs.SHARPNESS.getPriceByLevel(0) + "/" + Buffs.SHARPNESS.getPriceByLevel(1) + "/" + Buffs.SHARPNESS.getPriceByLevel(2) + " credits");
                player.sendMessage(CC.SECONDARY + "/knockback" + CC.GRAY + " - " + CC.PRIMARY + "applies knockback to your sword for " + Buffs.KNOCKBACK.getPriceByLevel(0) + "/" + Buffs.KNOCKBACK.getPriceByLevel(1) + " credits");
                player.sendMessage(CC.SECONDARY + "/diamond" + CC.GRAY + " - " + CC.PRIMARY + "applies a set of diamond armor for " + Buffs.DIAMOND.getPriceByLevel(0) + " credits");
                player.sendMessage(CC.SECONDARY + "/gold" + CC.GRAY + " - " + CC.PRIMARY + "applies a set of golden armor for free");
                player.sendMessage(CC.SECONDARY + "/leather" + CC.GRAY + " - " + CC.PRIMARY + "applies a set of leather armor for free");
                break;
            default:
                player.sendMessage(usageMessage);
        }
    }
}
