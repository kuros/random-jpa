package com.github.kuros.random.jpa.provider;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kumar Rohit on 5/13/15.
 */
public final class SQLCharacterLengthProvider {

    private static final String QUERY = "SELECT isc.TABLE_NAME,\n" +
            "  COLUMN_NAME,\n" +
            "  CHARACTER_MAXIMUM_LENGTH\n" +
            "FROM INFORMATION_SCHEMA.COLUMNS isc\n" +
            "  INNER JOIN information_schema.tables ist\n" +
            "    ON isc.table_name = ist.table_name\n" +
            "WHERE Table_Type = 'BASE TABLE' and CHARACTER_MAXIMUM_LENGTH > 0";


    private EntityManager entityManager;

    private SQLCharacterLengthProvider(final EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public static SQLCharacterLengthProvider newInstance(final EntityManager entityManager) {
        return new SQLCharacterLengthProvider(entityManager);
    }

    public List<ColumnCharacterLength> getColumnLengths() {
        final List<ColumnCharacterLength> foreignKeyRelations = new ArrayList<ColumnCharacterLength>();

        final Query query = entityManager.createNativeQuery(QUERY);
        final List resultList = query.getResultList();
        for (Object o : resultList) {
            final Object[] row = (Object[]) o;

            final ColumnCharacterLength relation = ColumnCharacterLength.newInstance((String)row[0], (String)row[1], (Integer) row[2]);
            foreignKeyRelations.add(relation);
        }

        return foreignKeyRelations;
    }
}
