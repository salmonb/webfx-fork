package naga.framework.orm.entity.resultset;

import naga.commons.util.Arrays;
import naga.commons.util.async.Batch;
import naga.framework.expression.Expression;
import naga.framework.expression.lci.CompilerDomainModelReader;
import naga.framework.expression.lci.ParserDomainModelReader;
import naga.framework.expression.sqlcompiler.ExpressionSqlCompiler;
import naga.framework.expression.sqlcompiler.sql.DbmsSqlSyntaxOptions;
import naga.framework.expression.sqlcompiler.sql.SqlCompiled;
import naga.framework.expression.terms.*;
import naga.framework.orm.domainmodel.DataSourceModel;
import naga.framework.orm.domainmodel.DomainModel;
import naga.framework.orm.entity.EntityId;
import naga.platform.services.update.GeneratedKeyBatchIndex;
import naga.platform.services.update.UpdateArgument;
import naga.platform.services.update.UpdateResult;

import java.util.*;

/**
 * @author Bruno Salmon
 */
public class EntityChangesToUpdateBatchGenerator {

    private EntityChangesToUpdateBatchGenerator() {
    }

    public static BatchGenerator createUpdateBatchGenerator(EntityChanges changes, DataSourceModel dataSourceModel, UpdateArgument... initialUpdates) {
        return createUpdateBatchGenerator(changes, dataSourceModel.getId(), DbmsSqlSyntaxOptions.POSTGRES_SYNTAX, dataSourceModel.getDomainModel(), initialUpdates);
    }

    public static BatchGenerator createUpdateBatchGenerator(EntityChanges changes, Object dataSourceId, DbmsSqlSyntaxOptions dbmsSyntax, DomainModel domainModel, UpdateArgument... initialUpdates) {
        return createUpdateBatchGenerator(changes, dataSourceId, dbmsSyntax, domainModel.getParserDomainModelReader(), domainModel.getCompilerDomainModelReader(), initialUpdates);
    }

    public static BatchGenerator createUpdateBatchGenerator(EntityChanges changes, Object dataSourceId, DbmsSqlSyntaxOptions dbmsSyntax, ParserDomainModelReader parserModelReader, CompilerDomainModelReader compilerModelReader, UpdateArgument... initialUpdates) {
        return new BatchGenerator(changes, dataSourceId, dbmsSyntax, compilerModelReader, initialUpdates);
    }

    public static Batch<UpdateArgument> generateUpdateBatch(EntityChanges changes, DataSourceModel dataSourceModel, UpdateArgument... initialUpdates) {
        return createUpdateBatchGenerator(changes, dataSourceModel, initialUpdates).generate();
    }

    public static Batch<UpdateArgument> generateUpdateBatch(EntityChanges changes, Object dataSourceId, DbmsSqlSyntaxOptions dbmsSyntax, DomainModel domainModel, UpdateArgument... initialUpdates) {
        return createUpdateBatchGenerator(changes, dataSourceId, dbmsSyntax, domainModel, initialUpdates).generate();
    }

    public static Batch<UpdateArgument> generateUpdateBatch(EntityChanges changes, Object dataSourceId, DbmsSqlSyntaxOptions dbmsSyntax, ParserDomainModelReader parserModelReader, CompilerDomainModelReader compilerModelReader, UpdateArgument... initialUpdates) {
        return createUpdateBatchGenerator(changes, dataSourceId, dbmsSyntax, parserModelReader, compilerModelReader, initialUpdates).generate();
    }

    public static class BatchGenerator {

        final static Expression WHERE_ID_EQUALS_PARAM = new Equals(IdExpression.singleton, Parameter.UNNAMED_PARAMETER);

        private final EntityChanges changes;
        private final Object dataSourceId;
        private final DbmsSqlSyntaxOptions dbmsSyntax;
        private final CompilerDomainModelReader compilerModelReader;
        private final List<UpdateArgument> updateArguments;
        private final Map<EntityId, Integer> newEntityIdInsertBatchIndexes = new IdentityHashMap<>();

        BatchGenerator(EntityChanges changes, Object dataSourceId, DbmsSqlSyntaxOptions dbmsSyntax, CompilerDomainModelReader compilerModelReader, UpdateArgument... initialUpdates) {
            updateArguments = new ArrayList<>(Arrays.asList(initialUpdates));
            this.changes = changes;
            this.dataSourceId = dataSourceId;
            this.dbmsSyntax = dbmsSyntax;
            this.compilerModelReader = compilerModelReader;
        }

        public Batch<UpdateArgument> generate() {
            // First generating delete statements
            generateDeletes();
            // Then insert and update statements. Statements parameters values may temporary contains EntityId objects,
            // which will be replaced on next step while sorting statements.
            generateInsertUpdates();
            // Finally sorting the statements so that any statement (insert or update) that is referring to a new entity
            // will be executed after that entity has been inserted into the database. For such statements, the parameter
            // value referring to the new entity is replaced with a GeneratedKeyBatchIndex object that contains the index
            // of the insert statement in the batch. The UpdateService must replace that value with the generated key
            // returned by that insert statement (which will be already executed at this stage thanks to the sort).
            // This sorting method also replaces all other (not new) EntityId with their primary key in the parameter
            // values so that they can be used as is without any transformation by the UpdateService.
            sortStatementsByCreationOrder();
            // Returning the batch
            return new Batch<>(updateArguments.toArray(new UpdateArgument[updateArguments.size()]));
        }

        public void applyGeneratedKeys(Batch<UpdateResult> ar) {
            for (Map.Entry<EntityId, Integer> entry : getNewEntityIdInsertBatchIndexes().entrySet())
                entry.getKey().setGeneratedKey(ar.getArray()[entry.getValue()].getGeneratedKeys()[0]);
        }

        public Map<EntityId, Integer> getNewEntityIdInsertBatchIndexes() {
            return newEntityIdInsertBatchIndexes;
        }

        void sortStatementsByCreationOrder() {
            int size = updateArguments.size();
            int sorted = 0;
            List<UpdateArgument> sortedList = new ArrayList<>(updateArguments);
            while (sorted < size) {
                boolean someResolved = false;
                for (int batchIndex = 0; batchIndex < size; batchIndex++) {
                    UpdateArgument arg = updateArguments.get(batchIndex);
                    if (arg != null) {
                        Object[] parameters = arg.getParameters();
                        for (int parameterIndex = 0, length = Arrays.length(parameters); parameterIndex < length; parameterIndex++) {
                            Object value = parameters[parameterIndex];
                            if (value instanceof EntityId) {
                                EntityId entityId = (EntityId) value;
                                if (!entityId.isNew())
                                    parameters[parameterIndex] = entityId.getPrimaryKey();
                                else {
                                    Integer insertIndex = newEntityIdInsertBatchIndexes.get(entityId);
                                    if (insertIndex >= sorted)
                                        break;
                                    parameters[parameterIndex] = new GeneratedKeyBatchIndex(insertIndex);
                                }
                            }
                        }
                        sortedList.set(sorted++, arg);
                        updateArguments.set(batchIndex, null);
                        someResolved = true;
                    }
                }
                if (!someResolved)
                    throw new IllegalStateException("Cyclic references detected");
            }
            for (int i = 0; i < size; i++)
                updateArguments.set(i, sortedList.get(i));
        }


        void generateDeletes() {
            Collection<EntityId> deletedEntities = changes.getDeletedEntityIds();
            if (deletedEntities != null && !deletedEntities.isEmpty()) {
                List<EntityId> deletedList = new ArrayList<>(deletedEntities);
                // Sorting according to classes references
                Collections.sort(deletedList, Comparator.comparing(id -> id.getDomainClass().getName()));
                for (EntityId deletedId : deletedList) // java 8 forEach doesn't compile with GWT
                    generateDelete(deletedId);
            }
        }

        void generateDelete(EntityId id) {
            Delete delete = new Delete(id.getDomainClass(), null, WHERE_ID_EQUALS_PARAM);
            Object[] parameterValues = {id.getPrimaryKey()};
            addToBatch(compileDelete(delete, parameterValues), parameterValues);
        }

        SqlCompiled compileDelete(Delete delete, Object[] parameterValues) {
            return ExpressionSqlCompiler.compileDelete(delete, parameterValues, dbmsSyntax, compilerModelReader);
        }

        void generateInsertUpdates() {
            EntityResultSet rs = changes.getInsertedUpdatedEntityResultSet();
            if (rs != null) {
                for (EntityId id : rs.getEntityIds()) {
                    List<Equals> assignments = new ArrayList<>();
                    List values = new ArrayList();
                    for (Object fieldId : rs.getFieldIds(id)) // java 8 forEach doesn't compile with GWT
                        if (fieldId != null) {
                            assignments.add(new Equals(id.getDomainClass().getField(fieldId), Parameter.UNNAMED_PARAMETER));
                            values.add(rs.getFieldValue(id, fieldId));
                        }
                    ExpressionArray setClause = new ExpressionArray(assignments);
                    if (id.isNew()) { // insert statement
                        newEntityIdInsertBatchIndexes.put(id, updateArguments.size());
                        Insert insert = new Insert(id.getDomainClass(), setClause);
                        Object[] parameterValues = values.isEmpty() ? null : values.toArray();
                        addToBatch(compileInsert(insert, parameterValues), parameterValues);
                    } else { // update statement
                        Update update = new Update(id.getDomainClass(), setClause, WHERE_ID_EQUALS_PARAM);
                        values.add(id.getPrimaryKey());
                        Object[] parameterValues = values.toArray();
                        addToBatch(compileUpdate(update, parameterValues), parameterValues);
                    }
                }
                // TODO: sort sql statements
            }
        }

        SqlCompiled compileInsert(Insert insert, Object[] parameterValues) {
            return ExpressionSqlCompiler.compileInsert(insert, parameterValues, dbmsSyntax, compilerModelReader);
        }

        SqlCompiled compileUpdate(Update update, Object[] parameterValues) {
            return ExpressionSqlCompiler.compileUpdate(update, parameterValues, dbmsSyntax, compilerModelReader);
        }

        void addToBatch(SqlCompiled sqlcompiled, Object[] parameterValues) {
            updateArguments.add(new UpdateArgument(sqlcompiled.getSql(), parameterValues, false, dataSourceId));
        }
    }
}
