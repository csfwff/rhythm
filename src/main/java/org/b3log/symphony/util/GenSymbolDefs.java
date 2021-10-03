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
        System.out.println("输入从 iconfont 得到的 svg 链接，支持换行填写多个，输入 q 开始生成：");
        List<String> list = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String srcLink = scanner.nextLine();
            if (srcLink.equals("q")) {
                break;
            }
            list.add(srcLink);
        }
        System.out.println("生成结果：");
        for (String link : list) {
            StringBuilder stringBuilder = new StringBuilder();
            Document document = Jsoup.parse(link);
            String icon = document.select("svg").html();
            String viewBox = document.select("svg").attr("viewBox");
            stringBuilder.append("<symbol id=\"\" viewBox=\"");
            stringBuilder.append(viewBox);
            stringBuilder.append("\">");
            stringBuilder.append(icon);
            stringBuilder.append("</symbol>");

            System.out.println(stringBuilder);
        }
    }
}
