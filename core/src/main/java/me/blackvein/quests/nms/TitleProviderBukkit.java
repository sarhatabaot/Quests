package me.blackvein.quests.nms;

import org.bukkit.entity.Player;

class TitleProviderBukkit extends TitleProvider {

    @SuppressWarnings("deprecation")
    @Override
    void sendTitlePacket(final Player player, final String title, final String subtitle) {
        player.sendTitle(title, subtitle);
    }
}
