package org.b3log.symphony.util;

import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.jdbc.JdbcRepository;
import org.b3log.latke.repository.jdbc.JdbcTransaction;
import java.sql.SQLException;
import java.sql.Savepoint;

/** A savepoint inside the existing owner transaction, never a nested or independently committed transaction. */
public final class ApiV3ChatTransaction {
    private ApiV3ChatTransaction() { }
    @FunctionalInterface public interface Write<T>{T run()throws RepositoryException;}
    public static <T>T write(Write<T> work)throws RepositoryException {
        final var transaction=JdbcRepository.TX.get();
        if(transaction==null||!transaction.isActive())throw new IllegalStateException("chat transaction required");
        final var connection=transaction.getConnection();final Savepoint point;
        try{point=connection.setSavepoint();}catch(SQLException e){throw new RepositoryException(e);}
        try{
            final T value=work.run();connection.releaseSavepoint(point);return value;
        }catch(SQLException failure){
            final var error=new RepositoryException(failure);rollback(transaction,point,error);throw error;
        }catch(RepositoryException|RuntimeException|Error error){rollback(transaction,point,error);throw error;}
    }
    private static void rollback(JdbcTransaction transaction,Savepoint point,Throwable error){
        try{transaction.getConnection().rollback(point);}
        catch(SQLException failure){error.addSuppressed(failure);transaction.rollback();return;}
        try{transaction.getConnection().releaseSavepoint(point);}catch(SQLException failure){error.addSuppressed(failure);}
    }
}
