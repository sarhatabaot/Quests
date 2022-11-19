package me.blackvein.quests.convo.quests.objectives.npcs;

import me.blackvein.quests.convo.quests.QuestsEditorNumericPrompt;
import me.blackvein.quests.convo.quests.objectives.NpcsPrompt;
import me.blackvein.quests.convo.quests.stages.StageMainPrompt;
import me.blackvein.quests.events.editor.quests.QuestsEditorPostOpenNumericPromptEvent;
import me.blackvein.quests.util.CK;
import me.blackvein.quests.util.Lang;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

/**
 * @author sarhatabaot
 */
public class NpcsKillListPrompt extends QuestsEditorNumericPrompt {

    private final NpcsPrompt npcsPrompt;

    public NpcsKillListPrompt(final NpcsPrompt npcsPrompt, final ConversationContext context) {
        super(context);
        this.npcsPrompt = npcsPrompt;
    }

    private final int size = 4;

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public String getTitle(final ConversationContext context) {
        return Lang.get("stageEditorNPCs");
    }

    @Override
    public ChatColor getNumberColor(final ConversationContext context, final int number) {
        return switch (number) {
            case 1, 2 -> ChatColor.BLUE;
            case 3 -> ChatColor.RED;
            case 4 -> ChatColor.GREEN;
            default -> null;
        };
    }

    @Override
    public String getSelectionText(final ConversationContext context, final int number) {
        return switch (number) {
            case 1 -> ChatColor.YELLOW + Lang.get("stageEditorNPCUniqueIds");
            case 2 -> ChatColor.YELLOW + Lang.get("stageEditorSetKillAmounts");
            case 3 -> ChatColor.RED + Lang.get("clear");
            case 4 -> ChatColor.GREEN + Lang.get("done");
            default -> null;
        };
    }

    @Override
    @SuppressWarnings("unchecked")
    public String getAdditionalText(final ConversationContext context, final int number) {
        switch (number) {
            case 1:
                if (npcsPrompt.plugin.getDependencies().getCitizens() != null || npcsPrompt.plugin.getDependencies().getZnpcs() != null) {
                    if (context.getSessionData(npcsPrompt.pref + CK.S_NPCS_TO_KILL) == null) {
                        return ChatColor.GRAY + "(" + Lang.get("noneSet") + ")";
                    } else {
                        final StringBuilder text = new StringBuilder();
                        final List<String> npcsToKill = (List<String>) context.getSessionData(npcsPrompt.pref + CK.S_NPCS_TO_KILL);
                        if (npcsToKill != null) {
                            for (final String s : npcsToKill) {
                                text.append("\n").append(ChatColor.GRAY).append("     - ").append(ChatColor.BLUE)
                                        .append(npcsPrompt.plugin.getDependencies().getNPCName(UUID.fromString(s)))
                                        .append(ChatColor.GRAY).append(" (").append(ChatColor.AQUA).append(s)
                                        .append(ChatColor.GRAY).append(")");
                            }
                        }
                        return text.toString();
                    }
                } else {
                    return ChatColor.GRAY + " (" + Lang.get("notInstalled") + ")";
                }
            case 2:
                if (context.getSessionData(npcsPrompt.pref + CK.S_NPCS_TO_KILL_AMOUNTS) == null) {
                    return ChatColor.GRAY + "(" + Lang.get("noneSet") + ")";
                } else {
                    final StringBuilder text = new StringBuilder();
                    final List<Integer> npcsToKillAmounts
                            = (List<Integer>) context.getSessionData(npcsPrompt.pref + CK.S_NPCS_TO_KILL_AMOUNTS);
                    if (npcsToKillAmounts != null) {
                        for (final Integer i : npcsToKillAmounts) {
                            text.append("\n").append(ChatColor.GRAY).append("     - ").append(ChatColor.BLUE).append(i);
                        }
                    }
                    return text.toString();
                }
            case 3:
            case 4:
                return "";
            default:
                return null;
        }
    }

    @Override
    public @NotNull String getBasicPromptText(final @NotNull ConversationContext context) {
        if (context.getPlugin() != null) {
            final QuestsEditorPostOpenNumericPromptEvent event
                    = new QuestsEditorPostOpenNumericPromptEvent(context, this);
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
                return new NpcIdsToKillPrompt(NpcsPrompt.this, context);
            case 2:
                return new NpcAmountsToKillPrompt(NpcsPrompt.this, context);
            case 3:
                context.getForWhom().sendRawMessage(ChatColor.YELLOW + Lang.get("stageEditorObjectiveCleared"));
                context.setSessionData(npcsPrompt.pref + CK.S_NPCS_TO_KILL, null);
                context.setSessionData(npcsPrompt.pref + CK.S_NPCS_TO_KILL_AMOUNTS, null);
                return new NpcsKillListPrompt(context);
            case 4:
                final int one;
                final int two;
                final List<UUID> kill = (List<UUID>) context.getSessionData(npcsPrompt.pref + CK.S_NPCS_TO_KILL);
                final List<Integer> killAmounts
                        = (List<Integer>) context.getSessionData(npcsPrompt.pref + CK.S_NPCS_TO_KILL_AMOUNTS);
                if (kill != null) {
                    one = kill.size();
                } else {
                    one = 0;
                }
                if (killAmounts != null) {
                    two = killAmounts.size();
                } else {
                    two = 0;
                }
                if (one == two) {
                    return new StageMainPrompt(npcsPrompt.stageNum, context);
                } else {
                    context.getForWhom().sendRawMessage(ChatColor.RED + Lang.get("listsNotSameSize"));
                    return new NpcsKillListPrompt(context);
                }
            default:
                return new NpcsPrompt(npcsPrompt.stageNum, context);
        }
    }
}
