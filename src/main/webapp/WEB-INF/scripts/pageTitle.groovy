import java.text.SimpleDateFormat

// This groovy script renders the final page title if needed
// The following parameters will be available
//
// String orgTitle : Origional page title
// String title : New title format as set in rvt_irclogs.xml
// SimpleDateFormat dateFormatter : dateformatter as set in rvt_irclogs.xml
// RenderContext renderContext

final String selectedYear = renderContext.getRequest().getParameter("ircyear");
final String selectedMonth = renderContext.getRequest().getParameter("ircmonth");
final String selectedDay = renderContext.getRequest().getParameter("ircday");

// Only replace when we have a year, month and day
if (selectedYear != null && selectedMonth != null && selectedDay != null && title!=null) {

    // create a new title
    String newTitle = title.replace("##ORGTITLE##", orgTitle);

    if (dateFormatter != null) {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MMM-dd", renderContext.getMainResourceLocale());
        Date selectedDate = formatter.parse(selectedYear+"-"+selectedMonth+"-"+selectedDay);

        // Create a date
        Calendar logsDate = GregorianCalendar.getInstance();
        logsDate.setTime(selectedDate);

        // Replace IRCLOGDATE with a assembled date
        newTitle = newTitle.replace("##IRCLOGDATE##", dateFormatter.format(logsDate.getTime()));
    }

    println newTitle;
}
