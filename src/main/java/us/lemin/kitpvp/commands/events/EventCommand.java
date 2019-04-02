package us.lemin.kitpvp.commands.events;

import org.bukkit.entity.Player;
import us.lemin.core.CorePlugin;
import us.lemin.core.commands.PlayerCommand;
import us.lemin.core.player.CoreProfile;
import us.lemin.core.player.rank.Rank;
import us.lemin.core.utils.message.CC;
import us.lemin.core.utils.timer.Timer;
import us.lemin.kitpvp.KitPvPPlugin;
import us.lemin.kitpvp.events.Event;
import us.lemin.kitpvp.events.EventType;
import us.lemin.kitpvp.managers.EventManager;
import us.lemin.kitpvp.player.KitProfile;
import us.lemin.kitpvp.player.PlayerState;

import java.util.Arrays;
import java.util.stream.Collectors;

public class EventCommand extends PlayerCommand {
    private static final String EVENT_TYPES;

    static {
        String types = String.join(", ", Arrays.stream(EventType.values()).map(EventType::getName).collect(Collectors.toList()));
        EVENT_TYPES = CC.GREEN + "Valid event types: " + types;
    }

    private final KitPvPPlugin plugin;

    public EventCommand(KitPvPPlugin plugin) {
        super("event");
        this.plugin = plugin;

        setUsage(
                CC.SECONDARY + "/event host <type>" + CC.GRAY + " - " + CC.PRIMARY + "host an event of the specified type",
//                CC.SECONDARY + "/event status <type>" + CC.GRAY + " - " + CC.PRIMARY + "see the event status of the specified type",
                CC.SECONDARY + "/event spectate <type>" + CC.GRAY + " - " + CC.PRIMARY + "join an event of the specified type",
                CC.SECONDARY + "/event join <type>" + CC.GRAY + " - " + CC.PRIMARY + "join an event of the specified type",
                CC.SECONDARY + "/event leave" + CC.GRAY + " - " + CC.PRIMARY + "leave your current event",
                EVENT_TYPES
        );
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 1) {
            player.sendMessage(usageMessage);
            return;
        }

        KitProfile profile = plugin.getPlayerManager().getProfile(player);
        String arg = args[0].toLowerCase();

        switch (arg) {
            case "host": {
                CoreProfile coreProfile = CorePlugin.getInstance().getProfileManager().getProfile(player.getUniqueId());

                if (coreProfile != null && !coreProfile.hasRank(Rank.VOTER)) {
                    player.sendMessage(CC.RED + "Only voters can host events! Vote at https://namemc.com/server/lemin.us");
                    return;
                }

                if (profile.getState() != PlayerState.SPAWN) {
                    player.sendMessage(CC.RED + "You must be in spawn to host an event!");
                    return;
                }

                if (args.length < 2) {
                    player.sendMessage(CC.RED + "You need to specify a valid event type!");
                    player.sendMessage(EVENT_TYPES);
                    return;
                }

                EventType type = EventType.getByName(args[1]);

                if (type == null) {
                    player.sendMessage(CC.RED + "That's an invalid event type!");
                    player.sendMessage(EVENT_TYPES);
                    return;
                }

                EventManager manager = plugin.getEventManager();

                if (manager.isEventActive(type)) {
                    player.sendMessage(CC.RED + "That event is already running!");
                    return;
                }

                Timer timer = profile.getEventHostTimer();

                if (timer.isActive()) {
                    player.sendMessage(CC.RED + "You can't host events for another " + timer.formattedExpiration() + ".");
                    return;
                }

                Event event = manager.getEventByType(type);

                event.host(player, profile);
                break;
            }
            case "spectate": {
                if (profile.getState() != PlayerState.SPAWN) {
                    player.sendMessage(CC.RED + "You must be in spawn to spectate an event!");
                    return;
                }

                if (args.length < 2) {
                    player.sendMessage(CC.RED + "You need to specify a valid event type!");
                    player.sendMessage(EVENT_TYPES);
                    return;
                }

                EventType type = EventType.getByName(args[1]);

                if (type == null) {
                    player.sendMessage(CC.RED + "That's an invalid event type!");
                    player.sendMessage(EVENT_TYPES);
                    return;
                }

                EventManager manager = plugin.getEventManager();

                if (!manager.isEventActive(type)) {
                    player.sendMessage(CC.RED + "That event isn't running!");
                    return;
                }

                Event event = manager.getEventByType(type);

                event.spectate(player, profile);
                break;
            }
            case "join": {
                if (profile.getState() != PlayerState.SPAWN) {
                    player.sendMessage(CC.RED + "You must be in spawn to join an event!");
                    return;
                }

                if (args.length < 2) {
                    player.sendMessage(CC.RED + "You need to specify a valid event type!");
                    player.sendMessage(EVENT_TYPES);
                    return;
                }

                EventType type = EventType.getByName(args[1]);

                if (type == null) {
                    player.sendMessage(CC.RED + "That's an invalid event type!");
                    player.sendMessage(EVENT_TYPES);
                    return;
                }

                EventManager manager = plugin.getEventManager();

                if (!manager.isEventActive(type)) {
                    player.sendMessage(CC.RED + "That event isn't running!");
                    return;
                }

                Event event = manager.getEventByType(type);

                event.join(player, profile);
                break;
            }
            case "leave": {
                Event event = profile.getActiveEvent();

                if (event == null) {
                    player.sendMessage(CC.RED + "You aren't in an event!");
                    return;
                }

                event.leave(player, profile);
                break;
            }
            default:
                player.sendMessage(usageMessage);
                break;
        }
    }
}
