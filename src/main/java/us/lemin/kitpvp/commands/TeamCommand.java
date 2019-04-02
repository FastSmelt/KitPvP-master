package us.lemin.kitpvp.commands;


/*public class TeamCommand extends PlayerCommand {

    private final KitPvPPlugin plugin;

    public TeamCommand(KitPvPPlugin plugin) {
        super("team");
        this.plugin = plugin;
        setAliases("clan", "f", "faction");
        setUsage(
                CC.SECONDARY + "/team create <name>" + CC.GRAY + " - " + CC.PRIMARY + "host an event of the specified type",
                CC.SECONDARY + "/team invite <player>" + CC.GRAY + " - " + CC.PRIMARY + "invite a player to your team",
                CC.SECONDARY + "/team kick <player>" + CC.GRAY + " - " + CC.PRIMARY + "kicks a player from your current team",
                CC.SECONDARY + "/team join <team> <password>" + CC.GRAY + " - " + CC.PRIMARY + "join a team after being invited or input a password",
                CC.SECONDARY + "/team leave" + CC.GRAY + " - " + CC.PRIMARY + "leave your current team",
                CC.SECONDARY + "/team info <team:player>" + CC.GRAY + " - " + CC.PRIMARY + "shows information about a team or a player's team",
                CC.SECONDARY + "/team chat" + CC.GRAY + " - " + CC.PRIMARY + "changes your chat mode to team",
                CC.SECONDARY + "/team hq" + CC.GRAY + " - " + CC.PRIMARY + "teleports you to your team's headquarters",
                CC.SECONDARY + "/team sethq" + CC.GRAY + " - " + CC.PRIMARY + "sets your team's headquarters to your current location",
                CC.SECONDARY + "/team rally" + CC.GRAY + " - " + CC.PRIMARY + "teleports you to your team's rally point",
                CC.SECONDARY + "/team setrally" + CC.GRAY + " - " + CC.PRIMARY + "sets your team's rally point to your current location",
                CC.SECONDARY + "/team setpassword" + CC.GRAY + " - " + CC.PRIMARY + "sets your team's password",
                CC.SECONDARY + "/team removepassword" + CC.GRAY + " - " + CC.PRIMARY + "removes your team's password",
                CC.SECONDARY + "/team ff" + CC.GRAY + " - " + CC.PRIMARY + "toggles friendly fire on or off"
        );
    }


    @Override
    public void execute(Player player, String[] args) {
        KitProfile profile = plugin.getPlayerManager().getProfile(player);
        TeamManager teamManager = plugin.getTeamManager();
        Team team = null;
        if (profile.getLastKnownTeam() != null) {
            team = teamManager.getTeamByName(profile.getLastKnownTeam());
        }

        if (args.length < 1) {
            player.sendMessage(usageMessage);
            return;
        }

        switch (args[0]) {
            case "create":
                if (args[1] == null) {
                    player.sendMessage(usageMessage);
                    return;
                }
                if (team != null) {
                    player.sendMessage(CC.RED + "You're currently already in a team.");
                    return;
                }
                plugin.getTeamManager().createTeam(player, args[1]);
                break;
            case "invite":
                if (args[1] == null) {
                    player.sendMessage(usageMessage);
                    return;
                }

                if (team == null) {
                    player.sendMessage(CC.RED + "You're not currently in a team.");
                    return;
                }

                if (plugin.getServer().getPlayer(args[1]) == null) {
                    player.sendMessage(CC.RED + "That player could not be found.");
                    return;
                }

                Player invitedPlayer = plugin.getServer().getPlayer(args[1]);

                if (team.getMembers().containsKey(invitedPlayer.getUniqueId())) {
                    player.sendMessage(CC.RED + "That player is already a member of the team, use /team kick <player> to remove them.");
                }

                if (team.getInvitedIds().contains(invitedPlayer.getUniqueId())) {
                    player.sendMessage(CC.RED + "That player is already invited, use /team revoke <player> to remove the invite.");
                    return;
                }

                team.inviteId(invitedPlayer.getUniqueId());
                team.messageTeam(CC.SECONDARY + player.getName() + CC.PRIMARY + " has invited " + CC.SECONDARY + invitedPlayer.getName() + CC.PRIMARY + " to join your team!");
                player.sendMessage(CC.PRIMARY + "You have invited " + CC.SECONDARY + invitedPlayer.getName() + CC.PRIMARY + " to join your team!");
                invitedPlayer.sendMessage(CC.PRIMARY + "You have been invited to join team " + CC.SECONDARY +
                        profile.getLastKnownTeam() + CC.PRIMARY + " by " + CC.SECONDARY + player.getName() + CC.PRIMARY + ".");
                break;
            case "revoke":
                if (args[1] == null) {
                    player.sendMessage(usageMessage);
                    return;
                }

                if (team == null) {
                    player.sendMessage(CC.RED + "You're not currently in a team.");
                    return;
                }

                if (!team.hasRank(TeamRank.OFFICER, player.getUniqueId())) {
                    player.sendMessage(CC.RED + "You must be an officer to revoke an invite.");
                    return;
                }
                String name = args[1];
                Player revokedPlayer = plugin.getServer().getPlayer(name);
                final Team revokingTeam = team;
                if (revokedPlayer == null) {
                    ProfileUtil.lookupProfileAsync(plugin, name, (mojangProfile, b) -> {
                        if (b) {
                            if (!revokingTeam.getInvitedIds().contains(mojangProfile.getId())) {
                                player.sendMessage(CC.RED + "That player is not currently invited, use /team invite <player> to invite them.");
                                return;
                            }
                            revokingTeam.revokeInvitedId(mojangProfile.getId());
                            player.sendMessage(CC.PRIMARY + "You have successfully revoked " + CC.SECONDARY + mojangProfile.getName() + CC.PRIMARY + "'s invite.");
                        } else {
                            player.sendMessage(CC.RED + "That player could not be found.");
                        }
                    });
                } else {
                    if (!revokingTeam.getInvitedIds().contains(revokedPlayer.getUniqueId())) {
                        player.sendMessage(CC.RED + "That player is not currently invited, use /team invite <player> to invite them.");
                        return;
                    }
                    revokingTeam.revokeInvitedId(revokedPlayer.getUniqueId());
                    revokingTeam.messageTeam(CC.SECONDARY + player.getName() + CC.PRIMARY + " has revoked " + CC.SECONDARY + revokedPlayer.getName()
                            + CC.PRIMARY + "'s invitation to join the team.");
                    player.sendMessage(CC.PRIMARY + "You have successfully revoked " + CC.SECONDARY + revokedPlayer.getName() + CC.PRIMARY + "'s invitation to join the team.");
                    return;
                }
                break;
            case "kick":
                if (args[1] == null) {
                    player.sendMessage(usageMessage);
                    return;
                }

                if (team == null) {
                    player.sendMessage(CC.RED + "You're not currently in a team.");
                    return;
                }

                if (!team.hasRank(TeamRank.OFFICER, player.getUniqueId())) {
                    player.sendMessage(CC.RED + "You must be an officer to kick a player from the team.");
                    return;
                }

                String kickedName = args[1];
                Player kickedPlayer = plugin.getServer().getPlayer(kickedName);
                if (kickedPlayer.getUniqueId() == player.getUniqueId()) {
                    player.sendMessage(CC.RED + "You can't kick yourself from the clan, you must use /team leave.");
                    return;
                }
                final Team kickingTeam = team;
                if (kickedPlayer == null) {
                    ProfileUtil.lookupProfileAsync(plugin, kickedName, (mojangProfile, b) -> {
                        if (b) {
                            if (!kickingTeam.getMembers().containsKey(mojangProfile.getId())) {
                                player.sendMessage(CC.RED + "That player is not currently in the team, use /team invite <player> to invite them.");
                                return;
                            }
                            final TeamRank kickedRank = kickingTeam.getMembers().get(mojangProfile.getId());
                            if (kickingTeam.isRankHigher(kickedRank, kickingTeam.getRankById(player.getUniqueId()))) {
                                kickingTeam.removeMember(mojangProfile.getId());
                                kickingTeam.messageTeam(CC.SECONDARY + player.getName() + CC.PRIMARY + " has kicked " + CC.SECONDARY + mojangProfile.getName()
                                        + CC.PRIMARY + " from the team.");
                                player.sendMessage(CC.PRIMARY + "You have successfully kicked " + CC.SECONDARY + mojangProfile.getName() + CC.PRIMARY + " from the team.");
                            } else {
                                player.sendMessage(CC.RED + "You must have a higher rank in order to kick that player from the team.");
                            }
                        } else {
                            player.sendMessage(CC.RED + "That player could not be found.");
                        }
                    });
                } else {
                    if (!kickingTeam.getMembers().containsKey(kickedPlayer.getUniqueId())) {
                        player.sendMessage(CC.RED + "That player is not currently in the team, use /team invite <player> to invite them.");
                        return;
                    }
                    final TeamRank kickedRank = kickingTeam.getMembers().get(kickedPlayer.getUniqueId());
                    if (kickingTeam.isRankHigher(kickedRank, kickingTeam.getRankById(player.getUniqueId()))) {
                        kickingTeam.removeMember(kickedPlayer.getUniqueId());
                        kickingTeam.messageTeam(CC.SECONDARY + player.getName() + CC.PRIMARY + " has kicked " + CC.SECONDARY + kickedPlayer.getName()
                                + CC.PRIMARY + " from the team.");
                        player.sendMessage(CC.PRIMARY + "You have successfully kicked " + CC.SECONDARY + kickedPlayer.getName() + CC.PRIMARY + " from the team.");
                        kickedPlayer.sendMessage(CC.PRIMARY + "You were kicked from team " + CC.SECONDARY + kickingTeam.getName() + CC.PRIMARY + " by " + CC.SECONDARY
                                + player.getName() + CC.PRIMARY + ".");
                    } else {
                        player.sendMessage(CC.RED + "You must have a higher rank in order to kick that player from the team.");
                    }
                    return;
                }
                break;
            case "leave":
                if (team == null) {
                    player.sendMessage(CC.RED + "You're not currently in a team.");
                    return;
                }
                if (team.hasRank(TeamRank.LEADER, player.getUniqueId())) {
                    player.sendMessage(CC.RED + "You must disband the team in order to leave it.");
                    return;
                }
                team.removeMember(player.getUniqueId());
                profile.setLastKnownTeam(null);
                team.messageTeam(CC.SECONDARY + player.getName() + CC.PRIMARY + " has left the team.");
                player.sendMessage(CC.PRIMARY + "You have left team " + CC.SECONDARY + team.getName() + CC.PRIMARY + ".");
                break;
            case "setpassword":
                if (args[1] == null) {
                    player.sendMessage(usageMessage);
                    return;
                }
                if (team == null) {
                    player.sendMessage(CC.RED + "You're not currently in a team.");
                    return;
                }
                if (!team.hasRank(TeamRank.OFFICER, player.getUniqueId())) {
                    player.sendMessage(CC.RED + "You must be an officer to set the team password.");
                    return;
                }
                team.messageTeam(CC.SECONDARY + player.getName() + CC.PRIMARY + " has changed the team password to " + CC.SECONDARY + args[1] + CC.PRIMARY + ".");
                team.setPassword(args[1]);
                break;
            case "removepassword":
                if (team == null) {
                    player.sendMessage(CC.RED + "You're not currently in a team.");
                    return;
                }
                if (!team.hasRank(TeamRank.OFFICER, player.getUniqueId())) {
                    player.sendMessage(CC.RED + "You must be an officer to set the team password.");
                    return;
                }
                team.messageTeam(CC.SECONDARY + player.getName() + CC.PRIMARY + " has removed the team password.");
                team.setPassword(null);
                break;
            case "join":
                if (args[1] == null) {
                    player.sendMessage(usageMessage);
                    return;
                }
                if (team != null) {
                    player.sendMessage(CC.RED + "You're currently already in a team.");
                    return;
                }
                if (!teamManager.doesTeamExist(args[1])) {
                    player.sendMessage(CC.RED + "That team doesn't exist.");
                    return;
                }
                boolean teamLoaded = teamManager.getTeamByName(args[1]) != null;
                Team loadedTeam = teamLoaded ? teamManager.getTeamByName(args[1]) : teamManager.registerTeam(args[1]);
                boolean invited = loadedTeam.getInvitedIds().contains(player.getUniqueId());
                if (loadedTeam.getInvitedIds().contains(player.getUniqueId())) {
                    loadedTeam.getInvitedIds().remove(player.getUniqueId());
                    loadedTeam.addMember(player.getUniqueId(), TeamRank.MEMBER);
                    profile.setLastKnownTeam(loadedTeam.getTeamId());
                    loadedTeam.messageTeam(CC.SECONDARY + player.getName() + CC.PRIMARY + " has joined the team.");
                    player.sendMessage(CC.PRIMARY + "You have successfully joined team " + CC.SECONDARY + loadedTeam.getName() + CC.PRIMARY + ".");
                } else {
                    System.out.println(args.length + "args length");
                    if (args.length >= 3 && args[2] == null) {
                        player.sendMessage(CC.RED + "You need an invitation or a password to join that team.");
                        if (!teamLoaded) {
                            teamManager.unregisterTeam(loadedTeam.getName());
                        }
                    } else if (args.length >= 3 && args[2].equals(loadedTeam.getPassword())) {
                        loadedTeam.addMember(player.getUniqueId(), TeamRank.MEMBER);
                        profile.setLastKnownTeam(loadedTeam.getTeamId());
                        loadedTeam.messageTeam(CC.SECONDARY + player.getName() + CC.PRIMARY + " has joined the team.");
                        player.sendMessage(CC.PRIMARY + "You have successfully joined team " + CC.SECONDARY + loadedTeam.getName() + CC.PRIMARY + ".");
                    } else {
                        player.sendMessage(CC.RED + "You need an invitation or a password to join that team.");
                        if (!teamLoaded) {
                            teamManager.unregisterTeam(loadedTeam.getName());
                        }
                    }
                }
                break;
            case "disband":
                if (team == null) {
                    player.sendMessage(CC.RED + "You're not currently in a team.");
                    return;
                }
                if (!team.hasRank(TeamRank.LEADER, player.getUniqueId())) {
                    player.sendMessage(CC.RED + "You must be the leader in order to disband the team.");
                    return;
                }
                team.messageTeam(CC.PRIMARY + "Your team has been disbanded.");
                team.getMembers().keySet().forEach(uuid -> {
                    KitProfile profile1 = plugin.getPlayerManager().getProfile(plugin.getServer().getPlayer(uuid));
                    if (profile1 == null) {
                        return;
                    }
                    profile1.setLastKnownTeam(null);
                });
                team.getMembers().clear();
                teamManager.unregisterTeam(team.getName());
                CorePlugin.getInstance().getMongoStorage().deleteDocument("betateams", team.getTeamId());
                teamManager.doesTeamExist(team.getTeamId());
                break;
            case "info":
                if (args[1] == null) {
                    player.sendMessage(usageMessage);
                    return;
                }
                if (!teamManager.doesTeamExist(args[1])) {
                    player.sendMessage(CC.RED + "That team doesn't exist.");
                    return;
                }
                Team queriedTeam = teamManager.getTeamByName(args[1]);
                player.sendMessage(queriedTeam.getName() + " name");
                player.sendMessage(queriedTeam.getTeamId() + " id");
                player.sendMessage(teamManager.getMembersSortedByRank(queriedTeam));
                break;
            case "reset":
                profile.setLastKnownTeam(null);
        }
    }
}*/
