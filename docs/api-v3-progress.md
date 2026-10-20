# /api/v3 当前进度与剩余工作

快照：2026-10-20；以本文件生成时工作区源码为准。后续新增入口须同步重扫，本文件不替代历史批次记录。

## 判定口径

- **已注册源码**：`Router.requestMapping` 调用 `ApiV3Processor.register()`，后者能到达具体 Processor 的 Dispatcher 注册；不把孤立的处理方法、服务类或 API.md 条目计为接口。
- **隔离证据**：历史批次明确记载执行过的 runner；常含 SQLite、仓储/锁/身份/外部服务适配器，不等于真实 MySQL、完整 IoC、浏览器或端到端验证。本轮定向重跑结果见下方证据表，其余历史 runner 未全量重跑。
- **运行验收**：当前没有本次可确认的完整应用启动、迁移、真实 HTTP/Socket、PC/mobile 与第三方客户端全链路验收证据。下表所有源码接口均仍需运行验收。
- 当前范围排除管理后台、金手指、内部回调、游戏计分和支付管理；公开支付跳转仍在范围。用户侧职业、勋章、会员能力不能仅因同模块存在后台接口就整体排除。OAuth管理员登记/管理为用户明确纳入的例外，其余后台仍排除。

## 当前数量

已注册 **197 条（196 HTTP + 1 WebSocket）**；HTTP按 METHOD+完整path计数；GET/PUT/DELETE共用路径仍分别计数。方法分布：DELETE 20, GET 108, POST 39, PUT 29, WEBSOCKET 1。

连接链路：`processor/Router.java#requestMapping` → `processor/ApiV3Processor.java#register` → 各模块 `register`。以下路径均包含完整 `/api/v3` 前缀，代码位置以生成时行号为准。

注意：当前生成的“全仓库744路由、398浏览器引用”是不同口径，包含旧HTTP/WS等，不能当作v3接口数。静态注册成功也不证明完整IoC启动成功。

## 已注册接口清单

每个分组显示实际 Dispatcher 所在文件；“处理器与门禁”来自注册表达式，仅用于定位，不替代方法内授权审查。`WEBSOCKET` 是协议入口，不是普通GET资源。

### 注册与找回密码（6）

代码：`src/main/java/org/b3log/symphony/processor/ApiV3AccountProcessor.java`。状态：已注册源码；运行未验收。

| 方法 | 路径 | 行号 | 处理器与门禁 |
| --- | --- | --- | --- |
| GET | `/api/v3/auth/registration-options` | 38 | `p::registrationOptions,anonymous::handle` |
| GET | `/api/v3/auth/captcha-options` | 39 | `p::captchaOptions,anonymous::handle` |
| POST | `/api/v3/auth/password-reset/complete` | 40 | `p::completePasswordReset,anonymous::handle` |
| POST | `/api/v3/auth/password-reset/request` | 41 | `p::requestPasswordReset,anonymous::handle` |
| POST | `/api/v3/auth/registration/request` | 42 | `p::requestRegistration,anonymous::handle` |
| POST | `/api/v3/auth/registration/complete` | 43 | `p::completeRegistration,anonymous::handle` |

### 活跃奖励（2）

代码：`src/main/java/org/b3log/symphony/processor/ApiV3ActivityProcessor.java`。状态：已注册源码；运行未验收。

| 方法 | 路径 | 行号 | 处理器与门禁 |
| --- | --- | --- | --- |
| GET | `/api/v3/me/activity/yesterday-reward` | 24 | `p::status, auth::activityRead` |
| POST | `/api/v3/me/activity/yesterday-reward` | 25 | `p::claim, auth::activityWrite, writes::handle` |

### 文章热度（本轮接线）（1）

代码：`src/main/java/org/b3log/symphony/processor/ApiV3ArticleHeatProcessor.java`。状态：已注册源码；运行未验收。

| 方法 | 路径 | 行号 | 处理器与门禁 |
| --- | --- | --- | --- |
| GET | `/api/v3/articles/{articleId}/heat` | 20 | `p::heat, auth::contentRead` |

### 文章感谢与打赏（4）

代码：`src/main/java/org/b3log/symphony/processor/ApiV3ArticleRewardProcessor.java`。状态：已注册源码；运行未验收。

| 方法 | 路径 | 行号 | 处理器与门禁 |
| --- | --- | --- | --- |
| GET | `/api/v3/articles/{articleId}/thanks` | 23 | `processor::thanks, auth::contentRead` |
| GET | `/api/v3/articles/{articleId}/reward` | 24 | `processor::reward, auth::contentRead` |
| POST | `/api/v3/articles/{articleId}/thanks` | 25 | `processor::thank, auth::contentWrite, writes::handle` |
| POST | `/api/v3/articles/{articleId}/reward` | 26 | `processor::purchase, auth::contentWrite, writes::handle` |

### 文章创作（3）

代码：`src/main/java/org/b3log/symphony/processor/ApiV3ArticleWriteProcessor.java`。状态：已注册源码；运行未验收。

| 方法 | 路径 | 行号 | 处理器与门禁 |
| --- | --- | --- | --- |
| POST | `/api/v3/articles` | 26 | `processor::create, auth::contentWrite, writes::handle` |
| PUT | `/api/v3/articles/{articleId}` | 27 | `processor::update, auth::contentWrite, writes::handle` |
| DELETE | `/api/v3/articles/{articleId}` | 28 | `processor::remove, auth::contentWrite, writes::handle` |

### 背包使用（4）

代码：`src/main/java/org/b3log/symphony/processor/ApiV3BagProcessor.java`。状态：已注册源码；运行未验收。

| 方法 | 路径 | 行号 | 处理器与门禁 |
| --- | --- | --- | --- |
| POST | `/api/v3/me/bag/checkin1day/use` | 24 | `p::oneDay, auth::activityWrite, writes::handle` |
| POST | `/api/v3/me/bag/checkin2days/use` | 25 | `p::twoDays, auth::activityWrite, writes::handle` |
| POST | `/api/v3/me/bag/patchCheckinCard/use` | 26 | `p::patch, auth::activityWrite, writes::handle` |
| POST | `/api/v3/me/bag/nameCard/use` | 27 | `p::name, auth::activityWrite, auth::settingsWrite, writes::handle` |

### 清风明月详情及写入（5）

代码：`src/main/java/org/b3log/symphony/processor/ApiV3BreezemoonProcessor.java`。状态：已注册源码；运行未验收。

| 方法 | 路径 | 行号 | 处理器与门禁 |
| --- | --- | --- | --- |
| GET | `/api/v3/breezemoons/{breezemoonId}` | 33 | `p::detail, auth::optionalContent, anonymous::handle` |
| GET | `/api/v3/me/watching/breezemoons` | 34 | `p::following, auth::contentRead` |
| PUT | `/api/v3/breezemoons/{breezemoonId}` | 35 | `p::update, auth::contentWrite, writes::handle` |
| POST | `/api/v3/breezemoons` | 36 | `p::create, auth::contentWrite, writes::handle` |
| DELETE | `/api/v3/breezemoons/{breezemoonId}` | 37 | `p::remove, auth::contentWrite, writes::handle` |

### 聊天室（10）

代码：`src/main/java/org/b3log/symphony/processor/ApiV3ChatProcessor.java`。状态：已注册源码；运行未验收。

| 方法 | 路径 | 行号 | 处理器与门禁 |
| --- | --- | --- | --- |
| POST | `/api/v3/chat/messages` | 32 | `p::create,auth::contentWrite,writes::handle` |
| DELETE | `/api/v3/chat/messages/{messageId}` | 33 | `p::withdraw,auth::contentWrite,writes::handle` |
| GET | `/api/v3/chat/messages` | 34 | `p::list,auth::contentRead` |
| GET | `/api/v3/chat/online-users` | 35 | `p::online,auth::contentRead` |
| GET | `/api/v3/chat/topic` | 36 | `p::topic,auth::contentRead` |
| PUT | `/api/v3/chat/topic` | 37 | `p::setTopic,auth::contentWrite,writes::handle` |
| POST | `/api/v3/chat/barrager` | 38 | `p::sendBarrage,auth::contentWrite,writes::handle` |
| GET | `/api/v3/chat/barrager/price` | 39 | `p::barragerPrice,auth::contentRead` |
| GET | `/api/v3/chat/messages/{messageId}` | 40 | `p::detail,auth::contentRead` |
| GET | `/api/v3/chat/messages/{messageId}/context` | 41 | `p::context,auth::contentRead` |

### 专栏管理（4）

代码：`src/main/java/org/b3log/symphony/processor/ApiV3ColumnProcessor.java`。状态：已注册源码；运行未验收。

| 方法 | 路径 | 行号 | 处理器与门禁 |
| --- | --- | --- | --- |
| PUT | `/api/v3/columns/{columnId}/title` | 26 | `p::title, auth::contentWrite, writes::handle` |
| PUT | `/api/v3/columns/{columnId}/cover` | 27 | `p::cover, auth::contentWrite, writes::handle` |
| PUT | `/api/v3/me/columns/order` | 28 | `p::order, auth::contentWrite, writes::handle` |
| DELETE | `/api/v3/columns/{columnId}` | 29 | `p::remove, auth::contentWrite, writes::handle` |

### 评论写入及投票（8）

代码：`src/main/java/org/b3log/symphony/processor/ApiV3CommentProcessor.java`。状态：已注册源码；运行未验收。

| 方法 | 路径 | 行号 | 处理器与门禁 |
| --- | --- | --- | --- |
| POST | `/api/v3/articles/{articleId}/comments` | 27 | `processor::create, auth::commentsWrite, writes::handle` |
| PUT | `/api/v3/comments/{commentId}` | 28 | `processor::update, auth::commentsWrite, writes::handle` |
| DELETE | `/api/v3/comments/{commentId}` | 29 | `processor::remove, auth::commentsWrite, writes::handle` |
| POST | `/api/v3/comments/{commentId}/thanks` | 30 | `processor::thank, auth::commentsWrite, writes::handle` |
| POST | `/api/v3/comments/{commentId}/accept` | 31 | `processor::accept, auth::commentsWrite, writes::handle` |
| GET | `/api/v3/comments/{commentId}/vote` | 32 | `processor::vote, auth::contentRead` |
| PUT | `/api/v3/comments/{commentId}/vote` | 33 | `processor::setVote, auth::commentsWrite, writes::handle` |
| DELETE | `/api/v3/comments/{commentId}/vote` | 34 | `processor::clearVote, auth::commentsWrite, writes::handle` |

### 鱼游、大事记、同城与统计读取（8）

代码：`src/main/java/org/b3log/symphony/processor/ApiV3CommunityProcessor.java`。状态：已注册源码；运行未验收。

| 方法 | 路径 | 行号 | 处理器与门禁 |
| --- | --- | --- | --- |
| GET | `/api/v3/fish-games` | 23 | `processor::games, auth::optionalContent, anonymous::handle` |
| GET | `/api/v3/fish-games/{gameId}/comments` | 24 | `processor::gameComments, auth::optionalContent, anonymous::handle` |
| GET | `/api/v3/fish-games/{gameId}` | 25 | `processor::game, auth::optionalContent, anonymous::handle` |
| GET | `/api/v3/milestones` | 26 | `processor::milestones, auth::optionalContent, anonymous::handle` |
| GET | `/api/v3/milestones/{milestoneId}` | 27 | `processor::milestone, auth::optionalContent, anonymous::handle` |
| GET | `/api/v3/statistics` | 28 | `processor::statistics, auth::optionalContent, anonymous::handle` |
| GET | `/api/v3/cities/{cityName}/users` | 29 | `processor::cityUsers, auth::optionalProfile, anonymous::handle` |
| GET | `/api/v3/cities/{cityName}/articles` | 30 | `processor::cityArticles, auth::optionalContent, anonymous::handle` |

### 文章与评论读取（3）

代码：`src/main/java/org/b3log/symphony/processor/ApiV3ContentProcessor.java`。状态：已注册源码；运行未验收。

| 方法 | 路径 | 行号 | 处理器与门禁 |
| --- | --- | --- | --- |
| GET | `/api/v3/articles` | 30 | `processor::articles, auth::optionalContent, anonymous::handle` |
| GET | `/api/v3/articles/{articleId}/comments` | 31 | `processor::comments, auth::optionalContent, anonymous::handle` |
| GET | `/api/v3/articles/{articleId}` | 32 | `processor::article, auth::optionalContent, anonymous::handle` |

### 搜索、标签、领域、主页与清风明月列表（19）

代码：`src/main/java/org/b3log/symphony/processor/ApiV3DiscoveryProcessor.java`。状态：已注册源码；运行未验收。

| 方法 | 路径 | 行号 | 处理器与门禁 |
| --- | --- | --- | --- |
| GET | `/api/v3/search` | 38 | `processor::search, auth::optionalContent, anonymous::handle` |
| GET | `/api/v3/tags` | 39 | `processor::tags, auth::optionalContent, anonymous::handle` |
| GET | `/api/v3/tags/{tagId}/articles` | 40 | `processor::tagArticles, auth::optionalContent, anonymous::handle` |
| GET | `/api/v3/tags/{tagId}` | 41 | `processor::tag, auth::optionalContent, anonymous::handle` |
| GET | `/api/v3/domains` | 42 | `processor::domains, auth::optionalContent, anonymous::handle` |
| GET | `/api/v3/domains/{domainId}/tags` | 43 | `processor::domainTags, auth::optionalContent, anonymous::handle` |
| GET | `/api/v3/domains/{domainId}/articles` | 44 | `processor::domainArticles, auth::optionalContent, anonymous::handle` |
| GET | `/api/v3/domains/{domainId}` | 45 | `processor::domain, auth::optionalContent, anonymous::handle` |
| GET | `/api/v3/users/{userName}/comments` | 46 | `processor::userComments, auth::optionalContent, anonymous::handle` |
| GET | `/api/v3/users/{userName}/watching/articles` | 47 | `processor::watchedArticles, auth::optionalContent, anonymous::handle` |
| GET | `/api/v3/users/{userName}/watching` | 48 | `processor::watching, auth::optionalContent, anonymous::handle` |
| GET | `/api/v3/users/{userName}/articles` | 49 | `processor::userArticles, auth::optionalContent, anonymous::handle` |
| GET | `/api/v3/users/{userName}/breezemoons` | 50 | `processor::userBreezemoons, auth::optionalContent, anonymous::handle` |
| GET | `/api/v3/users/{userName}/following/users` | 51 | `processor::followingUsers, auth::optionalProfile, anonymous::handle` |
| GET | `/api/v3/users/{userName}/following/tags` | 52 | `processor::followingTags, auth::optionalContent, anonymous::handle` |
| GET | `/api/v3/users/{userName}/following/articles` | 53 | `processor::followingArticles, auth::optionalContent, anonymous::handle` |
| GET | `/api/v3/users/{userName}/followers` | 54 | `processor::followers, auth::optionalProfile, anonymous::handle` |
| GET | `/api/v3/users/{userName}` | 55 | `processor::user, auth::optionalProfile, anonymous::handle` |
| GET | `/api/v3/breezemoons` | 56 | `processor::breezemoons, auth::optionalContent, anonymous::handle` |

### 草稿写入（3）

代码：`src/main/java/org/b3log/symphony/processor/ApiV3DraftProcessor.java`。状态：已注册源码；运行未验收。

| 方法 | 路径 | 行号 | 处理器与门禁 |
| --- | --- | --- | --- |
| POST | `/api/v3/me/drafts` | 28 | `processor::create, auth::contentWrite, writes::handle` |
| PUT | `/api/v3/me/drafts/{draftId}` | 29 | `processor::update, auth::contentWrite, writes::handle` |
| DELETE | `/api/v3/me/drafts/{draftId}` | 30 | `processor::remove, auth::contentWrite, writes::handle` |

### 本人表情设置与分组读取（本轮接线，4）

代码：`src/main/java/org/b3log/symphony/processor/ApiV3EmotionProcessor.java`。状态：已注册源码；运行未验收。

| 方法 | 路径 | 行号 | 处理器与门禁 |
| --- | --- | --- | --- |
| GET | `/api/v3/me/settings/emotions` | 22 | `p::emotions, auth::profileRead` |
| GET | `/api/v3/me/settings/emotion-options` | 23 | `p::options, auth::profileRead` |
| GET | `/api/v3/me/settings/emoji-groups` | 24 | `p::groups, auth::profileRead` |
| GET | `/api/v3/me/settings/emoji-groups/{groupId}/emojis` | 25 | `p::items, auth::profileRead` |

### 鱼游投票及评论（3）

代码：`src/main/java/org/b3log/symphony/processor/ApiV3FishGameProcessor.java`。状态：已注册源码；运行未验收。

| 方法 | 路径 | 行号 | 处理器与门禁 |
| --- | --- | --- | --- |
| PUT | `/api/v3/fish-games/{gameId}/vote` | 26 | `p::vote, auth::reactionsWrite, writes::handle` |
| DELETE | `/api/v3/fish-games/{gameId}/vote` | 27 | `p::clearVote, auth::reactionsWrite, writes::handle` |
| POST | `/api/v3/fish-games/{gameId}/comments` | 28 | `p::comment, auth::commentsWrite, writes::handle` |

### 关注、收藏及订阅（12）

代码：`src/main/java/org/b3log/symphony/processor/ApiV3FollowProcessor.java`。状态：已注册源码；运行未验收。

| 方法 | 路径 | 行号 | 处理器与门禁 |
| --- | --- | --- | --- |
| GET | `/api/v3/me/follows/users` | 31 | `p::users, auth::profileRead` |
| GET | `/api/v3/me/follows/tags` | 32 | `p::tags, auth::contentRead` |
| GET | `/api/v3/me/collections` | 33 | `p::collections, auth::contentRead` |
| GET | `/api/v3/me/subscriptions` | 34 | `p::subscriptions, auth::contentRead` |
| PUT | `/api/v3/me/follows/users/{targetId}` | 35 | `p::followUser, auth::followsWrite, writes::handle` |
| DELETE | `/api/v3/me/follows/users/{targetId}` | 36 | `p::unfollowUser, auth::followsWrite, writes::handle` |
| PUT | `/api/v3/me/follows/tags/{targetId}` | 37 | `p::followTag, auth::followsWrite, writes::handle` |
| DELETE | `/api/v3/me/follows/tags/{targetId}` | 38 | `p::unfollowTag, auth::followsWrite, writes::handle` |
| PUT | `/api/v3/me/collections/{targetId}` | 39 | `p::collect, auth::followsWrite, writes::handle` |
| DELETE | `/api/v3/me/collections/{targetId}` | 40 | `p::uncollect, auth::followsWrite, writes::handle` |
| PUT | `/api/v3/me/subscriptions/{targetId}` | 41 | `p::subscribe, auth::followsWrite, writes::handle` |
| DELETE | `/api/v3/me/subscriptions/{targetId}` | 42 | `p::unsubscribe, auth::followsWrite, writes::handle` |

### 大事记投稿（3）

代码：`src/main/java/org/b3log/symphony/processor/ApiV3MilestoneProcessor.java`。状态：已注册源码；运行未验收。

| 方法 | 路径 | 行号 | 处理器与门禁 |
| --- | --- | --- | --- |
| POST | `/api/v3/milestones` | 27 | `p::create, auth::contentWrite, writes::handle` |
| GET | `/api/v3/me/milestones` | 28 | `p::list, auth::contentRead` |
| GET | `/api/v3/me/milestones/{milestoneId}` | 29 | `p::detail, auth::contentRead` |

### 通知（7）

代码：`src/main/java/org/b3log/symphony/processor/ApiV3NotificationProcessor.java`。状态：已注册源码；运行未验收。

| 方法 | 路径 | 行号 | 处理器与门禁 |
| --- | --- | --- | --- |
| GET | `/api/v3/me/notifications` | 35 | `processor::list, auth::notificationsRead` |
| GET | `/api/v3/me/notifications/unread-count` | 36 | `processor::unread, auth::notificationsRead` |
| GET | `/api/v3/me/notifications/{notificationId}` | 37 | `processor::get, auth::notificationsRead` |
| POST | `/api/v3/me/notifications/{notificationId}/read` | 38 | `processor::mark, auth::notificationsWrite, writes::handle` |
| POST | `/api/v3/me/notifications/read` | 39 | `processor::markCategory, auth::notificationsWrite, writes::handle` |
| DELETE | `/api/v3/me/notifications/{notificationId}` | 40 | `processor::remove, auth::notificationsWrite, writes::handle` |
| DELETE | `/api/v3/me/notifications` | 41 | `processor::removeCategory, auth::notificationsWrite, writes::handle` |

### OAuth授权协议（4）

代码：`src/main/java/org/b3log/symphony/processor/ApiV3OAuthProcessor.java`。状态：已注册源码；运行未验收。

| 方法 | 路径 | 行号 | 处理器与门禁 |
| --- | --- | --- | --- |
| GET | `/api/v3/oauth/authorize` | 35 | `p::authorize,p::privacy,p::rate,anonymous::handle` |
| POST | `/api/v3/oauth/consent` | 36 | `p::consent,p::privacy,p::rate,p::cookieOnly` |
| POST | `/api/v3/oauth/token` | 37 | `p::token,p::privacy,p::rate,anonymous::handle` |
| POST | `/api/v3/oauth/revoke` | 38 | `p::revoke,p::privacy,p::rate,anonymous::handle` |

### 段评、段落及评论详情（3）

代码：`src/main/java/org/b3log/symphony/processor/ApiV3ParagraphProcessor.java`。状态：已注册源码；运行未验收。

| 方法 | 路径 | 行号 | 处理器与门禁 |
| --- | --- | --- | --- |
| GET | `/api/v3/articles/{articleId}/paragraphs` | 23 | `processor::paragraphs, auth::optionalContent, anonymous::handle` |
| GET | `/api/v3/articles/{articleId}/paragraph-comments` | 24 | `processor::comments, auth::optionalContent, anonymous::handle` |
| GET | `/api/v3/comments/{commentId}` | 25 | `processor::comment, auth::optionalContent, anonymous::handle` |

### 本人设置、资产及创作读取（8）

代码：`src/main/java/org/b3log/symphony/processor/ApiV3PersonalProcessor.java`。状态：已注册源码；运行未验收。

| 方法 | 路径 | 行号 | 处理器与门禁 |
| --- | --- | --- | --- |
| GET | `/api/v3/me/settings` | 22 | `processor::settings, auth::profileRead` |
| GET | `/api/v3/me/checkin` | 23 | `processor::checkin, auth::activityRead` |
| GET | `/api/v3/me/bag` | 24 | `processor::bag, auth::activityRead` |
| GET | `/api/v3/me/points` | 25 | `processor::points, auth::activityRead` |
| GET | `/api/v3/me/drafts` | 26 | `processor::drafts, auth::contentRead` |
| GET | `/api/v3/me/drafts/{draftId}` | 27 | `processor::draft, auth::contentRead` |
| GET | `/api/v3/me/columns` | 28 | `processor::columns, auth::contentRead` |
| GET | `/api/v3/me/articles/{articleId}/source` | 29 | `processor::articleSource, auth::contentRead` |

### 私信（9）

代码：`src/main/java/org/b3log/symphony/processor/ApiV3PrivateMessageProcessor.java`。状态：已注册源码；运行未验收。

| 方法 | 路径 | 行号 | 处理器与门禁 |
| --- | --- | --- | --- |
| GET | `/api/v3/me/conversations` | 23 | `p::list,auth::messagesRead` |
| GET | `/api/v3/me/conversations/{peerId}` | 24 | `p::conversation,auth::messagesRead` |
| GET | `/api/v3/me/conversations/{peerId}/messages` | 25 | `p::history,auth::messagesRead` |
| GET | `/api/v3/me/messages/unread-count` | 26 | `p::unread,auth::messagesRead` |
| GET | `/api/v3/me/messages/{messageId}` | 27 | `p::message,auth::messagesRead` |
| POST | `/api/v3/me/conversations/{peerId}/messages` | 28 | `p::send,auth::messagesWrite,writes::handle` |
| POST | `/api/v3/me/conversations/{peerId}/read` | 29 | `p::markRead,auth::messagesWrite,writes::handle` |
| POST | `/api/v3/me/conversations/read` | 30 | `p::markAllRead,auth::messagesWrite,writes::handle` |
| DELETE | `/api/v3/me/messages/{messageId}` | 31 | `p::withdraw,auth::messagesWrite,writes::handle` |

### 认证、本人及公开专栏（8）

代码：`src/main/java/org/b3log/symphony/processor/ApiV3Processor.java`。状态：已注册源码；运行未验收。

| 方法 | 路径 | 行号 | 处理器与门禁 |
| --- | --- | --- | --- |
| POST | `/api/v3/auth/login` | 90 | `processor::login, anonymous::handle` |
| POST | `/api/v3/auth/refresh` | 91 | `processor::refresh, anonymous::handle` |
| POST | `/api/v3/auth/logout` | 92 | `processor::logout, anonymous::handle` |
| GET | `/api/v3/me` | 93 | `processor::me, auth::handle` |
| GET | `/api/v3/columns` | 94 | `processor::columns, anonymous::handle` |
| GET | `/api/v3/columns/hot` | 95 | `processor::hotColumns, anonymous::handle` |
| GET | `/api/v3/columns/{columnId}/articles` | 96 | `processor::columnArticles, anonymous::handle` |
| GET | `/api/v3/columns/{columnId}` | 97 | `processor::column, anonymous::handle` |

### 排行榜（1）

代码：`src/main/java/org/b3log/symphony/processor/ApiV3RankingProcessor.java`。状态：已注册源码；运行未验收。

| 方法 | 路径 | 行号 | 处理器与门禁 |
| --- | --- | --- | --- |
| GET | `/api/v3/rankings/{type}` | 22 | `processor::ranking, auth::optionalContent, anonymous::handle` |

### emoji reaction（10）

代码：`src/main/java/org/b3log/symphony/processor/ApiV3ReactionProcessor.java`。状态：已注册源码；运行未验收。

| 方法 | 路径 | 行号 | 处理器与门禁 |
| --- | --- | --- | --- |
| GET | `/api/v3/reactions/emojis` | 28 | `p::emojis, auth::optionalContent, anonymous::handle` |
| GET | `/api/v3/articles/{targetId}/reactions` | 29 | `p::article, auth::optionalContent, anonymous::handle` |
| GET | `/api/v3/comments/{targetId}/reactions` | 30 | `p::comment, auth::optionalContent, anonymous::handle` |
| GET | `/api/v3/chat/messages/{targetId}/reactions` | 31 | `p::chat, auth::contentRead` |
| PUT | `/api/v3/articles/{targetId}/reactions` | 32 | `p::setArticle, auth::reactionsWrite, writes::handle` |
| DELETE | `/api/v3/articles/{targetId}/reactions` | 33 | `p::clearArticle, auth::reactionsWrite, writes::handle` |
| PUT | `/api/v3/comments/{targetId}/reactions` | 34 | `p::setComment, auth::reactionsWrite, writes::handle` |
| DELETE | `/api/v3/comments/{targetId}/reactions` | 35 | `p::clearComment, auth::reactionsWrite, writes::handle` |
| PUT | `/api/v3/chat/messages/{targetId}/reactions` | 36 | `p::setChat, auth::reactionsWrite, writes::handle` |
| DELETE | `/api/v3/chat/messages/{targetId}/reactions` | 37 | `p::clearChat, auth::reactionsWrite, writes::handle` |

### 实时取票及Socket（5）

代码：`src/main/java/org/b3log/symphony/processor/ApiV3RealtimeProcessor.java`。状态：已注册源码；运行未验收。

| 方法 | 路径 | 行号 | 处理器与门禁 |
| --- | --- | --- | --- |
| POST | `/api/v3/realtime/tickets` | 39 | `p::ticket,auth::notificationsRead,writes::handle` |
| POST | `/api/v3/articles/{articleId}/realtime-ticket` | 40 | `p::articleTicket,auth::contentRead,writes::handle` |
| POST | `/api/v3/chat/realtime-ticket` | 41 | `p::chatTicket,auth::contentRead,writes::handle` |
| POST | `/api/v3/me/messages/realtime-ticket` | 42 | `p::messagesTicket,auth::messagesRead,writes::handle` |
| WEBSOCKET | `/api/v3/realtime` | 43 | `beans.getReference(ApiV3RealtimeChannel.class)` |

### 红包类型目录（本轮接线，1）

代码：`src/main/java/org/b3log/symphony/processor/ApiV3RedPacketProcessor.java`。状态：已注册源码；运行未验收。

| 方法 | 路径 | 行号 | 处理器与门禁 |
| --- | --- | --- | --- |
| GET | `/api/v3/chat/red-packet-types` | 18 | `processor::types, auth::contentRead` |

### 个人设置（19）

代码：`src/main/java/org/b3log/symphony/processor/ApiV3SettingsProcessor.java`。状态：已注册源码；运行未验收。

| 方法 | 路径 | 行号 | 处理器与门禁 |
| --- | --- | --- | --- |
| GET | `/api/v3/me/settings/personality` | 30 | `p::personality, auth::profileRead` |
| GET | `/api/v3/me/settings/personality-options` | 31 | `p::personalityOptions, auth::profileRead` |
| GET | `/api/v3/me/settings/avatar` | 32 | `p::avatar, auth::profileRead` |
| GET | `/api/v3/me/settings/profile` | 33 | `p::profile, auth::profileRead` |
| GET | `/api/v3/me/settings/privacy` | 34 | `p::privacy, auth::profileRead` |
| GET | `/api/v3/me/settings/display` | 35 | `p::display, auth::profileRead` |
| GET | `/api/v3/me/settings/skins` | 36 | `p::skins, auth::profileRead` |
| GET | `/api/v3/me/settings/functions` | 37 | `p::functions, auth::profileRead` |
| GET | `/api/v3/me/settings/location` | 38 | `p::location, auth::profileRead` |
| GET | `/api/v3/me/settings/locale` | 39 | `p::locale, auth::profileRead` |
| GET | `/api/v3/me/settings/locale-options` | 40 | `p::localeOptions, auth::profileRead` |
| PUT | `/api/v3/me/settings/personality` | 41 | `p::updatePersonality, auth::settingsWrite, writes::handle` |
| PUT | `/api/v3/me/settings/avatar` | 42 | `p::updateAvatar, auth::settingsWrite, writes::handle` |
| PUT | `/api/v3/me/settings/functions` | 43 | `p::updateFunctions, auth::settingsWrite, writes::handle` |
| PUT | `/api/v3/me/settings/location` | 44 | `p::updateLocation, auth::settingsWrite, writes::handle` |
| PUT | `/api/v3/me/settings/locale` | 45 | `p::updateLocale, auth::settingsWrite, writes::handle` |
| PUT | `/api/v3/me/settings/profile` | 46 | `p::updateProfile, auth::settingsWrite, writes::handle` |
| PUT | `/api/v3/me/settings/privacy` | 47 | `p::updatePrivacy, auth::settingsWrite, writes::handle` |
| PUT | `/api/v3/me/settings/display` | 48 | `p::updateDisplay, auth::settingsWrite, writes::handle` |

### 上传签票（1）

代码：`src/main/java/org/b3log/symphony/processor/ApiV3UploadProcessor.java`。状态：已注册源码；运行未验收。

| 方法 | 路径 | 行号 | 处理器与门禁 |
| --- | --- | --- | --- |
| POST | `/api/v3/uploads/tickets` | 24 | `processor::ticket, auth::uploadsWrite, writes::handle` |

### 文章投票（3）

代码：`src/main/java/org/b3log/symphony/processor/ApiV3VoteProcessor.java`。状态：已注册源码；运行未验收。

| 方法 | 路径 | 行号 | 处理器与门禁 |
| --- | --- | --- | --- |
| GET | `/api/v3/articles/{articleId}/vote` | 23 | `processor::read, auth::contentRead` |
| PUT | `/api/v3/articles/{articleId}/vote` | 24 | `processor::set, auth::contentWrite, writes::handle` |
| DELETE | `/api/v3/articles/{articleId}/vote` | 25 | `processor::clear, auth::contentWrite, writes::handle` |

### 职业（2）

代码：`src/main/java/org/b3log/symphony/processor/ApiV3ProfessionProcessor.java`。状态：已注册源码；运行未验收。

| 方法 | 路径 | 行号 | 处理器与门禁 |
| --- | --- | --- | --- |
| GET | `/api/v3/professions` | 23 | `p::catalog, auth::optionalProfile, anonymous::handle` |
| GET | `/api/v3/me/professions` | 24 | `p::mine, auth::profileRead` |

### 勋章（2）

代码：`src/main/java/org/b3log/symphony/processor/ApiV3MedalProcessor.java`。状态：已注册源码；运行未验收。

| 方法 | 路径 | 行号 | 处理器与门禁 |
| --- | --- | --- | --- |
| GET | `/api/v3/me/medals` | 23 | `p::mine, auth::profileRead` |
| GET | `/api/v3/users/{userName}/medals` | 24 | `p::displayed, auth::optionalProfile, anonymous::handle` |

### 会员（2）

代码：`src/main/java/org/b3log/symphony/processor/ApiV3MembershipProcessor.java`。状态：已注册源码；运行未验收。

| 方法 | 路径 | 行号 | 处理器与门禁 |
| --- | --- | --- | --- |
| GET | `/api/v3/me/membership` | 22 | `processor::me, auth::profileRead` |
| GET | `/api/v3/membership/levels` | 23 | `processor::levels, auth::contentRead` |

### 主职业设置（1）

代码：`src/main/java/org/b3log/symphony/processor/ApiV3ProfessionPrimaryProcessor.java`。状态：已注册源码；运行未验收。

| 方法 | 路径 | 行号 | 处理器与门禁 |
| --- | --- | --- | --- |
| PUT | `/api/v3/me/professions/primary` | 25 | `p::set, auth::settingsWrite, writes::handle` |

### 勋章显示设置（1）

代码：`src/main/java/org/b3log/symphony/processor/ApiV3MedalDisplayProcessor.java`。状态：已注册源码；运行未验收。

| 方法 | 路径 | 行号 | 处理器与门禁 |
| --- | --- | --- | --- |
| PUT | `/api/v3/me/medals/{medalId}/display` | 23 | `p::display, auth::settingsWrite, writes::handle` |

### OAuth客户端管理（3）

代码：`src/main/java/org/b3log/symphony/processor/ApiV3ClientManagementProcessor.java`。状态：已注册源码；运行未验收。

| 方法 | 路径 | 行号 | 处理器与门禁 |
| --- | --- | --- | --- |
| GET | `/api/v3/admin/oauth/clients` | 28 | `p::list,auth::settingsWrite,p::administrator` |
| GET | `/api/v3/admin/oauth/clients/{clientId}` | 29 | `p::detail,auth::settingsWrite,p::administrator` |
| POST | `/api/v3/admin/oauth/clients` | 30 | `p::create,auth::settingsWrite,p::administrator,writes::handle` |

### OAuth客户端停用（1）

代码：`src/main/java/org/b3log/symphony/processor/ApiV3ClientDisableProcessor.java`。状态：已注册源码；运行未验收。

| 方法 | 路径 | 行号 | 处理器与门禁 |
| --- | --- | --- | --- |
| DELETE | `/api/v3/admin/oauth/clients/{clientId}` | 32 | `p::disable, auth::settingsWrite, p::administrator, writes::handle` |

### 邀请码状态（1）

代码：`src/main/java/org/b3log/symphony/processor/ApiV3InvitationProcessor.java`。状态：已注册源码；运行未验收。

| 方法 | 路径 | 行号 | 处理器与门禁 |
| --- | --- | --- | --- |
| POST | `/api/v3/invitation-codes/status` | 28 | `p::status, auth::activityRead, budget::handle` |

### 本人匿名内容（2）

代码：`src/main/java/org/b3log/symphony/processor/ApiV3AnonymousContentProcessor.java`。状态：已注册源码；运行未验收。

| 方法 | 路径 | 行号 | 处理器与门禁 |
| --- | --- | --- | --- |
| GET | `/api/v3/me/anonymous-articles` | 21 | `p::articles, auth::contentRead` |
| GET | `/api/v3/me/anonymous-comments` | 22 | `p::comments, auth::contentRead` |

### OAuth客户端修改（1）

代码：`src/main/java/org/b3log/symphony/processor/ApiV3ClientUpdateProcessor.java`。状态：已注册源码；运行未验收。

| 方法 | 路径 | 行号 | 处理器与门禁 |
| --- | --- | --- | --- |
| PUT | `/api/v3/admin/oauth/clients/{clientId}` | 30 | `p::update, auth::settingsWrite, p::administrator, writes::handle` |

## 测试证据与局限

以下引用历史记录的实际结果，不把测试文件存在等同于已经执行；各runner可能重复覆盖共享代码，禁止相加宣传“全接口通过”。完整细节见 `docs/api-v3-development-plan.md` 对应日期批次。

| 模块 | 可核查隔离证据 | 仍缺证据 |
| --- | --- | --- |
| Token/协议/草稿/幂等/严格提交 | `tools/api-v3-token-test.mjs`、`api-v3-write-test.mjs`；历史记录21项协议/草稿及6组真实Latke提交故障 | 真实MySQL提交不确定性、完整HTTP/Cookie/MFA与跨站行为 |
| OAuth/账号/聊天室/共享写事务 | `tools/api-v3-chat-event-test.mjs`；2026-10-20历史记录通用157组 | 完整IoC、真实短信/极验、OAuth浏览器同意→回调→换码、并发MySQL与匿名盾 |
| 通知/文章/chat/私信实时 | `tools/api-v3-socket-test.mjs`；私信实时批次51组；`api-v3-article-realtime-test.mjs`另有定向覆盖 | 实际WS升级、代理/重连、多账号撤权、跨实例和旧客户端并用 |
| 私信/头像/MBTI | `tools/api-v3-private-message-test.mjs` 20组、`api-v3-settings-test.mjs` 9组（MBTI写入批次） | 真实私有Markdown运行、MySQL行锁、客户端发送/已读/撤回/刷新及头像图床 |
| 文章/评论/互动/删除/段评 | `tools/api-v3-article-test.mjs`、`api-v3-comment-test.mjs`、`api-v3-comment-removal-test.mjs`、`api-v3-interaction-removal-test.mjs`、`api-v3-paragraph-test.mjs`、reaction专项；历史分批有通过记录 | 后台并发删改、权限变动/费用、异步通知/索引、真实两端渲染及旧编辑歧义兼容 |
| 设置/关注/专栏/清风/鱼游/签到/奖励/背包/排行/大事记/上传 | `tools/api-v3-{settings,profile,follow,column,breezemoon,fish-game,checkin,activity,bag,ranking,milestone,upload}-test.mjs`（分别的历史批次） | 不应以单个runner推广为所有模块已验收；真实DB/IoC/第三方网络/PC及mobile仍缺 |
| 内容发现/个人/社区等读取 | 对应Java Test与`tools/api-v3-*-smoke.mjs`存在；早期部分仅记录语法解析或未运行 | 不能把有smoke文件计作HTTP已通过；需要真实数据/权限分页验证 |
| 本轮文章热度及并行新增读取 | 主代理重跑`tools/api-v3-socket-test.mjs` 55组通过（含热度及红包实际路由/控制器/授权）；`tools/api-v3-emotion-test.mjs` 5组通过（真实SQL/Dispatcher/auth/controller、SQLite及依赖适配器）；`tools/api-v3-red-packet-test.mjs`纯目录1组通过 | 热度仅当前实例近似连接数；表情未验真实MySQL/浏览器/全局盾；红包已覆盖隔离HTTP/鉴权，不能推断实际领取/发送已实现 |
| 第二轮职业/勋章/会员 | 主代理重跑 profession 6组、medal 4组、membership 7组通过；均包含生产SQL/路由/控制器/授权隔离执行，勋章专项另执行真实AnonymousViewCheckMidware首访/计次逻辑 | SQLite及Token/请求依赖适配器不等于应用联调；职业匿名盾为钩子适配，真实全局黑名单/解封、MySQL/IoC和PC/mobile仍未验收 |
| 第三轮写入与客户端管理 | 主代理重跑主职业8组、勋章显示6组、OAuth管理10组 | MySQL并发、完整IoC、真实客户端与旧入口竞争 |
| 第四轮匿名创作/邀请码/客户端维护 | 主代理重跑匿名内容10组、邀请码5组、停用9组、修改11组及管理回归10组；生产SQL/鉴权/严格提交、SocketTicket与Code/Consent核心隔离执行 | SQLite不能代替MySQL；缺HTTP/WS、两端浏览器、第三方OAuth和全站反漏扫 |

## 历史矛盾与当前更正

1. 计划矩阵曾把“文章热度/在线人数”列为源码已接入，但截至本轮开始，只能查到旧 `GET /api/article/heat/{articleId}`（ArticleProcessor），v3文章订阅人数亦不能自动形成HTTP接口。本轮主代理已接入 `ApiV3ArticleHeatProcessor` / `ApiV3ArticleHeatQueryService`；当前清单已计入该GET。先按当前数据库读者授权，再相加当前节点旧文章连接计数及当前节点v3已认证文章订阅连接数；这是近似连接数、可能重复，不是去重人数或全站实时在线人数。
2. 早期设计称“私信、账号恢复不直接搬v3”和“PKCE尚未实现”，后续已经新增独立私信/账号/OAuth协议入口与核心；这些旧句不能覆盖当前源码事实。管理员OAuth登记管理已按用户明确范围接入，禁止自助登记。
3. 注册短信、私信撤回、私信实时的历史“未接线/待兼容批准”已由后续实际接线覆盖；当前不再把它们列为缺入口。仍需部署与运行验收。
4. “有源码”“隔离测试通过”“用户确认规则”“已运行验收”分别记录；后续阶段不能反推历史整站已经安全验收。

## 剩余能力、依赖与待确认规则

“缺入口”只表示没有对应v3资源，并不表示应该照搬旧接口。具体方法/path需设计后成为公开契约，以下旧URL仅用于追溯。

| 能力 | 当前可证明状态与旧来源 | 下一步与依赖 |
| --- | --- | --- |
| OAuth客户端登记/维护 | `ApiV3ClientRegistrationService#createApprovedInCurrentTransaction`已有已批准应用登记核心；列表/详情/登记HTTP已接入 | 用户已确认仅管理员登记和维护及20回调/2048字符；第一方管理员授权、幂等列表/详情/登记已接入，逻辑停用及完整Token撤销已接入；回调/权限PUT已接入，实际集合变更令全部旧Token/码/待确认失效；仅改名称保留授权，禁止自助登记 |
| OAuth token/revoke风控 | 协议入口已注册且保留anonymous门禁 | 用户确认凭据是否可作为这两个端点登录证明；未批准前不加anonymous.viewSkips；真实第三方非浏览器换码必须联调 |
| 鱼游投稿/编辑申请 | v3仅目录/详情/评论读、投票和评论写；旧 `FishGameProcessor` `/api/fish-games` POST与`/{id}/edit` | 缺v3入口/协调核心；等待用户提供OAuth说明URL校验入口、证明与归属规则，保留待审流程 |
| 聊天红包发送/详情/领取 | 已新增GET `/api/v3/chat/red-packet-types`；`ApiV3ChatMessageDto`只给红包摘要，旧ChatroomProcessor红包链仍存在 | 缺独立v3写核心、详情和领取入口；安全类型/猜拳扣分/心跳负积分/dice不支持等规则先确认，不能套普通消息发送 |
| 公开支付跳转 | 旧 `/pay/wechat` 与 `/charge/point`存在；支付回调及管理排除 | 缺v3入口与受控跳转契约；需要确认提供方、支付目的/金额选项/回跳规则；不把服务端回调公开成v3 |
| 账号敏感设置 | 旧SettingsProcessor手机/邮箱验证码与修改、密码修改、普通积分改名；MFAProcessor四条MFA能力 | 缺v3入口、证明/撤权/幂等协调与scope选择；不得输出手机/邮箱/2FA密钥至公开DTO，先明确本人敏感信息允许范围与验证规则 |
| 邀请、转账、导出 | 旧`/invitecode/state`、`/point/buy-invitecode`、`/point/transfer`、`/export/posts` | 邀请码存储状态POST已接入（activity.read，仅used/unused/stopped）；购买、转账、导出仍缺入口/共享积分事务及下载契约，unused不保证可注册 |
| 自定义表情与分组 | 旧EmojiProcessor13条（列表/上传/分组/成员/分享/导入/迁移）及settings/emotionList | 已新增本人表情选择/目录/分组/组内表情4条GET，profile.read且不接收他人ID；reaction目录不能替代表情库；选择修改/上传/组管理/分享/导入/迁移仍缺v3写入口及独立服务、事务、URL与所有权规则 |
| 文章/评论版本与举报 | 旧ArticleProcessor历史版本两入口、CommentProcessor版本入口、Router `/report` | 缺v3资源；先确定作者/读者能读哪些版本、删除/私密/匿名边界与举报对象/频控；不能裸返旧历史对象 |
| 本人匿名创作清单 | GET /me/anonymous-articles及/me/anonymous-comments已接入，content.read本人元数据 | 正文经已有详情读取；讨论邀请用有界候选/共享解析/hash绑定，超技术预算503，真实MySQL与并发验收仍待做 |
| 用户职业 | 已注册GET /professions及/me/professions，目录与本人状态/进度读取 | 主职业PUT已接入；详情/记录/排行、提示/隐私写入及他人资料仍待实现；用户侧与后台分开，写入需幂等事务核心 |
| 用户勋章 | 已注册GET /me/medals及/users/{userName}/medals，本人含隐藏、公开仅有效展示 | 显示PUT已接入，排序写入仍待实现；只读不触发旧过期清理，不开放后台全定义目录 |
| 用户会员 | 已注册GET /membership/levels及/me/membership，档位与本人状态/已知配置白名单 | 开通和本人配置写入、公开配置资源仍待实现；积分开通需事务/幂等，禁止直接套旧开通链 |
| 复读机转录站 | RepeaterProcessor旧`GET /api/repeater/items`、`GET /api/repeater/next`、`POST /api/repeater`及`POST /api/repeater/{id}/like`；API.md独立公开模块 | 当前无v3入口，不能因不在原迁移矩阵就当作已完成；需有界DTO/分页及投稿/点赞事务与幂等，明确第三方迁移契约 |
| 游戏云存档 | CloudProcessor旧`POST /api/cloud/get`和`POST /api/cloud/sync`，与游戏计分不同 | 当前无v3入口；范围需单独确认，绝不能直接开放任意gameId（cloud还含sys-bag/风控等内部记录）；若纳入须限定外部游戏命名空间与owner/体积/版本契约 |
| website_v3迁移 | 历史计划只证明登录/refresh/专栏页面迁移；当前仓库不含计划中`src/api/fishpi.ts`等客户端源码 | 对客户端另行盘点路由、删除apiKey/旧WS/mock降级，不能从后端接口推断页面已接入 |
| 全链路验收 | 上列已注册接口皆缺本轮真实运行确认 | 依赖数据库迁移、Java25完整类型/IoC启动、IDEA重编译/重启确认、专用测试账号/短信/图床/浏览器及第三方OAuth客户端 |

## 并行分工与合入顺序

| 工作包 | 本轮负责人/状态 | 独占区域与可并行边界 | 合入依赖 |
| --- | --- | --- | --- |
| 当前清单与缺口 | inventory代理首轮181条，主代理第二轮更新187条源码清单 | 仅`docs/api-v3-progress.md`；不改代码或公开文档 | 主代理最终重扫注册数、逐条核对新增入口 |
| 本人设置/表情读取 | settings代理，4条GET已总接线，隔离5组通过 | 专用新Processor/Service/Test；避免改公共注册/公开文档 | 主代理审阅及ApiV3Processor总接线后再计入注册数 |
| 红包/支付盘点与安全目录 | chat_payments代理，类型GET已总接线，纯目录专项通过 | 专用报告及类型目录源码；未确认的经济规则不实现写入 | 规则确认、共享积分锁与事务/幂等核心；主代理总接线 |
| 文章热度与总接线 | 主代理，热度已总接线、55组Socket专项通过 | 热度Processor/Service/专项；ApiV3Processor、API.md、UPDATE_LOG、AGENTS及共享计划由主代理维护 | 当前读者可见性、实例计数契约、专项测试；完整运行另验 |
| 鱼游OAuth投稿/编辑 | 后续工作包，受规则依赖 | FishGame专用协调器/DTO/test可独立设计；不改共享锁顺序 | 用户提供校验入口与归属规则 |
| OAuth登记与维护 | 第三轮主代理登记3接口；第四轮oauth_disable完成停用/修改 | 独立Processor/Service/专项，共用原登记字段校验；公共文档与总接线由主代理维护 | 第一方当前管理员、授权失效事务与真实并发验收 |
| 主职业/勋章显示写入 | inventory/settings起稿、主代理复核接线，8/6组专项通过 | 各自独占Processor/Service/专项，幂等operation隔离 | 隐私/排序及真实新旧竞争仍待完成 |
| 本人匿名内容 | anonymous_content完成两GET、主代理接线并复跑10组 | 独立Query/DTO/专项，invitation_read交叉只读审查 | 当前讨论权限与MySQL验收 |
| 邀请码状态 | invitation_read完成POST、主代理接线并复跑5组 | 只读存量状态、独立输入/DTO，不推断注册可用性 | 购买/转账/导出仍独立待实现 |
| 敏感账号 | 后续工作包，受规则依赖 | 公共auth/scope/Token撤权需串行审阅 | 明确批准的规则与兼容影响 |
| 职业读取 | inventory代理第二轮完成2 GET，主代理接线并复跑6组通过 | 独占ApiV3Profession*及专项/work文档 | 详情/记录/排行和选择/提示/隐私写入后续独立核心 |
| 勋章读取 | settings代理第二轮完成2 GET，主代理接线并复跑4组通过 | 独占ApiV3Medal*及专项/work文档 | 显示/排序写入后续独立核心，不复用旧查询副作用 |
| 会员读取 | chat_payments代理第二轮完成2 GET，主代理接线并复跑7组通过 | 独占ApiV3Membership*及专项/work文档 | 开通/配置写入后续事务和结算核心 |
| 版本/举报/邀请/转账及其他缺口 | 后续独立工作包 | 各领域分开实现，共用权限/积分锁串行审阅 | 用户侧范围/字段规则及写核心 |
| 应用/客户端联调 | 末端统一验收，未执行 | 迁移、IDEA重启、HTTP/WS/PC/mobile；用户掌控真实服务 | 所有合并及规则确认完成；不能用适配器跳过 |

主代理公共文件串行汇总，代理不要互改`API.md`、`UPDATE_LOG.md`、`AGENTS.md`、共享计划或总注册。公共scope/迁移/Repository模型有交集时先审阅锁序与部署依赖，再合入。

## 人工全链路验收

1. 先由用户按SQL依赖迁移并通过IDEA重新编译/完整重启Java服务，确认日志过滤器与IoC注册成功；检查PC/mobile所用资源是否读取target/classes。未确认生效前不把浏览器旧行为判为代码失败。
2. 从PC及mobile登录/极验、注册申请→短信→激活、找回申请→短信→完成→再登录，取得正确scope的第一方及第三方Bearer；覆盖MFA、错误/过期/重复证明、已占手机号/名称、关闭注册、邀请码竞争与奖励一次性。
3. OAuth从登记回调→同意页→明确批准/拒绝→303→S256换码→资源读取→轮换刷新→撤销，覆盖错误state/PKCE/回调、重复码、错误owner、撤权与停用；确认Cookie/apiKey不能代替业务Bearer。
4. 公开列表→文章/评论/段评详情→本人源码/草稿→发布/部分编辑/互动→删除，核对费用/计数/通知与刷新一致；两账号覆盖讨论受邀/未受邀、匿名开关、打赏正文、私密评论、匿名作者、他人资源和重复/并发幂等键。
5. 本人设置读取→修改→重新读取与旧PC/mobile页面核对；资产查询→允许的卡片/奖励使用→余额/背包/记录核对；关注/收藏/订阅→各列表及取消。非法值/越权/幂等冲突失败不得留下部分写入。
6. 私信A发送B→两方历史/详情/会话→B未读→指定/全部已读→A本人撤回，第三人拒绝；聊天室历史→发送/话题/弹幕/本人撤回；通知/文章/chat/messages按各自scope取票→首帧认证→提交后提示→HTTP同步→断线重连同步，覆盖撤权、错误channel/target、停用和跨实例限制。红包仅测试实际开放入口，不根据消息摘要推断已支持领取。
7. 鱼游/清风/同城/排行/大事记与专栏从列表→详情→已开放写操作→再次读取，检查审核、隐私、所有者、分页、排序及冲突；上传签票→图床file直传→公开URL展示，缺scope/过期票据/非法文件拒绝。
8. 所有公开入口逐项检查未鉴权、越权、CSRF、XSS、注入、SSRF、批量滥用和敏感信息；盾开启环境覆盖首次/计次验证码、黑名单→/test→validateCaptcha解封、anonymous.viewSkips，以及开发关闭盾的差异；静态/CDN版本号、sourceMappingURL、目录枚举、上传下载路径必须实际检查。当前均未作为运行通过项。

本轮盘点与新增读取接口不执行Maven，不访问8080、数据库、生产，不做Git提交；没有运行浏览器/真实HTTP/外部短信或支付。公开接口文档变更由主代理汇总后仍需同步网站API文章。

第二轮对应实现、文件、完整人工链路见 [职业](api-v3-profession-work.md)、[勋章](api-v3-medal-work.md)、[会员](api-v3-membership-work.md)。主代理已完成总接线，当前187条可达注册与表内METHOD/path完全一致，无重复及漏项；仅源码口径，不作为完整应用启动证据。

第三轮：累计提交6231c4f0已推送origin/api_v3后，新增主职业PUT、勋章显示PUT及管理员OAuth列表/详情/登记3接口。专项分别8/6/10组通过（隔离实际SQL/JDBC/路由，不代替运行验收）。当前192条可达注册与逐项表完全相等、无重复；新开发内容尚未再次提交。详情见 [主职业](api-v3-profession-primary-work.md)、[显示设置](api-v3-medal-display-work.md)、[客户端管理](api-v3-client-management-work.md)。

第四轮：匿名本人列表两GET、邀请码状态POST、OAuth客户端DELETE/PUT共5接口接线，当前197条与逐项表全等无重复。主代理专项匿名10/邀请码5/停用9/修改11、共享管理回归10组通过；invitation_read交叉审匿名/停用未发现实质问题。实际文件与人工链见 [匿名内容](api-v3-anonymous-content-work.md)、[邀请码](api-v3-invitation-work.md)、[停用](api-v3-client-disable-work.md)、[修改](api-v3-client-update-work.md)及development-plan第四轮。源码和隔离结果不代表真实MySQL/应用/浏览器验收；当前新增仍未再次提交。
