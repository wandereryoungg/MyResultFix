package com.young.myresultfix;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

public class HotFixUtil {

    private static final String TAG = "young";
    private static final String NAME_BASE_DEX_CLASS_LOADER = "dalvik.system.BaseDexClassLoader";
    private static final String FIELD_DEX_ELEMENTS = "dexElements";
    private static final String FIELD_PATH_LIST = "pathList";
    private static final String DEX_SUFFIX = ".dex";
    private static final String APK_SUFFIX = ".apk";
    private static final String JAR_SUFFIX = ".jar";
    private static final String ZIP_SUFFIX = ".zip";
    private static final String DEX_DIR = "patch";
    private static final String OPTIMIZE_DEX_DIR = "optidex";

    public void doHotFix(Context context) {
        if (context == null) {
            return;
        }
        File dexDir = context.getExternalFilesDir(DEX_DIR);
        if (!dexDir.exists()) {
            Log.e("young", "热更新目录不存在，无法热更新");
            return;
        }
        File opdexFile = context.getDir(OPTIMIZE_DEX_DIR, Context.MODE_PRIVATE);
        if (!opdexFile.exists()) {
            opdexFile.mkdir();
        }
        File[] files = dexDir.listFiles();
        if (files == null || files.length == 0) {
            return;
        }
        String dexPath = getPatchDexPath(files);
        String opdexPath = opdexFile.getAbsolutePath();
        PathClassLoader classLoader = (PathClassLoader) context.getClassLoader();
        DexClassLoader dexClassLoader = new DexClassLoader(dexPath, opdexPath, null, classLoader);
        Object pathElements = getDexElements(classLoader);
        Object dexElements = getDexElements(dexClassLoader);
        Object combineObject = combineElementArray(pathElements, dexElements);
        setDexElements(classLoader, combineObject);

    }

    private String getPatchDexPath(File[] files) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.getName().endsWith(DEX_SUFFIX) || file.getName().endsWith(ZIP_SUFFIX) || file.getName().endsWith(APK_SUFFIX) || file.getName().endsWith(JAR_SUFFIX)) {
                if (i != 0 && i != files.length - 1) {
                    builder.append(File.pathSeparator);
                }
                builder.append(file.getAbsolutePath());
            }
        }
        return builder.toString();
    }

    private Object getDexElements(ClassLoader classLoader) {
        try {
            Class<?> baseDexClassLoaderClazz = Class.forName(NAME_BASE_DEX_CLASS_LOADER);
            Field pathListField = baseDexClassLoaderClazz.getDeclaredField(FIELD_PATH_LIST);
            pathListField.setAccessible(true);
            Object dexPathList = pathListField.get(classLoader);
            Field dexElementsField = dexPathList.getClass().getDeclaredField(FIELD_DEX_ELEMENTS);
            dexElementsField.setAccessible(true);
            Object dexElements = dexElementsField.get(dexPathList);
            return dexElements;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Object combineElementArray(Object pathElements, Object dexElements) {
        Class<?> componentType = pathElements.getClass().getComponentType();
        int i = Array.getLength(pathElements);
        int j = Array.getLength(dexElements);
        int k = i + j;
        Object result = Array.newInstance(componentType, k);
        System.arraycopy(dexElements, 0, result, 0, j);
        System.arraycopy(pathElements, 0, result, j, i);
        return result;
    }

    private void setDexElements(ClassLoader classLoader, Object value) {
        try {
            Class<?> baseDexClassLoaderClazz = Class.forName(NAME_BASE_DEX_CLASS_LOADER);
            Field pathListField = baseDexClassLoaderClazz.getDeclaredField(FIELD_PATH_LIST);
            pathListField.setAccessible(true);
            Object pathList = pathListField.get(classLoader);
            Field dexElementsField = pathList.getClass().getDeclaredField(FIELD_DEX_ELEMENTS);
            dexElementsField.setAccessible(true);
            dexElementsField.set(pathList, value);
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
