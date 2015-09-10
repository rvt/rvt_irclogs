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

package nl.rvantwisk.jahia.irclogs.eggdrop;

import nl.rvantwisk.jahia.irclogs.interfaces.FilenameDateParser;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * <p/>
 * Parses a filename of a egglog file and returns a date object of the date of the chatlog
 * <p/>
 * User: rvt
 * Date: 11/17/12
 * Time: 7:45 AM
 * To change this template use File | Settings | File Templates.
 */
public class EgglogDateDDMMMYYYYParser implements FilenameDateParser {
    private static final Map<String, Integer> monthMap;

    static {
        // we migth get feedback of users using a different filename for there filenames,
        // we can then decide what to do...
        Map<String, Integer> aMap = new HashMap<String, Integer>();
        aMap.put("Jan", 0);
        aMap.put("Feb", 1);
        aMap.put("Mar", 2);
        aMap.put("Apr", 3);
        aMap.put("May", 4);
        aMap.put("Jun", 5);
        aMap.put("Jul", 6);
        aMap.put("Aug", 7);
        aMap.put("Sep", 8);
        aMap.put("Oct", 9);
        aMap.put("Nov", 10);
        aMap.put("Dec", 11);
        monthMap = Collections.unmodifiableMap(aMap);
    }

    private static final Pattern FILEPATTERN = Pattern.compile(".*.log.(\\d\\d)(\\w\\w\\w)(\\d\\d\\d\\d)");
    private static final Pattern LINEPATTERN = Pattern.compile("\\[(\\d\\d:\\d\\d:?\\d?\\d?)\\].<(.*?)>(.+)");

    /**
     * Parses the filename and returns a date of which the logfiles was generated from
     * <p/>
     * Returns null if the filename wasn't parsable
     *
     * @param file
     * @return
     */
    public Calendar getDate(File file) {
        Matcher matcher = FILEPATTERN.matcher(file.getName());

        if (matcher.find()) {

            if (!monthMap.containsKey(matcher.group(2))) {
                return null;
            }

            Integer day = Integer.valueOf(matcher.group(1));
            Integer year = Integer.valueOf(matcher.group(3));
            Integer month = monthMap.get(matcher.group(2));

            return new GregorianCalendar(year, month, day);
        }
        return null;
    }

    public Pattern getFilepattern() {
        return FILEPATTERN;
    }

    public Pattern getLinepattern() {
        return LINEPATTERN;
    }
}
