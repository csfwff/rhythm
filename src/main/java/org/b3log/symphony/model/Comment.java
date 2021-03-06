/*
 * Rhythm - A modern community (forum/BBS/SNS/blog) platform written in Java.
 * Modified version from Symphony, Thanks Symphony :)
 * Copyright (C) 2012-present, b3log.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.b3log.symphony.model;

/**
 * This class defines all comment model relevant keys.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.15.0.1, Jan 30, 2019
 * @since 0.2.0
 */
public final class Comment {

    /**
     * Comment.
     */
    public static final String COMMENT = "comment";

    /**
     * Comments.
     */
    public static final String COMMENTS = "comments";

    /**
     * Key of comment content.
     */
    public static final String COMMENT_CONTENT = "commentContent";

    /**
     * Key of comment create time.
     */
    public static final String COMMENT_CREATE_TIME = "commentCreateTime";

    /**
     * Key of comment create time str.
     */
    public static final String COMMENT_CREATE_TIME_STR = "commentCreateTimeStr";

    /**
     * Key of comment author id.
     */
    public static final String COMMENT_AUTHOR_ID = "commentAuthorId";

    /**
     * Key of comment on article id.
     */
    public static final String COMMENT_ON_ARTICLE_ID = "commentOnArticleId";

    /**
     * Key of comment sharp URL.
     */
    public static final String COMMENT_SHARP_URL = "commentSharpURL";

    /**
     * Key of original comment id.
     */
    public static final String COMMENT_ORIGINAL_COMMENT_ID = "commentOriginalCommentId";

    /**
     * Key of comment status.
     */
    public static final String COMMENT_STATUS = "commentStatus";

    /**
     * Key of comment IP.
     */
    public static final String COMMENT_IP = "commentIP";

    /**
     * Key of comment UA.
     */
    public static final String COMMENT_UA = "commentUA";

    /**
     * Key of comment anonymous.
     */
    public static final String COMMENT_ANONYMOUS = "commentAnonymous";

    /**
     * Key of comment thank count.
     */
    public static final String COMMENT_THANK_CNT = "commentThankCnt";

    /**
     * Key of comment good count.
     */
    public static final String COMMENT_GOOD_CNT = "commentGoodCnt";

    /**
     * Key of comment bad count.
     */
    public static final String COMMENT_BAD_CNT = "commentBadCnt";

    /**
     * Key of comment score.
     */
    public static final String COMMENT_SCORE = "commentScore";

    /**
     * Key of comment reply count.
     */
    public static final String COMMENT_REPLY_CNT = "commentReplyCnt";

    /**
     * Key of comment audio URL.
     */
    public static final String COMMENT_AUDIO_URL = "commentAudioURL";

    /**
     * Key of comment offered. https://github.com/b3log/symphony/issues/486
     */
    public static final String COMMENT_QNA_OFFERED = "commentQnAOffered";

    /**
     * Key of comment visible.
     */
    public static final String COMMENT_VISIBLE = "commentVisible";

    //// Transient ////
    /**
     * Key of comment revision count.
     */
    public static final String COMMENT_REVISION_COUNT = "commentRevisionCount";

    /**
     * Key of comment vote.
     */
    public static final String COMMENT_T_VOTE = "commentVote";

    /**
     * Key of commenter.
     */
    public static final String COMMENT_T_COMMENTER = "commenter";

    /**
     * Key of comment author email.
     */
    public static final String COMMENT_T_AUTHOR_EMAIL = "commentAuthorEmail";

    /**
     * Key of comment id.
     */
    public static final String COMMENT_T_ID = "commentId";

    /**
     * Key of comment ids.
     */
    public static final String COMMENT_T_IDS = "commentIds";

    /**
     * Key of comment on symphony article id.
     */
    public static final String COMMENT_T_SYMPHONY_ID = "commentSymphonyArticleId";

    /**
     * Key of comment author thumbnail URL.
     */
    public static final String COMMENT_T_AUTHOR_THUMBNAIL_URL = "commentAuthorThumbnailURL";

    /**
     * Key of comment author name.
     */
    public static final String COMMENT_T_AUTHOR_NAME = "commentAuthorName";

    /**
     * Key of comment author URL.
     */
    public static final String COMMENT_T_AUTHOR_URL = "commentAuthorURL";

    /**
     * Key of comment article title.
     */
    public static final String COMMENT_T_ARTICLE_TITLE = "commentArticleTitle";

    /**
     * Key of comment article type.
     */
    public static final String COMMENT_T_ARTICLE_TYPE = "commentArticleType";

    /**
     * Key of comment article perfect.
     */
    public static final String COMMENT_T_ARTICLE_PERFECT = "commentArticlePerfect";

    /**
     * Key of comment article author name.
     */
    public static final String COMMENT_T_ARTICLE_AUTHOR_NAME = "commentArticleAuthorName";

    /**
     * Key of comment article author URL.
     */
    public static final String COMMENT_T_ARTICLE_AUTHOR_URL = "commentArticleAuthorURL";

    /**
     * Key of comment article author thumbnail URL.
     */
    public static final String COMMENT_T_ARTICLE_AUTHOR_THUMBNAIL_URL = "commentArticleAuthorThumbnailURL";

    /**
     * Key of comment article permalink.
     */
    public static final String COMMENT_T_ARTICLE_PERMALINK = "commentArticlePermalink";

    /**
     * Key of comment thank label.
     */
    public static final String COMMENT_T_THANK_LABEL = "commentThankLabel";

    /**
     * Key of comment nice.
     */
    public static final String COMMENT_T_NICE = "commentNice";

    /**
     * Key of comment replies.
     */
    public static final String COMMENT_T_REPLIES = "commentReplies";

    /**
     * Key of comment original author thumbnail URL.
     */
    public static final String COMMENT_T_ORIGINAL_AUTHOR_THUMBNAIL_URL = "commentOriginalAuthorThumbnailURL";

    //// Status constants
    /**
     * Comment status - valid.
     */
    public static final int COMMENT_STATUS_C_VALID = 0;

    /**
     * Comment status - invalid.
     */
    public static final int COMMENT_STATUS_C_INVALID = 1;

    // Anonymous constants
    /**
     * Comment anonymous - public.
     */
    public static final int COMMENT_ANONYMOUS_C_PUBLIC = 0;

    /**
     * Comment anonymous - anonymous.
     */
    public static final int COMMENT_ANONYMOUS_C_ANONYMOUS = 1;

    // QnA offered constants
    /**
     * Comment offered - not yet.
     */
    public static final int COMMENT_QNA_OFFERED_C_NOT = 0;

    /**
     * Comment offered - yes.
     */
    public static final int COMMENT_QNA_OFFERED_C_YES = 1;

    // Visible constants
    /**
     * Comment visible - all.
     */
    public static final int COMMENT_VISIBLE_C_ALL = 0;

    /**
     * Comment visible - only author.
     */
    public static final int COMMENT_VISIBLE_C_AUTHOR = 1;

    //// Validation constants
    /**
     * Max comment content length.
     */
    public static final int MAX_COMMENT_CONTENT_LENGTH = 4096;

    /**
     * Private constructor.
     */
    private Comment() {
    }
}
