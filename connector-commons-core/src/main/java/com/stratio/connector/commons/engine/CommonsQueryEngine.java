package com.stratio.connector.commons.engine;

import com.stratio.connector.commons.connection.ConnectionHandler;
import com.stratio.meta.common.connector.IQueryEngine;
import com.stratio.meta.common.exceptions.ExecutionException;
import com.stratio.meta.common.exceptions.UnsupportedException;
import com.stratio.meta.common.logicalplan.LogicalWorkflow;
import com.stratio.meta.common.result.QueryResult;
import com.stratio.meta2.common.data.ClusterName;

/**
 * Created by dgomez on 22/09/14.
 */
public abstract class CommonsQueryEngine extends CommonsUtils implements IQueryEngine{



    public QueryResult execute(ClusterName targetCluster, LogicalWorkflow workflow, ConnectionHandler  connection) throws UnsupportedException, ExecutionException {

        QueryResult result = null;
        startWork(targetCluster,connection);
        result = execute(targetCluster,workflow);
        endWork(targetCluster, connection);

        return result;
    }

    @Override
    public abstract QueryResult execute(ClusterName targetCluster, LogicalWorkflow workflow) throws UnsupportedException, ExecutionException;
}
