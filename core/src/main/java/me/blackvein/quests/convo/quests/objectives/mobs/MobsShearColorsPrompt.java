package me.blackvein.quests.convo.quests.objectives.mobs;

import me.blackvein.quests.convo.quests.QuestsEditorStringPrompt;
import me.blackvein.quests.convo.quests.objectives.MobsPrompt;
import me.blackvein.quests.events.editor.quests.QuestsEditorPostOpenStringPromptEvent;
import me.blackvein.quests.util.CK;
import me.blackvein.quests.util.Lang;
import me.blackvein.quests.util.MiscUtil;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;

/**
 * @author sarhatabaot
 */
public class MobsShearColorsPrompt extends QuestsEditorStringPrompt {

    private final MobsPrompt mobsPrompt;

    public MobsShearColorsPrompt(final MobsPrompt mobsPrompt, final ConversationContext context) {
        super(context);
        this.mobsPrompt = mobsPrompt;
    }

    @Override
    public String getTitle(final ConversationContext context) {
        return Lang.get("stageEditorColors");
    }

    @Override
    public String getQueryText(final ConversationContext context) {
        return Lang.get("stageEditorShearColorsPrompt");
    }

    @Override
    public @NotNull String getPromptText(final @NotNull ConversationContext context) {
        if (context.getPlugin() != null) {
            final QuestsEditorPostOpenStringPromptEvent event
                    = new QuestsEditorPostOpenStringPromptEvent(context, this);
            context.getPlugin().getServer().getPluginManager().callEvent(event);
        }

        final StringBuilder cols = new StringBuilder(ChatColor.LIGHT_PURPLE + "- " + getTitle(context) + " - \n");
        final DyeColor[] colArr = DyeColor.values();
        for (int i = 0; i < colArr.length; i++) {
            if (i < (colArr.length - 1)) {
                cols.append(MiscUtil.snakeCaseToUpperCamelCase(colArr[i].name())).append(", ");
            } else {
                cols.append(MiscUtil.snakeCaseToUpperCamelCase(colArr[i].name())).append("\n");
            }
        }
        return cols.toString() + ChatColor.YELLOW + getQueryText(context);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Prompt acceptInput(final @NotNull ConversationContext context, final String input) {
        if (input == null) {
            return null;
        }
        if (!input.equalsIgnoreCase(Lang.get("cmdCancel"))) {
            final LinkedList<String> colors = new LinkedList<>();
            for (final String s : input.split(" ")) {
                if (MiscUtil.getProperDyeColor(s) != null) {
                    colors.add(s);
                    context.setSessionData(mobsPrompt.pref + CK.S_SHEAR_COLORS, colors);

                    LinkedList<Integer> amounts = new LinkedList<>();
                    if (context.getSessionData(mobsPrompt.pref + CK.S_SHEAR_AMOUNTS) != null) {
                        amounts = (LinkedList<Integer>) context.getSessionData(mobsPrompt.pref + CK.S_SHEAR_AMOUNTS);
                    }
                    if (amounts != null) {
                        for (int i = 0; i < colors.size(); i++) {
                            if (i >= amounts.size()) {
                                amounts.add(1);
                            }
                        }
                    }
                    context.setSessionData(mobsPrompt.pref + CK.S_SHEAR_AMOUNTS, amounts);
                } else {
                    context.getForWhom().sendRawMessage(ChatColor.RED + Lang.get("stageEditorInvalidDye")
                            .replace("<input>", s));
                    return new MobsShearColorsPrompt(context);
                }
            }
        }
        return new MobsShearListPrompt(MobsPrompt.this, context);
    }
}
