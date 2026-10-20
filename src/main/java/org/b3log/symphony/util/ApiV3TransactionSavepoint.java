package org.b3log.symphony.util;

import org.b3log.latke.repository.jdbc.JdbcRepository;
import org.b3log.latke.repository.jdbc.JdbcTransaction;
import java.sql.Savepoint;
import java.sql.SQLException;
import java.util.concurrent.Callable;

/** Local all-or-nothing boundary inside an already owned JDBC transaction. Never commits its owner. */
public final class ApiV3TransactionSavepoint {
    private ApiV3TransactionSavepoint(){ }
    public static <T>T run(final Callable<T> work)throws Exception {
        final JdbcTransaction transaction=JdbcRepository.TX.get();
        if(transaction==null||!transaction.isActive())throw new IllegalStateException("owner transaction required");
        final var connection=transaction.getConnection();final Savepoint point=connection.setSavepoint();
        try{
            final T result=work.call();connection.releaseSavepoint(point);return result;
        }catch(Exception|Error error){
            try{connection.rollback(point);}
            catch(SQLException failure){error.addSuppressed(failure);transaction.rollback();throw error;}
            try{connection.releaseSavepoint(point);}catch(SQLException failure){error.addSuppressed(failure);}
            throw error;
        }
    }
}
