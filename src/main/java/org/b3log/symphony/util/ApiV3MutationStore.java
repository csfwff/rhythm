package org.b3log.symphony.util;

import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.Transaction;
import org.json.JSONObject;

/** Storage port for testing transaction semantics without starting a database or the application. */
public interface ApiV3MutationStore {
    Transaction beginTransaction();
    boolean hasTransactionBegun();
    JSONObject getForUpdate(String id) throws RepositoryException;
    String add(JSONObject row) throws RepositoryException;
    void update(String id, JSONObject row, String... propertyNames) throws RepositoryException;
}
