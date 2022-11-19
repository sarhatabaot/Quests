package me.blackvein.quests.convo.quests.objectives.mobs;

import me.blackvein.quests.convo.quests.QuestsEditorStringPrompt;
import me.blackvein.quests.convo.quests.objectives.MobsPrompt;
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
public class MobsTameAmountsPrompt extends QuestsEditorStringPrompt {

    private final MobsPrompt mobsPrompt;

    public MobsTameAmountsPrompt(final MobsPrompt mobsPrompt, final ConversationContext context) {
        super(context);
        this.mobsPrompt = mobsPrompt;
    }

    @Override
    public String getTitle(final ConversationContext context) {
        return null;
    }

    @Override
    public String getQueryText(final ConversationContext context) {
        return Lang.get("stageEditorTameAmountsPrompt");
    }

    @Override
    public @NotNull String getPromptText(final @NotNull ConversationContext context) {
        if (context.getPlugin() != null) {
            final QuestsEditorPostOpenStringPromptEvent event
                    = new QuestsEditorPostOpenStringPromptEvent(context, this);
            context.getPlugin().getServer().getPluginManager().callEvent(event);
        }

        return ChatColor.YELLOW + getQueryText(context);
    }

    @Override
    public Prompt acceptInput(final @NotNull ConversationContext context, final String input) {
        if (input == null) {
            return null;
        }
        if (!input.equalsIgnoreCase(Lang.get("cmdCancel"))) {
            final LinkedList<Integer> mobAmounts = new LinkedList<>();
            for (final String s : input.split(" ")) {
                try {
                    final int i = Integer.parseInt(s);
                    if (i < 1) {
                        context.getForWhom().sendRawMessage(ChatColor.RED + Lang.get("invalidMinimum")
                                .replace("<number>", "1"));
                        return new MobsTameAmountsPrompt(context);
                    }
                    mobAmounts.add(i);
                } catch (final NumberFormatException e) {
                    context.getForWhom().sendRawMessage(ChatColor.RED + Lang.get("reqNotANumber")
                            .replace("<input>", input));
                    return new MobsTameAmountsPrompt(context);
                }
            }
            context.setSessionData(mobsPrompt.pref + CK.S_TAME_AMOUNTS, mobAmounts);
        }
        return new MobsTameListPrompt(mobsPrompt, context);
    }
}
