package us.lemin.kitpvp.player;

import lombok.Getter;
import lombok.Setter;

public class PlayerStatistics {
    @Getter
    @Setter
    private int kills, deaths, killStreak, highestKillStreak, credits, eventWins, duelWins, duelLosses;

    public double getKillDeathRatio() {
        return kills == 0 ? 0.0 : deaths == 0 ? kills : Math.round(((double) kills / deaths) * 10.0) / 10.0;
    }

    public boolean handleDeath() {
        boolean newRecordStreak = killStreak > highestKillStreak;

        if (newRecordStreak) {
            highestKillStreak = killStreak;
        }

        killStreak = 0;
        deaths++;

        return newRecordStreak;
    }

    public void handleKill() {
        kills++;
        killStreak++;
    }
}
