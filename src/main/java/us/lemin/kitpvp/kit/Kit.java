package us.lemin.kitpvp.kit;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import us.lemin.core.CorePlugin;
import us.lemin.core.player.CoreProfile;
import us.lemin.core.utils.item.ItemBuilder;
import us.lemin.core.utils.message.CC;
import us.lemin.core.utils.timer.Timer;
import us.lemin.core.utils.timer.impl.IntegerTimer;
import us.lemin.kitpvp.KitPvPPlugin;
import us.lemin.kitpvp.player.KitProfile;
import us.lemin.kitpvp.player.PlayerState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public abstract class Kit implements Listener {
    protected final KitPvPPlugin plugin;
    @Getter
    private final String name;
    @Getter
    private final ItemStack icon;
    private final KitContents contents;
    private final List<PotionEffect> effects;
    private final Map<String, Integer> cooldownTimers = new HashMap<>();
    private final Map<UUID, Map<String, Timer>> playerTimersById = new HashMap<>();

    public Kit(KitPvPPlugin plugin, String name, ItemStack icon, String... description) {
        this.plugin = plugin;
        this.name = name;

        ItemBuilder builder = ItemBuilder.from(icon);
        String[] coloredDescription = new String[description.length];

        for (int i = 0; i < description.length; i++) {
            coloredDescription[i] = CC.PRIMARY + description[i];
        }

        this.icon = builder.name(CC.SECONDARY + name).lore(coloredDescription).build();
        this.contents = contentsBuilder().build();
        this.effects = effects();

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public Kit(KitPvPPlugin plugin, String name, Material icon, String... description) {
        this(plugin, name, new ItemStack(icon), description);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        playerTimersById.remove(event.getPlayer().getUniqueId());
    }

    protected void registerCooldownTimer(String id, int seconds) {
        cooldownTimers.put(id, seconds);
    }

    protected boolean isOnCooldown(Player player, String id) {
        if (!playerTimersById.containsKey(player.getUniqueId())) {
            playerTimersById.put(player.getUniqueId(), new HashMap<>());
        }

        Map<String, Timer> timers = playerTimersById.get(player.getUniqueId());

        if (!timers.containsKey(id)) {
            timers.put(id, new IntegerTimer(TimeUnit.SECONDS, cooldownTimers.get(id)));
        }

        Timer timer = timers.get(id);

        boolean onCooldown = timer.isActive();

        if (onCooldown) {
            player.sendMessage(CC.RED + "You can't do this for another " + timer.formattedExpiration() + ".");
        }

        return onCooldown;
    }

    public void repairKit(Player player) {
        player.getInventory().setArmorContents(null);
        player.getInventory().setArmorContents(contents.getArmor());
    }

    public void purchaseKit(Player player) {
        KitProfile profile = plugin.getPlayerManager().getProfile(player);

        if (ownsKit(player)) {
            player.sendMessage(CC.PRIMARY + "You already own " + CC.ACCENT + this.getName());
            return;
        }

        if (profile.getStatistics().getCredits() >= 500) {
            profile.addPurchasedKit(this);
            player.sendMessage(CC.PRIMARY + "You successfully purchased " + CC.ACCENT + this.getName() + CC.PRIMARY
                    + " for " + CC.ACCENT + "500 " + CC.PRIMARY + "credits.");
            profile.getStatistics().setCredits(profile.getStatistics().getCredits() - 250);
        } else {
            player.sendMessage(CC.RED + "You can't afford that kit!");
        }
    }

    private boolean ownsKit(Player player) {
        KitProfile profile = plugin.getPlayerManager().getProfile(player);
        CoreProfile coreProfile = CorePlugin.getInstance().getProfileManager().getProfile(player.getUniqueId());
        return coreProfile.hasDonor() ? coreProfile.hasDonor() : profile.getPurchasedKits().contains(this.name);
    }

    protected boolean isInvalidKit(Player player) {
        KitProfile profile = plugin.getPlayerManager().getProfile(player);
        return profile.getCurrentKit() != this;
    }

    public void apply(Player player) {
        KitProfile profile = plugin.getPlayerManager().getProfile(player);

        if (profile.getState() != PlayerState.SPAWN) {
            player.sendMessage(CC.RED + "You can't choose a kit right now!");
            return;
        }

        if (!ownsKit(player)) {
            player.sendMessage(CC.RED + "You need to purchase that kit before you can use it!");
            return;
        }

        profile.setKit(this);
        contents.apply(player);

        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }

        if (effects != null) {
            player.addPotionEffects(effects);
        }

        onEquip(player);
        player.sendMessage(CC.PRIMARY + "You have equipped the " + CC.SECONDARY + name + CC.PRIMARY + " kit.");
    }

    protected abstract void onEquip(Player player);

    protected abstract List<PotionEffect> effects();

    protected abstract KitContents.Builder contentsBuilder();

    public Timer getCooldownTimer(Player player, String kitName) {
        Map<String, Timer> timers = playerTimersById.get(player.getUniqueId());

        if (timers == null) {
            return null;
        }

        String cooldownId = kitName.toLowerCase();

        if (!timers.containsKey(cooldownId)) {
            return null;
        }

        Timer timer = timers.get(cooldownId);

        return timer.isActive(false) ? timer : null;
    }
}
