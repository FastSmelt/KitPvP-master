package us.lemin.kitpvp.kit;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import us.lemin.core.utils.item.ItemBuilder;
import us.lemin.core.utils.message.CC;
import us.lemin.kitpvp.KitPvPPlugin;

import java.util.List;

public abstract class ArenaKit {
    protected final KitPvPPlugin plugin;
    @Getter
    private final String name;
    @Getter
    private final ItemStack icon;
    private final KitContents contents;
    private final List<PotionEffect> effects;

    public ArenaKit(KitPvPPlugin plugin, String name, ItemStack icon, String... description) {
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
    }

    public ArenaKit(KitPvPPlugin plugin, String name, Material icon, String... description) {
        this(plugin, name, new ItemStack(icon), description);
    }

    protected abstract void onEquip(Player player);

    protected abstract List<PotionEffect> effects();

    protected abstract KitContents.Builder contentsBuilder();
}
