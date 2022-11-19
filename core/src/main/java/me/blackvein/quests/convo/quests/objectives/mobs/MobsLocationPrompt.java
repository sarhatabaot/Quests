package me.blackvein.quests.convo.quests.objectives.mobs;

import me.blackvein.quests.convo.quests.QuestsEditorStringPrompt;
import me.blackvein.quests.convo.quests.objectives.MobsPrompt;
import me.blackvein.quests.events.editor.quests.QuestsEditorPostOpenStringPromptEvent;
import me.blackvein.quests.util.CK;
import me.blackvein.quests.util.ConfigUtil;
import me.blackvein.quests.util.Lang;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

/**
 * @author sarhatabaot
 */
public class MobsLocationPrompt extends QuestsEditorStringPrompt {

    private final MobsPrompt mobsPrompt;

    public MobsLocationPrompt(final MobsPrompt mobsPrompt, final ConversationContext context) {
        super(context);
        this.mobsPrompt = mobsPrompt;
    }

    @Override
    public String getTitle(final ConversationContext context) {
        return null;
    }

    @Override
    public String getQueryText(final ConversationContext context) {
        return Lang.get("stageEditorMobLocationPrompt");
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
        final Player player = (Player) context.getForWhom();
        if (input.equalsIgnoreCase(Lang.get("cmdAdd"))) {
            final Block block = mobsPrompt.plugin.getQuestFactory().getSelectedKillLocations().get(player.getUniqueId());
            if (block != null) {
                final Location loc = block.getLocation();
                final LinkedList<String> locations;
                if (context.getSessionData(mobsPrompt.pref + CK.S_MOB_KILL_LOCATIONS) != null) {
                    locations = (LinkedList<String>) context.getSessionData(mobsPrompt.pref + CK.S_MOB_KILL_LOCATIONS);
                } else {
                    locations = new LinkedList<>();
                }
                if (locations != null) {
                    locations.add(ConfigUtil.getLocationInfo(loc));
                }
                context.setSessionData(mobsPrompt.pref + CK.S_MOB_KILL_LOCATIONS, locations);
                final Map<UUID, Block> temp = mobsPrompt.plugin.getQuestFactory().getSelectedKillLocations();
                temp.remove(player.getUniqueId());
                mobsPrompt.plugin.getQuestFactory().setSelectedKillLocations(temp);
            } else {
                player.sendMessage(ChatColor.RED + Lang.get("stageEditorNoBlock"));
                return new MobsLocationPrompt(context);
            }
            return new MobsKillListPrompt(mobsPrompt, context);
        } else if (input.equalsIgnoreCase(Lang.get("cmdCancel"))) {
            final Map<UUID, Block> temp = mobsPrompt.plugin.getQuestFactory().getSelectedKillLocations();
            temp.remove(player.getUniqueId());
            mobsPrompt.plugin.getQuestFactory().setSelectedKillLocations(temp);
            return new MobsKillListPrompt(mobsPrompt, context);
        } else {
            return new MobsLocationPrompt(context);
        }
    }
}
