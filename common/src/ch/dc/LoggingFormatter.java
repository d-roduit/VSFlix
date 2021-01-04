package ch.dc;

import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LoggingFormatter extends Formatter {

    public LoggingFormatter() {
        super();
    }

    public String format(LogRecord record) {
        // Create a StringBuffer to contain the formatted record
        StringBuilder sb = new StringBuilder();

        // Get the date from the LogRecord and add it to the buffer
        Date date = new Date(record.getMillis());
        sb.append(date.toString());
        sb.append(";");

        // Get the level name and add it to the buffer
        sb.append(record.getLevel().getName());
        sb.append(";");

        sb.append(record.getLoggerName());
        sb.append(";");

        sb.append(record.getSourceClassName());
        sb.append(";");

        sb.append(record.getMessage());
        sb.append("\r\n");

        return sb.toString();
    }
}