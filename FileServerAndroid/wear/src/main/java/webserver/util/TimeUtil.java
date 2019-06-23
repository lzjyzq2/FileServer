package webserver.util;

import java.util.Calendar;

public class TimeUtil {
    public static String getNowTime(){
        return Calendar.getInstance().get(Calendar.YEAR)+"."+
                (Calendar.getInstance().get(Calendar.MONTH)+1)+"."+
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)+" "
                +Calendar.getInstance().get(Calendar.HOUR_OF_DAY)+":"
                +Calendar.getInstance().get(Calendar.MINUTE)+":"
                +Calendar.getInstance().get(Calendar.SECOND);
    }
}
