/*package us.lemin.kitpvp.kit;

import org.bukkit.event.Event;
import org.bukkit.plugin.EventExecutor;

import java.util.HashMap;
import java.util.Map;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.plugin.EventExecutor;
import us.lemin.core.utils.message.CC;
import us.lemin.core.utils.timer.Timer;
import us.lemin.core.utils.timer.impl.DoubleTimer;
import us.lemin.kitpvp.KitPvPPlugin;
import us.lemin.kitpvp.player.KitProfile;

public abstract class AbilityKit implements EventExecutor {
    private final Map<Class<? extends Event>, MethodData> methodsByEventClass = new HashMap<>();
    private final Map<UUID, Map<String, Timer>> abilityCooldowns = new HashMap<>();

    public AbilityKit(String description) {

        for (Method method : getClass().getDeclaredMethods()) {
            Ability ability = method.getAnnotation(Ability.class);

            if (ability == null || method.getParameters().length != 1) {
                continue;
            }

            Class<?> clazz = method.getParameterTypes()[0];

            if (Event.class.isAssignableFrom(clazz)) {
                Class<? extends Event> eventClass = clazz.asSubclass(Event.class);
                methodsByEventClass.put(eventClass, new MethodData(method, ability));
                Bukkit.getServer().getPluginManager().registerEvent(eventClass, new DummyListener(),
                        EventPriority.NORMAL, this, KitPvPPlugin.getInstance());
            }
        }
    }

    @Override
    public void execute(Listener listener, Event event) throws EventException {
        if (event instanceof Cancellable) {
            Cancellable ev = (Cancellable) event;

            if (ev.isCancelled()) {
                return;
            }
        }

        if (event instanceof PlayerEvent) {
            MethodData data = methodsByEventClass.get(event.getClass());

            if (data != null) {
                try {
                    PlayerEvent playerEvent = (PlayerEvent) event;
                    Player player = playerEvent.getPlayer();
                    KitProfile profile = KitPvPPlugin.getInstance().getPlayerManager().getProfile(player);

                    if (profile.getCurrentKit() != this) {
                        return;
                    }

                    String abilityName = data.ability.name();

                    abilityCooldowns.putIfAbsent(player.getUniqueId(), new HashMap<>());

                    Map<String, Timer> playerCooldowns = abilityCooldowns.get(player.getUniqueId());

                    playerCooldowns.putIfAbsent(abilityName, new DoubleTimer(data.ability.cooldown()));

                    Timer timer = playerCooldowns.get(abilityName);

                    if (timer.isActive(false)) {
                        player.sendMessage(CC.RED + "You're still on cooldown for " + CC.SECONDARY
                                + abilityName + CC.RED + " for " + timer.formattedExpiration() + ".");
                        return;
                    }

                    boolean startCooldown = (boolean) data.method.invoke(this, event);

                    if (startCooldown) {
                        timer.isActive();
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @RequiredArgsConstructor
    private static final class MethodData {
        private final Method method;
        private final Ability ability;
    }

    private static final class DummyListener implements Listener {
    }
}
*/