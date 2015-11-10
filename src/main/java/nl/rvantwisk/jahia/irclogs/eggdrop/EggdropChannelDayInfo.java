package nl.rvantwisk.jahia.irclogs.eggdrop;

import nl.rvantwisk.jahia.irclogs.IRClogLine;
import nl.rvantwisk.jahia.irclogs.interfaces.ChannelDayInfo;
import nl.rvantwisk.jahia.irclogs.interfaces.ChatlogChannel;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by rvt on 11/5/15.
 */
final public class EggdropChannelDayInfo implements ChannelDayInfo, Serializable {
    private static final Logger logger = Logger.getLogger(ChatlogChannel.class);

    private final Path path;
    private final LocalDate filesDate;
    private final EggChatlogChannel channel;
    private long numLines;

    public EggdropChannelDayInfo(Path path, LocalDate filesDate, EggChatlogChannel channel) {
        this.channel = channel;
        this.filesDate = filesDate;
        this.path = path;

        try {
            numLines = countLines();
        } catch (IOException e) {
            logger.error("Error generating counting lines from logfile (constructor)" + path + " with error ", e);
            numLines = 0;
        }
    }

    @Override
    public int getNumLines() {
        return (int) numLines;
    }

    @Override
    public LocalDate getDate() {
        return filesDate;
    }

    private Predicate<String> matchesWithRegex() {
        return p -> channel.getLinePattern().matcher(p).find();
    }

    @Override
    public List<IRClogLine> getLines() {

        final Function<String, IRClogLine> lineToIRClogLine
                = t -> {
            Matcher matcher = channel.getLinePattern().matcher(t);
            matcher.find();
            String[] timeParsed = matcher.group(1).split(":");
            ZonedDateTime timeOfLine = ZonedDateTime.of(filesDate,
                    LocalTime.of(Integer.parseInt(timeParsed[0]), Integer.parseInt(timeParsed[1]), 0, 0),
                    java.time.ZoneId.systemDefault()
            );

            final String user = matcher.group(2);
            final String entry = matcher.group(3);

            return IRClogLine.newBuilder().withDate(timeOfLine).withUser(user).withEntry(entry).build();
        };

        try (Stream<String> stream = Files.lines(path, Charset.forName("ISO-8859-1"))) {
            return stream.filter(matchesWithRegex())
                    .map(lineToIRClogLine)
                    .collect(Collectors.<IRClogLine>toList());
        } catch (IOException e) {
            logger.error("Error generating lines from logfile " + path + " with error ", e);
            return Collections.emptyList();
        }
    }

    /**
     * Count the number of lines in this chat log
     * returns 0 if some error ac
     *
     * @return
     */
    private long countLines() throws IOException {
        try (Stream<String> stream = Files.lines(path, Charset.forName("ISO-8859-1"))) {
            return stream.filter(matchesWithRegex()).count();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EggdropChannelDayInfo that = (EggdropChannelDayInfo) o;

        if (!path.equals(that.path)) return false;
        return filesDate.equals(that.filesDate);

    }

    @Override
    public int hashCode() {
        int result = path.hashCode();
        result = 31 * result + filesDate.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "EggdropChannelDayInfo{" +
                "channel=" + channel +
                ", filesDate=" + filesDate +
                ", path=" + path +
                '}';
    }
}



