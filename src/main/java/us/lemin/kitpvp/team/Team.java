package us.lemin.kitpvp.team;

import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import us.lemin.core.CorePlugin;
import us.lemin.core.storage.database.MongoRequest;

import java.util.*;
import java.util.stream.Stream;

public class Team {
    @Getter
    @Setter
    private String teamId;
    @Setter
    @Getter
    private String name;
    @Setter
    @Getter
    private Location headquarters;
    @Setter
    @Getter
    private Location rally;
    @Getter
    @Setter
    private boolean friendlyFire = false;

    @Getter
    private HashMap<UUID, TeamRank> members = new HashMap<>();
    @Getter
    private List<UUID> invitedIds = new ArrayList<UUID>() {
    };
    @Getter
    @Setter
    private String password;
    @Getter
    @Setter
    private List<String> membersSorted;

    @SuppressWarnings("unchecked")
    public Team(String name) {
        this.name = name;
        this.teamId = name.toLowerCase();
        CorePlugin.getInstance().getMongoStorage().getOrCreateDocument("betateams", teamId, (document, exists) -> {
            if (exists) {
                this.name = document.getString("name");
                this.headquarters = (Location) document.get("headquarters");
                this.rally = (Location) document.get("rally");
                this.friendlyFire = document.getBoolean("friendly_fire", friendlyFire);
                List<UUID> invitedIds = (ArrayList<UUID>) document.get("invited_ids");
                this.invitedIds.addAll(invitedIds);
                Document membersDoc = ((Document) document.get("members"));
                membersDoc.forEach((key, value) -> this.members.put(UUID.fromString(key), TeamRank.valueOf(value.toString())));
            }
            save(false);
        });
    }

    public void save(boolean async) {
        Document membersDoc = new Document();
        members.forEach(((uuid, teamRank) -> membersDoc.append(uuid.toString(), teamRank.toString())));
        MongoRequest request = MongoRequest.newRequest("betateams", teamId)
                .put("name", name)
                .put("headquarters", headquarters)
                .put("rally", rally)
                .put("friendly_fire", friendlyFire)
                .put("members", membersDoc)
                .put("invited_ids", invitedIds);
        if (async) {
            CorePlugin.getInstance().getServer().getScheduler().runTaskAsynchronously(CorePlugin.getInstance(), request::run);
        } else {
            request.run();
        }
    }

    public void inviteId(UUID uuid) {
        invitedIds.add(uuid);
    }

    public void revokeInvitedId(UUID uuid) {
        invitedIds.remove(uuid);
    }

    public void addMember(UUID uuid, TeamRank teamRank) {
        members.put(uuid, teamRank);
    }

    public void removeMember(UUID uuid) {
        members.remove(uuid);
    }

    public TeamRank getRankById(UUID uuid) {
        return members.get(uuid);
    }

    public boolean isRankHigher(TeamRank kicked, TeamRank kicker) {
        return kicked.ordinal() < kicker.ordinal();
    }

    public boolean hasRank(TeamRank requiredRank, UUID uuid) {
        return getRankById(uuid).ordinal() <= requiredRank.ordinal();
    }

    public Stream<Player> getOnlinePlayers() {
        return members.keySet().stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull);
    }

    public void messageTeam(String message) {
        getOnlinePlayers().forEach(p -> p.sendMessage(message));
    }


}
