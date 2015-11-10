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

package nl.rvantwisk.jahia.irclogs.taglib;

import nl.rvantwisk.jahia.irclogs.IRClogLine;
import org.apache.commons.lang.StringUtils;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Small tag library for IRC log rendering
 */
public class IRCLogsTag {
    private static final Pattern userPattern = Pattern.compile("/?Users?/\\S+/?\\S*", Pattern.CASE_INSENSITIVE);
    private static final Pattern homePattern = Pattern.compile("/?Home/\\S+/?\\S", Pattern.CASE_INSENSITIVE);
    private static final Pattern emailPattern = Pattern.compile("([_A-Za-z0-9-]+)(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})", Pattern.CASE_INSENSITIVE);
    private static final Pattern urlPattern = Pattern.compile("(\\A|\\s)((http|https|ftp):\\S+)(\\s|\\z)", Pattern.CASE_INSENSITIVE);

    /**
     * Converts a string to a specific color, usefull for rendering names in different colors
     *
     * @param value
     * @return
     */
    public static String strToColor(final String value) {
        return IRClogLine.strToColor(value);
    }

    /**
     * Format a ZonedDateTime to a string
     *
     * @param zdt
     * @param pattern
     * @return
     */
    public static String zonedDateTimeFormatter(final ZonedDateTime zdt, final String pattern) {
        return DateTimeFormatter.ofPattern(pattern).format(zdt);
    }

    /**
     * Format a line
     *
     * @param line
     * @return
     */
    public static String formatLine(String line) {
        line = StringUtils.replaceEach(line, new String[]{"&", "\"", "<", ">", "#"}, new String[]{"&amp;", "&quot;", "&lt;", "&gt;", "&#35;"});
        line = userPattern.matcher(line).replaceAll("<span class=\"removed\">/Users/&lt;removed&gt;/...</span>");
        line = homePattern.matcher(line).replaceAll("<span class=\"removed\">/Home/&lt;removed&gt;/...</span>");
        line = emailPattern.matcher(line).replaceAll("<span class=\"removed\">(obscured mail address)</span>");
        line = urlPattern.matcher(line).replaceAll("$1<a target=\"_blank\" href=\"$2\">$2</a>$4");
        return line;
    }

    /**
     * find's the month number based on teh font's name, and searches within the locales
     * This is usefull when youa re on a page and switch locale, then we can still lookup
     * The data
     *
     * @param month
     * @param localedToTry
     * @return
     */
    public static Integer findMonth(String month, String localedToTry) {
        if (month == null) {
            return 0;
        }
        month = month.toLowerCase();
        for (int i = 1; i < 13; i++) {
            for (String sLocale : localedToTry.split(",")) {
                final Locale locale = new Locale(sLocale);
                final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM").withLocale(locale);
                if (formatter.format(LocalDate.of(2000, i, 1)).toLowerCase().equals(month)) {
                    return i;
                }
                ;
            }
        }

        return 0;
    }
}
