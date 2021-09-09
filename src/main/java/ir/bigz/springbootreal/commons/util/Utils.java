package ir.bigz.springbootreal.commons.util;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Utils {

    static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd - HH:mm:ss z");
    static final DateTimeFormatter SIMPLE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    public static String getTimeNow(){
        return ZonedDateTime.now(ZoneId.of("Asia/Tehran")).format(FORMATTER);
    }

    public static String convertTimestamp(Timestamp timestamp){
        LocalDateTime localDateTime = timestamp.toLocalDateTime();
        return localDateTime.format(FORMATTER);
    }

    public static Timestamp convertTimeString(String time){
        LocalDateTime from = LocalDateTime.from(FORMATTER.parse(time));
        return Timestamp.valueOf(from);
    }
}
