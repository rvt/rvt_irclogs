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

package nl.rvantwisk.jahia.irclogs.interfaces;

import nl.rvantwisk.jahia.irclogs.IRClogLine;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: rvt
 * Date: 11/20/12
 * Time: 6:25 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ChatlogChannel {

    /**
     * Returns the name of this channel
     * @return
     */
    public String getChannel();

    /**
     * Returns the number of years within this channel
     * @return
     */
    public List<Integer> getYears();

    /**
     * Returns the number of months within this year
     *
     * @param year
     * @return
     */
    public List<Integer> getMonth(Integer year);

    /**
     * Return a list of Day's within this channel
     *
     * @param year
     * @param month
     * @return
     */
    List<Integer> getDays(Integer year, Integer month);

    /**
     * returns a list of IRCLoglines for this date
     * @param year
     * @param month
     * @param day
     * @return
     */
    List<IRClogLine> getLines(Integer year, Integer month, Integer day);

    /**
     * job that will be execute that allows the channel to fecch/cache or do whatever that's needed
     */
    void runJob();

}
