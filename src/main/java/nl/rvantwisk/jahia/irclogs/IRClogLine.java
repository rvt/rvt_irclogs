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

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toMap;

/**
 * Created with IntelliJ IDEA.
 * <p>
 * Decription of a single line of a log file
 * <p>
 * User: rvt
 * Date: 11/17/12
 * Time: 7:20 PM
 * To change this template use File | Settings | File Templates.
 */
public final class IRClogLine implements Serializable, Comparable<IRClogLine> {

    public static final int COLOR_TABLE_SIZE = 26;
    public static final int END_COLOR = 0xc0c0c0;
    public static final Map<Integer, String> colorTable;

    private static final long serialVersionUID = -7558517532917359327L;

    private final ZonedDateTime date;
    private final String user;
    private final String entry;
    private final boolean systemMessage; // feature usage to show logged-in/logged-out message

    static {
//        colorTable = new HashMap<Integer, String>();
        final int colorStep = END_COLOR / COLOR_TABLE_SIZE;

//        colorTable = Collections.unmodifiableMap(IntStream.range(1, COLOR_TABLE_SIZE).collect(Collectors.toMap(Function.identity(), p -> String.format("#%x", (colorStep * i) & 0xFFFFFF)")));

        colorTable = Collections.unmodifiableMap(
                IntStream.range(0, COLOR_TABLE_SIZE)
                        .boxed()
                        .collect(toMap(i -> i, i -> String.format("#%x", (colorStep * i) & 0xFFFFFF))));
    }

    public IRClogLine(ZonedDateTime date, String user, String entry, boolean systemMessage) {
        this.date = date;
        this.user = user;
        this.entry = entry;
        this.systemMessage = systemMessage;
    }

    private IRClogLine(Builder builder) {
        date = builder.date;
        user = builder.user;
        entry = builder.entry;
        systemMessage = builder.systemMessage;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(IRClogLine copy) {
        Builder builder = new Builder();
        builder.date = copy.date;
        builder.user = copy.user;
        builder.entry = copy.entry;
        builder.systemMessage = copy.systemMessage;
        return builder;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public String getUser() {
        return user;
    }

    public String getEntry() {
        return entry;
    }

    public boolean isSystemMessage() {
        return systemMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IRClogLine that = (IRClogLine) o;

        if (!date.equals(that.date)) return false;
        return user.equals(that.user);
    }

    @Override
    public int hashCode() {
        int result = date.hashCode();
        result = 31 * result + user.hashCode();
        return result;
    }

    @Override
    public int compareTo(IRClogLine that) {
        if (this.date.compareTo(that.date) < 0) {
            return -1;
        } else if (this.date.compareTo(that.date) > 0) {
            return 1;
        }

        if (this.user.compareTo(that.user) < 0) {
            return -1;
        } else if (this.user.compareTo(that.user) > 0) {
            return 1;
        }
        return 0;
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

    public static final class Builder {
        private String user;
        private String entry;
        private boolean systemMessage;
        private ZonedDateTime date;

        private Builder() {
        }

        public Builder withUser(String val) {
            user = val;
            return this;
        }

        public Builder withEntry(String val) {
            entry = val;
            return this;
        }

        public Builder withSystemMessage(boolean val) {
            systemMessage = val;
            return this;
        }

        public IRClogLine build() {
            return new IRClogLine(this);
        }

        public Builder withDate(ZonedDateTime val) {
            date = val;
            return this;
        }
    }
}
