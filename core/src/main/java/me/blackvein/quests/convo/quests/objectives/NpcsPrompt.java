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

import me.blackvein.quests.Quests;
import me.blackvein.quests.convo.quests.QuestsEditorNumericPrompt;
import me.blackvein.quests.convo.quests.QuestsEditorStringPrompt;
import me.blackvein.quests.convo.quests.objectives.npcs.NpcsDeliveryListPrompt;
import me.blackvein.quests.convo.quests.objectives.npcs.NpcsIdsToTalkToPrompt;
import me.blackvein.quests.convo.quests.objectives.npcs.NpcsKillListPrompt;
import me.blackvein.quests.convo.quests.stages.StageMainPrompt;
import me.blackvein.quests.events.editor.quests.QuestsEditorPostOpenNumericPromptEvent;
import me.blackvein.quests.events.editor.quests.QuestsEditorPostOpenStringPromptEvent;
import me.blackvein.quests.util.CK;
import me.blackvein.quests.util.ItemUtil;
import me.blackvein.quests.util.Lang;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.Set;
import java.util.UUID;

public class NpcsPrompt extends QuestsEditorNumericPrompt {
    private final Quests plugin;
    private final int stageNum;
    private final String pref;
    private final int size = 4;

    public NpcsPrompt(final int stageNum, final ConversationContext context) {
        super(context);
        this.plugin = (Quests)context.getPlugin();
        this.stageNum = stageNum;
        this.pref = "stage" + stageNum;
    }

    
    @Override
    public int getSize() {
        return size;
    }
    
    @Override
    public String getTitle(final ConversationContext context) {
        return Lang.get("stageEditorNPCs");
    }
    
    @Override
    public ChatColor getNumberColor(final ConversationContext context, final int number) {
        return switch (number) {
            case 1, 2, 3 -> ChatColor.BLUE;
            case 4 -> ChatColor.GREEN;
            default -> null;
        };
    }
    
    @Override
    public String getSelectionText(final ConversationContext context, final int number) {
        return switch (number) {
            case 1 -> ChatColor.YELLOW + Lang.get("stageEditorDeliverItems");
            case 2 -> ChatColor.YELLOW + Lang.get("stageEditorTalkToNPCs");
            case 3 -> ChatColor.YELLOW + Lang.get("stageEditorKillNPCs");
            case 4 -> ChatColor.GREEN + Lang.get("done");
            default -> null;
        };
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public String getAdditionalText(final ConversationContext context, final int number) {
        switch(number) {
        case 1:
            if (plugin.getDependencies().getCitizens() != null || plugin.getDependencies().getZnpcs() != null) {
                if (context.getSessionData(pref + CK.S_DELIVERY_ITEMS) == null) {
                    return ChatColor.GRAY + "(" + Lang.get("noneSet") + ")";
                } else {
                    final StringBuilder text = new StringBuilder();
                    final LinkedList<String> npcs
                            = (LinkedList<String>) context.getSessionData(pref + CK.S_DELIVERY_NPCS);
                    final LinkedList<ItemStack> items 
                            = (LinkedList<ItemStack>) context.getSessionData(pref + CK.S_DELIVERY_ITEMS);
                    if (npcs != null && items != null) {
                        for (int i = 0; i < npcs.size(); i++) {
                            text.append("\n").append(ChatColor.GRAY).append("     - ").append(ChatColor.BLUE)
                                    .append(ItemUtil.getName(items.get(i))).append(ChatColor.GRAY).append(" x ")
                                    .append(ChatColor.AQUA).append(items.get(i).getAmount()).append(ChatColor.GRAY)
                                    .append(" ").append(Lang.get("to")).append(" ").append(ChatColor.BLUE)
                                    .append(plugin.getDependencies().getNPCName(UUID.fromString(npcs.get(i))));
                        }
                    }
                    return text.toString();
                }
            } else {
                return ChatColor.GRAY + " (" + Lang.get("notInstalled") + ")";
            }
        case 2:
            if (plugin.getDependencies().getCitizens() != null || plugin.getDependencies().getZnpcs() != null) {
                if (context.getSessionData(pref + CK.S_NPCS_TO_TALK_TO) == null) {
                    return ChatColor.GRAY + "(" + Lang.get("noneSet") + ")";
                } else {
                    final StringBuilder text = new StringBuilder();
                    final LinkedList<String> npcs
                            = (LinkedList<String>) context.getSessionData(pref + CK.S_NPCS_TO_TALK_TO);
                    if (npcs != null) {
                        for (final String npc : npcs) {
                            text.append("\n").append(ChatColor.GRAY).append("     - ").append(ChatColor.BLUE)
                                    .append(plugin.getDependencies().getNPCName(UUID.fromString(npc)));
                        }
                    }
                    return text.toString();
                }
            } else {
                return ChatColor.GRAY + "(" + Lang.get("notInstalled") + ")";
            }
        case 3:
            if (plugin.getDependencies().getCitizens() != null || plugin.getDependencies().getZnpcs() != null) {
                if (context.getSessionData(pref + CK.S_NPCS_TO_KILL) == null) {
                    return ChatColor.GRAY + "(" + Lang.get("noneSet") + ")";
                } else {
                    final StringBuilder text = new StringBuilder();
                    final LinkedList<String> npcs
                            = (LinkedList<String>) context.getSessionData(pref + CK.S_NPCS_TO_KILL);
                    final LinkedList<Integer> amounts 
                            = (LinkedList<Integer>) context.getSessionData(pref + CK.S_NPCS_TO_KILL_AMOUNTS);
                    if (npcs != null && amounts != null) {
                        for (int i = 0; i < npcs.size(); i++) {
                            text.append("\n").append(ChatColor.GRAY).append("     - ").append(ChatColor.BLUE)
                                    .append(plugin.getDependencies().getNPCName(UUID.fromString(npcs.get(i))))
                                    .append(ChatColor.GRAY).append(" x ").append(ChatColor.AQUA).append(amounts.get(i));
                        }
                    }
                    return text.toString();
                }
            } else {
                return ChatColor.GRAY + "(" + Lang.get("notInstalled") + ")";
            }
        case 4:
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
            if (plugin.getDependencies().getCitizens() != null || plugin.getDependencies().getZnpcs() != null) {
                return new NpcsDeliveryListPrompt(this, context);
            } else {
                context.getForWhom().sendRawMessage(ChatColor.RED + Lang.get("stageEditorNoCitizens"));
                return new StageMainPrompt(stageNum, context);
            }
        case 2:
            if (plugin.getDependencies().getCitizens() != null || plugin.getDependencies().getZnpcs() != null) {
                return new NpcsIdsToTalkToPrompt(this, context);
            } else {
                context.getForWhom().sendRawMessage(ChatColor.RED + Lang.get("stageEditorNoCitizens"));
                return new StageMainPrompt(stageNum, context);
            }
        case 3:
            if (plugin.getDependencies().getCitizens() != null || plugin.getDependencies().getZnpcs() != null) {
                return new NpcsKillListPrompt(this, context);
            } else {
                context.getForWhom().sendRawMessage(ChatColor.RED + Lang.get("stageEditorNoCitizens"));
                return new StageMainPrompt(stageNum, context);
            }
        case 4:
            try {
                return new StageMainPrompt(stageNum, context);
            } catch (final Exception e) {
                context.getForWhom().sendRawMessage(ChatColor.RED + Lang.get("itemCreateCriticalError"));
                return Prompt.END_OF_CONVERSATION;
            }
        default:
            return new NpcsPrompt(stageNum, context);
        }
    }

}
