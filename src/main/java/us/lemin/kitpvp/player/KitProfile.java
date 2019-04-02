package us.lemin.kitpvp.player;

import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import us.lemin.core.CorePlugin;
import us.lemin.core.player.CoreProfile;
import us.lemin.core.player.PlayerProfile;
import us.lemin.core.storage.database.MongoRequest;
import us.lemin.core.utils.timer.Timer;
import us.lemin.core.utils.timer.impl.DoubleTimer;
import us.lemin.core.utils.timer.impl.IntegerTimer;
import us.lemin.kitpvp.KitPvPPlugin;
import us.lemin.kitpvp.arena.Match;
import us.lemin.kitpvp.events.Event;
import us.lemin.kitpvp.kit.Kit;
import us.lemin.kitpvp.tasks.VulnerableTask;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
public class KitProfile extends PlayerProfile {
    private final UUID id;
    private final String name;
    private final PlayerDamageData damageData = new PlayerDamageData();
    private final PlayerStatistics statistics = new PlayerStatistics();
    private final Timer pearlTimer = new DoubleTimer(16);
    private final Timer eventHostTimer = new IntegerTimer(TimeUnit.MINUTES, 5);
    private final int worth;
    private PlayerState state = PlayerState.SPAWN;
    private List<String> purchasedKits = new ArrayList<>(Arrays.asList("PvP", "Archer"));
    private Kit currentKit;
    private Kit lastKit;
    private Event activeEvent;
    private boolean fallDamageEnabled = true;
    private boolean awaitingTeleport;
    private boolean controllable = true;
    private boolean frozen = false;
    private Match activeMatch;
    private Set<UUID> requestedDuels = new HashSet<>();
    private UUID lastAttacked;
    private String lastKnownTeam;
    private VulnerableTask vulnerableTask;
    private boolean diedWhileVulnerable = false;
    private boolean scoreboardEnabled = true;

    public KitProfile(String name, UUID id) {
        super(id, "kitpvp");
        this.name = name;
        this.id = id;
        CoreProfile coreProfile = CorePlugin.getInstance().getProfileManager().getProfile(id);
        this.worth = coreProfile.hasDonor() ? 15 : 10;
        load();
    }

    @Override
    public void deserialize(Document document) {
        this.purchasedKits = (List<String>) document.get("purchased_kits");
        String lastKitName = document.getString("last_kit_name");
        this.lastKnownTeam = document.getString("last_team");
        this.scoreboardEnabled = document.getBoolean("scoreboard_enabled", true);
        if (lastKitName != null) {
            lastKit = KitPvPPlugin.getInstance().getKitManager().getFfaKitByName(lastKitName);
        }
        statistics.setDeaths(document.getInteger("deaths", 0));
        statistics.setEventWins(document.getInteger("event_wins", 0));
        statistics.setHighestKillStreak(document.getInteger("highest_kill_streak", 0));
        statistics.setKills(document.getInteger("kills", 0));
        statistics.setKillStreak(document.getInteger("kill_streak", 0));
        statistics.setCredits(document.getInteger("credits", 0));
        statistics.setDuelWins(document.getInteger("duel_wins", 0));
        statistics.setDuelLosses(document.getInteger("duels_losses", 0));
    }

    @Override
    public MongoRequest serialize() {
        MongoRequest request = MongoRequest.newRequest("kitpvp", id)
                .put("deaths", statistics.getDeaths())
                .put("event_wins", statistics.getEventWins())
                .put("highest_kill_streak", statistics.getHighestKillStreak())
                .put("kills", statistics.getKills())
                .put("kill_streak", statistics.getKillStreak())
                .put("credits", statistics.getCredits())
                .put("duel_wins", statistics.getDuelWins())
                .put("duel_losses", statistics.getDuelLosses())
                .put("purchased_kits", purchasedKits)
                .put("last_team", lastKnownTeam)
                .put("scoreboard_enabled", scoreboardEnabled)
                .put("name", name);
        if (lastKit != null) {
            request.put("last_kit_name", lastKit.getName());
        }
        return request;
    }

    public void setKit(Kit kit) {
        this.currentKit = this.lastKit = kit;
    }

    public void addPurchasedKit(Kit kit) {
        this.purchasedKits.add(kit.getName());
    }
}
