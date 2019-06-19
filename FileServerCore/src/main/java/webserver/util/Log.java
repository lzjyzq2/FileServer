package webserver.util;

import static java.lang.System.out;

public class Log {
    class LogType {
        public static final int i = 30;
        public static final int e = 31;
        public static final int m = 34;
    }

    private static final String color_head_h = "\033[";
    private static final String color_head_e = "m";
    private static final String color_end = "\033[0m";
    private static final String e_color = "36";

    public static void e(String tag, String msg) {
        log(LogType.e, tag, msg);
    }

    public static void i(String tag, String msg) {
        log(LogType.i, tag, msg);
    }

    public static void m(String tag, String msg) {
        log(LogType.m, tag, msg);
    }

    private static void log(int logType, String tag, String msg) {
        out.println(color_head_h + logType + color_head_e + "[" + tag + "][" + TimeUtil.getNowTime() + "]:" + msg + color_end);
    }
}
