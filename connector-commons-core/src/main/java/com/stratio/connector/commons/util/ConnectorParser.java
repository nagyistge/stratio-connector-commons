/*
 * Stratio Deep
 *
 *   Copyright (c) 2014, Stratio, All rights reserved.
 *
 *   This library is free software; you can redistribute it and/or modify it under the terms of the
 *   GNU Lesser General Public License as published by the Free Software Foundation; either version
 *   3.0 of the License, or (at your option) any later version.
 *
 *   This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 *   even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *   Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License along with this library.
 */
package com.stratio.connector.commons.util;

/**
 * This class is the responsible to parse the information.
 * Created by jmgomez on 3/09/14.
 */
public class ConnectorParser {

    /**
     * This method parse the hosts string.
     *
     * @param hosts the hosts string.
     * @return the hosts in an Array.
     */
    public static String[] hosts(String hosts) {
        return hosts.split(",");

    }

    /**
     * This method parse the ips string.
     *
     * @param ips the ips string.
     * @return the ips in an Array.
     */
    public static String[] ports(String ips) {
        return ips.split(",");
    }
}