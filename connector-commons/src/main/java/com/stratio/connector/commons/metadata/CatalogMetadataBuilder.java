/*
 * Licensed to STRATIO (C) under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership.  The STRATIO (C) licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.stratio.connector.commons.metadata;

import com.stratio.connector.commons.TimerJ;
import com.stratio.crossdata.common.data.CatalogName;
import com.stratio.crossdata.common.data.TableName;
import com.stratio.crossdata.common.metadata.CatalogMetadata;
import com.stratio.crossdata.common.metadata.TableMetadata;
import com.stratio.crossdata.common.statements.structures.Selector;

import java.util.HashMap;
import java.util.Map;

/**
 * Builder for CatalogMetadata.
 *
 * @author darroyo
 */
public class CatalogMetadataBuilder {

    /**
     * The catalog name.
     */
    private CatalogName catalogName;
    /**
     * The options.
     */
    private Map<Selector, Selector> options;
    /**
     * The tables metadata.
     */
    private Map<TableName, TableMetadata> tables;

    /**
     * Instantiates a new catalog metadata builder.
     *
     * @param catalogName the catalog name
     */
    public CatalogMetadataBuilder(String catalogName) {
        this.catalogName = new CatalogName(catalogName);
        options = null;
        tables = new HashMap<TableName, TableMetadata>();

    }

    /**
     * Adds tables to the catalog.
     *
     * @param tableMetadataList the table metadata list
     * @return the catalog metadata builder
     */
    @TimerJ
    public CatalogMetadataBuilder withTables(TableMetadata... tableMetadataList) {
        for (TableMetadata tableMetadata : tableMetadataList) {
            tables.put(tableMetadata.getName(), tableMetadata);
        }
        return this;
    }

    /**
     * Set the options. Any options previously created are removed.
     *
     * @param opts the opts
     * @return the catalog metadata builder
     */
    @TimerJ
    public CatalogMetadataBuilder withOptions(Map<Selector, Selector> opts) {
        options = new HashMap<Selector, Selector>(opts);
        return this;
    }

    /**
     * Builds the CatalogMetadata.
     *
     * @return the catalog metadata
     */
    @TimerJ
    public CatalogMetadata build() {
        return new CatalogMetadata(catalogName, options, tables);
    }

}
