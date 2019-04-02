package us.lemin.kitpvp.scoreboard;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import us.lemin.core.api.scoreboardapi.ScoreboardUpdateEvent;
import us.lemin.core.api.scoreboardapi.api.ScoreboardAdapter;
import us.lemin.core.utils.message.CC;
import us.lemin.core.utils.player.PlayerUtil;
import us.lemin.core.utils.timer.Timer;
import us.lemin.kitpvp.KitPvPPlugin;
import us.lemin.kitpvp.arena.Match;
import us.lemin.kitpvp.events.Event;
import us.lemin.kitpvp.events.EventStage;
import us.lemin.kitpvp.player.KitProfile;
import us.lemin.kitpvp.player.PlayerStatistics;

@RequiredArgsConstructor
public class KitPvPAdapter implements ScoreboardAdapter {
    
    private final KitPvPPlugin plugin;
    
    @Override
    public void onUpdate(ScoreboardUpdateEvent event) {
        Player player = event.getPlayer();
        KitProfile profile = plugin.getPlayerManager().getProfile(player);
        if (!profile.isScoreboardEnabled()) {
            return;
        }
        
        event.setTitle(CC.PRIMARY + "Okolie " + CC.GRAY + CC.SPLITTER + CC.SECONDARY + " KitPvP");
        event.setSeparator(CC.BOARD_SEPARATOR);

        plugin.getServer().getOnlinePlayers().forEach(players -> {
            KitProfile loopProfile = plugin.getPlayerManager().getProfile(players);
            Objective objective = player.getScoreboard().getObjective("objectiveBelow");
            Score score = objective.getScore(players);
            switch (profile.getState()) {
                case SPAWN:
                case FFA:
                    objective.setDisplayName("Kills");
                    score.setScore(loopProfile.getStatistics().getKills());
                    break;
                case DUEL:
                case ARENA:
                    objective.setDisplayName("Wins");
                    score.setScore(loopProfile.getStatistics().getDuelWins());
                    break;
                case EVENT:
                    objective.setDisplayName("Wins");
                    score.setScore(loopProfile.getStatistics().getEventWins());
                    break;
                default:
                    objective.setDisplayName("Kills");
                    score.setScore(loopProfile.getStatistics().getKills());
                    break;
            }
        });


        switch (profile.getState()) {
            case SPAWN:

                PlayerStatistics spawnStats = profile.getStatistics();
                event.addLine(CC.PRIMARY + "Kills: " + CC.SECONDARY + spawnStats.getKills());
                event.addLine(CC.PRIMARY + "Deaths: " + CC.SECONDARY + spawnStats.getDeaths());
                event.addLine(CC.PRIMARY + "Kill Streak: " + CC.SECONDARY + spawnStats.getKillStreak());
                event.addLine(CC.PRIMARY + "KDR: " + CC.SECONDARY + spawnStats.getKillDeathRatio());
                event.addLine(CC.PRIMARY + "Credits: " + CC.SECONDARY + spawnStats.getCredits());

                break;
            case FFA:
                PlayerStatistics stats = profile.getStatistics();

                event.addLine(CC.PRIMARY + "Kills: " + CC.SECONDARY + stats.getKills());
                event.addLine(CC.PRIMARY + "Deaths: " + CC.SECONDARY + stats.getDeaths());
                event.addLine(" ");
                event.addLine(CC.PRIMARY + "Kill Streak: " + CC.SECONDARY + stats.getKillStreak());
                event.addLine(CC.PRIMARY + "Active Kit: " + CC.SECONDARY + profile.getCurrentKit().getName());

                if (profile.getCurrentKit() != null) {
                    if (profile.getCurrentKit().getCooldownTimer(player, profile.getCurrentKit().getName()) != null) {
                        Timer cooldown = profile.getCurrentKit().getCooldownTimer(player, profile.getCurrentKit().getName());
                        event.addLine(" ");
                        event.addLine(CC.GRAY + " - " + CC.PRIMARY + "Cooldown: " + CC.SECONDARY + cooldown.formattedClock());
                    }
                }

                if (profile.getPearlTimer().isActive(false)) {
                    event.addLine(" ");
                    Timer pearlCooldown = profile.getPearlTimer();
                    event.addLine(" ");
                    event.addLine(CC.GRAY + " - " + CC.PRIMARY + "Pearl: " + CC.SECONDARY + pearlCooldown.formattedClock());
                }
                
                break;
            case EVENT:
                Event activeEvent = profile.getActiveEvent();

                if (activeEvent == null) {
                    return;
                }

                switch (activeEvent.getType()) {
                    case SUMO:
                    case BRACKETS:
                        if (activeEvent.getCurrentStage() == EventStage.FIGHTING) {
                            Player first = plugin.getServer().getPlayer(activeEvent.remainingFighterIds().get(0));

                            if (first == null) {
                                return;
                            }

                            Player second = plugin.getServer().getPlayer(activeEvent.remainingFighterIds().get(1));

                            if (second == null) {
                                return;
                            }
                            event.addLine(CC.PRIMARY + "Round: " + CC.ACCENT + activeEvent.getRound());
                            event.addLine(CC.PRIMARY + first.getName() + CC.SECONDARY + " (" + PlayerUtil.getPing(first) + " ms)");
                            event.addLine(CC.ACCENT + "vs.");
                            event.addLine(CC.PRIMARY + second.getName() + CC.SECONDARY + " (" + PlayerUtil.getPing(second) + " ms)");
                        } else if (activeEvent.getCurrentStage() == EventStage.INTERMISSION) {
                            event.addLine(CC.PRIMARY + "Remaining: " + CC.SECONDARY + (activeEvent.remainingPlayerIds().size() + activeEvent.remainingFighterIds().size() + activeEvent.advancingPlayerIds().size()));
                        } else if (activeEvent.getCurrentStage() == EventStage.WAITING_FOR_PLAYERS) {
                            event.addLine(CC.PRIMARY + "Playing: " + CC.SECONDARY + activeEvent.remainingPlayerIds().size());
                        }
                        break;
                }
                break;
            case ARENA:
                PlayerStatistics arenaStats = profile.getStatistics();
                event.addLine(CC.PRIMARY + "Wins: " + CC.SECONDARY + arenaStats.getDuelWins());
                event.addLine(CC.PRIMARY + "Losses: " + CC.SECONDARY + arenaStats.getDuelLosses());
                break;
            case DUEL:
                Match match = profile.getActiveMatch();
                Player opponent;
                if (profile.getActiveMatch().getFirstPlayerId() != event.getPlayer().getUniqueId()) {
                    opponent = plugin.getServer().getPlayer(profile.getActiveMatch().getFirstPlayerId());
                } else {
                    opponent = plugin.getServer().getPlayer(profile.getActiveMatch().getSecondPlayerId());
                }
                event.addLine(CC.PRIMARY + "Opponent: " + CC.SECONDARY + opponent.getName());
                event.addLine(CC.PRIMARY + "Duration: " + CC.SECONDARY + match.formatTimeMillisToClock());
                break;
        }
    }

    @Override
    public int updateRate() {
        return 5;
    }
}
