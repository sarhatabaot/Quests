package me.blackvein.quests.convo.quests.objectives.npcs;

import me.blackvein.quests.convo.quests.QuestsEditorStringPrompt;
import me.blackvein.quests.convo.quests.objectives.NpcsPrompt;
import me.blackvein.quests.events.editor.quests.QuestsEditorPostOpenStringPromptEvent;
import me.blackvein.quests.util.CK;
import me.blackvein.quests.util.Lang;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.Set;
import java.util.UUID;

/**
 * @author sarhatabaot
 */
public class NpcIdsToKillPrompt extends QuestsEditorStringPrompt {

    private final NpcsPrompt npcsPrompt;

    public NpcIdsToKillPrompt(final NpcsPrompt npcsPrompt, final ConversationContext context) {
        super(context);
        this.npcsPrompt = npcsPrompt;
    }

    @Override
    public String getTitle(final ConversationContext context) {
        return null;
    }

    @Override
    public String getQueryText(final ConversationContext context) {
        return Lang.get("enterNpcUniqueIds");
    }

    @Override
    public @NotNull String getPromptText(final @NotNull ConversationContext context) {
        if (context.getPlugin() != null) {
            final QuestsEditorPostOpenStringPromptEvent event
                    = new QuestsEditorPostOpenStringPromptEvent(context, this);
            npcsPrompt.plugin.getServer().getPluginManager().callEvent(event);
        }

        if (context.getForWhom() instanceof Player) {
            final Set<UUID> selectingNpcs = npcsPrompt.plugin.getQuestFactory().getSelectingNpcs();
            selectingNpcs.add(((Player) context.getForWhom()).getUniqueId());
            npcsPrompt.plugin.getQuestFactory().setSelectingNpcs(selectingNpcs);
            return ChatColor.YELLOW + Lang.get("questEditorClickNPCStart");
        } else {
            return ChatColor.YELLOW + getQueryText(context);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Prompt acceptInput(final @NotNull ConversationContext context, final String input) {
        if (input == null) {
            return null;
        }
        if (!input.equalsIgnoreCase(Lang.get("cmdCancel"))) {
            final String[] args = input.split(" ");
            final LinkedList<String> npcs = context.getSessionData(npcsPrompt.pref + CK.S_NPCS_TO_KILL) != null
                    ? (LinkedList<String>) context.getSessionData(npcsPrompt.pref + CK.S_NPCS_TO_KILL) : new LinkedList<>();
            for (final String s : args) {
                try {
                    final UUID uuid = UUID.fromString(s);
                    if (npcsPrompt.plugin.getDependencies().getNPCName(uuid) != null && npcs != null) {
                        npcs.add(uuid.toString());
                    } else {
                        context.getForWhom().sendRawMessage(ChatColor.RED + Lang.get("stageEditorInvalidNPC")
                                .replace("<input>", s));
                        return new NpcIdsToKillPrompt(context);
                    }
                } catch (final IllegalArgumentException e) {
                    context.getForWhom().sendRawMessage(ChatColor.RED + Lang.get("stageEditorNotListOfUniqueIds")
                            .replace("<data>", s));
                    return new NpcIdsToKillPrompt(context);
                }
            }
            context.setSessionData(npcsPrompt.pref + CK.S_NPCS_TO_KILL, npcs);

            LinkedList<Integer> amounts = new LinkedList<>();
            if (context.getSessionData(npcsPrompt.pref + CK.S_NPCS_TO_KILL_AMOUNTS) != null) {
                amounts = (LinkedList<Integer>) context.getSessionData(npcsPrompt.pref + CK.S_NPCS_TO_KILL_AMOUNTS);
            }
            if (npcs != null && amounts != null) {
                for (int i = 0; i < npcs.size(); i++) {
                    if (i >= amounts.size()) {
                        amounts.add(1);
                    }
                }
            }
            context.setSessionData(npcsPrompt.pref + CK.S_NPCS_TO_KILL_AMOUNTS, amounts);
        }
        final Set<UUID> selectingNpcs = npcsPrompt.plugin.getQuestFactory().getSelectingNpcs();
        selectingNpcs.remove(((Player) context.getForWhom()).getUniqueId());
        npcsPrompt.plugin.getQuestFactory().setSelectingNpcs(selectingNpcs);
        return new NpcsKillListPrompt(npcsPrompt, context);
    }
}
