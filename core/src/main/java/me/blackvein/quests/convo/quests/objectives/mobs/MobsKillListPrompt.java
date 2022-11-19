package me.blackvein.quests.convo.quests.objectives.mobs;

import me.blackvein.quests.convo.quests.QuestsEditorNumericPrompt;
import me.blackvein.quests.convo.quests.objectives.MobsPrompt;
import me.blackvein.quests.events.editor.quests.QuestsEditorPostOpenNumericPromptEvent;
import me.blackvein.quests.util.CK;
import me.blackvein.quests.util.Lang;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author sarhatabaot
 */
public class MobsKillListPrompt extends QuestsEditorNumericPrompt {

    private final MobsPrompt mobsPrompt;

    public MobsKillListPrompt(final MobsPrompt mobsPrompt, final ConversationContext context) {
        super(context);
        this.mobsPrompt = mobsPrompt;
    }

    private final int size = 7;

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public String getTitle(final ConversationContext context) {
        return Lang.get("stageEditorKillMobs");
    }

    @Override
    public ChatColor getNumberColor(final ConversationContext context, final int number) {
        switch (number) {
            case 1:
            case 2:
            case 4:
            case 5:
                return ChatColor.BLUE;
            case 3:
                if (context.getForWhom() instanceof Player) {
                    return ChatColor.BLUE;
                } else {
                    return ChatColor.GRAY;
                }
            case 6:
                return ChatColor.RED;
            case 7:
                return ChatColor.GREEN;
            default:
                return null;
        }
    }

    @Override
    public String getSelectionText(final ConversationContext context, final int number) {
        switch (number) {
            case 1:
                return ChatColor.YELLOW + Lang.get("stageEditorSetMobTypes");
            case 2:
                return ChatColor.YELLOW + Lang.get("stageEditorSetMobAmounts");
            case 3:
                if (context.getForWhom() instanceof Player) {
                    return ChatColor.YELLOW + Lang.get("stageEditorSetKillLocations");
                } else {
                    return ChatColor.GRAY + Lang.get("stageEditorSetKillLocations");
                }
            case 4:
                return ChatColor.YELLOW + Lang.get("stageEditorSetKillLocationRadii");
            case 5:
                return ChatColor.YELLOW + Lang.get("stageEditorSetKillLocationNames");
            case 6:
                return ChatColor.RED + Lang.get("clear");
            case 7:
                return ChatColor.GREEN + Lang.get("done");
            default:
                return null;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public String getAdditionalText(final ConversationContext context, final int number) {
        switch (number) {
            case 1:
                if (context.getSessionData(mobsPrompt.pref + CK.S_MOB_TYPES) == null) {
                    return ChatColor.GRAY + "(" + Lang.get("noneSet") + ")";
                } else {
                    final StringBuilder text = new StringBuilder();
                    final List<String> mobTypes = (List<String>) context.getSessionData(mobsPrompt.pref + CK.S_MOB_TYPES);
                    if (mobTypes != null) {
                        for (final String s : mobTypes) {
                            text.append("\n").append(ChatColor.GRAY).append("     - ").append(ChatColor.AQUA).append(s);
                        }
                    }
                    return text.toString();
                }
            case 2:
                if (context.getSessionData(mobsPrompt.pref + CK.S_MOB_AMOUNTS) == null) {
                    return ChatColor.GRAY + "(" + Lang.get("noneSet") + ")";
                } else {
                    final StringBuilder text = new StringBuilder();
                    final List<Integer> mobAmounts = (List<Integer>) context.getSessionData(mobsPrompt.pref + CK.S_MOB_AMOUNTS);
                    if (mobAmounts != null) {
                        for (final Integer i : mobAmounts) {
                            text.append("\n").append(ChatColor.GRAY).append("     - ").append(ChatColor.AQUA).append(i);
                        }
                    }
                    return text.toString();
                }
            case 3:
                if (context.getSessionData(mobsPrompt.pref + CK.S_MOB_KILL_LOCATIONS) == null) {
                    return ChatColor.GRAY + "(" + Lang.get("noneSet") + ")";
                } else {
                    final StringBuilder text = new StringBuilder();
                    final List<String> mobsKillLocations
                            = (List<String>) context.getSessionData(mobsPrompt.pref + CK.S_MOB_KILL_LOCATIONS);
                    if (mobsKillLocations != null) {
                        for (final String s : mobsKillLocations) {
                            text.append("\n").append(ChatColor.GRAY).append("     - ").append(ChatColor.AQUA).append(s);
                        }
                    }
                    return text.toString();
                }
            case 4:
                if (context.getSessionData(mobsPrompt.pref + CK.S_MOB_KILL_LOCATIONS_RADIUS) == null) {
                    return ChatColor.GRAY + "(" + Lang.get("noneSet") + ")";
                } else {
                    final StringBuilder text = new StringBuilder();
                    final List<Integer> mobKillLocationsRadius
                            = (List<Integer>) context.getSessionData(mobsPrompt.pref + CK.S_MOB_KILL_LOCATIONS_RADIUS);
                    if (mobKillLocationsRadius != null) {
                        for (final int i : mobKillLocationsRadius) {
                            text.append("\n").append(ChatColor.GRAY).append("     - ").append(ChatColor.AQUA).append(i);
                        }
                    }
                    return text.toString();
                }
            case 5:
                if (context.getSessionData(mobsPrompt.pref + CK.S_MOB_KILL_LOCATIONS_NAMES) == null) {
                    return ChatColor.GRAY + "(" + Lang.get("noneSet") + ")";
                } else {
                    final StringBuilder text = new StringBuilder();
                    final List<String> mobKillLocationsNames
                            = (List<String>) context.getSessionData(mobsPrompt.pref + CK.S_MOB_KILL_LOCATIONS_NAMES);
                    if (mobKillLocationsNames != null) {
                        for (final String s : mobKillLocationsNames) {
                            text.append("\n").append(ChatColor.GRAY).append("     - ").append(ChatColor.AQUA).append(s);
                        }
                    }
                    return text.toString();
                }
            case 6:
            case 7:
                return "";
            default:
                return null;
        }
    }

    @Override
    public @NotNull String getBasicPromptText(final @NotNull ConversationContext context) {
        if (context.getPlugin() != null) {
            final QuestsEditorPostOpenNumericPromptEvent event = new QuestsEditorPostOpenNumericPromptEvent(context, this);
            context.getPlugin().getServer().getPluginManager().callEvent(event);
        }

        final StringBuilder text = new StringBuilder(ChatColor.AQUA + "- " + getTitle(context) + " -");
        for (int i = 1; i <= size; i++) {
            text.append("\n").append(getNumberColor(context, i)).append(ChatColor.BOLD).append(i)
                    .append(ChatColor.RESET).append(" - ").append(getSelectionText(context, i)).append(" ")
                    .append(getAdditionalText(context, i));
        }
        return text.toString();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Prompt acceptValidatedInput(final @NotNull ConversationContext context, final Number input) {
        switch (input.intValue()) {
            case 1:
                return new MobsTypesPrompt(MobsPrompt.this, context);
            case 2:
                return new MobsAmountsPrompt(MobsPrompt.this, context);
            case 3:
                if (context.getForWhom() instanceof Player) {
                    final Map<UUID, Block> temp = mobsPrompt.plugin.getQuestFactory().getSelectedKillLocations();
                    temp.put(((Player) context.getForWhom()).getUniqueId(), null);
                    mobsPrompt.plugin.getQuestFactory().setSelectedKillLocations(temp);
                    return new MobsLocationPrompt(MobsPrompt.this, context);
                } else {
                    context.getForWhom().sendRawMessage(ChatColor.YELLOW + Lang.get("consoleError"));
                    return new MobsKillListPrompt(context);
                }
            case 4:
                return new MobsRadiiPrompt(MobsPrompt.this, context);
            case 5:
                return new MobsLocationNamesPrompt(MobsPrompt.this, context);
            case 6:
                context.getForWhom().sendRawMessage(ChatColor.YELLOW + Lang.get("stageEditorObjectiveCleared"));
                context.setSessionData(mobsPrompt.pref + CK.S_MOB_TYPES, null);
                context.setSessionData(mobsPrompt.pref + CK.S_MOB_AMOUNTS, null);
                context.setSessionData(mobsPrompt.pref + CK.S_MOB_KILL_LOCATIONS, null);
                context.setSessionData(mobsPrompt.pref + CK.S_MOB_KILL_LOCATIONS_RADIUS, null);
                context.setSessionData(mobsPrompt.pref + CK.S_MOB_KILL_LOCATIONS_NAMES, null);
                return new MobsKillListPrompt(context);
            case 7:
                final int one;
                final int two;
                final int three;
                final int four;
                final int five;
                final List<String> types = (List<String>) context.getSessionData(mobsPrompt.pref + CK.S_MOB_TYPES);
                final List<Integer> amounts = (List<Integer>) context.getSessionData(mobsPrompt.pref + CK.S_MOB_AMOUNTS);
                final List<String> locations = (List<String>) context.getSessionData(mobsPrompt.pref + CK.S_MOB_KILL_LOCATIONS);
                final List<Integer> radii
                        = (List<Integer>) context.getSessionData(mobsPrompt.pref + CK.S_MOB_KILL_LOCATIONS_RADIUS);
                final List<String> names = (List<String>) context.getSessionData(mobsPrompt.pref + CK.S_MOB_KILL_LOCATIONS_NAMES);
                if (types != null) {
                    one = types.size();
                } else {
                    one = 0;
                }
                if (amounts != null) {
                    two = amounts.size();
                } else {
                    two = 0;
                }
                if (locations != null) {
                    three = locations.size();
                } else {
                    three = 0;
                }
                if (radii != null) {
                    four = radii.size();
                } else {
                    four = 0;
                }
                if (names != null) {
                    five = names.size();
                } else {
                    five = 0;
                }
                if (one == two) {
                    if (three != 0 || four != 0 || five != 0) {
                        if (two == three && three == four && four == five) {
                            return new MobsPrompt(mobsPrompt.stageNum, context);
                        } else {
                            context.getForWhom().sendRawMessage(ChatColor.RED + Lang.get("listsNotSameSize"));
                            return new MobsKillListPrompt(context);
                        }
                    } else {
                        return new MobsPrompt(mobsPrompt.stageNum, context);
                    }
                } else {
                    context.getForWhom().sendRawMessage(ChatColor.RED + Lang.get("listsNotSameSize"));
                    return new MobsKillListPrompt(context);
                }
            default:
                return new MobsPrompt(mobsPrompt.stageNum, context);
        }
    }
}
