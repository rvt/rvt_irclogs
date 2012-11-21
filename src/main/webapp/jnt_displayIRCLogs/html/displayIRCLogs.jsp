<%@ page import="org.jahia.modules.irclogs.ChatLogCache" %>
<%@ page import="org.jahia.services.content.JCRNodeWrapper" %>
<%@ page import="org.jahia.modules.irclogs.IRClogLine" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.*" %>
<%@ page import="org.jahia.services.render.RenderContext" %>
<%@ page import="org.jahia.registries.ServicesRegistry" %>
<%@ page import="org.jahia.services.cache.CacheHelper" %>
<%@ page import="org.jahia.modules.irclogs.interfaces.ChatlogChannel" %>
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

<template:addResources type="css" resources="irclogs.css" />

<%
    RenderContext renderContext = (RenderContext) request.getAttribute("renderContext");
    String[] monthLocale = new String[12];
    SimpleDateFormat sdf = new SimpleDateFormat("MMM", renderContext.getRequest().getLocale());
    for (int i=0; i<12;i++) {
        Calendar c=GregorianCalendar.getInstance();
        c.set(2012, i, 1);
        monthLocale[i] = sdf.format(c.getTime());
    }
    JCRNodeWrapper currentMNode = (JCRNodeWrapper) request.getAttribute("currentNode");
    String channel = currentMNode.getProperty("channel").getString();
    ChatLogCache clc = ChatLogCache.instance();

    Integer selectedYear=null;
    Integer selectedMonth=null;
    Integer selectedDay=null;
    List<Integer> years=null;
    List<Integer> months=null;
    List<Integer> days=null;
    List<IRClogLine>  lines = null;
    Calendar logsDate=null;

    ChatlogChannel chatlogChannel = clc.getChannel(channel);
    if (chatlogChannel!=null) {
        selectedYear = Integer.valueOf(request.getParameter("year")==null?"0":request.getParameter("year"));
        selectedMonth = Integer.valueOf(request.getParameter("month")==null?"-1":request.getParameter("month"));
        selectedDay = Integer.valueOf(request.getParameter("day")==null?"0":request.getParameter("day"));


        if (chatlogChannel!=null) {
            years = chatlogChannel.getYears();
            months=chatlogChannel.getMonth(selectedYear);
            days=chatlogChannel.getDays(selectedYear, selectedMonth);
        }

        if (selectedYear>0 && selectedMonth>-1 && selectedDay>0) {
            lines=chatlogChannel.getLines(selectedYear, selectedMonth, selectedDay);

            logsDate=GregorianCalendar.getInstance();
            logsDate.set(selectedYear, selectedMonth, selectedDay);
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
<c:set var="monthLocale" value="<%=monthLocale %>"/>
<c:set var="logsDate" value="<%=logsDate %>"/>



<c:if test="${chatlogChannel == null}">
    <fmt:message key="irc_log_waiting_for_channel" />
</c:if>
<c:if test="${chatlogChannel != null}">
    <div class="irclogs">

        <c:if test="${logsDate!=null}">
            <h1>#${currentNode.properties["channel"].string}&nbsp;<fmt:message key="irc_log_from" /> : <fmt:formatDate value="${logsDate.time}" pattern="EEEEE d MMMMM yyyy" /></h1>
        </c:if>
        <c:if test="${logsDate==null}">
            <h1>#${currentNode.properties["channel"].string}&nbsp;<fmt:message key="irc_log"/></h1>
        </c:if>
        <div class="yearsMenu">
        <span><fmt:message key="irc_log_year" /></span>
        <ul>
            <c:forEach items="${chatlogChannel.years}" var="year">
                <c:url value="${url.base}${renderContext.mainResource.node.path}.html" var="yearUrl">
                    <c:param name="year" value="${year}" />
                </c:url>

                <li style="display:inline;"><a <c:if test="${year == selectedYear}">class="selected"</c:if> href="${yearUrl}">${year}</a></li>
            </c:forEach>
        </ul>
    </div>

    <c:if test="${selectedYear > 0}">
        <div class="monthMenu">
            <span><fmt:message key="irc_log_month" /></span>
            <ul>
            <c:forEach items="${months}" var="month">
                <c:url value="${url.base}${renderContext.mainResource.node.path}.html" var="monthUrl">
                    <c:param name="year" value="${selectedYear}" />
                    <c:param name="month" value="${month}" />
                </c:url>
                <li style="display:inline;"><a <c:if test="${month == selectedMonth}">class="selected"</c:if> href="${monthUrl}">${monthLocale[month]}</a></li>
            </c:forEach>
            </ul>
        </div>
    </c:if>

    <c:if test="${selectedMonth > -1}">
        <div class="daysMenu">
            <span><fmt:message key="irc_log_day" /></span>
            <ul>
            <c:forEach items="${days}" var="day">
                <c:url value="${url.base}${renderContext.mainResource.node.path}.html" var="dayUrl">
                    <c:param name="year" value="${selectedYear}" />
                    <c:param name="month" value="${selectedMonth}" />
                    <c:param name="day" value="${day}" />
                </c:url>
                <li style="display:inline;"><a <c:if test="${day == selectedDay}">class="selected"</c:if> href="${dayUrl}">${day}</a></li>
            </c:forEach>
            </ul>
        </div>
    </c:if>

        <c:if test="${fn:length(lines)>0}">
            <table class="chatlog">
                <c:forEach items="${lines}" var="line">
                    <tr>
                        <td class="time"><fmt:formatDate value="${line.date.time}" pattern="HH:mm" /></td>
                        <td class="name" style="color:${irc:strToColor(line.user)}">${line.user}</td>
                        <td class="text">${line.entry}</td>
                    </tr>
                </c:forEach>
            </table>
        </c:if>
        <c:if test="${fn:length(lines)==0 && (selectedYear>0 && selectedMonth>-1 && selectedDay>0)}">
            <p class="nodata"><fmt:message key="irc_log_no_data_today" /></p>
        </c:if>
        <c:if test="${selectedYear==0 || selectedMonth==-1 || selectedDay==0}">
            <p class="nodata"><fmt:message key="irc_log_please_select_dmy" /></p>
        </c:if>
    </div>

</c:if>
