<%@ page import="nl.rvantwisk.jahia.irclogs.ChatLogCache" %>
<%@ page import="nl.rvantwisk.jahia.irclogs.IRClogLine" %>
<%@ page import="nl.rvantwisk.jahia.irclogs.interfaces.ChannelDayInfo" %>
<%@ page import="nl.rvantwisk.jahia.irclogs.interfaces.ChatlogChannel" %>
<%@ page import="nl.rvantwisk.jahia.irclogs.taglib.IRCLogsTag" %>
<%@ page import="org.jahia.services.content.JCRNodeWrapper" %>
<%@ page import="org.jahia.services.render.RenderContext" %>
<%@ page import="java.io.IOException" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.util.GregorianCalendar" %>
<%@ page import="java.util.List" %>
<%@ taglib prefix="jcr" uri="http://www.jahia.org/tags/jcr" %>
<%@ taglib prefix="irc" uri="http://www.jahia.org/tags/irclogs" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="utility" uri="http://www.jahia.org/tags/utilityLib" %>
<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%--
  ~ This file is part of the rvt_irclogs project, a Jahia module to display IRC logs
  ~
  ~ Copyright (C) 2010 R. van Twisk (rvt@dds.nl)
  ~
  ~ This file may be distributed and/or modified under the terms of the
  ~ GNU General Public License version 2 as published by the Free Software
  ~ Foundation and appearing in the file gpl-2.0.txt included in the
  ~ packaging of this file.
  ~
  ~ This program is distributed in the hope that it will be useful,
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~ GNU General Public License for more details.
  ~
  ~ You should have received a copy of the GNU General Public License
  ~ along with this program; if not, write to the Free Software
  ~ Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
  ~
  ~ This copyright notice MUST APPEAR in all copies of the script!
  --%>

<%--@elvariable id="currentNode" type="org.jahia.services.content.JCRNodeWrapper"--%>
<%--@elvariable id="renderContext" type="org.jahia.services.render.RenderContext"--%>

<template:addResources type="css" resources="irclogs.css"/>
<%
    // use attribute expiration
    final RenderContext renderContext = (RenderContext) request.getAttribute("renderContext");
    final String mainResourceLocale = renderContext.getMainResource().getLocale().toString();

    JCRNodeWrapper currentMNode = (JCRNodeWrapper) request.getAttribute("currentNode");
    String channel = currentMNode.getProperty("channel").getString();
    ChatLogCache clc = ChatLogCache.getInstance();

    Integer selectedYear = null;
    Integer selectedMonth = null;
    Integer selectedDay = null;
    double percentSizeInc = 60.;
    double percentWeightInc = 800.;
    double sizeMulti = 0.;
    double weightMulti = 0.;
    List<Integer> years = null;
    List<Integer> months = null;
    List<ChannelDayInfo> days = null;
    List<IRClogLine> lines = null;
    Calendar logsDate = null;
    String error = null;
    ChatlogChannel chatlogChannel = clc.getChannel(channel);
    if (chatlogChannel != null) {
        selectedYear = Integer.valueOf(request.getParameter("ircyear") == null ? "0" : request.getParameter("ircyear"));
        selectedMonth = IRCLogsTag.findMonth(request.getParameter("ircmonth"), "en,nl");
        selectedDay = Integer.valueOf(request.getParameter("ircday") == null ? "0" : request.getParameter("ircday"));

        years = chatlogChannel.getYears();
        months = chatlogChannel.getMonth(selectedYear);
        days = chatlogChannel.getDays(selectedYear, selectedMonth);


        if (selectedYear > 0 && selectedMonth > 0) {
            int maxDay = chatlogChannel.getMaxDay(selectedYear, selectedMonth);
            sizeMulti = percentSizeInc / (maxDay < 1 ? 1 : maxDay);
            weightMulti = percentWeightInc / (maxDay < 1 ? 1 : maxDay);
        }

        if (selectedYear > 0 && selectedMonth > 0 && selectedDay > 0) {
            try {
                lines = chatlogChannel.getLines(selectedYear, selectedMonth, selectedDay);
            } catch (IOException e) {
                error = "There was an error reading the file + " + e.getMessage();
            }
            logsDate = GregorianCalendar.getInstance();
            logsDate.set(selectedYear, selectedMonth - 1, selectedDay);
        }

        // If we render a irc log of today, we refresh every 5 minute
        // TODO: We should properly take into account at some point the server's time zone
        Calendar c = new GregorianCalendar();
        if (c.get(Calendar.YEAR) == selectedYear && c.get(Calendar.MONTH) == selectedMonth && c.get(Calendar.DAY_OF_MONTH) == selectedDay) {
            request.setAttribute("expiration", "5");
        }
    }
%>

<c:set var="chatlogChannel" value="<%=chatlogChannel %>"/>
<c:set var="selectedYear" value="<%=selectedYear %>"/>
<c:set var="selectedMonth" value="<%=selectedMonth %>"/>
<c:set var="selectedDay" value="<%=selectedDay %>"/>
<c:set var="years" value="<%=years %>"/>
<c:set var="months" value="<%=months %>"/>
<c:set var="days" value="<%=days %>"/>
<c:set var="lines" value="<%=lines %>"/>
<c:set var="logsDate" value="<%=logsDate %>"/>
<c:set var="error" value="<%=error %>"/>
<c:set var="sizeMulti" value="<%=sizeMulti %>"/>
<c:set var="weightMulti" value="<%=weightMulti %>"/>

<fmt:setLocale value="<%=mainResourceLocale %>"/>
<c:if test="${chatlogChannel == null}">
    <fmt:message key="irc_log_waiting_for_channel"/>
</c:if>


<c:if test="${not empty error}">
    <span style="color:red">${error}</span><br/>
</c:if>


<c:if test="${chatlogChannel != null}">
    <div class="irclogs">

        <c:if test="${logsDate!=null}">
            <h1>#${currentNode.properties["channel"].string}&nbsp;<fmt:message key="irc_log_from"/> : <fmt:formatDate value="${logsDate.time}" pattern="EEEEE d MMMMM yyyy"/></h1>
        </c:if>
        <c:if test="${logsDate==null}">
            <h1>#${currentNode.properties["channel"].string}&nbsp;<fmt:message key="irc_log"/></h1>
        </c:if>
        <div class="yearsMenu">
            <span><fmt:message key="irc_log_year"/></span>
            <ul>
                <c:forEach items="${chatlogChannel.years}" var="year">
                    <c:url value="${url.base}${renderContext.mainResource.node.path}.html" var="yearUrl">
                        <c:param name="ircyear" value="${year}"/>
                    </c:url>

                    <li style="display:inline;"><a
                            <c:if test="${year == selectedYear}">class="selected"</c:if> href="${yearUrl}">${year}</a></li>
                </c:forEach>
            </ul>
        </div>

        <c:if test="${selectedYear > 0}">
            <div class="monthMenu">
                <span><fmt:message key="irc_log_month"/></span>
                <ul>
                    <c:forEach items="${months}" var="month">
                        <fmt:parseDate pattern="yyyy-MM-dd" value="2000-${month}-1" var="parsedDate"/>
                        <fmt:formatDate value='${parsedDate}' pattern='MMMM' var="formattedMonth"/>
                        <c:url value="${url.base}${renderContext.mainResource.node.path}.html" var="monthUrl">
                            <c:param name="ircyear" value="${selectedYear}"/>
                            <c:param name="ircmonth" value="${formattedMonth}"/>
                        </c:url>
                        <li style="display:inline;"><a
                                <c:if test="${month == selectedMonth}">class="selected"</c:if> href="${monthUrl}"><fmt:formatDate value="${parsedDate}" pattern="MMM"/></a></li>
                    </c:forEach>
                </ul>
            </div>
        </c:if>

        <c:if test="${selectedMonth>0}">
            <div class="daysMenu">
                <span><fmt:message key="irc_log_day"/></span>
                <ul>
                    <c:forEach items="${days}" var="day">
                        <fmt:parseDate pattern="yyyy-MM-dd" value="2000-${selectedMonth}-1" var="parsedDate"/>
                        <fmt:formatDate value='${parsedDate}' pattern='MMMM' var="formattedMonth"/>
                        <c:url value="${url.base}${renderContext.mainResource.node.path}.html" var="dayUrl">
                            <c:param name="ircyear" value="${selectedYear}"/>
                            <c:param name="ircmonth" value="${formattedMonth}"/>
                            <c:param name="ircday" value="${day.date.dayOfMonth}"/>
                        </c:url>
                        <fmt:formatNumber var="fontSize" type="number" maxFractionDigits="0" value="${sizeMulti * day.numLines+100}" />
                        <fmt:formatNumber var="fontWeight" type="number" maxFractionDigits="0" value="${weightMulti * day.numLines+100}" />
                        <li style="display:inline;"><a title="<fmt:message key="irc_log_todayslines" />&nbsp;${day.numLines}"
                                                       <c:if test="${day.date.dayOfMonth == selectedDay}">class="selected"</c:if> href="${dayUrl}"><span style="font-size:${fontSize}%;font-weight:${fontWeight};">${day.date.dayOfMonth}</span></a></li>
                    </c:forEach>
                </ul>
            </div>
        </c:if>

        <c:if test="${fn:length(lines)>0}">
            <table class="chatlog">
                <c:forEach items="${lines}" var="line">
                    <tr>
                        <td class="time"> ${irc:zonedDateTimeFormatter(line.date, "HH:mm '<span class=\"small\">'z'</span>'")}</td>
                        <td class="name" style="color:${irc:strToColor(line.user)}">${line.user}</td>
                        <td class="text">${irc:formatLine(line.entry)}</td>
                    </tr>
                </c:forEach>
            </table>
        </c:if>
        <c:if test="${fn:length(lines)==0 && (selectedYear>0 && selectedMonth>-1 && selectedDay>0)}">
            <p class="nodata"><fmt:message key="irc_log_no_data_today"/></p>
        </c:if>
        <c:if test="${selectedYear==0 || selectedMonth==-1 || selectedDay==0}">
            <p class="nodata"><fmt:message key="irc_log_please_select_dmy"/></p>
        </c:if>
    </div>

</c:if>
