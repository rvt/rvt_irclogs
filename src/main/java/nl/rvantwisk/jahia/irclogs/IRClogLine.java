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

import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 *
 * Decription of a single line of a log file
 *
 * User: rvt
 * Date: 11/17/12
 * Time: 7:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class IRClogLine {
    private final Calendar date;
    private final String user;
    private final String entry;
    private final Boolean systemMessage; // feature usage to show logged-in/logged-out message

    public IRClogLine(Calendar date, String user, String entry, Boolean systemMessage) {
        this.date = date;
        this.user = user;
        this.entry = entry;
        this.systemMessage = systemMessage;
    }

    public Calendar getDate() {
        return date;
    }

    public String getUser() {
        return user;
    }

    public String getEntry() {
        return entry;
    }

}
