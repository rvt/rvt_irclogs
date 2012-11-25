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

package org.jahia.modules.irclogs.filters;

import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.filter.AbstractFilter;
import org.jahia.services.render.filter.RenderChain;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Filter the generate a new title for a specific log entry of a IRC logfile
 *
 * User: rvt
 * Date: 11/18/12
 * Time: 6:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class IRCLogPageTitleFilter extends AbstractFilter {
    private String title = null;
    private SimpleDateFormat dateFormatter = null;


    @Override
    public String execute(String previousOut, RenderContext renderContext, Resource resource, RenderChain chain) throws Exception {

        final String selectedYear = renderContext.getRequest().getParameter("ircyear");
        final String selectedMonth = renderContext.getRequest().getParameter("ircmonth");
        final String selectedDay = renderContext.getRequest().getParameter("ircday");

        // Only replace when we have a year, month and day
        if (selectedYear != null && selectedMonth != null && selectedDay != null && title!=null) {

            // Split title so we can get and replace it
            final String[] beforeAfterTitle = previousOut.split("<title>");
            if (beforeAfterTitle.length==2) {

                final String[] beforeAfterEndTitle = beforeAfterTitle[1].split("</title>");
                if (beforeAfterEndTitle.length == 2) {

                    // create a new title
                    String newTitle = getTitle();
                    newTitle = newTitle.replace("##ORGTITLE##", beforeAfterEndTitle[0]);
                    if (dateFormatter != null) {

                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MMM-dd", renderContext.getMainResourceLocale());
                        Date selectedDate = formatter.parse(selectedYear+"-"+selectedMonth+"-"+selectedDay);

                        // Create a date
                        Calendar logsDate = GregorianCalendar.getInstance();
                        logsDate.setTime(selectedDate);

                        // Replace IRCLOGDATE with a assembled date
                        newTitle = newTitle.replace("##IRCLOGDATE##", dateFormatter.format(logsDate.getTime()));
                    }

                    // assemble new title
                    previousOut = beforeAfterTitle[0] + "<title>" + newTitle + "</title>" + beforeAfterEndTitle[1];
                }
            }
        }

        return previousOut;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDateFormat() {
        return dateFormatter.toPattern();
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormatter = new SimpleDateFormat(dateFormat);
    }

}
