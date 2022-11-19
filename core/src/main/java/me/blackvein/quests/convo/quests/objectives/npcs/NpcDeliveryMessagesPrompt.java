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

import java.util.Arrays;
import java.util.LinkedList;

/**
 * @author sarhatabaot
 */
public class NpcDeliveryMessagesPrompt extends QuestsEditorStringPrompt {

    private final NpcsPrompt npcsPrompt;

    public NpcDeliveryMessagesPrompt(final NpcsPrompt npcsPrompt, final ConversationContext context) {
        super(context);
        this.npcsPrompt = npcsPrompt;
    }

    @Override
    public String getTitle(final ConversationContext context) {
        return null;
    }

    @Override
    public String getQueryText(final ConversationContext context) {
        return Lang.get("stageEditorDeliveryMessagesPrompt");
    }

    @Override
    public @NotNull String getPromptText(final @NotNull ConversationContext context) {
        if (context.getPlugin() != null) {
            final QuestsEditorPostOpenStringPromptEvent event
                    = new QuestsEditorPostOpenStringPromptEvent(context, this);
            npcsPrompt.plugin.getServer().getPluginManager().callEvent(event);
        }

        return ChatColor.YELLOW + getQueryText(context) + "\n" + ChatColor.GOLD + Lang.get("stageEditorNPCNote");
    }

    @Override
    public Prompt acceptInput(final @NotNull ConversationContext context, final String input) {
        if (input == null) {
            return null;
        }
        if (!input.equalsIgnoreCase(Lang.get("cmdCancel"))) {
            final String[] args = input.split(Lang.get("charSemi"));
            final LinkedList<String> messages = new LinkedList<>(Arrays.asList(args));
            context.setSessionData(npcsPrompt.pref + CK.S_DELIVERY_MESSAGES, messages);
        }
        return new NpcsDeliveryListPrompt(npcsPrompt, context);
    }
}
