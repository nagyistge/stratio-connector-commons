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

package com.stratio.connector.commons.ftest.workFlow;

import com.stratio.meta.common.connector.Operations;
import com.stratio.meta.common.data.Cell;
import com.stratio.meta.common.data.Row;
import com.stratio.meta.common.logicalplan.*;
import com.stratio.meta.common.statements.structures.relationships.Operator;
import com.stratio.meta.common.statements.structures.relationships.Relation;
import com.stratio.meta2.common.data.ColumnName;
import com.stratio.meta2.common.data.TableName;
import com.stratio.meta2.common.statements.structures.selectors.ColumnSelector;
import com.stratio.meta2.common.statements.structures.selectors.IntegerSelector;
import com.stratio.meta2.common.statements.structures.selectors.Selector;
import com.stratio.meta2.common.statements.structures.selectors.StringSelector;

import static com.stratio.connector.commons.ftest.helper.TextConstant.names;
import java.util.*;

/**
 * Example workflows to test basic functionality of the different connectors.
 * This class assumes the existence of a catalog named {@code example} and with
 * two tables named {@code users}, and {@code information}.
 *
 * Table {@code users} contains the following fields:
 * <li>
 *   <ul>id: integer, PK</ul>
 *   <ul>name: text, PK, indexed</ul>
 *   <ul>age: integer</ul>
 *   <ul>bool: boolean</ul>
 * </li>
 *
 * Table {@code information} contains the following fields:
 * <li>
 *   <ul>id: integer, PK</ul>
 *   <ul>phrase: text, indexed</ul>
 *   <ul>email: text</ul>
 *   <ul>score: double</ul>
 * </li>
 */
public class ExampleWorkflows {

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_AGE = "age";
    public static final String COLUMN_BOOL = "bool";
    public static final String ALIAS_ID = "users.id";
    public static final String ALIAS_NAME = "users.name";
    public static final String ALIAS_AGE = "users.age";

    public String table;
    public String catalog;

    public ExampleWorkflows(String catalog, String table) {
        this.catalog = catalog;
        this.table = table;
    }




    public static Boolean[] booleans = {true,false};

    public List<Row> getRows(int init) {

        List<Row> rows = new ArrayList<Row>();

        for (int i = init*10000; i < (init+1)*10000; i++) {
            Row row = new Row();
            row.addCell(COLUMN_ID, new Cell(i));
            row.addCell(COLUMN_NAME, new Cell(names[i % names.length].toLowerCase()));
            row.addCell(COLUMN_AGE, new Cell(i % 111));
            row.addCell(COLUMN_BOOL, new Cell(booleans[i % booleans.length]));
            rows.add(row);
        }
        return rows;
    }

  /**
   * Get a project operator taking the table name from the first column. This operation
   * assumes all columns belong to the same table.
   * @param columnNames The list of columns.
   * @return A {@link com.stratio.meta.common.logicalplan.Project}.
   */
  public Project getProject(ColumnName ... columnNames){
    TableName table = new TableName(
        columnNames[0].getTableName().getCatalogName().getName(),
        columnNames[0].getTableName().getName());
    return new Project(Operations.PROJECT, table, Arrays.asList(columnNames));
  }

  /**
   * Get a select operator.
   * @param alias The alias
   * @param columnNames The list of columns.
   * @return A {@link com.stratio.meta.common.logicalplan.Select}.
   */
  public Select getSelect(String [] alias, ColumnName ... columnNames){
    Map<String, String> columnMap = new HashMap<>();
    int aliasIndex = 0;
    for(ColumnName column : columnNames){
      columnMap.put(column.getQualifiedName(), alias[aliasIndex]);
      aliasIndex++;
    }
    return new Select(Operations.SELECT_OPERATOR, columnMap);
  }

  /**
   * Get a filter operator.
   * @param filterOp The Filter operation.
   * @param column The column name.
   * @param op The relationship operator.
   * @param right The right selector.
   * @return A {@link com.stratio.meta.common.logicalplan.Filter}.
   */
  public Filter getFilter(Operations filterOp, ColumnName column, Operator op, Selector right){
    Selector left = new ColumnSelector(column);
    Relation r = new Relation(left, op, right);
    return new Filter(filterOp, r);
  }


  /**
   * Get a basic select.
   * SELECT name, users.age FROM example.users;
   * @return A {@link com.stratio.meta.common.logicalplan.LogicalWorkflow}.
   */
  public LogicalWorkflow getBasicSelect(){
    ColumnName name = new ColumnName(catalog,table , COLUMN_NAME);
    ColumnName age = new ColumnName( catalog,table, COLUMN_AGE);
    String [] outputNames = {ALIAS_NAME, ALIAS_AGE};
    LogicalStep project = getProject(name, age);
    LogicalStep select = getSelect(outputNames, name, age);
    project.setNextStep(select);
    LogicalWorkflow lw = new LogicalWorkflow(Arrays.asList(project));
    return lw;
  }

  /**
   * Get a basic select.
   * SELECT * FROM example.users;
   * @return A {@link com.stratio.meta.common.logicalplan.LogicalWorkflow}.
   */
  public LogicalWorkflow getBasicSelectAsterisk(){
    ColumnName id = new ColumnName(catalog,table, COLUMN_ID);
    ColumnName name = new ColumnName(catalog,table, COLUMN_NAME);
    ColumnName age = new ColumnName(catalog,table, COLUMN_AGE);
    ColumnName bool = new ColumnName(catalog,table, COLUMN_BOOL);
    String [] outputNames = {COLUMN_ID, COLUMN_NAME,COLUMN_AGE, COLUMN_BOOL};
    LogicalStep project = getProject(id, name, age, bool);
    /*LogicalStep select = getSelect(outputNames, id, name, age, bool);
    project.setNextStep(select);*/
    LogicalWorkflow lw = new LogicalWorkflow(Arrays.asList(project));
    return lw;
  }

  /**
   * Get a basic select with a single where clause on an indexed field.
   * SELECT users.id, users.name, users.age FROM example.users WHERE users.name='user1';
   * @return A {@link com.stratio.meta.common.logicalplan.LogicalWorkflow}.
   */
  public LogicalWorkflow getSelectIndexedField(){
    ColumnName id = new ColumnName(catalog,table, COLUMN_ID);
    ColumnName name = new ColumnName(catalog,table, COLUMN_NAME);
    ColumnName age = new ColumnName(catalog,table, COLUMN_AGE);
    String [] outputNames = {ALIAS_NAME, ALIAS_AGE};
    LogicalStep project = getProject(id, name, age);
    LogicalStep filter = getFilter(Operations.FILTER_INDEXED_EQ,
                                   name, Operator.COMPARE, new StringSelector(names[0].toLowerCase()));
    project.setNextStep(filter);
    LogicalStep select = getSelect(outputNames, name, age);
    filter.setNextStep(select);
    LogicalWorkflow lw = new LogicalWorkflow(Arrays.asList(project));
    return lw;
  }
/*
  /**
   * Get a basic select with a single where clause on a non-indexed field.
   * SELECT users.id, users.name, users.age FROM example.users WHERE users.age=42;
   * @return A {@link com.stratio.meta.common.logicalplan.LogicalWorkflow}.
   */
  public LogicalWorkflow getSelectNonIndexedField(){
    ColumnName id = new ColumnName(catalog,table, COLUMN_ID);
    ColumnName name = new ColumnName(catalog,table, COLUMN_NAME);
    ColumnName age = new ColumnName(catalog,table, COLUMN_AGE);
    String [] outputNames = {ALIAS_NAME, ALIAS_AGE};
    LogicalStep project = getProject(id, name, age);
    LogicalStep filter = getFilter(Operations.FILTER_NON_INDEXED_EQ,
                                   age, Operator.COMPARE, new IntegerSelector(42));
    project.setNextStep(filter);
    LogicalStep select = getSelect(outputNames, name, age);
    filter.setNextStep(select);
    LogicalWorkflow lw = new LogicalWorkflow(Arrays.asList(project));
    return lw;
  }

  /**
   * Get a basic select with two where clauses.
   * SELECT users.id, users.name, users.age FROM example.users WHERE users.name='user1' AND users.age=42;
   * @return A {@link com.stratio.meta.common.logicalplan.LogicalWorkflow}.
   */
  public LogicalWorkflow getSelectMixedWhere(){
    ColumnName id = new ColumnName(catalog,table, COLUMN_ID);
    ColumnName name = new ColumnName(catalog,table, COLUMN_NAME);
    ColumnName age = new ColumnName(catalog,table, COLUMN_AGE);
    String [] outputNames = {ALIAS_ID, ALIAS_NAME, ALIAS_AGE};
    LogicalStep project = getProject(id, name, age);
    LogicalStep filterName = getFilter(Operations.FILTER_INDEXED_EQ,
                                   name, Operator.COMPARE, new StringSelector(names[1].toLowerCase()));
    project.setNextStep(filterName);
    LogicalStep filterAge = getFilter(Operations.FILTER_NON_INDEXED_EQ,
            age, Operator.COMPARE, new IntegerSelector(40));
    filterName.setNextStep(filterAge);
    LogicalStep select = getSelect(outputNames, id, name, age);
    filterAge.setNextStep(select);
    LogicalWorkflow lw = new LogicalWorkflow(Arrays.asList(project));
    return lw;
  }

}
