package us.lemin.kitpvp.team;

public enum TeamRank {

    LEADER("Leader"),
    OFFICER("Officer"),
    MEMBER("Member");

    private final String name;

    TeamRank(String name) {
        this.name = name;
    }

    public static TeamRank getByName(String name) {
        for (TeamRank rank : values()) {
            if (rank.name().equalsIgnoreCase(name)) {
                return rank;
            }
        }

        return null;
    }
}
