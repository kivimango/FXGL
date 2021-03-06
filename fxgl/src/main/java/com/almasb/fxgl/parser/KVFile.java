/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.parser;

import com.almasb.fxgl.core.logging.Logger;
import javafx.util.Pair;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Represents a simple key value file, similar to {@link java.util.Properties}.
 * However, it is easier to work with in the fxgl asset management context.
 * <p>
 * <pre>
 * Example of a .kv file:
 *
 * hp = 100.50
 * level = 5
 * name = Test Name
 * canJump = true
 *
 * </pre>
 * <p>
 * Only primitive types and String are supported. The resulting data type
 * will be determined by the field type with matching name.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class KVFile {

    private static final Logger log = Logger.get("FXGL.KVFile");

    private List<Pair<String, String>> entries = new ArrayList<>();

    private Predicate<String[]> validEntry = kv -> {
        boolean valid = true;
        if (kv.length != 2) {
            log.warning("Syntax error: " + Arrays.toString(kv));
            valid = false;
        }
        return valid;
    };

    /**
     * Constructs KVFile from lines of plain text.
     * Each line must be in format:
     * <p>
     * <pre>
     * key = value
     * </pre>
     * <p>
     * Empty spaces are ignored before, after and inbetween tokens.
     *
     * @param fileLines list of lines from file
     */
    public KVFile(List<String> fileLines) {
        entries = fileLines.stream()
                .map(s -> s.split("=", 2))
                .filter(validEntry)
                .map(kv -> new Pair<>(kv[0].trim(), kv[1].trim()))
                .collect(Collectors.toList());
    }

    private KVFile() {
    }

    private void setKV(Object instance, String key, String value) throws Exception {
        Field field = instance.getClass().getDeclaredField(key);
        field.setAccessible(true);

        switch (field.getType().getSimpleName()) {
            case "int":
                field.setInt(instance, Integer.parseInt(value));
                break;
            case "short":
                field.setShort(instance, Short.parseShort(value));
                break;
            case "long":
                field.setLong(instance, Long.parseLong(value));
                break;
            case "byte":
                field.setByte(instance, Byte.parseByte(value));
                break;
            case "double":
                field.setDouble(instance, Double.parseDouble(value));
                break;
            case "float":
                field.setFloat(instance, Float.parseFloat(value));
                break;
            case "boolean":
                field.setBoolean(instance, Boolean.parseBoolean(value));
                break;
            case "char":
                field.setChar(instance, value.length() > 0 ? value.charAt(0) : ' ');
                break;
            case "String":
                field.set(instance, value);
                break;
            default:
                log.warning("Unknown field type: " + field.getType().getSimpleName());
                log.warning("Only primitive data types and String are supported!");
                break;
        }
    }

    /**
     * A factory constructor for KVFile, which also populates the
     * created instance with key-value data from given object using
     * its declared fields.
     *
     * @param data object to convert to kv file
     * @return kv file
     */
    public static KVFile from(Object data) {
        try {
            KVFile file = new KVFile();

            for (Field f : data.getClass().getDeclaredFields()) {
                f.setAccessible(true);

                file.entries.add(new Pair<>(f.getName(), f.get(data).toString()));
            }

            return file;
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot create KVFile from: " + data + " Error: " + e);
        }
    }

    /**
     * Converts an instance of KVFile to instance of the data
     * structure and populates its fields with appropriate values.
     *
     * @param type data structure type
     * @return instance of type
     */
    public <T> T to(Class<T> type) {
        try {
            T instance = type.newInstance();

            for (Pair<String, String> kv : entries)
                setKV(instance, kv.getKey(), kv.getValue());

            return instance;
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot parse KVFile to: " + type + " Error: " + e);
        }
    }

    @Override
    public String toString() {
        return "KVFile [entries=" + entries.toString() + "]";
    }
}
