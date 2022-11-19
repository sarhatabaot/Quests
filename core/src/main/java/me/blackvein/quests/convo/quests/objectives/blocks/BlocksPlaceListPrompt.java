package me.blackvein.quests.convo.quests.objectives.blocks;

import me.blackvein.quests.convo.quests.QuestsEditorNumericPrompt;
import me.blackvein.quests.convo.quests.objectives.BlocksPrompt;
import me.blackvein.quests.events.editor.quests.QuestsEditorPostOpenNumericPromptEvent;
import me.blackvein.quests.util.CK;
import me.blackvein.quests.util.ItemUtil;
import me.blackvein.quests.util.Lang;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

/**
 * @author sarhatabaot
 */
public class BlocksPlaceListPrompt extends QuestsEditorNumericPrompt {

    private final BlocksPrompt blocksPrompt;
    private final int size = 5;

    public BlocksPlaceListPrompt(final BlocksPrompt blocksPrompt, final ConversationContext context) {
        super(context);
        this.blocksPrompt = blocksPrompt;
    }



    @Override
    public int getSize() {
        return size;
    }

    @Override
    public String getTitle(final ConversationContext context) {
        return Lang.get("stageEditorPlaceBlocks");
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
            case 1 -> ChatColor.YELLOW + Lang.get("stageEditorSetBlockNames");
            case 2 -> ChatColor.YELLOW + Lang.get("stageEditorSetBlockAmounts");
            case 3 -> ChatColor.YELLOW + Lang.get("stageEditorSetBlockDurability");
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
                if (context.getSessionData(blocksPrompt.pref() + CK.S_PLACE_NAMES) != null) {
                    final StringBuilder text = new StringBuilder();
                    final List<String> placeNames = (List<String>) context.getSessionData(blocksPrompt.pref() + CK.S_PLACE_NAMES);
                    if (placeNames != null) {
                        for (final String s : placeNames) {
                            text.append("\n").append(ChatColor.GRAY).append("     - ").append(ChatColor.AQUA)
                                    .append(ItemUtil.getPrettyItemName(s));
                        }
                    }
                    return text.toString();
                } else {
                    return "";
                }
            case 2:
                if (context.getSessionData(blocksPrompt.pref() + CK.S_PLACE_AMOUNTS) != null) {
                    final StringBuilder text = new StringBuilder();
                    final List<Integer> placeAmounts
                            = (List<Integer>) context.getSessionData(blocksPrompt.pref() + CK.S_PLACE_AMOUNTS);
                    if (placeAmounts != null) {
                        for (final Integer i : placeAmounts) {
                            text.append("\n").append(ChatColor.GRAY).append("     - ").append(ChatColor.AQUA).append(i);
                        }
                    }
                    return text.toString();
                } else {
                    return "";
                }
            case 3:
                if (context.getSessionData(blocksPrompt.pref() + CK.S_PLACE_DURABILITY) != null) {
                    final StringBuilder text = new StringBuilder();
                    final List<Short> placeDurability
                            = (List<Short>) context.getSessionData(blocksPrompt.pref() + CK.S_PLACE_DURABILITY);
                    if (placeDurability != null) {
                        for (final Short s : placeDurability) {
                            text.append("\n").append(ChatColor.GRAY).append("     - ").append(ChatColor.AQUA).append(s);
                        }
                    }
                    return text.toString();
                } else {
                    return "";
                }
            case 4, 5:
                return "";
            default:
                return null;
        }
    }

    @Override
    public @NotNull String getBasicPromptText(final @NotNull ConversationContext context) {
        if (context.getPlugin() != null) {
            final QuestsEditorPostOpenNumericPromptEvent event
                    = new QuestsEditorPostOpenNumericPromptEvent(context, this);
            context.getPlugin().getServer().getPluginManager().callEvent(event);
        }

        final StringBuilder text = new StringBuilder(ChatColor.GOLD + "- " + getTitle(context) + " -");
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
                return new BlockPlaceNamesPrompt(blocksPrompt, context);
            case 2:
                return new BlockPlaceAmountsPrompt(blocksPrompt, context);
            case 3:
                return new BlockPlaceDurabilityPrompt(blocksPrompt, context);
            case 4:
                context.getForWhom().sendRawMessage(ChatColor.YELLOW + Lang.get("stageEditorObjectiveCleared"));
                context.setSessionData(blocksPrompt.pref() + CK.S_PLACE_NAMES, null);
                context.setSessionData(blocksPrompt.pref() + CK.S_PLACE_AMOUNTS, null);
                context.setSessionData(blocksPrompt.pref() + CK.S_PLACE_DURABILITY, null);
                return new BlocksPlaceListPrompt(blocksPrompt,context);
            case 5:
                final int one;
                final int two;
                final List<Integer> names = (List<Integer>) context.getSessionData(blocksPrompt.pref() + CK.S_PLACE_NAMES);
                final List<Integer> amounts = (List<Integer>) context.getSessionData(blocksPrompt.pref() + CK.S_PLACE_AMOUNTS);
                if (names != null) {
                    one = names.size();
                } else {
                    one = 0;
                }
                if (amounts != null) {
                    two = amounts.size();
                } else {
                    two = 0;
                }
                if (one == two) {
                    final int missing;
                    LinkedList<Short> durability
                            = (LinkedList<Short>) context.getSessionData(blocksPrompt.pref() + CK.S_PLACE_DURABILITY);
                    if (durability != null) {
                        missing = one - durability.size();
                    } else {
                        missing = one;
                        durability = new LinkedList<>();
                    }
                    for (int i = 0; i < missing; i++) {
                        durability.add((short) 0);
                    }
                    context.setSessionData(blocksPrompt.pref() + CK.S_PLACE_DURABILITY, durability);
                    return new BlocksPrompt(blocksPrompt.stageNum(), context);
                } else {
                    context.getForWhom().sendRawMessage(ChatColor.RED + Lang.get("listsNotSameSize"));
                    return new BlocksPlaceListPrompt(blocksPrompt,context);
                }
            default:
                return new BlocksPrompt(blocksPrompt.stageNum(), context);
        }
    }
}
