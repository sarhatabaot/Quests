package me.blackvein.quests.convo.quests.objectives.npcs;

import me.blackvein.quests.convo.quests.QuestsEditorStringPrompt;
import me.blackvein.quests.convo.quests.objectives.NpcsPrompt;
import me.blackvein.quests.events.editor.quests.QuestsEditorPostOpenStringPromptEvent;
import me.blackvein.quests.util.CK;
import me.blackvein.quests.util.Lang;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;

/**
 * @author sarhatabaot
 */
public class NpcAmountsToKillPrompt extends QuestsEditorStringPrompt {

    private final NpcsPrompt npcsPrompt;

    public NpcAmountsToKillPrompt(final NpcsPrompt npcsPrompt, final ConversationContext context) {
        super(context);
        this.npcsPrompt = npcsPrompt;
    }

    @Override
    public String getTitle(final ConversationContext context) {
        return null;
    }

    @Override
    public String getQueryText(final ConversationContext context) {
        return Lang.get("stageEditorKillNPCsPrompt");
    }

    @Override
    public @NotNull String getPromptText(final @NotNull ConversationContext context) {
        if (context.getPlugin() != null) {
            final QuestsEditorPostOpenStringPromptEvent event
                    = new QuestsEditorPostOpenStringPromptEvent(context, this);
            npcsPrompt.plugin.getServer().getPluginManager().callEvent(event);
        }

        return ChatColor.YELLOW + getQueryText(context);
    }

    @Override
    public Prompt acceptInput(final @NotNull ConversationContext context, final String input) {
        if (input == null) {
            return null;
        }
        if (!input.equalsIgnoreCase(Lang.get("cmdCancel"))) {
            final String[] args = input.split(" ");
            final LinkedList<Integer> amounts = new LinkedList<>();
            for (final String s : args) {
                try {
                    if (Integer.parseInt(s) > 0) {
                        amounts.add(Integer.parseInt(s));
                    } else {
                        context.getForWhom().sendRawMessage(ChatColor.RED + Lang.get("invalidMinimum")
                                .replace("<number>", "1"));
                        return new NpcAmountsToKillPrompt(context);
                    }
                } catch (final NumberFormatException e) {
                    context.getForWhom().sendRawMessage(ChatColor.RED + Lang.get("stageEditorNotListOfUniqueIds")
                            .replace("<data>", s));
                    return new NpcAmountsToKillPrompt(context);
                }
            }
            context.setSessionData(npcsPrompt.pref + CK.S_NPCS_TO_KILL_AMOUNTS, amounts);
        }
        return new NpcsKillListPrompt(npcsPrompt, context);
    }
}
