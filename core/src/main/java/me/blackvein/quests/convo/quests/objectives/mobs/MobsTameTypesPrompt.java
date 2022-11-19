package me.blackvein.quests.convo.quests.objectives.mobs;

import me.blackvein.quests.convo.quests.QuestsEditorStringPrompt;
import me.blackvein.quests.convo.quests.objectives.MobsPrompt;
import me.blackvein.quests.events.editor.quests.QuestsEditorPostOpenStringPromptEvent;
import me.blackvein.quests.util.CK;
import me.blackvein.quests.util.Lang;
import me.blackvein.quests.util.MiscUtil;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Tameable;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author sarhatabaot
 */
public class MobsTameTypesPrompt extends QuestsEditorStringPrompt {

    private final MobsPrompt mobsPrompt;

    public MobsTameTypesPrompt(final MobsPrompt mobsPrompt, final ConversationContext context) {
        super(context);
        this.mobsPrompt = mobsPrompt;
    }

    @Override
    public String getTitle(final ConversationContext context) {
        return Lang.get("eventEditorMobsTitle");
    }

    @Override
    public String getQueryText(final ConversationContext context) {
        return Lang.get("stageEditorMobsPrompt");
    }

    @Override
    public @NotNull String getPromptText(final @NotNull ConversationContext context) {
        if (context.getPlugin() != null) {
            final QuestsEditorPostOpenStringPromptEvent event
                    = new QuestsEditorPostOpenStringPromptEvent(context, this);
            context.getPlugin().getServer().getPluginManager().callEvent(event);
        }

        final StringBuilder mobs = new StringBuilder(ChatColor.LIGHT_PURPLE + getTitle(context) + "\n");
        final List<EntityType> mobArr = new LinkedList<>(Arrays.asList(EntityType.values()));
        final List<EntityType> toRemove = new LinkedList<>();
        for (final EntityType type : mobArr) {
            final Class<? extends Entity> ec = type.getEntityClass();
            if (!type.isAlive() || (ec != null && !Tameable.class.isAssignableFrom(ec))) {
                toRemove.add(type);
            }
        }
        mobArr.removeAll(toRemove);
        mobArr.sort(Comparator.comparing(EntityType::name));
        for (int i = 0; i < mobArr.size(); i++) {
            mobs.append(ChatColor.AQUA).append(MiscUtil.snakeCaseToUpperCamelCase(mobArr.get(i).name()));
            if (i < (mobArr.size() - 1)) {
                mobs.append(ChatColor.GRAY).append(", ");
            }
        }
        mobs.append("\n").append(ChatColor.YELLOW).append(getQueryText(context));
        return mobs.toString();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Prompt acceptInput(final @NotNull ConversationContext context, final String input) {
        if (input == null) {
            return null;
        }
        if (!input.equalsIgnoreCase(Lang.get("cmdCancel"))) {
            final LinkedList<String> mobTypes = new LinkedList<>();
            for (final String s : input.split(" ")) {
                if (MiscUtil.getProperMobType(s) != null) {
                    final EntityType type = MiscUtil.getProperMobType(s);
                    if (type != null) {
                        final Class<? extends Entity> ec = type.getEntityClass();
                        if (type.isAlive() || (ec != null && Tameable.class.isAssignableFrom(ec))) {
                            mobTypes.add(s);
                            context.setSessionData(mobsPrompt.pref + CK.S_TAME_TYPES, mobTypes);

                            LinkedList<Integer> amounts = new LinkedList<>();
                            if (context.getSessionData(mobsPrompt.pref + CK.S_TAME_AMOUNTS) != null) {
                                amounts = (LinkedList<Integer>) context.getSessionData(mobsPrompt.pref + CK.S_TAME_AMOUNTS);
                            }
                            if (amounts != null) {
                                for (int i = 0; i < mobTypes.size(); i++) {
                                    if (i >= amounts.size()) {
                                        amounts.add(1);
                                    }
                                }
                            }
                            context.setSessionData(mobsPrompt.pref + CK.S_TAME_AMOUNTS, amounts);
                        } else {
                            context.getForWhom().sendRawMessage(ChatColor.RED + Lang.get("stageEditorInvalidMob")
                                    .replace("<input>", s));
                            return new MobsTameTypesPrompt(context);
                        }
                    }
                } else {
                    context.getForWhom().sendRawMessage(ChatColor.RED + Lang.get("stageEditorInvalidMob")
                            .replace("<input>", s));
                    return new MobsTameTypesPrompt(context);
                }
            }
        }
        return new MobsTameListPrompt(mobsPrompt, context);
    }
}
