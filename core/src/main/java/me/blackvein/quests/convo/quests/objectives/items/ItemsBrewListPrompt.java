package me.blackvein.quests.convo.quests.objectives.items;

import me.blackvein.quests.convo.generic.ItemStackPrompt;
import me.blackvein.quests.convo.quests.QuestsEditorNumericPrompt;
import me.blackvein.quests.convo.quests.objectives.ItemsPrompt;
import me.blackvein.quests.events.editor.quests.QuestsEditorPostOpenNumericPromptEvent;
import me.blackvein.quests.util.CK;
import me.blackvein.quests.util.ItemUtil;
import me.blackvein.quests.util.Lang;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

/**
 * @author sarhatabaot
 */
public class ItemsBrewListPrompt extends QuestsEditorNumericPrompt {

    private final ItemsPrompt itemsPrompt;

    public ItemsBrewListPrompt(final ItemsPrompt itemsPrompt, final ConversationContext context) {
        super(context);
        this.itemsPrompt = itemsPrompt;
    }

    private final int size = 3;

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public String getTitle(final ConversationContext context) {
        return Lang.get("stageEditorBrewPotions");
    }

    @Override
    public ChatColor getNumberColor(final ConversationContext context, final int number) {
        return switch (number) {
            case 1 -> ChatColor.BLUE;
            case 2 -> ChatColor.RED;
            case 3 -> ChatColor.GREEN;
            default -> null;
        };
    }

    @Override
    public String getSelectionText(final ConversationContext context, final int number) {
        return switch (number) {
            case 1 -> ChatColor.YELLOW + Lang.get("stageEditorDeliveryAddItem");
            case 2 -> ChatColor.RED + Lang.get("clear");
            case 3 -> ChatColor.GREEN + Lang.get("done");
            default -> null;
        };
    }

    @Override
    @SuppressWarnings("unchecked")
    public String getAdditionalText(final ConversationContext context, final int number) {
        switch (number) {
            case 1:
                if (context.getSessionData(itemsPrompt.pref + CK.S_BREW_ITEMS) == null) {
                    return ChatColor.GRAY + "(" + Lang.get("noneSet") + ")";
                } else {
                    final StringBuilder text = new StringBuilder();
                    final List<ItemStack> brewItems = (List<ItemStack>) context.getSessionData(itemsPrompt.pref + CK.S_BREW_ITEMS);
                    if (brewItems != null) {
                        for (final ItemStack is : brewItems) {
                            text.append("\n").append(ChatColor.GRAY).append("     - ")
                                    .append(ItemUtil.getDisplayString(is));
                        }
                    }
                    return text.toString();
                }
            case 2:
            case 3:
                return "";
            default:
                return null;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public @NotNull String getBasicPromptText(final ConversationContext context) {
        // Check/add newly made item
        if (context.getSessionData("tempStack") != null) {
            if (context.getSessionData(itemsPrompt.pref + CK.S_BREW_ITEMS) != null) {
                final List<ItemStack> items = (List<ItemStack>) context.getSessionData(itemsPrompt.pref + CK.S_BREW_ITEMS);
                if (items != null) {
                    items.add((ItemStack) context.getSessionData("tempStack"));
                    context.setSessionData(itemsPrompt.pref + CK.S_BREW_ITEMS, items);
                }
            } else {
                final LinkedList<ItemStack> items = new LinkedList<>();
                items.add((ItemStack) context.getSessionData("tempStack"));
                context.setSessionData(itemsPrompt.pref + CK.S_BREW_ITEMS, items);
            }
            ItemStackPrompt.clearSessionData(context);
        }

        if (context.getPlugin() != null) {
            final QuestsEditorPostOpenNumericPromptEvent event
                    = new QuestsEditorPostOpenNumericPromptEvent(context, this);
            context.getPlugin().getServer().getPluginManager().callEvent(event);
        }

        final StringBuilder text = new StringBuilder(ChatColor.GOLD + "- " + getTitle(context) + " -");
        for (int i = 1; i <= size; i++) {
            text.append("\n").append(getNumberColor(context, i)).append(ChatColor.BOLD).append(i).append(ChatColor.RESET).append(" - ").append(getSelectionText(context, i)).append(" ").append(getAdditionalText(context, i));
        }
        return text.toString();
    }

    @Override
    protected Prompt acceptValidatedInput(final @NotNull ConversationContext context, final Number input) {
        switch (input.intValue()) {
            case 1:
                return new ItemStackPrompt(context, ItemsBrewListPrompt.this);
            case 2:
                context.getForWhom().sendRawMessage(ChatColor.YELLOW + Lang.get("stageEditorObjectiveCleared"));
                context.setSessionData(itemsPrompt.pref + CK.S_BREW_ITEMS, null);
                return new ItemsBrewListPrompt(context);
            default:
                return new ItemsPrompt(itemsPrompt.stageNum, context);
        }
    }
}
