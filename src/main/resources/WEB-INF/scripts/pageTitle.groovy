package scripts

import java.time.LocalDate
import java.time.format.DateTimeFormatter

// This groovy script renders the final page title if needed
// The following parameters will be available
//
// String orgTitle : Origional page title
// String title : New title format as set in rvt_irclogs.xml
// String localesToTry : string with comma seperate list of locales to try to map the months back to a int
// SimpleDateFormat dateFormatter : dateformatter as set in rvt_irclogs.xml
// RenderContext renderContext

final String selectedYear = renderContext.getRequest().getParameter("ircyear");
final String selectedMonth = renderContext.getRequest().getParameter("ircmonth");
final String selectedDay = renderContext.getRequest().getParameter("ircday");

// Generously stolen from the TLD because we cannot static load it due to OSGi
public static Integer findMonth(String month, String localedToTry) {
    if (month==null) {
        return 0;
    }
    month=month.toLowerCase();
    for (int i = 1;i< 13; i++){
        for (String sLocale : localedToTry.split(",")) {
            final Locale locale = new Locale(sLocale);
            final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM").withLocale(locale);
            if (formatter.format(LocalDate.of(2000, i, 1)).toLowerCase().equals(month)) {
                return i;
            };
        }
    }
    return 0;
}

// Only replace when we have a year, month and day
if (selectedYear != null && selectedMonth != null && selectedDay != null && title != null) {

    if (dateFormatter != null) {
        // Replace IRCLOGDATE with a assembled date
        def int y = selectedYear.toInteger();
        def int d = selectedDay.toInteger();
        def int m = findMonth(selectedMonth, localesToTry);
        if (m>0) {
            newTitle = title.replace("##ORGTITLE##", orgTitle);
            newTitle = newTitle.replace("##IRCLOGDATE##", dateFormatter.format(LocalDate.of(
                    y,
                    m,
                    d)));
        }
    }

    println newTitle;
}
