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
public class BlockBreakDurabilityPrompt extends QuestsEditorStringPrompt {

    private final BlocksPrompt blocksPrompt;

    public BlockBreakDurabilityPrompt(final BlocksPrompt blocksPrompt, final ConversationContext context) {
        super(context);
        this.blocksPrompt = blocksPrompt;
    }

    @Override
    public String getTitle(final ConversationContext context) {
        return null;
    }

    @Override
    public String getQueryText(final ConversationContext context) {
        return Lang.get("stageEditorEnterBlockDurability");
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
            final LinkedList<Short> durability = new LinkedList<>();
            for (final String s : args) {
                try {
                    if (Short.parseShort(s) >= 0) {
                        durability.add(Short.parseShort(s));
                    } else {
                        context.getForWhom().sendRawMessage(ChatColor.RED
                                + Lang.get("invalidMinimum").replace("<number>", "0"));
                        return new BlockBreakDurabilityPrompt(blocksPrompt, context);
                    }
                } catch (final NumberFormatException e) {
                    context.getForWhom().sendRawMessage(ChatColor.RED + Lang.get("stageEditorNotListOfNumbers")
                            .replace("<data>", s));
                    return new BlockBreakDurabilityPrompt(blocksPrompt, context);
                }
            }
            context.setSessionData(blocksPrompt.pref() + CK.S_BREAK_DURABILITY, durability);
        }
        return new BlocksBreakListPrompt(blocksPrompt, context);
    }
}
