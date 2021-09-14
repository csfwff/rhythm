package org.b3log.symphony.util;

import com.google.common.collect.Maps;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.assertj.core.util.Lists;
import org.b3log.symphony.Server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class IpUtil {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(IpUtil.class);

    // apnic 数据下载地址
    final private static String DOWNLOAD_URL = "http://ftp.apnic.net/apnic/stats/apnic/delegated-apnic-latest";
    final private static String UA = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.63 Safari/537.36";
    private static File file = new File("delegated-apnic-latest.tmp");

    // 只存放属于中国的ip段
    private static final Map<Integer, List<int[]>> chinaIps = Maps.newHashMap();

    public static void init() {
        try {
            LOGGER.log(Level.INFO, "Downloading APNIC File...");
            try {
                final HttpResponse response = HttpRequest.get(DOWNLOAD_URL)
                        .connectionTimeout(30000).timeout(70000).header("User-Agent", UA)
                        .send();
                if (200 == response.statusCode()) {
                    response.charset("UTF-8");
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    fileOutputStream.write(response.bodyText().getBytes(StandardCharsets.UTF_8));
                    fileOutputStream.close();
                    System.out.println(response.bodyText().length());
                }
            } catch (Exception e) {
                LOGGER.log(Level.ERROR, "Cannot download APNIC file", e);
            }

            // ip格式: add1.add2.add3.add4
            // key为 : add1*256+add2
            // value为int[2]: int[0]存的add3*256+add4的开始ip int[4]存的结束ip
            Map<Integer, List<int[]>> map = Maps.newHashMap();

            InputStream input = new FileInputStream(file);
            List<String> lines = IOUtils.readLines(input, StandardCharsets.UTF_8);
            for (String line : lines) {
                if (line.startsWith("apnic|CN|ipv4|")) {
                    // 只处理属于中国的ipv4地址
                    String[] strs = line.split("\\|");
                    String ip = strs[3];
                    String[] add = ip.split("\\.");
                    int count = Integer.valueOf(strs[4]);

                    int startIp = Integer.parseInt(add[0]) * 256 + Integer.parseInt(add[1]);
                    while (count > 0) {
                        if (count >= 65536) {
                            // add1,add2 整段都是中国ip
                            chinaIps.put(startIp, Collections.EMPTY_LIST);
                            count -= 65536;
                            startIp += 1;
                        } else {

                            int[] ipRange = new int[2];
                            ipRange[0] = Integer.parseInt(add[2]) * 256 + Integer.parseInt(add[3]);
                            ipRange[1] = ipRange[0] + count;
                            count -= count;

                            List<int[]> list = map.get(startIp);
                            if (list == null) {
                                list = Lists.newArrayList();
                                map.put(startIp, list);
                            }

                            list.add(ipRange);
                        }
                    }
                }
            }
            chinaIps.putAll(map);
            LOGGER.log(Level.INFO, "APNIC is ready. [absoluteFilePath=" + file.getAbsoluteFile() + "]");
        } catch (Exception e) {
            LOGGER.log(Level.ERROR, "Initializing APNIC failed.", e);
        }
    }

    public static boolean isChinaIp(String ip) {
        if (StringUtils.isEmpty(ip)) {
            return false;
        }
        String[] strs = ip.split("\\.");
        if (strs.length != 4) {
            return false;
        }
        int key = Integer.valueOf(strs[0]) * 256 + Integer.valueOf(strs[1]);
        List<int[]> list = chinaIps.get(key);
        if (list == null) {
            return false;
        }
        if (list.size() == 0) {
            // 整段都是中国ip
            return true;
        }
        int ipValue = Integer.valueOf(strs[2]) * 256 + Integer.valueOf(strs[3]);
        for (int[] ipRange : list) {
            if (ipValue >= ipRange[0] && ipValue <= ipRange[1]) {
                return true;
            }
        }

        return false;
    }
}
