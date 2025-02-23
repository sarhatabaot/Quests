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

package me.blackvein.quests.player;

import me.blackvein.quests.quests.IQuest;
import me.blackvein.quests.quests.IStage;
import me.blackvein.quests.quests.Objective;
import me.blackvein.quests.QuestData;
import me.blackvein.quests.enums.ObjectiveType;
import me.blackvein.quests.module.ICustomObjective;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface IQuester extends Comparable<IQuester> {
    /**
     * @return The Questers UUID
     */
    UUID getUUID();

    /**
     * @param id The Questers UUID
     */
    void setUUID(final UUID id);

    String getQuestIdToTake();

    void setQuestIdToTake(final String questIdToTake);

    String getQuestIdToQuit();

    void setQuestIdToQuit(final String questIdToQuit);

    String getLastKnownName();

    void setLastKnownName(final String lastKnownName);

    int getQuestPoints();

    void setQuestPoints(final int questPoints);

    /**
     * Get compass target quest. Returns null if not set
     *
     * @return Quest or null
     * @deprecated Do not use
     */
    IQuest getCompassTargetTemp();

    /**
     * Set compass target quest. Does not update in-game
     *
     * @param quest The target quest
     */
    void setCompassTarget(final IQuest quest);

    ConcurrentHashMap<Integer, IQuest> getTimers();

    void setTimers(final ConcurrentHashMap<Integer, IQuest> timers);

    void removeTimer(final Integer timerId);

    /**
     * @deprecated Do not use
     * Do not use but there is no alternative.. ok
     */
    ConcurrentHashMap<IQuest, Integer> getCurrentQuestsTemp();

    void setCurrentQuests(final ConcurrentHashMap<IQuest, Integer> currentQuests);

    /**
     * @deprecated Do not use
     */
    ConcurrentSkipListSet<IQuest> getCompletedQuestsTemp();

    void setCompletedQuests(final ConcurrentSkipListSet<IQuest> completedQuests);

    ConcurrentHashMap<IQuest, Long> getCompletedTimes();

    void setCompletedTimes(final ConcurrentHashMap<IQuest, Long> completedTimes);

    ConcurrentHashMap<IQuest, Integer> getAmountsCompleted();

    void setAmountsCompleted(final ConcurrentHashMap<IQuest, Integer> amountsCompleted);

    ConcurrentHashMap<IQuest, QuestData> getQuestData();

    void setQuestData(final ConcurrentHashMap<IQuest, QuestData> questData);

    Player getPlayer();

    OfflinePlayer getOfflinePlayer();

    void sendMessage(final String message);

    IStage getCurrentStage(final IQuest quest);

    QuestData getQuestData(final IQuest quest);

    boolean hasJournal();

    ItemStack getJournal();

    int getJournalIndex();

    void updateJournal();

    boolean offerQuest(final IQuest quest, final boolean giveReason);

    boolean canAcceptOffer(final IQuest quest, final boolean giveReason);

    boolean isOnTime(final IQuest quest, final boolean giveReason);

    void takeQuest(final IQuest quest, final boolean ignoreRequirements);

    boolean abandonQuest(final IQuest quest, final String message);

    boolean abandonQuest(final IQuest quest, final String[] messages);

    void quitQuest(final IQuest quest, final String message);

    void quitQuest(final IQuest quest, final String[] messages);

    LinkedList<String> getCurrentRequirements(final IQuest quest, final boolean ignoreOverrides);

    LinkedList<String> getCurrentObjectives(final IQuest quest, final boolean ignoreOverrides);

    LinkedList<String> getObjectives(final IQuest quest, final boolean ignoreOverrides);

    boolean containsObjective(final IQuest quest, final String name);

    boolean hasCustomObjective(final IQuest quest, final String name);

    void breakBlock(final IQuest quest, final ItemStack itemStack);

    void damageBlock(final IQuest quest, final ItemStack itemStack);

    void placeBlock(final IQuest quest, final ItemStack itemStack);

    void useBlock(final IQuest quest, final ItemStack itemStack);

    void cutBlock(final IQuest quest, final ItemStack itemStack);

    void craftItem(final IQuest quest, final ItemStack itemStack);

    void smeltItem(final IQuest quest, final ItemStack itemStack);

    void enchantBook(final IQuest quest, final ItemStack itemStack,
                     final Map<Enchantment, Integer> enchantsToAdd);

    void enchantItem(final IQuest quest, final ItemStack itemStack);

    void brewItem(final IQuest quest, final ItemStack itemStack);

    void consumeItem(final IQuest quest, final ItemStack itemStack);

    void deliverToNPC(final IQuest quest, final UUID npc, final ItemStack itemStack);

    void interactWithNPC(final IQuest quest, final UUID npc);

    void killNPC(final IQuest quest, final UUID npc);

    void milkCow(final IQuest quest);

    void catchFish(final IQuest quest);

    void killMob(final IQuest quest, final Location killedLocation, final EntityType entityType);

    void killPlayer(final IQuest quest, final Player player);

    void reachLocation(final IQuest quest, final Location location);

    void tameMob(final IQuest quest, final EntityType entityType);

    void shearSheep(final IQuest quest, final DyeColor color);

    void sayPassword(final IQuest quest, final AsyncPlayerChatEvent evt);

    void finishObjective(final IQuest quest, final Objective objective, final EntityType mob,
                         final String extra, final UUID npc, final Location location, final DyeColor color,
                         final String pass, final ICustomObjective co);

    boolean testComplete(final IQuest quest);

    void addEmptiesFor(final IQuest quest, final int stage);

    boolean saveData();

    long getCompletionDifference(final IQuest quest);

    long getRemainingCooldown(final IQuest quest);

    FileConfiguration getBaseData();

    boolean hasData();

    boolean hasBaseData();

    void startStageTimer(final IQuest quest);

    void stopStageTimer(final IQuest quest);

    long getStageTime(final IQuest quest);

    void checkQuest(final IQuest quest);

    void showGUIDisplay(final UUID npc, final LinkedList<IQuest> quests);

    void hardQuit(final IQuest quest);

    void hardRemove(final IQuest quest);

    void hardClear();

    void hardStagePut(final IQuest key, final Integer val);

    void hardDataPut(final IQuest key, final QuestData val);

    boolean canUseCompass();

    void resetCompass();

    void findCompassTarget();

    void findNextCompassTarget(final boolean notify);

    boolean hasItem(final ItemStack is);

    Set<String> dispatchMultiplayerEverything(final IQuest quest, final ObjectiveType type,
                                              final BiFunction<IQuester, IQuest, Void> fun);

    Set<String> dispatchMultiplayerObjectives(final IQuest quest, final IStage currentStage,
                                              final Function<IQuester, Void> fun);

    List<IQuester> getMultiplayerQuesters(final IQuest quest);

    boolean meetsCondition(final IQuest quest, final boolean giveReason);

    boolean isSelectingBlock();

    boolean isInRegion(final String regionID);
}
