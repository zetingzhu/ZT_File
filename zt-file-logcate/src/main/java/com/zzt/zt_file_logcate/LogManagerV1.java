package com.zzt.zt_file_logcate;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LogManagerV1 {
    private static final String TAG = "LogManager";
    private static final int MAX_LOG_DAYS = 10;
    private final Context context;

    private static class InnerClass {
        private static final LogManagerV1 INSTANCE = new LogManagerV1();
    }

    public static LogManagerV1 getInstance() {
        return InnerClass.INSTANCE;
    }

    public LogManagerV1() {
        this.context = MyApplication.getInstance().getAppContext();
    }

    public void logMessage(String message) {
        try {
            JSONArray logArray = readLogs();
            JSONObject logEntry = new JSONObject();
            logEntry.put("timestamp", getCurrentTimestamp());
            logEntry.put("message", message);
            logArray.put(logEntry);
            writeLogs(logArray);
            cleanOldLogs();
        } catch (JSONException e) {
            Log.e(TAG, "JSON 处理错误: " + e.getMessage());
        }
    }

    public JSONArray readLogs() {
        File logFile = getLogFile();
        StringBuilder jsonText = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonText.append(line);
            }
            if (jsonText.length() > 0) {
                return new JSONArray(jsonText.toString());
            }
        } catch (IOException | JSONException e) {
            Log.e(TAG, "读取日志文件错误: " + e.getMessage());
        }
        return new JSONArray();
    }

    private void writeLogs(JSONArray logArray) {
        File logFile = getLogFile();
        try (FileWriter writer = new FileWriter(logFile)) {
            writer.write(logArray.toString());
        } catch (IOException e) {
            Log.e(TAG, "写入日志文件错误: " + e.getMessage());
        }
    }

    private File getLogFile() {
        File externalFilesDir = context.getExternalFilesDir(null);
        if (externalFilesDir != null) {
            String fileName = getCurrentDate() + ".json";
            return new File(externalFilesDir, fileName);
        }
        return null;
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
        File externalFilesDir = context.getExternalFilesDir(null);
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