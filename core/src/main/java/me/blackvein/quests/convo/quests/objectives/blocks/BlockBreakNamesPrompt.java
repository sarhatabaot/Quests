package me.blackvein.quests.convo.quests.objectives.blocks;

import me.blackvein.quests.convo.quests.QuestsEditorStringPrompt;
import me.blackvein.quests.convo.quests.objectives.BlocksPrompt;
import me.blackvein.quests.events.editor.quests.QuestsEditorPostOpenStringPromptEvent;
import me.blackvein.quests.util.CK;
import me.blackvein.quests.util.Lang;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;

/**
 * @author sarhatabaot
 */
public class BlockBreakNamesPrompt extends QuestsEditorStringPrompt {

    private final BlocksPrompt blocksPrompt;

    public BlockBreakNamesPrompt(final BlocksPrompt blocksPrompt, final ConversationContext context) {
        super(context);
        this.blocksPrompt = blocksPrompt;
    }

    @Override
    public String getTitle(final ConversationContext context) {
        return null;
    }

    @Override
    public String getQueryText(final ConversationContext context) {
        return Lang.get("stageEditorEnterBlockNames");
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

    @SuppressWarnings("unchecked")
    @Override
    public Prompt acceptInput(final @NotNull ConversationContext context, final String input) {
        if (input == null) {
            return null;
        }
        if (!input.equalsIgnoreCase(Lang.get("cmdCancel"))) {
            final String[] args = input.split(" ");
            final LinkedList<String> names = new LinkedList<>();
            for (final String s : args) {
                try {
                    final Material m = Material.matchMaterial(s);
                    if (m != null) {
                        if (m.isBlock()) {
                            names.add(m.name());
                        } else {
                            context.getForWhom().sendRawMessage(ChatColor.RED + Lang.get("stageEditorNotSolid")
                                    .replace("<input>", s));
                            return new BlockBreakNamesPrompt(context);
                        }
                    } else {
                        context.getForWhom().sendRawMessage(ChatColor.RED + Lang.get("stageEditorInvalidBlockName")
                                .replace("<input>", s));
                        return new BlockBreakNamesPrompt(context);
                    }
                } catch (final NumberFormatException e) {
                    context.getForWhom().sendRawMessage(ChatColor.RED + Lang.get("stageEditorNotListOfNumbers")
                            .replace("<data>", s));
                    return new BlockBreakNamesPrompt(context);
                }
            }
            context.setSessionData(blocksPrompt.pref + CK.S_BREAK_NAMES, names);

            LinkedList<Integer> amounts = new LinkedList<>();
            if (context.getSessionData(blocksPrompt.pref + CK.S_BREAK_AMOUNTS) != null) {
                amounts = (LinkedList<Integer>) context.getSessionData(blocksPrompt.pref + CK.S_BREAK_AMOUNTS);
            }
            for (int i = 0; i < names.size(); i++) {
                if (amounts != null) {
                    if (i >= amounts.size()) {
                        amounts.add(1);
                    }
                }
            }
            context.setSessionData(blocksPrompt.pref + CK.S_BREAK_AMOUNTS, amounts);
        }
        return new BlocksBreakListPrompt(blocksPrompt, context);
    }
}
