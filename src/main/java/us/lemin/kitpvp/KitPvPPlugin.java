package us.lemin.kitpvp;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import us.lemin.core.api.inventoryapi.InventoryManager;
import us.lemin.core.api.scoreboardapi.ScoreboardApi;
import us.lemin.core.storage.flatfile.Config;
import us.lemin.kitpvp.commands.*;
import us.lemin.kitpvp.commands.admin.EditRegionCommand;
import us.lemin.kitpvp.commands.admin.SetModeCommand;
import us.lemin.kitpvp.commands.admin.SetSpawnCommand;
import us.lemin.kitpvp.commands.classic.*;
import us.lemin.kitpvp.commands.events.EventCommand;
import us.lemin.kitpvp.commands.staff.StaffModeCommand;
import us.lemin.kitpvp.commands.toggle.ToggleScoreboardCommand;
import us.lemin.kitpvp.handlers.KillstreakHandler;
import us.lemin.kitpvp.inventory.*;
import us.lemin.kitpvp.listeners.*;
import us.lemin.kitpvp.managers.*;
import us.lemin.kitpvp.scoreboard.KitPvPAdapter;
import us.lemin.kitpvp.server.ServerMode;
import us.lemin.kitpvp.util.structure.Cuboid;

import java.lang.reflect.Field;

@Getter
public class KitPvPPlugin extends JavaPlugin {

    private static KitPvPPlugin instance;
    @Setter
    public ServerMode serverMode = ServerMode.REGULAR;
    private Config locationConfig;
    @Setter
    private Location spawnLocation;
    @Setter
    private Cuboid spawnCuboid;
    @Setter
    private Cuboid kothCuboid;
    private PlayerManager playerManager;
    private KitManager kitManager;
    private InventoryManager inventoryManager;
    private RegionManager regionManager;
    private EventManager eventManager;
    private ArenaManager arenaManager;
    private ScoreboardApi scoreboardApi;
    private KillstreakHandler killstreakHandler;

    public static KitPvPPlugin getInstance() {
        return instance;
    }

    private void registerSerializableClass(Class<?> clazz) {
        if (ConfigurationSerializable.class.isAssignableFrom(clazz)) {
            Class<? extends ConfigurationSerializable> serializable = clazz.asSubclass(ConfigurationSerializable.class);
            ConfigurationSerialization.registerClass(serializable);
        }
    }

    @Override
    public void onEnable() {
        instance = this;
        registerSerializableClass(Cuboid.class);

        locationConfig = new Config(this, "locations");

        World mainWorld = getServer().getWorlds().get(0);

        locationConfig.addDefault("spawn", mainWorld.getSpawnLocation());
        locationConfig.addDefault("spawn-cuboid", new Cuboid(mainWorld.getSpawnLocation()));
        locationConfig.copyDefaults();

        spawnLocation = locationConfig.getLocation("spawn");
        spawnCuboid = (Cuboid) locationConfig.get("spawn-cuboid");

        playerManager = new PlayerManager(this);
        kitManager = new KitManager(this);
        arenaManager = new ArenaManager(this);

        inventoryManager = new InventoryManager(this);
        inventoryManager.registerPlayerWrapper(new KitPlayerWrapper(this));
        inventoryManager.registerPlayerWrapper(new KitSelectorPlayerWrapper(this));
        inventoryManager.registerPlayerWrapper(new KitShopPlayerWrapper(this));
        inventoryManager.registerWrapper(new ShopWrapper(this));
        inventoryManager.registerPlayerWrapper(new SettingsPlayerWrapper(this));

        regionManager = new RegionManager();
        eventManager = new EventManager(this);

        scoreboardApi = new ScoreboardApi(this, new KitPvPAdapter(this), true);

        killstreakHandler = new KillstreakHandler(this);

        registerCommands(
                new StatisticsCommand(this),
                new KitCommand(this),
                new KitShopCommand(this),
                new ClearKitCommand(this),
                new HelpCommand(),
                new SetSpawnCommand(this),
                new EditRegionCommand(this),
                new SpawnCommand(this),
                new EventCommand(this),
                new ShopCommand(this),
                new RepairCommand(this),
                new RefillCommand(this),
                new AntiControlCommand(this),
                new SoupCommand(this),
                new SharpCommand(this),
                new ProtCommand(this),
                new SetModeCommand(this),
                new PowerCommand(this),
                new PunchCommand(this),
                new UnbreakingCommand(this),
                new ArcherCommand(this),
                new KnockbackCommand(this),
                new StrengthCommand(this),
                new SpeedCommand(this),
                new LeatherCommand(this),
                new GoldCommand(this),
                new DiamondCommand(this),
                new PoisonCommand(this),
                new ToggleScoreboardCommand(this),
                new SettingsCommand(this),
                new StaffModeCommand(this)
        );
        registerListeners(
                new PlayerListener(this, killstreakHandler),
                new WorldListener(),
                new InventoryListener(),
                new EntityListener(this),
                new RegionListener(this),
                new ArenaListener(this, arenaManager),
                new StaffModeListener(this)
        );

        disableGameRules(mainWorld,
                "doDaylightCycle",
                "doFireTick",
                "doMobSpawning",
                "showDeathMessages",
                "mobGriefing"
        );
    }

    @Override
    public void onDisable() {
        playerManager.saveAllProfiles();
        locationConfig.save();

        World mainWorld = getServer().getWorlds().get(0);

        for (Entity entity : mainWorld.getEntities()) {
            if (entity instanceof Player) {
                continue;
            }

            entity.remove();
        }
    }

    private void registerCommands(Command... commands) {
        try {
            Field commandMapField = getServer().getClass().getDeclaredField("commandMap");
            final boolean accessible = commandMapField.isAccessible();

            commandMapField.setAccessible(true);

            CommandMap commandMap = (CommandMap) commandMapField.get(getServer());

            for (Command command : commands) {
                commandMap.register(command.getName(), getName(), command);
            }

            commandMapField.setAccessible(accessible);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("An error occurred while registering commands", e);
        }
    }

    private void registerListeners(Listener... listeners) {
        PluginManager pluginManager = getServer().getPluginManager();

        for (Listener listener : listeners) {
            pluginManager.registerEvents(listener, this);
        }
    }

    private void disableGameRules(World world, String... gameRules) {
        for (String gameRule : gameRules) {
            world.setGameRuleValue(gameRule, "false");
        }
    }
}
