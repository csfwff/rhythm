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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * 用于生成可以将 iconfont 的 Svg 格式转换并可以直接粘贴至 symbol-defs.js 的工具
 */
public class GenSymbolDefs {

    public static void main(String[] args) {
        System.out.println("输入从 iconfont 得到的 svg 链接，支持换行填写多个，输入 q 停止填写并生成：");
        List<String> list = new ArrayList<>();
        List<String> nameList = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        int count = 0;
        while (true) {
            System.out.print(count + ": ");
            String srcLink = scanner.nextLine();
            if (srcLink.equals("q")) {
                break;
            }
            list.add(srcLink);
            System.out.print("它的ID是什么？");
            String srcId = scanner.nextLine();
            nameList.add(srcId);
            count++;
        }
        System.out.println("生成结果：");
        for (int i = 0; i < list.size(); i++) {
            final String link = list.get(i);
            StringBuilder stringBuilder = new StringBuilder();
            Document document = Jsoup.parse(link);
            String icon = document.select("svg").html();
            String viewBox = document.select("svg").attr("viewBox");
            stringBuilder.append("svg += '");
            stringBuilder.append("<symbol id=\"");
            stringBuilder.append(nameList.get(i));
            stringBuilder.append("\" viewBox=\"");
            stringBuilder.append(viewBox);
            stringBuilder.append("\">");
            stringBuilder.append(icon);
            stringBuilder.append("</symbol>");
            stringBuilder.append("';");

            System.out.println(stringBuilder.toString().replaceAll("\n", ""));
        }
    }
}
