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
public class MobsTameListPrompt extends QuestsEditorNumericPrompt {

    private final MobsPrompt mobsPrompt;

    public MobsTameListPrompt(final MobsPrompt mobsPrompt, final ConversationContext context) {
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
        return Lang.get("stageEditorTameMobs");
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
            case 1 -> ChatColor.YELLOW + Lang.get("stageEditorSetMobTypes");
            case 2 -> ChatColor.YELLOW + Lang.get("stageEditorSetTameAmounts");
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
                if (context.getSessionData(mobsPrompt.pref + CK.S_TAME_TYPES) == null) {
                    return ChatColor.GRAY + "(" + Lang.get("noneSet") + ")";
                } else {
                    final StringBuilder text = new StringBuilder();
                    final List<String> tameTypes = (List<String>) context.getSessionData(mobsPrompt.pref + CK.S_TAME_TYPES);
                    if (tameTypes != null) {
                        for (final String s : tameTypes) {
                            text.append("\n").append(ChatColor.GRAY).append("     - ").append(ChatColor.AQUA).append(s);
                        }
                    }
                    return text.toString();
                }
            case 2:
                if (context.getSessionData(mobsPrompt.pref + CK.S_TAME_AMOUNTS) == null) {
                    return ChatColor.GRAY + "(" + Lang.get("noneSet") + ")";
                } else {
                    final StringBuilder text = new StringBuilder();
                    final List<Integer> tameAmounts = (List<Integer>) context.getSessionData(mobsPrompt.pref + CK.S_TAME_AMOUNTS);
                    if (tameAmounts != null) {
                        for (final Integer i : tameAmounts) {
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
            final QuestsEditorPostOpenNumericPromptEvent event = new QuestsEditorPostOpenNumericPromptEvent(context, this);
            context.getPlugin().getServer().getPluginManager().callEvent(event);
        }

        final StringBuilder text = new StringBuilder(ChatColor.AQUA + "- " + getTitle(context) + " -\n");
        for (int i = 1; i <= size; i++) {
            text.append(getNumberColor(context, i)).append(ChatColor.BOLD).append(i).append(ChatColor.RESET)
                    .append(" - ").append(getSelectionText(context, i)).append(" ")
                    .append(getAdditionalText(context, i)).append("\n");
        }
        return text.toString();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Prompt acceptValidatedInput(final @NotNull ConversationContext context, final Number input) {
        switch (input.intValue()) {
            case 1:
                return new MobsTameTypesPrompt(MobsPrompt.this, context);
            case 2:
                return new MobsTameAmountsPrompt(MobsPrompt.this, context);
            case 3:
                context.getForWhom().sendRawMessage(ChatColor.YELLOW + Lang.get("stageEditorObjectiveCleared"));
                context.setSessionData(mobsPrompt.pref + CK.S_TAME_TYPES, null);
                context.setSessionData(mobsPrompt.pref + CK.S_TAME_AMOUNTS, null);
                return new MobsTameListPrompt(context);
            case 4:
                final int one;
                final int two;
                final List<String> types = (List<String>) context.getSessionData(mobsPrompt.pref + CK.S_TAME_TYPES);
                final List<Integer> amounts = (List<Integer>) context.getSessionData(mobsPrompt.pref + CK.S_TAME_AMOUNTS);
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
                if (one == two) {
                    return new MobsPrompt(mobsPrompt.stageNum, context);
                } else {
                    context.getForWhom().sendRawMessage(ChatColor.RED + Lang.get("listsNotSameSize"));
                    return new MobsTameListPrompt(context);
                }
            default:
                return new MobsPrompt(mobsPrompt.stageNum, context);
        }
    }
}
