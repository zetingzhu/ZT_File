package com.zzt.zt_file_logcate;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LogManagerV3 {
    private static final String TAG = "LogManager";
    private static final int MAX_LOG_DAYS = 10;
    private final Context context;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private static class InnerClass {
        private static final LogManagerV3 INSTANCE = new LogManagerV3();
    }

    public static LogManagerV3 getInstance() {
        return InnerClass.INSTANCE;
    }

    public LogManagerV3() {
        this.context = MyApplication.getInstance().getAppContext();
    }

    public void logMessage(String message) {
        try {
            JSONObject logEntry = new JSONObject();
            logEntry.put("timestamp", getCurrentTimestamp());
            logEntry.put("message", message);
            executorService.submit(() -> {
                appendLog(logEntry);
                cleanOldLogs();
            });
        } catch (JSONException e) {
            Log.e(TAG, "JSON 处理错误: " + e.getMessage());
        }
    }

    private synchronized void appendLog(JSONObject logEntry) {
        File logFile = getLogFile();
        if (logFile != null) {
            try (FileWriter writer = new FileWriter(logFile, true)) {
                writer.write(logEntry.toString());
                writer.write(",\n");
            } catch (IOException e) {
                Log.e(TAG, "追加日志文件错误: " + e.getMessage());
            }
        }
    }

    private File getLogFile() {
        File externalFilesDir = getLogDirFile();
        if (externalFilesDir != null) {
            String fileName = getCurrentDate() + ".json";
            return new File(externalFilesDir, fileName);
        }
        return null;
    }

    /**
     * 获取存储目录
     */
    public File getLogDirFile() {
        return context.getExternalFilesDir("fileLog");
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    private void cleanOldLogs() {
        File externalFilesDir = getLogDirFile();
        if (externalFilesDir != null) {
            File[] logFiles = externalFilesDir.listFiles();
            if (logFiles != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date tenDaysAgo = new Date(System.currentTimeMillis() - MAX_LOG_DAYS * 24 * 60 * 60 * 1000);
                for (File logFile : logFiles) {
                    String fileName = logFile.getName();
                    if (fileName.endsWith(".json")) {
                        try {
                            String dateStr = fileName.replace(".json", "");
                            Date fileDate = sdf.parse(dateStr);
                            if (fileDate != null && fileDate.before(tenDaysAgo)) {
                                if (logFile.delete()) {
                                    Log.d(TAG, "已删除旧日志文件: " + fileName);
                                } else {
                                    Log.e(TAG, "删除旧日志文件失败: " + fileName);
                                }
                            }
                        } catch (ParseException e) {
                            Log.e(TAG, "解析日志文件名日期失败: " + fileName);
                        }
                    }
                }
            }
        }
    }
}    