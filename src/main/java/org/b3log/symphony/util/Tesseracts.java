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

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.b3log.latke.util.Execs;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Tesseract-OCR utilities.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.3, Mar 28, 2021
 * @since 1.4.0
 */
public final class Tesseracts {

    /**
     * Recognizes a single character from the specified image file path.
     *
     * @param imagePath the specified image file path
     * @return the recognized character
     */
    public static String recognizeCharacter(final String imagePath) {
        try {
            Execs.exec(new String[]{"sh", "-c", "tesseract " + imagePath + " " + imagePath + " -l chi_sim --psm 8"}, 1000 * 3);
            String result = StringUtils.trim(IOUtils.toString(new FileInputStream(imagePath + ".txt"), StandardCharsets.UTF_8));
            File temp1 = new File(imagePath + ".txt");
            if (temp1.exists()) {
                temp1.delete();
            }
            return result;
        } catch (final IOException e) {
            return "";
        }
    }

    /**
     * Private constructor.
     */
    private Tesseracts() {
    }
}
