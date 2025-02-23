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

package me.blackvein.quests.quests;

import me.blackvein.quests.entity.CountableMob;
import me.blackvein.quests.enums.ObjectiveType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BukkitObjective implements Objective {
    private final ObjectiveType type;
    private final String message;
    private final int progress;
    private final int goal;
    private final Object progressObj;
    private final Object goalObj;

    public BukkitObjective(final ObjectiveType type, @Nullable final String message, final int progress,
                           final int goal) {
        this.type = type;
        this.message = message;
        this.progress = progress;
        this.goal = goal;
        this.progressObj = new Object();
        this.goalObj = new Object();
    }

    public BukkitObjective(final ObjectiveType type, @Nullable final String message, final @NotNull Object progress,
                           final @NotNull Object goal) {
        this.type = type;
        this.message = message;
        this.progressObj = progress;
        this.goalObj = goal;
        this.progress = getProgressCount();
        this.goal = getGoalCount();
    }

    private int getGoalCount() {
        if (goalObj instanceof ItemStack goalItem) {
            return goalItem.getAmount();
        }
        if (goalObj instanceof CountableMob goalMob) {
            return goalMob.getCount();
        }
        return 0;

    }


    private int getProgressCount() {
        if (progressObj instanceof ItemStack progressItem) {
            return progressItem.getAmount();
        }
        if (progressObj instanceof CountableMob progressMob) {
            return progressMob.getCount();
        }
        return 0;
    }

    @Override
    public ObjectiveType getType() {
        return type;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public int getProgress() {
        return progress;
    }

    @Override
    public int getGoal() {
        return goal;
    }

    @Override
    public @NotNull Object getProgressObject() {
        return progressObj;
    }

    @Override
    public @NotNull Object getGoalObject() {
        return goalObj;
    }

    public @Nullable ItemStack getProgressAsItem() {
        return progressObj instanceof ItemStack item ? item : null;
    }

    public @Nullable ItemStack getGoalAsItem() {
        return goalObj instanceof ItemStack item ? item : null;
    }

    public @Nullable CountableMob getProgressAsMob() {
        return progressObj instanceof CountableMob ? (CountableMob) progressObj : null;
    }

    public @Nullable CountableMob getGoalAsMob() {
        return goalObj instanceof CountableMob ? (CountableMob) goalObj : null;
    }
}
