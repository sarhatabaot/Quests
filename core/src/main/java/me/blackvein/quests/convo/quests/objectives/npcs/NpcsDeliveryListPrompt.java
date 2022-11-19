package me.blackvein.quests.convo.quests.objectives.npcs;

import me.blackvein.quests.convo.generic.ItemStackPrompt;
import me.blackvein.quests.convo.quests.QuestsEditorNumericPrompt;
import me.blackvein.quests.convo.quests.objectives.NpcsPrompt;
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
import java.util.UUID;

/**
 * @author sarhatabaot
 */
public class NpcsDeliveryListPrompt extends QuestsEditorNumericPrompt {

    private final NpcsPrompt npcsPrompt;

    public NpcsDeliveryListPrompt(final NpcsPrompt npcsPrompt, final ConversationContext context) {
        super(context);
        this.npcsPrompt = npcsPrompt;
    }

    private final int size = 5;

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public String getTitle(final ConversationContext context) {
        return Lang.get("stageEditorDeliverItems");
    }

    @Override
    public ChatColor getNumberColor(final ConversationContext context, final int number) {
        return switch (number) {
            case 1, 2, 3 -> ChatColor.BLUE;
            case 4 -> ChatColor.RED;
            case 5 -> ChatColor.GREEN;
            default -> null;
        };
    }

    @Override
    public String getSelectionText(final ConversationContext context, final int number) {
        return switch (number) {
            case 1 -> ChatColor.YELLOW + Lang.get("stageEditorDeliveryAddItem");
            case 2 -> ChatColor.YELLOW + Lang.get("stageEditorNPCUniqueIds");
            case 3 -> ChatColor.YELLOW + Lang.get("stageEditorDeliveryMessages");
            case 4 -> ChatColor.RED + Lang.get("clear");
            case 5 -> ChatColor.GREEN + Lang.get("done");
            default -> null;
        };
    }

    @Override
    @SuppressWarnings("unchecked")
    public String getAdditionalText(final ConversationContext context, final int number) {
        switch (number) {
            case 1:
                if (context.getSessionData(npcsPrompt.pref + CK.S_DELIVERY_ITEMS) == null) {
                    return ChatColor.GRAY + "(" + Lang.get("noneSet") + ")";
                } else {
                    final StringBuilder text = new StringBuilder();
                    final List<ItemStack> deliveryItems
                            = (List<ItemStack>) context.getSessionData(npcsPrompt.pref + CK.S_DELIVERY_ITEMS);
                    if (deliveryItems != null) {
                        for (final ItemStack is : deliveryItems) {
                            text.append("\n").append(ChatColor.GRAY).append("     - ")
                                    .append(ItemUtil.getDisplayString(is));
                        }
                    }
                    return text.toString();
                }
            case 2:
                if (context.getSessionData(npcsPrompt.pref + CK.S_DELIVERY_NPCS) == null) {
                    return ChatColor.GRAY + "(" + Lang.get("noneSet") + ")";
                } else {
                    final StringBuilder text = new StringBuilder();
                    final List<String> deliveryNpcs = (List<String>) context.getSessionData(npcsPrompt.pref + CK.S_DELIVERY_NPCS);
                    if (deliveryNpcs != null) {
                        for (final String s : deliveryNpcs) {
                            final UUID uuid = UUID.fromString(s);
                            text.append("\n").append(ChatColor.GRAY).append("     - ").append(ChatColor.AQUA)
                                    .append(npcsPrompt.plugin.getDependencies().getNPCName(uuid)).append(ChatColor.GRAY)
                                    .append(" (").append(ChatColor.BLUE).append(s).append(ChatColor.GRAY).append(")");
                        }
                    }
                    return text.toString();
                }
            case 3:
                if (context.getSessionData(npcsPrompt.pref + CK.S_DELIVERY_MESSAGES) == null) {
                    return ChatColor.GRAY + "(" + Lang.get("noneSet") + ")";
                } else {
                    final StringBuilder text = new StringBuilder();
                    final List<String> deliveryMessages
                            = (List<String>) context.getSessionData(npcsPrompt.pref + CK.S_DELIVERY_MESSAGES);
                    if (deliveryMessages != null) {
                        for (final String s : deliveryMessages) {
                            text.append("\n").append(ChatColor.GRAY).append("     - ").append(ChatColor.AQUA)
                                    .append("\"").append(s).append("\"");
                        }
                    }
                    return text.toString();
                }
            case 4:
            case 5:
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
            if (context.getSessionData(npcsPrompt.pref + CK.S_DELIVERY_ITEMS) != null) {
                final List<ItemStack> itemRew
                        = (List<ItemStack>) context.getSessionData(npcsPrompt.pref + CK.S_DELIVERY_ITEMS);
                if (itemRew != null) {
                    itemRew.add((ItemStack) context.getSessionData("tempStack"));
                }
                context.setSessionData(npcsPrompt.pref + CK.S_DELIVERY_ITEMS, itemRew);
            } else {
                final LinkedList<ItemStack> itemRews = new LinkedList<>();
                itemRews.add((ItemStack) context.getSessionData("tempStack"));
                context.setSessionData(npcsPrompt.pref + CK.S_DELIVERY_ITEMS, itemRews);
            }
            ItemStackPrompt.clearSessionData(context);
        }

        if (context.getPlugin() != null) {
            final QuestsEditorPostOpenNumericPromptEvent event
                    = new QuestsEditorPostOpenNumericPromptEvent(context, this);
            context.getPlugin().getServer().getPluginManager().callEvent(event);
        }

        final StringBuilder text = new StringBuilder(ChatColor.AQUA + "- " + getTitle(context) + " -");
        for (int i = 1; i <= size; i++) {
            text.append("\n").append(getNumberColor(context, i)).append(ChatColor.BOLD).append(i)
                    .append(ChatColor.RESET).append(" - ").append(getSelectionText(context, i)).append(" ")
                    .append(getAdditionalText(context, i));
        }
        return text.toString();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Prompt acceptValidatedInput(final @NotNull ConversationContext context, final Number input) {
        switch (input.intValue()) {
            case 1:
                return new ItemStackPrompt(context, NpcsDeliveryListPrompt.this);
            case 2:
                return new NpcDeliveryNpcsPrompt(NpcsPrompt.this, context);
            case 3:
                return new NpcDeliveryMessagesPrompt(NpcsPrompt.this, context);
            case 4:
                context.getForWhom().sendRawMessage(ChatColor.YELLOW + Lang.get("cleared"));
                context.setSessionData(npcsPrompt.pref + CK.S_DELIVERY_ITEMS, null);
                context.setSessionData(npcsPrompt.pref + CK.S_DELIVERY_NPCS, null);
                context.setSessionData(npcsPrompt.pref + CK.S_DELIVERY_MESSAGES, null);
                return new NpcsDeliveryListPrompt(context);
            case 5:
                final int one;
                final int two;
                final List<ItemStack> items = (List<ItemStack>) context.getSessionData(npcsPrompt.pref + CK.S_DELIVERY_ITEMS);
                final List<UUID> npcs = (List<UUID>) context.getSessionData(npcsPrompt.pref + CK.S_DELIVERY_NPCS);
                if (items != null) {
                    one = items.size();
                } else {
                    one = 0;
                }
                if (npcs != null) {
                    two = npcs.size();
                } else {
                    two = 0;
                }
                if (one == two) {
                    if (context.getSessionData(npcsPrompt.pref + CK.S_DELIVERY_MESSAGES) == null && one != 0) {
                        context.getForWhom().sendRawMessage(ChatColor.RED + Lang.get("stageEditorNoDeliveryMessage"));
                        return new NpcsDeliveryListPrompt(context);
                    } else {
                        return new NpcsPrompt(npcsPrompt.stageNum, context);
                    }
                } else {
                    context.getForWhom().sendRawMessage(ChatColor.RED + Lang.get("listsNotSameSize"));
                    return new NpcsDeliveryListPrompt(context);
                }
            default:
                return new NpcsPrompt(npcsPrompt.stageNum, context);
        }
    }
}
