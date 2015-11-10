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

import nl.rvantwisk.jahia.irclogs.IRClogLine;
import nl.rvantwisk.jahia.irclogs.interfaces.ChannelDayInfo;
import nl.rvantwisk.jahia.irclogs.interfaces.ChatlogChannel;
import nl.rvantwisk.jahia.irclogs.interfaces.FilenameDateParser;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created with IntelliJ IDEA.
 * User: rvt
 * Date: 11/16/12
 * Time: 1:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class EggChatlogChannel implements ChatlogChannel, InitializingBean {
    private static final Logger logger = Logger.getLogger(ChatlogChannel.class);
    private String channel;
    private String directory;
    private final FilenameDateParser dateParser;
    private final Pattern linePattern;

    private Map<Integer, Map<Integer, Map<Integer, ChannelDayInfo>>> mapOfAvailableDates = new TreeMap<>();

    EggChatlogChannel(FilenameDateParser dateParser) {
        this.dateParser = dateParser;
        linePattern = dateParser.getLinepattern();
    }

    /**
     * Return a list of available years within this chatlog
     *
     * @return
     */
    public List<Integer> getYears() {
        List<Integer> years = new ArrayList<>(mapOfAvailableDates.keySet());
        return years;
    }

    /**
     * Return a list of available months within a year within this chatlog
     *
     * @param year
     * @return
     */
    public List<Integer> getMonth(Integer year) {
        if (mapOfAvailableDates.get(year) != null) {
            List<Integer> months = new ArrayList<>(mapOfAvailableDates.get(year).keySet());
            return months;

        } else {
            return Collections.emptyList();
        }
    }

    /**
     * Returns a list of available days within months within years withn this chatlog
     *
     * @param year
     * @param month
     * @return
     */
    public List<ChannelDayInfo> getDays(Integer year, Integer month) {
        if (getMonth(year).size() > 0 && mapOfAvailableDates.get(year).get(month) != null) {
            return new ArrayList<>(mapOfAvailableDates.get(year).get(month).values());
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public List<IRClogLine> getLines(Integer year, Integer month, Integer day) throws IOException {
        ChannelDayInfo foo = mapOfAvailableDates.get(year).get(month).get(day);
        return foo.getLines();
    }

    public void runJob() {
        long startTime = System.currentTimeMillis();
        final Path fDirectory = Paths.get(directory);

        final List<Path> allPaths = new ArrayList<>(100);
        if (Files.isDirectory(fDirectory)) {
            // Optionally we could use walk??
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(fDirectory, channel + ".log.*")) {
                stream
                        .forEach(file -> {
                            allPaths.add(file);
                        });
            } catch (IOException e) {
                logger.warn("Failed to get DirectoryStream + ", e);
            }
        }

        final Function<EggdropChannelDayInfo, Integer> getDay = p -> p.getDate().getDayOfMonth();
        final Function<EggdropChannelDayInfo, Integer> getMonth = p -> p.getDate().getMonth().getValue();
        final Function<EggdropChannelDayInfo, Integer> getYear = p -> p.getDate().getYear();

        mapOfAvailableDates = allPaths.stream()
                .parallel()
                .map(path -> new EggdropChannelDayInfo(path, dateParser.getDate(path.toString()), this))
                .collect(Collectors.groupingByConcurrent(getYear,
                        Collectors.groupingBy(getMonth,
                                Collectors.toMap(getDay, Function.identity()))));

        long endTime = System.currentTimeMillis();
        logger.info("Processed channel " + channel + " with " + allPaths.size() + " files. Done in " + (endTime - startTime) + " ms.");
    }

    @Override
    public int getMaxDay(Integer year, Integer month) {
        final OptionalInt max = mapOfAvailableDates.get(year).get(month).values().stream().mapToInt(p -> p.getNumLines()).max();
        return max.isPresent() ? max.getAsInt() : 0;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public void afterPropertiesSet() throws Exception {
        runJob();
    }

    public Pattern getLinePattern() {
        return linePattern;
    }

    @Override
    public String toString() {
        return "EggChatlogChannel{" +
                "channel='" + channel + '\'' +
                ", directory='" + directory + '\'' +
                '}';
    }
}
