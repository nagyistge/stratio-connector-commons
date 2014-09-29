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

package com.stratio.connector.commons.ftest.functionalTestQuery;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.stratio.connector.commons.ftest.GenericConnectorTest;
import com.stratio.connector.commons.ftest.schema.TableMetadataBuilder;
import com.stratio.connector.commons.ftest.workFlow.LogicalWorkFlowCreator;
import com.stratio.meta.common.data.Cell;
import com.stratio.meta.common.data.Row;
import com.stratio.meta.common.exceptions.ExecutionException;
import com.stratio.meta.common.exceptions.UnsupportedException;
import com.stratio.meta.common.logicalplan.LogicalWorkflow;
import com.stratio.meta.common.result.QueryResult;
import com.stratio.meta2.common.data.ClusterName;
import com.stratio.meta2.common.metadata.ColumnType;
import com.stratio.meta2.common.metadata.TableMetadata;

public abstract class GenericLimitTest extends GenericConnectorTest {

    public static final String COLUMN_TEXT = "text";
    public static final String COLUMN_AGE = "age";
    public static final String COLUMN_MONEY = "money";
    private static final int RESULT_NUMBER = 15456;

    @Test
    public void limitTest() throws Exception {

        ClusterName clusterName = getClusterName();
        System.out.println("*********************************** INIT FUNCTIONAL TEST limitTest  ***********************************");

        for (int i = 0; i < 25463; i++) {
            insertRow(i, "text", clusterName);
        }

        refresh(CATALOG);

        LogicalWorkflow logicalPlan = createLogicalPlan(RESULT_NUMBER);

        QueryResult queryResult = (QueryResult) connector.getQueryEngine().execute(logicalPlan);

        assertEquals("The limited result is correct", RESULT_NUMBER, queryResult.getResultSet().size());

    }

    private LogicalWorkflow createLogicalPlan(int limit) {

        return new LogicalWorkFlowCreator(CATALOG, TABLE, getClusterName())
                        .addColumnName(COLUMN_TEXT, COLUMN_AGE, COLUMN_MONEY).addLimit(limit).getLogicalWorkflow();

    }

    private void insertRow(int ikey, String texto, ClusterName cLusterName) throws UnsupportedOperationException,
                    ExecutionException, UnsupportedException {

        Row row = new Row();
        Map<String, Cell> cells = new HashMap<>();
        cells.put(COLUMN_TEXT, new Cell(texto + ikey));
        cells.put(COLUMN_AGE, new Cell(10));
        cells.put(COLUMN_MONEY, new Cell(20));
        row.setCells(cells);

        TableMetadataBuilder tableMetadataBuilder = new TableMetadataBuilder(CATALOG, TABLE);
        tableMetadataBuilder.addColumn(COLUMN_TEXT, ColumnType.VARCHAR).addColumn(COLUMN_AGE, ColumnType.INT)
                        .addColumn(COLUMN_MONEY, ColumnType.INT);

        TableMetadata targetTable = tableMetadataBuilder.build();

        connector.getStorageEngine().insert(cLusterName, targetTable, row);

    }

}