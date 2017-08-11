package com.aylanetworks.aylasdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/*
 * AylaSDK
 *
 * Copyright 2015 Ayla Networks, all rights reserved
 */


public class AylaLog {

    private static final String LOG_TAG = "AYLA_LOG";
    private static String __logFileName;
    private static int __currentFile;
    private static final int FILE_MEMORY_LIMIT = 200000;
    private static final int NUM_OF_LOG_FILES = 3; //files will be replaced after max number is reached

    public enum LogLevel{
        Verbose,
        Debug,
        Info,
        Warning,
        Error,
        None
    }

    //Log levels for console and file logs.
    private static LogLevel __consoleLogLevel = LogLevel.Warning;
    private static LogLevel __fileLogLevel = LogLevel.Error;

    private static SimpleDateFormat __dateFormat =
            new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
    private static String __packageName = null;

    /**
     * Set console log level for Ayla logs. Logs will be displayed on the console only if the log
     * level in the message is higher priority than the saved console log level.
     * @param logLevel Log level
     */
    public static void setConsoleLogLevel(LogLevel logLevel){
        __consoleLogLevel = logLevel;
    }

    /**
     * Set file log level for Ayla logs. Logs will be saved to log files only if the log
     * level in the message is higher priority than the saved file log level.
     * @param logLevel Log level
     */
    public static void setFileLogLevel(LogLevel logLevel){
        __fileLogLevel = logLevel;
    }

    /**
     * Initialize AylaLog. Sets default log level for console and file loggers.
     */
    public static void initAylaLog(String fileName, LogLevel consoleLevel, LogLevel fileLevel){
        __logFileName = fileName;
        setConsoleLogLevel(consoleLevel);
        setFileLogLevel(fileLevel);
        __packageName = AylaNetworks.sharedInstance().getContext().getPackageName();

        if(fileLevel != LogLevel.None){
            //Get total Ayla file count and set __currentFile
            int fileCount = getFileCount();
            if(fileCount >= NUM_OF_LOG_FILES){
                rotateFiles();
                __currentFile = NUM_OF_LOG_FILES; //check if this would in any case be >5 and then delete
            } else {
                __currentFile = ++fileCount;
            }
        }
        Log.d(LOG_TAG, "initAylaLog(). __currentFile "+ __currentFile);

    }

    /**
     * Method to send logs to console and file if the log level is within the set level.
     * @param tag Tag for the log message.
     * @param msg Message to be logged.
     */
    public static void d(String tag, String msg) {
        if(__consoleLogLevel.ordinal() <= LogLevel.Debug.ordinal()){
            Log.d(tag, msg);
        }
        if(__fileLogLevel.ordinal() <= LogLevel.Debug.ordinal()){
            logToFile(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if(__consoleLogLevel.ordinal() <= LogLevel.Error.ordinal()){
            Log.e(tag, msg);
        }
        if(__fileLogLevel.ordinal() <= LogLevel.Error.ordinal()){
            logToFile(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if(__consoleLogLevel.ordinal() <= LogLevel.Warning.ordinal()){
            Log.w(tag, msg);
        }
        if(__fileLogLevel.ordinal() <= LogLevel.Warning.ordinal()){
            logToFile(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if(__consoleLogLevel.ordinal() <= LogLevel.Info.ordinal()){
            Log.i(tag, msg);
        }
        if(__fileLogLevel.ordinal() <= LogLevel.Info.ordinal()){
            logToFile(tag, msg);
        }
    }

    public static void v(String tag, String msg) {
        if(__consoleLogLevel.ordinal() <= LogLevel.Verbose.ordinal()){
            Log.v(tag, msg);
        }
        if(__fileLogLevel.ordinal() <= LogLevel.Verbose.ordinal()){
            logToFile(tag, msg);
        }
    }

    /**
     * Writes a debug log with this tag and message to the console, regardless of the log levels
     * set in the app.
     * @param tag Log tag
     * @param msg Log message
     */
    public static void consoleLogDebug(String tag, String msg){
        Log.d(tag, msg);
    }

    /**
     * Writes an error log with this tag and message to the console, regardless of the log levels
     * set in the app.
     * @param tag Log tag
     * @param msg Log message
     */
    public static void consoleLogError(String tag, String msg){
        Log.e(tag, msg);
    }

    /**
     * Saves logs to file if log level is above the fileLogLevel setting.
     * @param tag Tag for the log message.
     * @param message Message to be saved in the file.
     */
    public static void logToFile(String tag, String message){
        //ToDo: Check __fileLogLevel and then call format and save to file
        saveToFile(tag, message);
    }

    /**
     * Get path of the most recently written log file.
     * @return Absolute file path
     */
    public static String getCurrentLogFilePath(){
        return getFilePath(__currentFile);
    }


    private static String getFilePath(int fileNumber){
        File logDir = getLogDirectory();
        if(logDir != null){
            String currentFilePath = logDir.getAbsolutePath()+
                    "/"+ __logFileName + "("+String.valueOf(fileNumber)+ ")";
            return currentFilePath;
        }
       return null;
    }

    private static File getLogDirectory(){
        String path = Environment.getExternalStorageDirectory().getAbsolutePath().concat
                ("/sdk_logs");
        File logDirectory = new File(path);
        if(!logDirectory.exists()){
            logDirectory.mkdir();
        }
        return logDirectory;
    }

    private static String formatLogMessage(String tag, String msg){
        String spaceChar = ",  ";
        StringBuilder strBuilder = new StringBuilder(tag.length() + msg.length() + 48);
        String date = null;
        //add date
        Date currentDate = Calendar.getInstance().getTime();
        date = __dateFormat.format(currentDate);
        strBuilder.append(date);
        strBuilder.append(spaceChar);
        strBuilder.append(__packageName);
        strBuilder.append(spaceChar);
        strBuilder.append(tag);
        strBuilder.append(spaceChar);
        strBuilder.append(msg);
        strBuilder.append("\n\n");
        return strBuilder.toString();
    }

    private static synchronized void saveToFile(String tag, String message){
        String filePath = getFilePath(__currentFile);
        String formattedLog = formatLogMessage(tag, message);
        File file = new File(filePath);
        if(__currentFile < NUM_OF_LOG_FILES){
            if(file.length() > FILE_MEMORY_LIMIT){
                __currentFile++;
            }
        } else if(__currentFile == NUM_OF_LOG_FILES){
            if(file.length() >= FILE_MEMORY_LIMIT){
                rotateFiles();
            }
        } else{
            rotateFiles();
            __currentFile = NUM_OF_LOG_FILES;
        }
        writeMessageToFile(formattedLog);
    }

    private static void rotateFiles(){
        //Delete the first file
        File filetoDelete =  new File(getFilePath(1));
        if(filetoDelete != null){
            filetoDelete.delete();
        }

        // rotate all existing files
        for(int i=2; i<= NUM_OF_LOG_FILES; i++){
            File thisFile = new File(getFilePath(i));
            File renameFile = new File(getFilePath(i - 1));
            if(thisFile != null && renameFile != null){
                thisFile.renameTo(renameFile);
                if(i == NUM_OF_LOG_FILES){
                    thisFile.delete();
                }
            }
        }
    }

    private static void writeMessageToFile(String message){
        String currentFilePath = getFilePath(__currentFile);
        if(currentFilePath != null){
            File file = new File(currentFilePath);
            try {
                if(!file.exists()){
                    file.createNewFile();
                }
                file.setWritable(true);
                FileOutputStream fileOutputStream = new FileOutputStream(file, true);
                fileOutputStream.write(message.getBytes());
                fileOutputStream.flush();
                fileOutputStream.close();
            } catch (IOException e) {
                Log.e(LOG_TAG, "IOException in writeMessageToFile ");
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    Context context = AylaNetworks.sharedInstance().getContext();
                    if(context.checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") ==
                            PackageManager.PERMISSION_DENIED){
                        Log.d(LOG_TAG, "Extrenal storage permission denied. Disable logs ");
                        setFileLogLevel(LogLevel.None);
                    }
                }
            }
        }
    }

    /**
     * Get total number of log files present. Called at app start.
     * @return number of log files stored in the phone by this app.
     */
    private static int getFileCount(){
        int count = 0;
        File directory = getLogDirectory();
        if(directory != null && directory.isDirectory()){
            Log.d(LOG_TAG, "directory "+directory.getAbsolutePath());
            File[] fileList = directory.listFiles();
            if(fileList != null){
                for(File logFile: fileList){
                    Log.d(LOG_TAG, "__logFileName "+logFile.getAbsolutePath());
                    String name  = logFile.getName();
                    if(name != null){
                        if(name.contains(__logFileName)){
                            Log.d(LOG_TAG, "incrementing path ");
                            count++;
                        }
                    }
                }
            }
        }
        return count;
    }

    /**
     * Get an intent object that can be used to send an email to Ayla Support.
     * Call startActivity(intent) to send the email using default email application on phone.
     * @return Intent object that can be used to send an email to Ayla Support
     */
    public static Intent getEmailIntent(Context context,String[] supportEmail, String
            emailSubject,
                                        String emailMessage){
        String filePath1 = getFilePath(__currentFile);
        String filePath2 = null;
        if(__currentFile > 1){
            filePath2 = getFilePath(__currentFile -1);
        }

        Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        emailIntent.setType("plain/text");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, supportEmail);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, emailSubject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, emailMessage);
        ArrayList<Uri> attachmentUriList = new ArrayList<>(2);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            emailIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            String authority = __packageName;
            Uri contentUri1 = FileProvider.getUriForFile(context, authority, new File(filePath1));

            attachmentUriList.add(0, contentUri1);
            if (filePath2 != null) {
                Uri contentUri2 = FileProvider.getUriForFile(context, authority, new File(filePath2));
                attachmentUriList.add(1, contentUri2);
            }
        } else {
            attachmentUriList.add(0, Uri.parse("file://" + filePath1));
            if (filePath2 != null) {
                attachmentUriList.add(1, Uri.parse("file://" + filePath2));
            }
        }
        emailIntent.putExtra(Intent.EXTRA_STREAM,attachmentUriList);
        return emailIntent;
    }
}
