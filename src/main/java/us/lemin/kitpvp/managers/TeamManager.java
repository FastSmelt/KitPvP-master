package us.lemin.kitpvp.managers;

/*
public class TeamManager {

    public class CachedTeam {
        @Getter
        @Setter
        private long timeCached;
        private Team team;

        CachedTeam(long timeCached, Team team) {
            this.timeCached = timeCached;
            this.team = team;
        }
    }

    @Getter
    private final Map<String, Team> teams = new HashMap<>();
    @Getter
    private final LoadingCache<String, Team> teamLoadingCache;

    private final CorePlugin corePlugin;
    private final KitPvPPlugin plugin;

    public TeamManager(CorePlugin corePlugin, KitPvPPlugin plugin) {
        this.corePlugin = corePlugin;
        this.plugin = plugin;
        CacheLoader<String, Team> cacheLoader = new CacheLoader<String, Team>() {
            public Team load(String s) throws Exception {
                return teamLoadingCache.get(s);
            }
        };
        teamLoadingCache = CacheBuilder.newBuilder()
                .expireAfterAccess(10, TimeUnit.MINUTES).build(cacheLoader);
    }

    public Team getTeamByPlayer(Player player) {
        KitProfile profile = plugin.getPlayerManager().getProfile(player);
        if (profile.getLastKnownTeam() == null) return null;
        Team team;
        if (teams.containsKey(profile.getLastKnownTeam().toLowerCase())) {
            team = teams.get(profile.getLastKnownTeam().toLowerCase());
            return team.getMembers().containsKey(player.getUniqueId()) ? team :  null;
        } else if (teamLoadingCache.asMap().containsKey(profile.getLastKnownTeam())) {
            team = teamLoadingCache.asMap().get(profile.getLastKnownTeam());
            return team.getMembers().containsKey(player.getUniqueId()) ? team :  null;
        }
        return null;
    }

    public void unregisterTeam(String teamName) {
        String teamId = teamName.toLowerCase();
        if (teams.get(teamId) == null) {
            return;
        }
        teams.get(teamId).save(false);
        teamLoadingCache.put(teamId, teams.get(teamId));
        teams.remove(teamId);
    }

    public Team registerTeam(String teamName) {
        String teamId = teamName.toLowerCase();
        if (teamLoadingCache.asMap().get(teamId) != null) {
            teams.put(teamId, teamLoadingCache.asMap().get(teamId));
            teamLoadingCache.asMap().remove(teamId);
        }
        return teams.containsKey(teamId) ? teams.get(teamId) : teams.put(teamId, new Team(teamName));
    }

    public void createTeam(Player player, String teamName) {
        if (doesTeamExist(teamName)) {
            player.sendMessage(CC.RED + "That team name is already taken.");
            return;
        }

        KitProfile profile = plugin.getPlayerManager().getProfile(player);

        Team team = new Team(teamName);
        team.addMember(player.getUniqueId(), TeamRank.LEADER);
        teams.put(teamName.toLowerCase(), team);

        profile.setLastKnownTeam(teamName);
        player.sendMessage(CC.PRIMARY + "You have succesfully created team " + CC.SECONDARY + teamName + CC.PRIMARY + ".");
    }


    public void revokeInvite(Player player, String invitedName, Team team) {

    }

    public Team getTeamByName(String teamName) {
        return teams.get(teamName.toLowerCase());
    }

    public boolean doesTeamExist(String teamName) {
        Document document = corePlugin.getMongoStorage().getDocument("betateams", teamName.toLowerCase());
        System.out.println(teamName + " exists as: " + document);
        return document != null;
    }

    public void saveTeams(boolean async) {
        teams.values().forEach(team -> team.save(async));
        teamLoadingCache.asMap().values().forEach(cachedTeam -> cachedTeam.save(async));
    }

    public String getMembersSortedByRank(Team team) {
        HashMap<UUID, TeamRank> sortedMap = team.getMembers().entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.naturalOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        System.out.println("sortedmap entries " + sortedMap.size());
        List<String> sortedList = new ArrayList<>();
        sortedMap.forEach(((uuid, teamRank) -> System.out.println(uuid + " " + teamRank)));
        sortedMap.forEach((uuid, teamRank) -> ProfileUtil.lookupProfileAsync(plugin, uuid, ((mojangProfile, b) -> {
            if (b) {
                sortedList.add(mojangProfile.getName());
            } else {
                System.out.println(b);
            }
        })));
        System.out.println("sortedlist entries " + sortedList.size());
        StringBuilder ass = new StringBuilder();
        sortedList.forEach(string -> ass.append(string + ""));
        return ass.toString();
    }
}*/
