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
import me.blackvein.quests.convo.quests.objectives.mobs.MobsCowsPrompt;
import me.blackvein.quests.convo.quests.objectives.mobs.MobsFishPrompt;
import me.blackvein.quests.convo.quests.objectives.mobs.MobsKillListPrompt;
import me.blackvein.quests.convo.quests.objectives.mobs.MobsShearListPrompt;
import me.blackvein.quests.convo.quests.objectives.mobs.MobsTameListPrompt;
import me.blackvein.quests.convo.quests.stages.StageMainPrompt;
import me.blackvein.quests.events.editor.quests.QuestsEditorPostOpenNumericPromptEvent;
import me.blackvein.quests.util.CK;
import me.blackvein.quests.util.Lang;
import me.blackvein.quests.util.MiscUtil;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.Objects;

public class MobsPrompt extends QuestsEditorNumericPrompt {
    private final Quests plugin;
    private final int stageNum;
    private final String pref;

    private final int size = 6;

    public MobsPrompt(final int stageNum, final ConversationContext context) {
        super(context);
        this.plugin = (Quests) context.getPlugin();
        this.stageNum = stageNum;
        this.pref = "stage" + stageNum;
    }


    @Override
    public int getSize() {
        return size;
    }

    @Override
    public String getTitle(final ConversationContext context) {
        return Lang.get("stageEditorMobs");
    }

    @Override
    public ChatColor getNumberColor(final ConversationContext context, final int number) {
        return switch (number) {
            case 1, 2, 3, 4, 5 -> ChatColor.BLUE;
            case 6 -> ChatColor.GREEN;
            default -> null;
        };
    }

    @Override
    public String getSelectionText(final ConversationContext context, final int number) {
        return switch (number) {
            case 1 -> ChatColor.YELLOW + Lang.get("stageEditorKillMobs");
            case 2 -> ChatColor.YELLOW + Lang.get("stageEditorTameMobs");
            case 3 -> ChatColor.YELLOW + Lang.get("stageEditorCatchFish");
            case 4 -> ChatColor.YELLOW + Lang.get("stageEditorMilkCows");
            case 5 -> ChatColor.YELLOW + Lang.get("stageEditorShearSheep");
            case 6 -> ChatColor.GREEN + Lang.get("done");
            default -> null;
        };
    }

    @Override
    @SuppressWarnings("unchecked")
    public String getAdditionalText(final ConversationContext context, final int number) {
        switch (number) {
            case 1:
                if (context.getSessionData(pref + CK.S_MOB_TYPES) == null) {
                    return ChatColor.GRAY + "(" + Lang.get("noneSet") + ")";
                } else {
                    final StringBuilder text = new StringBuilder();
                    final LinkedList<String> mobs = (LinkedList<String>) context.getSessionData(pref + CK.S_MOB_TYPES);
                    final LinkedList<Integer> amounts = (LinkedList<Integer>) context.getSessionData(pref + CK.S_MOB_AMOUNTS);
                    if (mobs != null && amounts != null) {
                        if (context.getSessionData(pref + CK.S_MOB_KILL_LOCATIONS) == null) {
                            for (int i = 0; i < mobs.size(); i++) {
                                if (MiscUtil.getProperMobType(mobs.get(i)) != null) {
                                    text.append("\n").append(ChatColor.GRAY).append("     - ").append(ChatColor.AQUA)
                                            .append(MiscUtil.getPrettyMobName(Objects.requireNonNull(MiscUtil
                                                    .getProperMobType(mobs.get(i))))).append(ChatColor.GRAY).append(" x ")
                                            .append(ChatColor.DARK_AQUA).append(amounts.get(i));
                                }
                            }
                        } else {
                            final LinkedList<String> locations
                                    = (LinkedList<String>) context.getSessionData(pref + CK.S_MOB_KILL_LOCATIONS);
                            final LinkedList<Integer> radii
                                    = (LinkedList<Integer>) context.getSessionData(pref + CK.S_MOB_KILL_LOCATIONS_RADIUS);
                            final LinkedList<String> names
                                    = (LinkedList<String>) context.getSessionData(pref + CK.S_MOB_KILL_LOCATIONS_NAMES);
                            if (locations != null && radii != null && names != null) {
                                for (int i = 0; i < mobs.size(); i++) {
                                    String msg = Lang.get("blocksWithin");
                                    msg = msg.replace("<amount>", ChatColor.DARK_PURPLE + "" + radii.get(i) + ChatColor.GRAY);
                                    text.append("\n").append(ChatColor.GRAY).append("     - ").append(ChatColor.BLUE)
                                            .append(MiscUtil.getPrettyMobName(Objects.requireNonNull(MiscUtil
                                                    .getProperMobType(mobs.get(i))))).append(ChatColor.GRAY).append(" x ")
                                            .append(ChatColor.DARK_AQUA).append(amounts.get(i)).append(ChatColor.GRAY).append(msg)
                                            .append(ChatColor.YELLOW).append(names.get(i)).append(" (").append(locations.get(i))
                                            .append(")");
                                }
                            }
                        }
                    }
                    return text.toString();
                }
            case 2:
                if (context.getSessionData(pref + CK.S_TAME_TYPES) == null) {
                    return ChatColor.GRAY + "(" + Lang.get("noneSet") + ")";
                } else {
                    final StringBuilder text = new StringBuilder();
                    final LinkedList<String> mobs = (LinkedList<String>) context.getSessionData(pref + CK.S_TAME_TYPES);
                    final LinkedList<Integer> amounts = (LinkedList<Integer>) context.getSessionData(pref + CK.S_TAME_AMOUNTS);
                    if (mobs != null && amounts != null) {
                        for (int i = 0; i < mobs.size(); i++) {
                            text.append("\n").append(ChatColor.GRAY).append("     - ").append(ChatColor.BLUE)
                                    .append(mobs.get(i)).append(ChatColor.GRAY).append(" x ").append(ChatColor.AQUA)
                                    .append(amounts.get(i));
                        }
                    }
                    return text.toString();
                }
            case 3:
                if (context.getSessionData(pref + CK.S_FISH) == null) {
                    return ChatColor.GRAY + "(" + Lang.get("noneSet") + ")";
                } else {
                    final Integer fish = (Integer) context.getSessionData(pref + CK.S_FISH);
                    return ChatColor.GRAY + "(" + ChatColor.AQUA + fish + " " + Lang.get("stageEditorFish")
                            + ChatColor.GRAY + ")";
                }
            case 4:
                if (context.getSessionData(pref + CK.S_COW_MILK) == null) {
                    return ChatColor.GRAY + "(" + Lang.get("noneSet") + ")";
                } else {
                    final Integer cows = (Integer) context.getSessionData(pref + CK.S_COW_MILK);
                    return ChatColor.GRAY + "(" + ChatColor.AQUA + cows + " " + Lang.get("stageEditorCows")
                            + ChatColor.GRAY + ")";
                }
            case 5:
                if (context.getSessionData(pref + CK.S_SHEAR_COLORS) == null) {
                    return ChatColor.GRAY + "(" + Lang.get("noneSet") + ")";
                } else {
                    final StringBuilder text = new StringBuilder();
                    final LinkedList<String> colors = (LinkedList<String>) context.getSessionData(pref + CK.S_SHEAR_COLORS);
                    final LinkedList<Integer> amounts
                            = (LinkedList<Integer>) context.getSessionData(pref + CK.S_SHEAR_AMOUNTS);
                    if (colors != null && amounts != null) {
                        for (int i = 0; i < colors.size(); i++) {
                            text.append("\n").append(ChatColor.GRAY).append("     - ").append(ChatColor.BLUE)
                                    .append(colors.get(i)).append(ChatColor.GRAY).append(" x ").append(ChatColor.AQUA)
                                    .append(amounts.get(i));
                        }
                    }
                    return text.toString();
                }
            case 6:
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
        switch (input.intValue()) {
            case 1:
                return new MobsKillListPrompt(this, context);
            case 2:
                return new MobsTameListPrompt(this, context);
            case 3:
                return new MobsFishPrompt(this, context);
            case 4:
                return new MobsCowsPrompt(this, context);
            case 5:
                return new MobsShearListPrompt(this, context);
            case 6:
                try {
                    return new StageMainPrompt(stageNum, context);
                } catch (final Exception e) {
                    context.getForWhom().sendRawMessage(ChatColor.RED + Lang.get("itemCreateCriticalError"));
                    return Prompt.END_OF_CONVERSATION;
                }
            default:
                return new MobsPrompt(stageNum, context);
        }
    }

}
