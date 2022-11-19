/*
 * Copyright (c) 2014 PikaMug and contributors. All rights reserved.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN
 * NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
 * OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package me.blackvein.quests.convo.quests.objectives;

import me.blackvein.quests.convo.quests.QuestsEditorNumericPrompt;
import me.blackvein.quests.convo.quests.QuestsEditorStringPrompt;
import me.blackvein.quests.convo.quests.objectives.blocks.BlocksBreakListPrompt;
import me.blackvein.quests.convo.quests.objectives.blocks.BlocksDamageListPrompt;
import me.blackvein.quests.convo.quests.objectives.blocks.BlocksPlaceListPrompt;
import me.blackvein.quests.convo.quests.objectives.blocks.BlocksUseListPrompt;
import me.blackvein.quests.convo.quests.stages.StageMainPrompt;
import me.blackvein.quests.events.editor.quests.QuestsEditorPostOpenNumericPromptEvent;
import me.blackvein.quests.events.editor.quests.QuestsEditorPostOpenStringPromptEvent;
import me.blackvein.quests.util.CK;
import me.blackvein.quests.util.ItemUtil;
import me.blackvein.quests.util.Lang;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;

public class BlocksPrompt extends QuestsEditorNumericPrompt {
    private final int stageNum;
    private final String pref;
    private final int size = 5;

    public BlocksPrompt(final int stageNum, final ConversationContext context) {
        super(context);
        this.stageNum = stageNum;
        this.pref = "stage" + stageNum;
    }
    

    
    @Override
    public int getSize() {
        return size;
    }
    
    @Override
    public String getTitle(final ConversationContext context) {
        return Lang.get("stageEditorBlocks");
    }
    
    @Override
    public ChatColor getNumberColor(final ConversationContext context, final int number) {
        return switch (number) {
            case 1, 2, 3, 4 -> ChatColor.BLUE;
            case 5 -> ChatColor.GREEN;
            default -> null;
        };
    }
    
    @Override
    public String getSelectionText(final ConversationContext context, final int number) {
        return switch (number) {
            case 1 -> ChatColor.YELLOW + Lang.get("stageEditorBreakBlocks");
            case 2 -> ChatColor.YELLOW + Lang.get("stageEditorDamageBlocks");
            case 3 -> ChatColor.YELLOW + Lang.get("stageEditorPlaceBlocks");
            case 4 -> ChatColor.YELLOW + Lang.get("stageEditorUseBlocks");
            case 5 -> ChatColor.GREEN + Lang.get("done");
            default -> null;
        };
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public String getAdditionalText(final ConversationContext context, final int number) {
        switch(number) {
        case 1:
            if (context.getSessionData(pref + CK.S_BREAK_NAMES) == null) {
                return ChatColor.GRAY + "(" + Lang.get("noneSet") + ")";
            } else {
                final StringBuilder text = new StringBuilder();
                final LinkedList<String> names = (LinkedList<String>) context.getSessionData(pref + CK.S_BREAK_NAMES);
                final LinkedList<Integer> amounts
                        = (LinkedList<Integer>) context.getSessionData(pref + CK.S_BREAK_AMOUNTS);
                if (names != null && amounts != null) {
                    for (int i = 0; i < names.size(); i++) {
                        text.append("\n").append(ChatColor.GRAY).append("     - ").append(ChatColor.BLUE)
                                .append(ItemUtil.getPrettyItemName(names.get(i))).append(ChatColor.GRAY).append(" x ")
                                .append(ChatColor.DARK_AQUA).append(amounts.get(i));
                    }
                }
                return text.toString();
            }
        case 2:
            if (context.getSessionData(pref + CK.S_DAMAGE_NAMES) == null) {
                return ChatColor.GRAY + "(" + Lang.get("noneSet") + ")";
            } else {
                final StringBuilder text = new StringBuilder();
                final LinkedList<String> names = (LinkedList<String>) context.getSessionData(pref + CK.S_DAMAGE_NAMES);
                final LinkedList<Integer> amounts
                        = (LinkedList<Integer>) context.getSessionData(pref + CK.S_DAMAGE_AMOUNTS);
                if (names != null && amounts != null) {
                    for (int i = 0; i < names.size(); i++) {
                        text.append("\n").append(ChatColor.GRAY).append("     - ").append(ChatColor.BLUE)
                                .append(ItemUtil.getPrettyItemName(names.get(i))).append(ChatColor.GRAY).append(" x ")
                                .append(ChatColor.DARK_AQUA).append(amounts.get(i));
                    }
                }
                return text.toString();
            }
        case 3:
            if (context.getSessionData(pref + CK.S_PLACE_NAMES) == null) {
                return ChatColor.GRAY + "(" + Lang.get("noneSet") + ")";
            } else {
                final StringBuilder text = new StringBuilder();
                final LinkedList<String> names = (LinkedList<String>) context.getSessionData(pref + CK.S_PLACE_NAMES);
                final LinkedList<Integer> amounts
                        = (LinkedList<Integer>) context.getSessionData(pref + CK.S_PLACE_AMOUNTS);
                if (names != null && amounts != null) {
                    for (int i = 0; i < names.size(); i++) {
                        text.append("\n").append(ChatColor.GRAY).append("     - ").append(ChatColor.BLUE)
                                .append(ItemUtil.getPrettyItemName(names.get(i))).append(ChatColor.GRAY).append(" x ")
                                .append(ChatColor.DARK_AQUA).append(amounts.get(i));
                    }
                }
                return text.toString();
            }
        case 4:
            if (context.getSessionData(pref + CK.S_USE_NAMES) == null) {
                return ChatColor.GRAY + "(" + Lang.get("noneSet") + ")";
            } else {
                final StringBuilder text = new StringBuilder();
                final LinkedList<String> names = (LinkedList<String>) context.getSessionData(pref + CK.S_USE_NAMES);
                final LinkedList<Integer> amounts
                        = (LinkedList<Integer>) context.getSessionData(pref + CK.S_USE_AMOUNTS);
                if (names != null && amounts != null) {
                    for (int i = 0; i < names.size(); i++) {
                        text.append("\n").append(ChatColor.GRAY).append("     - ").append(ChatColor.BLUE)
                                .append(ItemUtil.getPrettyItemName(names.get(i))).append(ChatColor.GRAY).append(" x ")
                                .append(ChatColor.DARK_AQUA).append(amounts.get(i));
                    }
                }
                return text.toString();
            }
        case 5:
            return "";
        default:
            return null;
        }
    }

    @Override
    public @NotNull String getBasicPromptText(final ConversationContext context) {
        context.setSessionData(pref, Boolean.TRUE);

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

    @Override
    protected Prompt acceptValidatedInput(final @NotNull ConversationContext context, final Number input) {
        switch(input.intValue()) {
        case 1:
            return new BlocksBreakListPrompt(this, context);
        case 2:
            return new BlocksDamageListPrompt(this, context);
        case 3:
            return new BlocksPlaceListPrompt(this, context);
        case 4:
            return new BlocksUseListPrompt(this, context);
        case 5:
            try {
                return new StageMainPrompt(stageNum, context);
            } catch (final Exception e) {
                context.getForWhom().sendRawMessage(ChatColor.RED + Lang.get("itemCreateCriticalError"));
                return Prompt.END_OF_CONVERSATION;
            }
        default:
            return new BlocksPrompt(stageNum, context);
        }
    }

}
