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

import java.util.HashMap;
import java.util.Map;

/**
 * Small tag library for IRC log rendering
 */
public class IRCLogsTag {
    private static final int COLOR_TABLE_SIZE = 26;
    private static final int END_COLOR = 0xc0c0c0;
    private static final Map<Integer, String> colorTable;

    static {
        colorTable = new HashMap<Integer, String>();
        int colorStep = END_COLOR / COLOR_TABLE_SIZE;
        int i;
        for (i = 0; i < COLOR_TABLE_SIZE; i++) {
            String color = String.format("#%x", (colorStep * i) & 0xFFFFFF);
            colorTable.put(i, color);
        }
    }

    /**
     * Converts a string to a specific color, usefull for rendering names in different colors
     *
     * @param value
     * @return
     */
    public static String strToColor(final String value) {
        int entry;
        if (value.length() > 2) {
            entry = ((int) value.charAt(0) + (int) value.charAt(1) + (int) value.charAt(2)) % COLOR_TABLE_SIZE;
        } else if (value.length() == 2) {
            entry = ((int) value.charAt(0) + (int) value.charAt(1)) % COLOR_TABLE_SIZE;
        } else if (value.length() == 1) {
            entry = ((int) value.charAt(0)) % COLOR_TABLE_SIZE;
        } else {
            entry = 0;
        }

        return colorTable.get(entry);
    }


}
