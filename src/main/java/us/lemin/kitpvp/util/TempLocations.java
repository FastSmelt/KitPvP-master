package us.lemin.kitpvp.util;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

@UtilityClass
// TODO: make location management
public class TempLocations {
    private static final World MAIN_WORLD = Bukkit.getWorlds().get(0);
    public static final Location SUMO_SPAWN = new Location(MAIN_WORLD, -142.5, 42, 325.5, 180, 0);
    public static final Location SUMO_SPAWN_A = new Location(MAIN_WORLD, -142.5, 32, 320.5, 0, 0);
    public static final Location SUMO_SPAWN_B = new Location(MAIN_WORLD, -142.5, 32, 330.5, 180, 0);
    public static final Location BRACKETS_SPAWN = new Location(MAIN_WORLD, -13.5, 30, 325.5, 180, 0);
    public static final Location BRACKETS_SPAWN_A = new Location(MAIN_WORLD, -13.5, 20, 352.5, 180, 0);
    public static final Location BRACKETS_SPAWN_B = new Location(MAIN_WORLD, -13.5, 20, 298.5, 0, 0);
    public static final Location ARENA_SPAWN = new Location(MAIN_WORLD, 651.5, 31, -138.5, 0, 0);
    public static final Location ARENA_SPAWN_A = new Location(MAIN_WORLD, 651.5, 22, -111.5, 180, 0);
    public static final Location ARENA_SPAWN_B = new Location(MAIN_WORLD, 651.5, 22, -165.5, 0, 0);
    public static final Location OITC_SPAWN = new Location(MAIN_WORLD, 0, 0 ,0, 0, 0);
    public static final Location TDM_SPAWN = new Location(MAIN_WORLD, 0, 0 ,0, 0, 0);
}
