package org.b3log.symphony.repository;

import org.b3log.latke.repository.AbstractRepository;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.annotation.Repository;
import org.b3log.symphony.model.ApiV3ChatEvent;

@Repository
public class ApiV3ChatStateRepository extends AbstractRepository {
    public ApiV3ChatStateRepository(){super(ApiV3ChatEvent.STATE_REPOSITORY);}
    public long head()throws RepositoryException {
        if(hasTransactionBegun())throw new IllegalStateException("chat snapshot inside writer transaction");
        return revision(false);
    }
    public long lockHeadInCurrentTransaction()throws RepositoryException {
        if(!hasTransactionBegun())throw new IllegalStateException("chat event transaction required");
        return revision(true);
    }
    private long revision(boolean lock)throws RepositoryException {
        final var rows=select("SELECT revision FROM `"+getName()+"` WHERE oId=?"+(lock?" FOR UPDATE":""),ApiV3ChatEvent.ROOM);
        if(rows.size()!=1)throw new RepositoryException("chat event state unavailable");
        final long revision;
        try{revision=rows.getFirst().getLong("revision");}
        catch(RuntimeException e){throw new RepositoryException(e);}
        if(revision<0)throw new RepositoryException("invalid chat event state");
        return revision;
    }
}
