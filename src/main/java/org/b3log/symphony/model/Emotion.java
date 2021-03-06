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
 * This class defines all emotion model relevant keys.
 *
 * @author <a href="https://ld246.com/member/ZephyrJung">Zephyr</a>
 * @version 1.0.0.1, Dec 13, 2016
 * @since 1.5.0
 */
public final class Emotion {

    /**
     * Emotion.
     */
    public static final String EMOTION = "emotion";

    /**
     * Emotions.
     */
    public static final String EMOTIONS = "emotions";

    /**
     * Key of emotion user id.
     */
    public static final String EMOTION_USER_ID = "emotionUserId";

    /**
     * Key of emotion content.
     */
    public static final String EMOTION_CONTENT = "emotionContent";

    /**
     * Key of emotion sort.
     */
    public static final String EMOTION_SORT = "emotionSort";

    /**
     * Key of emotion type.
     */
    public static final String EMOTION_TYPE = "emotionType";

    // Type constants
    /**
     * Emotion type - Emoji.
     */
    public static final int EMOTION_TYPE_C_EMOJI = 0;

    /**
     * Key of a short list of all emojis used in setting.
     */
    public static final String SHORT_T_LIST = "shortLists";

    /**
     * Key of end flag of emoji short list.
     */
    public static final String EOF_EMOJI = "endOfEmoji";

    private Emotion() {
    }
}
