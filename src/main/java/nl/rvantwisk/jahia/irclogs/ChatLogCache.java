/*
 * This file is part of the rvt_irclogs project, a Jahia module to display IRC logs
 *
 * Copyright (C) 2010 R. van Twisk (rvt@dds.nl)
 *
 * This file may be distributed and/or modified under the terms of the
 * GNU General Public License version 2 as published by the Free Software
 * Foundation and appearing in the file gpl-2.0.txt included in the
 * packaging of this file.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * This copyright notice MUST APPEAR in all copies of the script!
 */

package nl.rvantwisk.jahia.irclogs;

import nl.rvantwisk.jahia.irclogs.interfaces.ChatlogChannel;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Class that keeps a cache of a specific channel
 * User: rvt
 * Date: 11/16/12
 * Time: 3:39 PM
 * To change this template use File | Settings | File Templates.
 */
public final class ChatLogCache {
    private static final Logger logger = Logger.getLogger(IRCJobRunner.class);
    private List<ChatlogChannel> channels = new ArrayList<>();
    private static ChatLogCache _instance;

    public ChatLogCache() {
        ChatLogCache._instance = this;
    }

    static public ChatLogCache getInstance() {
        return ChatLogCache._instance;
    }

    public ChatlogChannel getChannel(final String channel) {
        final Optional<ChatlogChannel> clc = channels.stream().filter(p -> p.getChannel().equals(channel)).findFirst();
        if (!clc.isPresent()) {
            logger.warn("No such channel with name : " + channel);
        }
        return clc.get();
    }

    /**
     * Retrieve a list of all registered channels
     *
     * @return list of IRC channels
     */
    public List<ChatlogChannel> getChannels() {
        return channels;
    }

    /**
     * Set a list of channels, usually done with a bean
     *
     * @param channels Adds a new channel
     */
    public void setChannels(List<ChatlogChannel> channels) {
        this.channels = channels;
    }

    @Override
    public String toString() {
        return "ChatLogCache{" +
                "channels=" + channels +
                '}';
    }
}
