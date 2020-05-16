package com.ucar.util;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;

/**
 * Description:
 *
 * @author 李自豪（zihao.li01@ucarinc.com）
 * @since 2020/5/8
 */
public class LoggerUtil {

    private static Log log;

    public static Log getLog() {
        if (LoggerUtil.log == null) {
            LoggerUtil.log = new SystemStreamLog();
        }
        return LoggerUtil.log;
    }

    public static void setLog(Log log) {
        LoggerUtil.log = log;
    }
}
