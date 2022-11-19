package me.blackvein.quests.convo.quests.objectives.blocks;

import me.blackvein.quests.convo.quests.QuestsEditorStringPrompt;
import me.blackvein.quests.convo.quests.objectives.BlocksPrompt;
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
public class BlockPlaceAmountsPrompt extends QuestsEditorStringPrompt {

    private final BlocksPrompt blocksPrompt;

    public BlockPlaceAmountsPrompt(final BlocksPrompt blocksPrompt, final ConversationContext context) {
        super(context);
        this.blocksPrompt = blocksPrompt;
    }

    @Override
    public String getTitle(final ConversationContext context) {
        return null;
    }

    @Override
    public String getQueryText(final ConversationContext context) {
        return Lang.get("stageEditorEnterBlockAmounts");
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
            final String[] args = input.split(" ");
            final LinkedList<Integer> amounts = new LinkedList<>();
            for (final String s : args) {
                try {
                    if (Integer.parseInt(s) > 0) {
                        amounts.add(Integer.parseInt(s));
                    } else {
                        context.getForWhom().sendRawMessage(ChatColor.RED
                                + Lang.get("invalidMinimum").replace("<number>", "1"));
                        return new BlockPlaceAmountsPrompt(context);
                    }
                } catch (final NumberFormatException e) {
                    context.getForWhom().sendRawMessage(ChatColor.RED + Lang.get("stageEditorNotListOfNumbers")
                            .replace("<data>", s));
                    return new BlockPlaceAmountsPrompt(context);
                }
            }
            context.setSessionData(blocksPrompt.pref + CK.S_PLACE_AMOUNTS, amounts);
        }
        return new BlocksPlaceListPrompt(blocksPrompt, context);
    }
}
