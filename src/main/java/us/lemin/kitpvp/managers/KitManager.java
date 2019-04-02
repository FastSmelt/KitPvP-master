package us.lemin.kitpvp.managers;

import lombok.Getter;
import us.lemin.kitpvp.KitPvPPlugin;
import us.lemin.kitpvp.kit.ArenaKit;
import us.lemin.kitpvp.kit.Kit;
import us.lemin.kitpvp.kit.impl.arena.Default;
import us.lemin.kitpvp.kit.impl.ffa.*;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class KitManager {
    private final Map<String, Kit> FfaKitNames = new LinkedHashMap<>();
    private final Map<Class<? extends Kit>, Kit> FfaKitClasses = new LinkedHashMap<>();
    private final Map<String, ArenaKit> ArenaKitNames = new LinkedHashMap<>();
    private final Map<Class<? extends ArenaKit>, ArenaKit> ArenaKitClasses = new LinkedHashMap<>();
    @Getter
    private final Kit defaultKit;

    public KitManager(KitPvPPlugin plugin) {
        registerFFAKits(
                new PvP(plugin),
                new Archer(plugin),
                new Thor(plugin),
                new Clout(plugin),
                new Ninja(plugin),
                new TimeMaster(plugin),
                new Noob(plugin),
                new Vampire(plugin),
                new Unholy(plugin),
                new Snail(plugin),
                new Flash(plugin),
                new Turtle(plugin),
                new Berserker(plugin),
                new Mage(plugin),
                new Scorpion(plugin),
                new Mineman(plugin),
                new Summoner(plugin),
                new Chemist(plugin),
                new Hulk(plugin),
                new Thanos(plugin)
        );
        registerArenaKits(new Default(plugin),
                new Default(plugin)
        );
        defaultKit = getFfaKitByClass(PvP.class);
    }

    private void registerFFAKits(Kit... kits) {
        for (Kit kit : kits) {
            FfaKitNames.put(kit.getName().toLowerCase(), kit);
            FfaKitClasses.put(kit.getClass(), kit);
        }
    }

    private void registerArenaKits(ArenaKit... kits) {
        for (ArenaKit arenaKit : kits) {
            ArenaKitNames.put(arenaKit.getName().toLowerCase(), arenaKit);
            ArenaKitClasses.put(arenaKit.getClass(), arenaKit);
        }
    }


    public Kit getFfaKitByName(String kitName) {
        return FfaKitNames.get(kitName.toLowerCase());
    }

    public Kit getFfaKitByClass(Class<? extends Kit> clazz) {
        return FfaKitClasses.get(clazz);
    }

    public Collection<Kit> getKits() {
        return FfaKitNames.values();
    }
}
