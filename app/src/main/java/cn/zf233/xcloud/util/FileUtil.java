package cn.zf233.xcloud.util;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;

import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

import cn.zf233.xcloud.common.Const;
import cn.zf233.xcloud.common.SdPathConst;
import cn.zf233.xcloud.exception.OpenFileException;

/**
 * Created by zf233 on 11/28/20
 */
public class FileUtil {

    // write to share
    public static void outputShared(Context context, String key, Object object) {
        SharedPreferences shared = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = shared.edit();
        editor.putString(key, JsonUtil.toGson(object));
        editor.apply();
        editor.commit();
    }

    // read from share
    public static <T> T inputShared(Context context, String key, Class<T> clazz) {
        SharedPreferences shared = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        String json = shared.getString(key, "");
        return JsonUtil.toObject(json, clazz);
    }

    // remove to share
    public static void removeShared(Context context, String key) {
        context.deleteSharedPreferences(key);
    }

    // write to external storage
    public static File outputFile(InputStream inputStream, String fileName) {
        // get external storage path
        String storage = SdPathConst.sdPath;
        File dirFile = new File(storage);
        if (!dirFile.exists()) {
            if (!dirFile.mkdirs()) {
                return null;
            }
        }
        File outputFile = new File(SdPathConst.sdPath, fileName);
        OutputStream outputStream = null;
        String existsFileName;
        try {
            if (outputFile.exists()) {
                int i = 2;
                while (true) {
                    existsFileName = fileName.substring(0, fileName.indexOf(".")) + "(" + i + ")" + fileName.substring(fileName.indexOf("."));
                    outputFile = new File(SdPathConst.sdPath, existsFileName);
                    if (!outputFile.exists()) {
                       break;
                    }
                    i++;
                }
//                if (outputFile.delete()) {
//                    Log.i("msg", "Delete file with same name");
//                }
            }
            boolean newFile = outputFile.createNewFile();
            if (newFile) {
                outputStream = new FileOutputStream(outputFile);
                byte[] buffer = new byte[1000];
                int length;
                while ((length = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, length);
                }
                outputStream.flush();
                return outputFile;
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (outputFile.exists()) {
                if (outputFile.delete()) {
                    Log.i("msg", "Error saving file, remove incomplete file");
                }
            }
            return null;
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // open from external storage
    public static void openFile(final String filePath, Context context) throws OpenFileException {
        if ("".equals(filePath)) {
            throw new OpenFileException("打开文件路径有误");
        }
        String ext = filePath.substring(filePath.lastIndexOf('.')).toLowerCase(Locale.US);
        StrictMode.VmPolicy defaultVmPolicy = null;
        try {
            defaultVmPolicy = StrictMode.getVmPolicy();
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
            String temp = ext.substring(1);
            String mime = mimeTypeMap.getMimeTypeFromExtension(temp);
            mime = TextUtils.isEmpty(mime) ? "" : mime;
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(android.content.Intent.ACTION_VIEW);
            java.io.File file = new java.io.File(filePath);
            intent.setDataAndType(Uri.fromFile(file), mime);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            throw new OpenFileException("文件打开失败,未找到对应应用");
        } finally {
            StrictMode.setVmPolicy(defaultVmPolicy);
        }
    }

    // uri conversion file path
    @SuppressLint("ObsoleteSdkInt")
    public static String getFilePathByUri(Context context, Uri uri) {
        try {
            String path = null;
            // 以 file:// 开头的
            if (ContentResolver.SCHEME_FILE.equals(uri.getScheme())) {
                path = uri.getPath();
                return path;
            }
            // 以 content:// 开头的，比如 content://media/extenral/images/media/17766
            if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme()) && Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        if (columnIndex > -1) {
                            path = cursor.getString(columnIndex);
                        }
                    }
                    cursor.close();
                }
                return path;
            }
            // 4.4及之后的 是以 content:// 开头的，比如 content://com.android.providers.media.documents/document/image%3A235700
            if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme()) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (DocumentsContract.isDocumentUri(context, uri)) {
                    if (isExternalStorageDocument(uri)) {
                        // ExternalStorageProvider
                        final String docId = DocumentsContract.getDocumentId(uri);
                        final String[] split = docId.split(":");
                        final String type = split[0];
                        if ("primary".equalsIgnoreCase(type)) {
                            path = Environment.getExternalStorageDirectory() + "/" + split[1];
                            return path;
                        }
                    } else if (isDownloadsDocument(uri)) {
                        // DownloadsProvider
                        final String id = DocumentsContract.getDocumentId(uri);
                        final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                                Long.parseLong(id));
                        path = getDataColumn(context, contentUri, null, null);
                        return path;
                    } else if (isMediaDocument(uri)) {
                        // MediaProvider
                        final String docId = DocumentsContract.getDocumentId(uri);
                        final String[] split = docId.split(":");
                        final String type = split[0];
                        Uri contentUri = null;
                        if ("image".equals(type)) {
                            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        } else if ("video".equals(type)) {
                            contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                        } else if ("audio".equals(type)) {
                            contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                        }
                        final String selection = "_id=?";
                        final String[] selectionArgs = new String[]{split[1]};
                        path = getDataColumn(context, contentUri, selection, selectionArgs);
                        return path;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // create and write log file
    public static void createAndWriteUncaughtExceptionLog(String exceptionDetail) {
        // get external storage path
        String storage = SdPathConst.sdPath;
        File dirFile = new File(storage);
        if (!dirFile.exists()) {
            if (!dirFile.mkdirs()) {
                return;
            }
        }
        File logFile = new File(SdPathConst.sdPath, Const.LOG_FILE_NAME.getDesc());
        FileWriter fileWriter = null;
        try {
            if (!logFile.exists()) {
                if (logFile.createNewFile()) {
                    Log.i("msg", "create the log file");
                }
            }
            if (logFile.exists()) {
                fileWriter = new FileWriter(logFile, true);
                fileWriter.append(DateTimeUtil.getNowDateTime()).append("--->").append(exceptionDetail).append("\r");
                fileWriter.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
