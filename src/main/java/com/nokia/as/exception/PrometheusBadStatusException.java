/**
 * © 2019 Nokia
 *
 * Licensed under the BSD 3 Clause license
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.nokia.as.exception;

/**
 * @author sebferrer
 * Prometheus exception regarding its status
 */
public class PrometheusBadStatusException extends Exception {

    public PrometheusBadStatusException() {
        super("Prometheus exception: status is in error");
    }

}