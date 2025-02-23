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

package me.blackvein.quests.convo.conditions.tasks;

import me.blackvein.quests.Quests;
import me.blackvein.quests.convo.conditions.main.ConditionMainPrompt;
import me.blackvein.quests.convo.quests.QuestsEditorNumericPrompt;
import me.blackvein.quests.convo.quests.QuestsEditorStringPrompt;
import me.blackvein.quests.events.editor.quests.QuestsEditorPostOpenNumericPromptEvent;
import me.blackvein.quests.events.editor.quests.QuestsEditorPostOpenStringPromptEvent;
import me.blackvein.quests.util.CK;
import me.blackvein.quests.util.Lang;
import me.blackvein.quests.util.MiscUtil;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class EntityPrompt extends QuestsEditorNumericPrompt {
    
    private final Quests plugin;
    
    public EntityPrompt(final ConversationContext context) {
        super(context);
        this.plugin = (Quests)context.getPlugin();
    }
    
    private final int size = 3;
    
    @Override
    public int getSize() {
        return size;
    }
    
    @Override
    public String getTitle(final ConversationContext context) {
        return Lang.get("conditionEditorEntity");
    }
    
    @Override
    public ChatColor getNumberColor(final ConversationContext context, final int number) {
        return switch (number) {
            case 1, 2 -> ChatColor.BLUE;
            case 3 -> ChatColor.GREEN;
            default -> null;
        };
    }
    
    @Override
    public String getSelectionText(final ConversationContext context, final int number) {
        return switch (number) {
            case 1 -> ChatColor.YELLOW + Lang.get("conditionEditorRideEntity");
            case 2 -> ChatColor.YELLOW + Lang.get("conditionEditorRideNPC");
            case 3 -> ChatColor.GREEN + Lang.get("done");
            default -> null;
        };
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public String getAdditionalText(final ConversationContext context, final int number) {
        switch(number) {
        case 1:
            if (context.getSessionData(CK.C_WHILE_RIDING_ENTITY) == null) {
                return ChatColor.GRAY + "(" + Lang.get("noneSet") + ")";
            } else {
                final StringBuilder text = new StringBuilder();
                final List<String> whileRidingEntity = (List<String>) context.getSessionData(CK.C_WHILE_RIDING_ENTITY);
                if (whileRidingEntity != null) {
                    for (final String s: whileRidingEntity) {
                        text.append("\n").append(ChatColor.GRAY).append("     - ").append(ChatColor.BLUE)
                                .append(MiscUtil.getProperMobType(s));
                    }
                }
                return text.toString();
            }
        case 2:
            if (plugin.getDependencies().getCitizens() != null || plugin.getDependencies().getZnpcs() != null) {
                if (context.getSessionData(CK.C_WHILE_RIDING_NPC) == null) {
                    return ChatColor.GRAY + "(" + Lang.get("noneSet") + ")";
                } else {
                    final StringBuilder text = new StringBuilder();
                    final List<UUID> whileRidingNpc = (List<UUID>) context.getSessionData(CK.C_WHILE_RIDING_NPC);
                    if (whileRidingNpc != null) {
                        for (final UUID u : whileRidingNpc) {
                            text.append("\n").append(ChatColor.GRAY).append("     - ").append(ChatColor.BLUE)
                                    .append(plugin.getDependencies().getNPCName(u));
                        }
                    }
                    return text.toString();
                }
            } else {
                return ChatColor.GRAY + "(" + Lang.get("notInstalled") + ")";
            }
        case 3:
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
            return new EntitiesPrompt(context);
        case 2:
            return new ConditionNpcsPrompt(context);
        case 3:
            try {
                return new ConditionMainPrompt(context);
            } catch (final Exception e) {
                context.getForWhom().sendRawMessage(ChatColor.RED + Lang.get("itemCreateCriticalError"));
                return Prompt.END_OF_CONVERSATION;
            }
        default:
            return new EntityPrompt(context);
        }
    }
    
    public static class EntitiesPrompt extends QuestsEditorStringPrompt {
        
        public EntitiesPrompt(final ConversationContext context) {
            super(context);
        }

        @Override
        public String getTitle(final ConversationContext context) {
            return Lang.get("conditionEditorEntitiesTitle");
        }

        @Override
        public String getQueryText(final ConversationContext context) {
            return Lang.get("conditionEditorEntitiesPrompt");
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
                if (type.getEntityClass() == null || !Vehicle.class.isAssignableFrom(type.getEntityClass())) {
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
        public Prompt acceptInput(final @NotNull ConversationContext context, final String input) {
            if (input == null) {
                return null;
            }
            if (!input.equalsIgnoreCase(Lang.get("cmdCancel"))) {
                final LinkedList<String> mobTypes = new LinkedList<>();
                for (final String s : input.split(" ")) {
                    if (MiscUtil.getProperMobType(s) != null) {
                        final EntityType type = MiscUtil.getProperMobType(s);
                        if (type != null && type.getEntityClass() != null
                                && Vehicle.class.isAssignableFrom(type.getEntityClass())) {
                            mobTypes.add(s);
                            context.setSessionData(CK.C_WHILE_RIDING_ENTITY, mobTypes);
                        } else {
                            context.getForWhom().sendRawMessage(ChatColor.RED + Lang.get("stageEditorInvalidMob")
                                    .replace("<input>", s));
                            return new EntitiesPrompt(context);
                        }
                    } else {
                        context.getForWhom().sendRawMessage(ChatColor.RED + Lang.get("stageEditorInvalidMob")
                                .replace("<input>", s));
                        return new EntitiesPrompt(context);
                    }
                }
            }
            return new EntityPrompt(context);
        }
    }
    
    public class ConditionNpcsPrompt extends QuestsEditorStringPrompt {
        
        public ConditionNpcsPrompt(final ConversationContext context) {
            super(context);
        }

        @Override
        public String getTitle(final ConversationContext context) {
            return Lang.get("conditionEditorNpcsTitle");
        }

        @Override
        public String getQueryText(final ConversationContext context) {
            return Lang.get("enterNpcUniqueIds");
        }
        
        @Override
        public @NotNull String getPromptText(final ConversationContext context) {
            if (context.getPlugin() != null) {
                final QuestsEditorPostOpenStringPromptEvent event
                        = new QuestsEditorPostOpenStringPromptEvent(context, this);
                context.getPlugin().getServer().getPluginManager().callEvent(event);
            }
            
            if (context.getForWhom() instanceof Player) {
                final Set<UUID> selectingNpcs = plugin.getQuestFactory().getSelectingNpcs();
                selectingNpcs.add(((Player) context.getForWhom()).getUniqueId());
                plugin.getQuestFactory().setSelectingNpcs(selectingNpcs);
                return ChatColor.YELLOW + getQueryText(context) + "\n" + ChatColor.GOLD + Lang.get("npcHint");
            } else {
                return ChatColor.YELLOW + getQueryText(context);
            }
        }

        @Override
        public Prompt acceptInput(final @NotNull ConversationContext context, final String input) {
            if (input == null) {
                return null;
            }
            if (!input.equalsIgnoreCase(Lang.get("cmdCancel"))) {
                final LinkedList<UUID> npcUuids = new LinkedList<>();
                try {
                    for (final String s : input.split(" ")) {
                        try {
                            final UUID u = UUID.fromString(s);
                            if (plugin.getDependencies().getNPCName(u) == null) {
                                context.getForWhom().sendRawMessage(ChatColor.RED + Lang.get("questEditorInvalidNPC"));
                                return new ConditionNpcsPrompt(context);
                            }
                            npcUuids.add(u);
                            context.setSessionData(CK.C_WHILE_RIDING_NPC, npcUuids);
                        } catch (Exception e) {
                            context.getForWhom().sendRawMessage(ChatColor.RED + Lang.get("questEditorInvalidNPC"));
                        }
                        return new ConditionNpcsPrompt(context);
                    }
                } catch (final NumberFormatException e) {
                    context.getForWhom().sendRawMessage(ChatColor.RED 
                            + Lang.get("reqNotANumber").replace("<input>", input));
                    return new ConditionNpcsPrompt(context);
                }
            }
            if (context.getForWhom() instanceof Player) {
                final Set<UUID> selectingNpcs = plugin.getQuestFactory().getSelectingNpcs();
                selectingNpcs.remove(((Player) context.getForWhom()).getUniqueId());
                plugin.getQuestFactory().setSelectingNpcs(selectingNpcs);
            }
            return new EntityPrompt(context);
        }
    }
}
