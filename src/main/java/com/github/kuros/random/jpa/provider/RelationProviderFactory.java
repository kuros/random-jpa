package com.github.kuros.random.jpa.provider;

import com.github.kuros.random.jpa.Database;
import com.github.kuros.random.jpa.database.mssql.provider.MSSQLRelationshipProvider;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kumar Rohit on 5/29/15.
 * Factory to create Relationship provider.
 */
public class RelationProviderFactory {

    public static RelationshipProvider getRelationshipProvider(final Database database, final EntityManager entityManager) {
        RelationshipProvider relationshipProvider;
        switch (database) {
            case MS_SQL_SERVER:
                relationshipProvider = MSSQLRelationshipProvider.newInstance(entityManager);
                break;
            case NONE:
                relationshipProvider = new EmptyRelationshipProvider();
                break;
            default:
                relationshipProvider = new EmptyRelationshipProvider();
        }

        return relationshipProvider;
    }

    public static class EmptyRelationshipProvider implements RelationshipProvider {

        @Override
        public List<ForeignKeyRelation> getForeignKeyRelations() {
            return new ArrayList<ForeignKeyRelation>();
        }
    }
}

