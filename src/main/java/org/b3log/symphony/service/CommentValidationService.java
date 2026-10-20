package org.b3log.symphony.service;

import org.b3log.latke.ioc.Inject;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.service.annotation.Service;
import org.b3log.symphony.censor.CensorFactory;
import org.b3log.symphony.censor.CensorResult;
import org.b3log.symphony.util.CommentValidation;
import org.b3log.symphony.util.CommentWriteQuota;
import org.b3log.symphony.util.ReservedWords;
import org.json.JSONObject;
import java.util.function.Consumer;

/** Session-independent adapter; v3 callers must supply only their Bearer actor. */
@Service
public class CommentValidationService {
    @Inject private LangPropsService labels;
    private CommentValidation.Rules rules() {
        return new CommentValidation.Rules() {
            public boolean allowWrite(final String actor, final boolean adding) { return CommentWriteQuota.access(actor, adding); }
            public String reservedWords(final String text) { return ReservedWords.processReservedWord(text); }
            public String label(final String key) { return labels.get(key); }
            public CensorResult censor(final String text) { return CensorFactory.getTextCensor().censor(text); }
        };
    }
    public void prepare(final JSONObject request, final JSONObject actor, final boolean adding) {
        CommentValidation.prepare(request, actor, adding, rules());
    }
    public void moderate(final JSONObject request, final Consumer<CensorResult> rejectedLog) {
        CommentValidation.moderate(request, rules(), rejectedLog);
    }
}
