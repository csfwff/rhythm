package org.b3log.symphony.service;

import org.b3log.latke.ioc.Inject;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.service.annotation.Service;
import org.b3log.symphony.censor.CensorFactory;
import org.b3log.symphony.censor.CensorResult;
import org.b3log.symphony.model.Tag;
import org.b3log.symphony.util.ArticleValidation;
import org.b3log.symphony.util.ArticleWriteQuota;
import org.b3log.symphony.util.Emotions;
import org.b3log.symphony.util.ReservedWords;
import org.b3log.symphony.util.Symphonys;
import org.json.JSONObject;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/** Binds shared validation to the real moderation, tag rules and old write quota, without session identity. */
@Service
public class ArticleValidationService {
    @Inject private LangPropsService labels;
    @Inject private TagQueryService tags;
    public void validate(final JSONObject request, final JSONObject actor, final boolean adding, final Consumer<String> rejectedLog) {
        ArticleValidation.validate(request, actor, adding, new ArticleValidation.Rules() {
            public String label(final String key) { return labels.get(key); }
            public String reservedWords(final String text) { return ReservedWords.processReservedWord(text); }
            public String clearEmotions(final String text) { return Emotions.clear(text); }
            public String formatTags(final String text) { return Tag.formatTags(text); }
            public List<String> invalidTags() { return tags.getInvalidTags(); }
            public boolean whiteListedTag(final String tag) { return Tag.containsWhiteListTags(tag); }
            public boolean validTag(final String tag) { return Tag.TAG_TITLE_PATTERN.matcher(tag).matches(); }
            public int maxTagLength() { return Tag.MAX_TAG_TITLE_LENGTH; }
            public boolean reservedTag(final String tag) { return Arrays.asList(Symphonys.RESERVED_TAGS).contains(tag); }
            public boolean allowWrite(final String id) { return ArticleWriteQuota.access(id); }
            public CensorResult censor(final String text) { return CensorFactory.getTextCensor().censor(text); }
        }, rejectedLog);
    }
}
