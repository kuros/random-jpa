package com.github.kuros.random.jpa.persistor.hepler;

import org.hibernate.Session;
import org.hibernate.jdbc.Work;

import javax.persistence.EntityManager;
import java.sql.Connection;
import java.sql.SQLException;

public class EntityManagerWithNoLockImpl implements EntityManagerWithNoLock {

    private EntityManager entityManagerWithNoLock;

    public EntityManagerWithNoLockImpl(final EntityManager entityManager) {
        try {
            entityManagerWithNoLock = entityManager.getEntityManagerFactory().createEntityManager();
            applyReadUnCommitted();
        } catch (final Exception e) {
            entityManagerWithNoLock = entityManager;
        }
    }

    private void applyReadUnCommitted() throws SQLException {
        final Session session = entityManagerWithNoLock.unwrap(Session.class);
        final ConnectionWorker connectionWorker = new ConnectionWorker();
        session.doWork(connectionWorker);
        final Connection connection = connectionWorker.getConnection();
        connection.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
    }

    public EntityManager getEntityManager() {
        return entityManagerWithNoLock;
    }

    private static class ConnectionWorker implements Work {
        private Connection connection;

        public void execute(final Connection connectionVal) throws SQLException {
            this.connection = connectionVal;
        }

        public Connection getConnection() {
            return connection;
        }
    }
}
