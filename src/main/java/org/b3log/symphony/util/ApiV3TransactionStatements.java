package org.b3log.symphony.util;

import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.jdbc.JdbcRepository;
import java.sql.SQLException;

/** Parameterized repository mutations on the caller's existing transaction, never a new connection. */
public final class ApiV3TransactionStatements {
    private ApiV3TransactionStatements(){ }
    public static int execute(String sql,Object...args)throws RepositoryException {
        final var tx=JdbcRepository.TX.get();
        if(tx==null||!tx.isActive())throw new IllegalStateException("owner transaction required");
        try(final var statement=tx.getConnection().prepareStatement(sql)){
            for(int i=0;i<args.length;i++)statement.setObject(i+1,args[i]);return statement.executeUpdate();
        }catch(SQLException e){throw new RepositoryException(e);}
    }
}
