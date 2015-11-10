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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created with IntelliJ IDEA.
 * <p>
 * Parses a filename of a egglog file and returns a date object of the date of the chatlog
 * <p>
 * User: rvt
 * Date: 11/17/12
 * Time: 7:45 AM
 * To change this template use File | Settings | File Templates.
 */
public class EgglogDateDDMMMYYYYParser implements FilenameDateParser {
    private static final List<String> monthMap = new ArrayList<>();

    private static final Pattern FILEPATTERN = Pattern.compile(".*.log.(\\d\\d)(\\w\\w\\w)(\\d\\d\\d\\d)");
    private static final Pattern LINEPATTERN = Pattern.compile("\\[(\\d\\d:\\d\\d:?\\d?\\d?)\\].<(.*?)>(.+)");
    private static final String DEFAULTLOCALE = "en";

    public EgglogDateDDMMMYYYYParser() {
        this(DEFAULTLOCALE);
    }

    public EgglogDateDDMMMYYYYParser(String locale) {
        final DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMM").withLocale(Locale.forLanguageTag(locale));
        monthMap.addAll(IntStream.range(1, 13)
                .mapToObj(i -> monthFormatter.format(LocalDate.of(2000, i, 1))).
                        collect(Collectors.toList()));
    }

    /**
     * Parses the filename and returns a date of which the logfiles was generated from
     * <p>
     * Returns null if the filename wasn't parsable
     *
     * @param file
     * @return
     */
    public LocalDate getDate(String file) {
        Matcher matcher = FILEPATTERN.matcher(file);

        if (matcher.find()) {
            if (!monthMap.contains(matcher.group(2))) {
                return null;
            }
            int day = Integer.parseInt(matcher.group(1));
            int year = Integer.parseInt(matcher.group(3));
            int month = monthMap.indexOf(matcher.group(2));
            return LocalDate.of(year, month + 1, day);
        }
        assert true : "Given file must exist";
        return null;
    }

    public Pattern getFilepattern() {
        return FILEPATTERN;
    }

    public Pattern getLinepattern() {
        return LINEPATTERN;
    }
}
