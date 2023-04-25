package org.b3log.symphony.util;

import com.alibaba.fastjson.JSON;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.symphony.model.Option;
import org.b3log.symphony.service.OptionMgmtService;
import org.b3log.symphony.service.OptionQueryService;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ReservedWords {

    private static final Logger LOGGER = LogManager.getLogger(ReservedWords.class);

    private static OptionMgmtService optionMgmtService;

    private static OptionQueryService optionQueryService;

    private static final String OPTION_NAME = "reservedWordList";

    private static final List<String> CACHE = new ArrayList<>();

    public static void init() {
        final BeanManager beanManager = BeanManager.getInstance();
        optionMgmtService = beanManager.getReference(OptionMgmtService.class);
        optionQueryService = beanManager.getReference(OptionQueryService.class);
        JSONObject option = optionQueryService.getOption(OPTION_NAME);
        if (null == option) {
            JSONObject addOption = new JSONObject();
            addOption.put(Keys.OBJECT_ID, OPTION_NAME);
            addOption.put(Option.OPTION_CATEGORY, "reversed-word-list");
            addOption.put(Option.OPTION_VALUE, "[]");
            optionMgmtService.addOption(addOption);
            LOGGER.log(Level.INFO, "Reserved word list is empty and generated.");
        } else {
            JSONArray array = new JSONArray(option.optString(Option.OPTION_VALUE));
            for (int i = 0; i < array.length(); i++) {
                String word = String.valueOf(array.get(i));
                CACHE.add(word);
            }
        }
        LOGGER.log(Level.INFO, "Reserved words system is ready.");
    }

    public static void add(String word) {
        if (!CACHE.contains(word)) {
            JSONObject option = optionQueryService.getOption(OPTION_NAME);
            JSONArray array = new JSONArray(option.optString(Option.OPTION_VALUE));
            array.put(word);
            JSONObject addOption = new JSONObject();
            addOption.put(Keys.OBJECT_ID, OPTION_NAME);
            addOption.put(Option.OPTION_CATEGORY, "reversed-word-list");
            addOption.put(Option.OPTION_VALUE, array.toString());
            optionMgmtService.updateOption(OPTION_NAME, addOption);
            CACHE.add(word);
        }
    }

    public static void remove(String word) {
        if (CACHE.contains(word)) {
            JSONObject option = optionQueryService.getOption(OPTION_NAME);
            JSONArray array = new JSONArray(option.optString(Option.OPTION_VALUE));
            for (int i = array.length() - 1; i >= 0; i--) {
                if (String.valueOf(array.get(i)).equals(word)) {
                    array.remove(i);
                }
            }
            JSONObject addOption = new JSONObject();
            addOption.put(Keys.OBJECT_ID, OPTION_NAME);
            addOption.put(Option.OPTION_CATEGORY, "reversed-word-list");
            addOption.put(Option.OPTION_VALUE, array.toString());
            optionMgmtService.updateOption(OPTION_NAME, addOption);
            for (int i = CACHE.size() - 1; i >= 0; i--) {
                if (CACHE.get(i).equals(word)) {
                    CACHE.remove(i);
                }
            }
        }
    }

    public static JSONObject get(String word) {
        if (CACHE.contains(word)) {
            JSONObject option = new JSONObject();
            option.put(Keys.OBJECT_ID, word);
            option.put(Option.OPTION_CATEGORY, word);
            option.put(Option.OPTION_VALUE, word);
            return option;
        }
        return new JSONObject();
    }

    public static List<JSONObject> getList() {
        List<JSONObject> list = new ArrayList<>();
        for (String i : CACHE) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(Option.OPTION_VALUE, i);
            jsonObject.put(Option.OPTION_CATEGORY, i);
            jsonObject.put(Keys.OBJECT_ID, i);
            list.add(jsonObject);
        }
        return list;
    }

    public static boolean contains(String word) {
        return CACHE.contains(word);
    }
}
