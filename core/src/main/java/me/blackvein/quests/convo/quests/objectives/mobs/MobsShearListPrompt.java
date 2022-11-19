package me.blackvein.quests.convo.quests.objectives.mobs;

import me.blackvein.quests.convo.quests.QuestsEditorNumericPrompt;
import me.blackvein.quests.convo.quests.objectives.MobsPrompt;
import me.blackvein.quests.events.editor.quests.QuestsEditorPostOpenNumericPromptEvent;
import me.blackvein.quests.util.CK;
import me.blackvein.quests.util.Lang;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author sarhatabaot
 */
public class MobsShearListPrompt extends QuestsEditorNumericPrompt {

    private final MobsPrompt mobsPrompt;

    public MobsShearListPrompt(final MobsPrompt mobsPrompt, final ConversationContext context) {
        super(context);
        this.mobsPrompt = mobsPrompt;
    }

    private final int size = 4;

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public String getTitle(final ConversationContext context) {
        return Lang.get("stageEditorShearSheep");
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
            case 1 -> ChatColor.YELLOW + Lang.get("stageEditorSetShearColors");
            case 2 -> ChatColor.YELLOW + Lang.get("stageEditorSetShearAmounts");
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
                if (context.getSessionData(mobsPrompt.pref + CK.S_SHEAR_COLORS) == null) {
                    return ChatColor.GRAY + "(" + Lang.get("noneSet") + ")";
                } else {
                    final StringBuilder text = new StringBuilder();
                    final List<String> shearColors = (List<String>) context.getSessionData(mobsPrompt.pref + CK.S_SHEAR_COLORS);
                    if (shearColors != null) {
                        for (final String s : shearColors) {
                            text.append("\n").append(ChatColor.GRAY).append("     - ").append(ChatColor.AQUA).append(s);
                        }
                    }
                    return text.toString();
                }
            case 2:
                if (context.getSessionData(mobsPrompt.pref + CK.S_SHEAR_AMOUNTS) == null) {
                    return ChatColor.GRAY + "(" + Lang.get("noneSet") + ")";
                } else {
                    final StringBuilder text = new StringBuilder();
                    final List<Integer> shearAmounts = (List<Integer>) context.getSessionData(mobsPrompt.pref + CK.S_SHEAR_AMOUNTS);
                    if (shearAmounts != null) {
                        for (final Integer i : shearAmounts) {
                            text.append("\n").append(ChatColor.GRAY).append("     - ").append(ChatColor.AQUA).append(i);
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
                return new MobsShearColorsPrompt(mobsPrompt, context);
            case 2:
                return new MobsShearAmountsPrompt(mobsPrompt, context);
            case 3:
                context.getForWhom().sendRawMessage(ChatColor.YELLOW + Lang.get("stageEditorObjectiveCleared"));
                context.setSessionData(mobsPrompt.pref + CK.S_SHEAR_COLORS, null);
                context.setSessionData(mobsPrompt.pref + CK.S_SHEAR_AMOUNTS, null);
                return new MobsShearListPrompt(context);
            case 4:
                final int one;
                final int two;
                final List<String> colors = (List<String>) context.getSessionData(mobsPrompt.pref + CK.S_SHEAR_COLORS);
                final List<Integer> amounts = (List<Integer>) context.getSessionData(mobsPrompt.pref + CK.S_SHEAR_AMOUNTS);
                if (colors != null) {
                    one = colors.size();
                } else {
                    one = 0;
                }
                if (amounts != null) {
                    two = amounts.size();
                } else {
                    two = 0;
                }
                if (one == two) {
                    return new MobsPrompt(mobsPrompt.stageNum, context);
                } else {
                    context.getForWhom().sendRawMessage(ChatColor.RED + Lang.get("listsNotSameSize"));
                    return new MobsShearListPrompt(context);
                }
            default:
                return new MobsPrompt(mobsPrompt.stageNum, context);
        }
    }
}
