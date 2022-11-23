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
package org.b3log.symphony.util;

import org.json.JSONObject;

import java.util.List;
import java.util.stream.Collectors;

public class DesensitizeUtil {

    public static List<JSONObject> articlesDesensitize(List<JSONObject> articles) {
        return articles.stream().peek(article -> {
            article.remove("articleUA");
            article.remove("articleOriginalContent");
            article.remove("articleContent");
            JSONObject articleAuthor = article.optJSONObject("articleAuthor");
            articleAuthor.remove("userLatestLoginIP");
            articleAuthor.remove("userPassword");
            articleAuthor.remove("userPhone");
            articleAuthor.remove("userQQ");
            articleAuthor.remove("userCity");
            articleAuthor.remove("userCountry");
            articleAuthor.remove("userEmail");
            articleAuthor.remove("secret2fa");
        }).collect(Collectors.toList());
    }

    public static JSONObject articleDesensitize(final JSONObject article) {
        article.remove("articleUA");
        JSONObject articleAuthor = article.optJSONObject("articleAuthor");
        articleAuthor.remove("userLatestLoginIP");
        articleAuthor.remove("userPassword");
        articleAuthor.remove("userPhone");
        articleAuthor.remove("userQQ");
        articleAuthor.remove("userCity");
        articleAuthor.remove("userCountry");
        articleAuthor.remove("userEmail");
        articleAuthor.remove("secret2fa");
        article.put("articleAuthor", articleAuthor);
        return article;
    }
}
