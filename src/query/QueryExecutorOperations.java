/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package query;

import java.util.List;

/**
 *
 * @author Jana
 */
public interface QueryExecutorOperations {

    QueryResult executeWithIndex(String aggFunc, String aggCol, List<WhereClause> whereClauses, List<String> operators);

    QueryResult executeWithoutIndex(String aggFunc, String aggCol, List<WhereClause> whereClauses, List<String> operators);
}
