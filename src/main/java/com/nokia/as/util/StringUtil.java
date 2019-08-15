/**
 * © 2019 Nokia
 *
 * Licensed under the BSD 3 Clause license
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.nokia.as.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author sebferrer
 * String util functions
 */
public class StringUtil {

    public static String URIencode(String s) {
        String result;
        try {
            result = URLEncoder.encode(s, "UTF-8")
                    .replaceAll(" ", "%20")
                    .replaceAll("\\+", "%20")
                    .replaceAll("\\%21", "!")
                    .replaceAll("\\%27", "'")
                    .replaceAll("\\%28", "(")
                    .replaceAll("\\%29", ")")
                    .replaceAll("\\%7E", "~");
        } catch (UnsupportedEncodingException e) {
            result = s;
        }

        return result;
    }

}