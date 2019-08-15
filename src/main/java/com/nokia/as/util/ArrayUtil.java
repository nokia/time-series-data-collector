/**
 * © 2019 Nokia
 *
 * Licensed under the BSD 3 Clause license
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.nokia.as.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.OptionalDouble;

/**
 * @author sebferrer
 * Array util functions
 */
public class ArrayUtil {

    public static Double getMaxDouble(ArrayList<Double> a) {
        return Collections.max(a);
    }

    public static Double getMinDouble(ArrayList<Double> a) {
        return Collections.min(a);
    }

    public static Double getAvgDouble(ArrayList<Double> a) {
        OptionalDouble average = a
                .stream()
                .mapToDouble(i -> i)
                .average();
        return average.isPresent() ? average.getAsDouble() : 0;
    }

    public static Long getMaxLong(ArrayList<Long> a) {
        return Collections.max(a);
    }

    public static Long getMinLong(ArrayList<Long> a) {
        return Collections.min(a);
    }

    public static Double getAvgLong(ArrayList<Long> a) {
        OptionalDouble average = a
                .stream()
                .mapToDouble(i -> i)
                .average();
        return average.isPresent() ? average.getAsDouble() : 0;
    }

    public static Integer getMaxInteger(ArrayList<Integer> a) {
        return Collections.max(a);
    }

    public static Integer getMinInteger(ArrayList<Integer> a) {
        return Collections.min(a);
    }

    public static Double getAvgInteger(ArrayList<Integer> a) {
        OptionalDouble average = a
                .stream()
                .mapToDouble(i -> i)
                .average();
        return average.isPresent() ? average.getAsDouble() : 0;
    }

    public static ArrayList<Double> addArrayBefore(ArrayList<Double> newArray, ArrayList<Double> array) {
        newArray.addAll(array);
        return newArray;
    }

    public static String join(ArrayList<?> list, String delim) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
            if (i < list.size() - 1) {
                sb.append(delim);
            }
        }
        return sb.toString();
    }

    public static <T> String join(T[] array, String delim) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            sb.append(array[i]);
            if (i < array.length - 1) {
                sb.append(delim);
            }
        }
        return sb.toString();
    }
}