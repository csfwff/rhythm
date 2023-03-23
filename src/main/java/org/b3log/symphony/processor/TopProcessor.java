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
package org.b3log.symphony.processor;

import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import org.b3log.latke.http.Dispatcher;
import org.b3log.latke.http.Request;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.http.renderer.AbstractFreeMarkerRenderer;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.ioc.Singleton;
import org.b3log.latke.model.User;
import org.b3log.symphony.model.Article;
import org.b3log.symphony.model.Common;
import org.b3log.symphony.model.UserExt;
import org.b3log.symphony.processor.middleware.AnonymousViewCheckMidware;
import org.b3log.symphony.repository.ArticleRepository;
import org.b3log.symphony.repository.PointtransferRepository;
import org.b3log.symphony.repository.SponsorRepository;
import org.b3log.symphony.service.*;
import org.b3log.symphony.util.Symphonys;
import org.b3log.symphony.util.Vocation;
import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.b3log.symphony.model.Common.UA;

/**
 * Top ranking list processor.
 * <ul>
 * <li>Shows top (/top), GET</li>
 * <li>Top balance ranking list (/top/balance), GET</li>
 * <li>Top consumption ranking list (/top/consumption), GET</li>
 * <li>Top checkin ranking list (/top/checkin), GET</li>
 * <li>Top link ranking list (/top/link), GET</li>
 * </ul>
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.0.0.0, Feb 11, 2020
 * @since 1.3.0
 */
@Singleton
public class TopProcessor {

    /**
     * Data model service.
     */
    @Inject
    private DataModelService dataModelService;

    /**
     * Pointtransfer query service.
     */
    @Inject
    private PointtransferQueryService pointtransferQueryService;

    /**
     * Activity query service.
     */
    @Inject
    private ActivityQueryService activityQueryService;

    /**
     * User query service.
     */
    @Inject
    private UserQueryService userQueryService;

    /**
     * Link query service.
     */
    @Inject
    private LinkQueryService linkQueryService;

    /**
     * Point transfer repository.
     */
    @Inject
    private PointtransferRepository pointtransferRepository;

    /**
     * Sponsor repository.
     */
    @Inject
    private SponsorRepository sponsorRepository;

    /**
     * Sponsor repository.
     */
    @Inject
    private ArticleRepository articleRepository;

    @Inject
    private AvatarQueryService avatarQueryService;

    /**
     * Register request handlers.
     */
    public static void register() {
        final BeanManager beanManager = BeanManager.getInstance();
        final AnonymousViewCheckMidware anonymousViewCheckMidware = beanManager.getReference(AnonymousViewCheckMidware.class);

        final TopProcessor topProcessor = beanManager.getReference(TopProcessor.class);
        Dispatcher.get("/top", topProcessor::showTop, anonymousViewCheckMidware::handle);
        Dispatcher.get("/top/link", topProcessor::showLink, anonymousViewCheckMidware::handle);
        Dispatcher.get("/top/balance", topProcessor::showBalance, anonymousViewCheckMidware::handle);
        Dispatcher.get("/top/consumption", topProcessor::showConsumption, anonymousViewCheckMidware::handle);
        Dispatcher.get("/top/checkin", topProcessor::showCheckin, anonymousViewCheckMidware::handle);
        Dispatcher.get("/top/online", topProcessor::showOnline, anonymousViewCheckMidware::handle);
        Dispatcher.get("/top/adr", topProcessor::showADR, anonymousViewCheckMidware::handle);
        Dispatcher.get("/top/mofish", topProcessor::showMofish, anonymousViewCheckMidware::handle);
        Dispatcher.get("/top/smallmofish", topProcessor::showSmallMofish, anonymousViewCheckMidware::handle);
        Dispatcher.get("/top/lifeRestart", topProcessor::showLifeRestart, anonymousViewCheckMidware::handle);
        Dispatcher.get("/top/evolve", topProcessor::showEvolve, anonymousViewCheckMidware::handle);
        Dispatcher.get("/top/emoji", topProcessor::showEmoji, anonymousViewCheckMidware::handle);
        Dispatcher.get("/top/xiaoice", topProcessor::showXiaoice, anonymousViewCheckMidware::handle);
        Dispatcher.get("/top/invite", topProcessor::showInvite, anonymousViewCheckMidware::handle);
        Dispatcher.get("/top/donate", topProcessor::showDonate, anonymousViewCheckMidware::handle);
        Dispatcher.get("/top/perfect", topProcessor::showPerfect, anonymousViewCheckMidware::handle);
    }

    /**
     * Shows top.
     *
     * @param context the specified context
     */
    public void showTop(final RequestContext context) {
        final AbstractFreeMarkerRenderer renderer = new SkinRenderer(context, "top/index.ftl");
        final Map<String, Object> dataModel = renderer.getDataModel();
        dataModel.put(Common.SELECTED, Common.TOP);

        dataModelService.fillHeaderAndFooter(context, dataModel);
        dataModelService.fillRandomArticles(dataModel);
        dataModelService.fillSideHotArticles(dataModel);
        dataModelService.fillSideTags(dataModel);
        dataModelService.fillLatestCmts(dataModel);
    }

    /**
     * Shows link ranking list.
     *
     * @param context the specified context
     */
    public void showLink(final RequestContext context) {
        final AbstractFreeMarkerRenderer renderer = new SkinRenderer(context, "top/link.ftl");
        final Map<String, Object> dataModel = renderer.getDataModel();
        final List<JSONObject> topLinks = linkQueryService.getTopLink(Symphonys.TOP_CNT);
        dataModel.put(Common.TOP_LINKS, topLinks);

        dataModelService.fillHeaderAndFooter(context, dataModel);
        dataModelService.fillRandomArticles(dataModel);
        dataModelService.fillSideHotArticles(dataModel);
        dataModelService.fillSideTags(dataModel);
        dataModelService.fillLatestCmts(dataModel);
    }

    /**
     * Shows balance ranking list.
     *
     * @param context the specified context
     */
    public void showBalance(final RequestContext context) {
        final AbstractFreeMarkerRenderer renderer = new SkinRenderer(context, "top/balance.ftl");
        final Map<String, Object> dataModel = renderer.getDataModel();
        final List<JSONObject> users = pointtransferQueryService.getTopBalanceUsers(Symphonys.TOP_CNT);
        dataModel.put(Common.TOP_BALANCE_USERS, users);

        dataModelService.fillHeaderAndFooter(context, dataModel);
        dataModelService.fillRandomArticles(dataModel);
        dataModelService.fillSideHotArticles(dataModel);
        dataModelService.fillSideTags(dataModel);
        dataModelService.fillLatestCmts(dataModel);
    }

    /**
     * Shows consumption ranking list.
     *
     * @param context the specified context
     */
    public void showConsumption(final RequestContext context) {
        final AbstractFreeMarkerRenderer renderer = new SkinRenderer(context, "top/consumption.ftl");
        final Map<String, Object> dataModel = renderer.getDataModel();
        final List<JSONObject> users = pointtransferQueryService.getTopConsumptionUsers(Symphonys.TOP_CNT);
        dataModel.put(Common.TOP_CONSUMPTION_USERS, users);

        dataModelService.fillHeaderAndFooter(context, dataModel);
        dataModelService.fillRandomArticles(dataModel);
        dataModelService.fillSideHotArticles(dataModel);
        dataModelService.fillSideTags(dataModel);
        dataModelService.fillLatestCmts(dataModel);
    }

    /**
     * Shows checkin ranking list.
     *
     * @param context the specified context
     */
    public void showCheckin(final RequestContext context) {
        final AbstractFreeMarkerRenderer renderer = new SkinRenderer(context, "top/checkin.ftl");
        final Map<String, Object> dataModel = renderer.getDataModel();
        final List<JSONObject> users = activityQueryService.getTopCheckinUsers(Symphonys.TOP_CNT);
        dataModel.put(Common.TOP_CHECKIN_USERS, users);

        dataModelService.fillHeaderAndFooter(context, dataModel);
        dataModelService.fillRandomArticles(dataModel);
        dataModelService.fillSideHotArticles(dataModel);
        dataModelService.fillSideTags(dataModel);
        dataModelService.fillLatestCmts(dataModel);
    }

    /**
     * Shows online ranking list.
     *
     * @param context the specified context
     */
    public void showOnline(final RequestContext context) {
        final AbstractFreeMarkerRenderer renderer = new SkinRenderer(context, "top/online.ftl");
        final Map<String, Object> dataModel = renderer.getDataModel();
        final List<JSONObject> users = activityQueryService.getTopOnlineTimeUsers(Symphonys.TOP_CNT);
        dataModel.put("onlineTopUsers", users);

        dataModelService.fillHeaderAndFooter(context, dataModel);
        dataModelService.fillRandomArticles(dataModel);
        dataModelService.fillSideHotArticles(dataModel);
        dataModelService.fillSideTags(dataModel);
        dataModelService.fillLatestCmts(dataModel);
    }

    /**
     * Shows ADR score ranking list.
     *
     * @param context
     */
    public void showADR(final RequestContext context) {
        final AbstractFreeMarkerRenderer renderer = new SkinRenderer(context, "top/adr.ftl");
        final Map<String, Object> dataModel = renderer.getDataModel();
        final List<JSONObject> users = activityQueryService.getTopADR(Symphonys.TOP_CNT);
        dataModel.put("topUsers", users);

        dataModelService.fillHeaderAndFooter(context, dataModel);
        dataModelService.fillRandomArticles(dataModel);
        dataModelService.fillSideHotArticles(dataModel);
        dataModelService.fillSideTags(dataModel);
        dataModelService.fillLatestCmts(dataModel);
    }

    /**
     * Shows emoji score ranking list.
     *
     * @param context
     */
    public void showEmoji(final RequestContext context) {
        final AbstractFreeMarkerRenderer renderer = new SkinRenderer(context, "top/emoji.ftl");
        final Map<String, Object> dataModel = renderer.getDataModel();
        final List<JSONObject> users = activityQueryService.getTopEmoji(Symphonys.TOP_CNT);
        dataModel.put("topUsers", users);

        dataModelService.fillHeaderAndFooter(context, dataModel);
        dataModelService.fillRandomArticles(dataModel);
        dataModelService.fillSideHotArticles(dataModel);
        dataModelService.fillSideTags(dataModel);
        dataModelService.fillLatestCmts(dataModel);
    }

    /**
     * Shows Mofish score ranking list.
     *
     * @param context
     */
    public void showMofish(final RequestContext context) {
        final AbstractFreeMarkerRenderer renderer = new SkinRenderer(context, "top/mofish.ftl");
        final Map<String, Object> dataModel = renderer.getDataModel();
        final List<JSONObject> users = activityQueryService.getTopMofish(Symphonys.TOP_CNT);
        dataModel.put("topUsers", users);

        dataModelService.fillHeaderAndFooter(context, dataModel);
        dataModelService.fillRandomArticles(dataModel);
        dataModelService.fillSideHotArticles(dataModel);
        dataModelService.fillSideTags(dataModel);
        dataModelService.fillLatestCmts(dataModel);
    }

     /**
     * Shows SmallMofish score ranking list.
     *
     * @param context
     */
    public void showSmallMofish(final RequestContext context) {
      final AbstractFreeMarkerRenderer renderer = new SkinRenderer(context, "top/smallmofish.ftl");
      final Map<String, Object> dataModel = renderer.getDataModel();
      final List<JSONObject> users = activityQueryService.getTopSmallMofish(Symphonys.TOP_CNT);
      dataModel.put("topUsers", users);

      dataModelService.fillHeaderAndFooter(context, dataModel);
      dataModelService.fillRandomArticles(dataModel);
      dataModelService.fillSideHotArticles(dataModel);
      dataModelService.fillSideTags(dataModel);
      dataModelService.fillLatestCmts(dataModel);
  }

    /**
     * Shows Life Restart ranking list.
     *
     * @param context
     */
    public void showLifeRestart(final RequestContext context) {
        final AbstractFreeMarkerRenderer renderer = new SkinRenderer(context, "top/lifeRestart.ftl");
        final Map<String, Object> dataModel = renderer.getDataModel();
        final List<JSONObject> users = activityQueryService.getTopLifeRestart(Symphonys.TOP_CNT);
        dataModel.put("topUsers", users);
        for (JSONObject user : users) {
            JSONObject profile = user.optJSONObject("profile");
            avatarQueryService.fillUserAvatarURL(profile);
            user.put("profile", profile);
        }

        dataModelService.fillHeaderAndFooter(context, dataModel);
        dataModelService.fillRandomArticles(dataModel);
        dataModelService.fillSideHotArticles(dataModel);
        dataModelService.fillSideTags(dataModel);
        dataModelService.fillLatestCmts(dataModel);
    }

    /**
     * Shows Evolve ranking list.
     *
     * @param context
     */
    public void showEvolve(final RequestContext context) {
        final AbstractFreeMarkerRenderer renderer = new SkinRenderer(context, "top/evolve.ftl");
        final Request request = context.getRequest();
        final String type = request.getParameter("type") != null ?
                request.getParameter("type") : "achievement";
        final Map<String, Object> dataModel = renderer.getDataModel();
        final List<JSONObject> users = activityQueryService.getEvolve(type, Symphonys.TOP_CNT);
        for (JSONObject user : users) {
            JSONObject profile = user.optJSONObject("profile");
            avatarQueryService.fillUserAvatarURL(profile);
            user.put("profile", profile);
        }
        dataModel.put("topUsers", users);
        dataModel.put("type", type);

        dataModelService.fillHeaderAndFooter(context, dataModel);
        dataModelService.fillRandomArticles(dataModel);
        dataModelService.fillSideHotArticles(dataModel);
        dataModelService.fillSideTags(dataModel);
        dataModelService.fillLatestCmts(dataModel);
    }

    /**
     * Shows Xiaoice ranking list.
     *
     * @param context
     */
    public void showXiaoice(final RequestContext context) {
        final AbstractFreeMarkerRenderer renderer = new SkinRenderer(context, "top/xiaoice.ftl");
        final Map<String, Object> dataModel = renderer.getDataModel();
        final Request request = context.getRequest();
        final String type = request.getParameter("type") != null ?
                request.getParameter("type") : "0";
        dataModel.put("type", type);
        final HttpResponse response = HttpRequest.get("https://pwl.yuis.cc/GetXiaoIceGameRank?key=xiaoIceGame&type=" + type)
                .connectionTimeout(3000).timeout(7000).header("User-Agent", Vocation.UA)
                .send();
        if (200 == response.statusCode()) {
            response.charset("UTF-8");
            final JSONObject result = new JSONObject(response.bodyText());
            JSONArray dataList = result.optJSONArray("data");
            List<JSONObject> resultList = new ArrayList<>();
            for (int i = 0; i < dataList.length(); i++) {
                JSONObject data = dataList.optJSONObject(i);
                String uname = data.optString("uname");
                JSONObject family = new JSONObject(data.optString("family"));

                String[] lvFilter = new String[]{"黄阶低级", "黄阶中级", "黄阶高级", "玄阶低级", "玄阶中级", "玄阶高级", "地阶低级", "地阶中级", "地阶高级", "天阶低级", "天阶中级", "天阶高级"};
                int ancestry = family.optInt("ancestry");
                int gongfa = family.optInt("gongfa");
                data.put("ancestry", lvFilter[ancestry]);
                data.put("gongfa", lvFilter[gongfa]);
                data.remove("family");
                try {
                    JSONObject user = userQueryService.getUserByName(uname);
                    data.put("userAvatarURL", user.optString(UserExt.USER_AVATAR_URL));
                    data.put("userIntro", user.optString(UserExt.USER_INTRO));
                    data.put("userURL", user.optString(User.USER_URL));
                    data.put("userNo", user.optInt(UserExt.USER_NO));
                    data.put("userAppRole", user.optInt(UserExt.USER_APP_ROLE));
                    avatarQueryService.fillUserAvatarURL(data);
                } catch (Exception e) {
                    continue;
                }

                resultList.add(data);
            }
            dataModel.put("data", resultList);
        }

        dataModelService.fillHeaderAndFooter(context, dataModel);
        dataModelService.fillRandomArticles(dataModel);
        dataModelService.fillSideHotArticles(dataModel);
        dataModelService.fillSideTags(dataModel);
        dataModelService.fillLatestCmts(dataModel);
    }

    /**
     * Shows Invite ranking list.
     *
     * @param context
     */
    public void showInvite(final RequestContext context) {
        final AbstractFreeMarkerRenderer renderer = new SkinRenderer(context, "top/invite.ftl");
        final Map<String, Object> dataModel = renderer.getDataModel();
        try {
            List<JSONObject> list = pointtransferRepository.select("select toId,count(toId) as c " +
                    "from " + pointtransferRepository.getName() + " " +
                    "where type = 6 " +
                    "group by toId " +
                    "order by c desc " +
                    "limit 64;");
            List<JSONObject> result = new ArrayList<>();
            for (JSONObject user : list) {
                try {
                    JSONObject userData = userQueryService.getUser(user.optString("toId"));
                    avatarQueryService.fillUserAvatarURL(userData);
                    user.put("profile", userData);
                    result.add(user);
                } catch (Exception e) {
                    continue;
                }
            }
            dataModel.put("data", result);
        } catch (Exception ignored) {
        }

        dataModelService.fillHeaderAndFooter(context, dataModel);
        dataModelService.fillRandomArticles(dataModel);
        dataModelService.fillSideHotArticles(dataModel);
        dataModelService.fillSideTags(dataModel);
        dataModelService.fillLatestCmts(dataModel);
    }

    /**
     * Shows Donate ranking list.
     *
     * @param context
     */
    public void showDonate(final RequestContext context) {
      final AbstractFreeMarkerRenderer renderer = new SkinRenderer(context, "top/donate.ftl");
      final Map<String, Object> dataModel = renderer.getDataModel();
      try {
        List<JSONObject> totalList = sponsorRepository.select("select sum(amount) as totalAmount " +
                  "from " + sponsorRepository.getName() + " " +
                  "limit 1;");
        JSONObject totalJSON = totalList.get(0);
        double totalAmount = totalJSON.optDouble("totalAmount");
        BigDecimal donateMakeDaysBigDecimal = new BigDecimal(String.valueOf(totalAmount / 13.33));
        double donateMakeDays = donateMakeDaysBigDecimal.setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();
        totalJSON.put("donateMakeDays", donateMakeDays);
        dataModel.put("totalData", totalJSON);
      }
      catch (Exception ignored) {
      }
      try {
          List<JSONObject> list = sponsorRepository.select("select userId as userId,sum(amount) as total,count(*) as totalCount " +
                  "from " + sponsorRepository.getName() + " " +
                  "group by userId " +
                  "order by total desc " +
                  "limit 64;");
          List<JSONObject> result = new ArrayList<>();
          for (JSONObject user : list) {
              try {
                  JSONObject userData = userQueryService.getUser(user.optString("userId"));
                  avatarQueryService.fillUserAvatarURL(userData);
                  user.put("profile", userData);
                  result.add(user);
              } catch (Exception e) {
                  continue;
              }
          }
          dataModel.put("data", result);
      } catch (Exception ignored) {
      }

      dataModelService.fillHeaderAndFooter(context, dataModel);
      dataModelService.fillRandomArticles(dataModel);
      dataModelService.fillSideHotArticles(dataModel);
      dataModelService.fillSideTags(dataModel);
      dataModelService.fillLatestCmts(dataModel);
  }

    /**
     * Shows Donate ranking list.
     *
     * @param context
     */
    public void showPerfect(final RequestContext context) {
        final AbstractFreeMarkerRenderer renderer = new SkinRenderer(context, "top/perfect.ftl");
        final Map<String, Object> dataModel = renderer.getDataModel();

        try {
            List<JSONObject> list = articleRepository.select("select "+ Article.ARTICLE_AUTHOR_ID +" as userId,count(*) as count " +
                    "from " + articleRepository.getName() + " " +
                    "where " + Article.ARTICLE_PERFECT + " = " + Article.ARTICLE_PERFECT_C_PERFECT+ " " +
                    "group by userId " +
                    "order by count desc " +
                    "limit 64;");
            List<JSONObject> result = new ArrayList<>();
            for (JSONObject user : list) {
                try {
                    JSONObject userData = userQueryService.getUser(user.optString("userId"));
                    avatarQueryService.fillUserAvatarURL(userData);
                    user.put("profile", userData);
                    result.add(user);
                } catch (Exception e) {
                    continue;
                }
            }
            dataModel.put("data", result);
        } catch (Exception ignored) {
        }

        dataModelService.fillHeaderAndFooter(context, dataModel);
        dataModelService.fillRandomArticles(dataModel);
        dataModelService.fillSideHotArticles(dataModel);
        dataModelService.fillSideTags(dataModel);
        dataModelService.fillLatestCmts(dataModel);
    }
}
