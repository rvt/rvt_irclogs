package nl.rvantwisk.jahia.irclogs.interfaces;

import nl.rvantwisk.jahia.irclogs.IRClogLine;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by rvt on 11/4/15.
 */
public interface ChannelDayInfo {

    List<IRClogLine> getLines();

    int getNumLines();

    LocalDate getDate();

}
