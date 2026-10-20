# /api/v3 第三方 Web API 接口盘点与设计

## 目标

本文将 Rhythm 当前 HTTP 接口面与新的、稳定的第三方 Web API 分开整理。内容基于源码中的路由注册、现有公开文档 [API.md](../API.md)，以及 classic PC/移动端 FTL 与未压缩 JavaScript 中的接口字面量引用。

可重复生成的完整路由清单见 [api-route-inventory.md](api-route-inventory.md)，执行以下命令即可更新：

```bash
node tools/api-route-inventory.mjs
```

清单记录实际 Dispatcher 声明、处理器、声明附近的中间件与浏览器端引用。每次 Processor 路由变更后都应复查。该脚本不参与构建，也不会改动编译后的前端资源。

## 当前接口面

现有 `/api` 命名空间不是面向第三方的版本化契约，混合了四类接口：

| 类别 | 典型现有路由 | 当前访问模型 | v3 处理决定 |
| --- | --- | --- | --- |
| 公开读模型 | `/api/articles/*`、`/api/article/{id}`、`/api/tags`、`/api/domains`、`/api/breezemoons`、`/api/fish-games`、`/api/statistic`、`/api/milestones` | `AnonymousViewCheckMidware` 或未在路由中声明中间件 | 仅将经过响应字段白名单的资源作为 v3 公开读接口提供 |
| 用户 Web API | `/api/user`、`/api/checkin/status`、`/api/user/bag`、`/api/article-drafts`、`/api/emoji/*`、`/api/columns/mine` | `loginCheck`，同时接受会话或 `apiKey` | 以 v3 Bearer Token 与 scope 重建，不直接代理原有处理器 |
| 页面/AJAX 支撑接口 | `/comment/thread/*`、`/api/article/heat/*`、`/api/watch/*`、`/api/rhypic/upload-ticket` | 混合，且经常与页面响应结构耦合 | 保持内部接口；只有在定义稳定资源响应后才考虑提升为 v3 接口 |
| 运营、管理、金手指与回调 | `/api/admin/*`、`/api/profession/admin/*`、`/api/medal/admin/*`、`/api/gold-finger/*`、`/api/games/*/score`、回调 URL | 不一致，部分要求登录，部分使用独立密钥 | 不得暴露到第三方 `/api/v3`；保留内部命名空间和独立服务凭据 |

### 现有 apiKey 行为

`LoginCheckMidware` 接受会话、query 参数 `apiKey` 或 JSON body 的 `apiKey`，并将解析出的用户写入 `context.attr(User.USER)`。`ApiCheckMidware` 只接受 apiKey，未通过时返回 JSON `Unauthorized`。因此，受 `loginCheck` 保护的路由通常可以通过 apiKey 调用，但这并不等同于它已经是稳定的外部接口：不同处理器的响应结构、所有权校验、CSRF 前提和限流并不一致。

`CSRFMidware` 是为 Cookie 页面设计的。支持 Bearer 凭据的新 API 写入接口不能直接照搬：只有接受 Cookie 身份验证时才校验 CSRF，或者让 `/api/v3` 写入接口只接受 Bearer Token 并拒绝会话 Cookie。

### FTL 数据与浏览器请求

FTL 路由渲染的是服务端数据模型，本身不构成另一套 HTTP API。将模板源纳入生成清单，是因为模板会加载页面脚本，也可能直接写 AJAX URL。迁移时应明确区分以下三种契约：

1. 页面路由 -> Processor -> FTL 数据模型：继续作为内部渲染契约维护。
2. FTL/JS -> 现有 JSON 接口：只有第三方客户端确有需要时，才映射为 v3 资源。
3. 第三方 Web API：返回明确 DTO 与分页信封，禁止直接返回 FTL 数据模型或 Repository JSON。

## 建议的 v3 资源范围

v3 首个版本应以只读为主，覆盖现有已面向第三方说明的能力，但不引入管理、积分变更、游戏分数或金手指接口。

| 模块 | 建议 v3 接口 | 访问要求 | 对应现有能力 |
| --- | --- | --- | --- |
| 内容发现 | `GET /api/v3/articles`、`/articles/{articleId}`、`/articles/{articleId}/comments`、`/tags`、`/domains`、`/search` | 公开；遵从文章可见性规则 | 文章、标签、领域、搜索 API |
| 用户 | `GET /api/v3/users/{userName}`、`/users/{userName}/articles`、`/users/{userName}/breezemoons`、`/me` | 公开资料；`/me` 需要 `profile.read` | 用户资料与个人主页列表 API |
| 社区信息流 | `GET /api/v3/breezemoons`、`/fish-games`、`/milestones`、`/statistics`、`/rankings/{type}` | 当前策略允许时公开 | 现有公开列表 API |
| 专栏 | `GET /api/v3/columns`、`/columns/{columnId}`、`/columns/{columnId}/articles` | 可见内容公开 | 现有专栏 API |
| 聊天室 | `/api/v3/chat/messages`、`/online-users`、`/topic`、`/barrager/price`、`POST /barrager`及chat取票 | 读content.read，写content.write与幂等 | 普通历史/发送/本人撤回、持久话题、当前实例即时弹幕；红包仍待独立接入 |
| 个人状态 | `GET /api/v3/me/checkin`、`/me/bag`、`/me/notifications` | 带 scope 的 Bearer Token | 现有 apiKey 路由 |
| 创作，第二阶段 | `POST /api/v3/articles`、`PATCH /api/v3/articles/{id}`、`POST /api/v3/articles/{id}/comments`、reaction、follow | 带 scope 的 Bearer Token 和明确授权 | 文章、评论、投票、关注处理器，完成独立写入审计后开放 |

私信、管理员 API、职业管理、勋章管理、会员管理、账号恢复、支付、上传代理、内部回调和金手指接口不应直接搬进 v3。文件上传采用独立的短期票据资源 POST /api/v3/uploads/tickets：uploads.write、Bearer身份、当前用户投影及公开目标校验；只共用现有RhyPic的Ed25519底层协议，不代理旧控制器或暴露内部转发/删除能力。图床实际接收与约束仍待端到端验证。

## v3 接口契约

以已经落地的 `API.md` 与 `api-v3-development-plan.md` 为准：JSON 信封为 `{ code, msg, data }`，成功 code 为数字 0；错误 code 为字符串错误码，data 为 null。请求 ID 放在 `X-Request-Id` 响应头。列表统一为 `data.list` 和 `data.pagination`，参数使用 `p`、`size`，分页信息包含 `p`、`size`、`total`、`pageCount`。

时间字段默认使用毫秒时间戳；不可见的资源统一按不存在返回。最初讨论过的 `{ data, meta }` / 游标方案未采用，不应据此改动已经使用 v3 的客户端。

HTTP 状态码：400 请求格式错误，401 缺失或无效 Token，403 缺少 scope，404 资源不存在或不可访问，409 状态冲突，422 校验失败，429 频率限制。既有匿名盾或全局黑名单拦截仍可能返回 `/test` 重定向，不能把所有响应都假设为 JSON。

当前新增文章与评论端点由 `ApiV3ContentProcessor` / `ApiV3ContentQueryService` 提供；它们显式接收 Bearer 阅读者，不复用依赖 Sessions 的页面内容组装。文章详情和评论列表共用文章可见性检查，打赏与私密评论按当前阅读者单独授权。

## 身份验证与授权框架

### 凭据

新增独立的 `api_v3_client` 与 `api_v3_token` 数据模型。现有用户 apiKey 生命周期长、权限宽，只保留给旧接口。v3 Bearer Token 应具备 Token ID、密钥哈希、归属用户 ID、可选 client ID、命名 scope、过期时间、撤销时间、创建元数据与最后使用元数据；数据库只能保存加盐哈希后的密钥。

v3 使用 `Authorization: Bearer <token>`，不得接受 query 参数中的凭据。初期可以发放用户自管的个人 Token；浏览器集成在开放写 scope 前应迁移到带 PKCE 的授权码 OAuth。

### OAuth PKCE 目标设计（尚待实现，不作为当前发布完成证据）

第三方客户端使用授权码 + S256 PKCE：客户端提前登记精确 redirect URI，发起授权时绑定 clientId、redirect URI、scope、state 与 codeChallenge；授权确认使用已登录用户并验证页面来源/CSRF。授权码短期有效且只允许消费一次，换 Token 必须验证 codeVerifier 与原 client/redirect URI，禁止 plain PKCE、隐式授权与客户端自行指定用户。Token 与授权码数据库只保存哈希；刷新 Token 继续使用轮换与家族撤销，授权 scope 取用户批准与客户端允许范围的交集。

当前源码只为已有 website_v3 第一方登录签发 Token；草稿写接口用于这套授权链路的开发。第三方 OAuth PKCE 尚未落地，不能把写接口源码或默认 write scope 当作第三方授权发布已完成。完整目标仍需补齐上述授权流程和端到端验证。

2026-10-19 补充：HTTP仍仅开放上述第一方登录链。Token服务内部已支持同事务的指定注册客户端发行、客户端绑定刷新/撤销、当前客户端允许范围内缩减scope及S256校验；授权码、redirect URI注册、同意页与换码/第三方刷新HTTP入口尚未接入。不能把这些内部方法当作可直接传入userId发行凭据的公开接口。刷新/撤销的数据库锁序为client→token，换码须先取client再当前读授权用户与码，Token对必须与消费码同事务；源码行锁与SQLite适配测试不是MySQL跨实例并发证明。

### Scope

初始 scope：`profile.read`、`content.read`、`activity.read`、`notifications.read`、`content.write`、`comments.write`、`reactions.write`、`follows.write`。每条接口都要在路由注册中声明所需 scope。特权操作使用独立内部凭据，不能复用这些 scope。

### 路由与服务结构

在资源 DTO 和安全类就绪后，再新增独立的 `ApiV3Processor`。每条 v3 路由按以下顺序处理：

```text
请求 ID -> Content-Type/请求体大小限制 -> 限流 -> Bearer 身份验证 -> scope ->
资源可见性/所有权 -> 入参校验 -> Service -> 响应 DTO 白名单 -> 审计日志
```

读取处理器应使用查询服务和可见性校验。写入处理器应调用管理服务，在服务边界再次校验所有权，所有变更使用事务，且只在提交后发布领域事件。禁止让 v3 直接操作 Repository 或返回其 `JSONObject`。

## 必做安全测试

每条 v3 路由的验收都必须包含安全审查。上线前至少验证：

| 领域 | 必测断言 |
| --- | --- |
| 身份验证 | 无 Token、无效 Token、过期 Token、已撤销 Token、错误 Authorization Header、旧 `apiKey` 都按设计失败 |
| 授权 | 缺少 scope、访问他人资源、隐藏用户/文章/评论、管理员专有动作、已停用用户均被拒绝 |
| Cookie/CSRF | 带 Cookie 的跨域写入被拒绝；仅 Bearer 的写入无需 CSRF Token；v3 不会意外回退到 session 身份验证 |
| 输入 | 空值、纯空白、超长、非法 ID/游标、重复 JSON 字段、非法枚举、非法 UTF-8/JSON、Markdown/HTML 载荷、路径穿越均被拒绝或规范化 |
| 注入与 SSRF | 搜索/排序/筛选参数有白名单；不请求调用方控制的 URL；不接受 SQL/JSON 查询片段 |
| 数据泄露 | 所有 DTO 使用字段白名单；测试断言不包含 password、email、phone、QQ、2FA、apiKey/token、IP、UA、session、内部请求 ID、审核字段 |
| 滥用 | 基于 Token、用户、IP 的限流返回 429；写入幂等键阻止重复发帖/扣费；分页与响应大小有上限 |
| 浏览器与 CORS | Origin 白名单精确匹配；通配 Origin 不携带凭据；预检仅允许需要的方法/Header；CSP 与缓存头匹配公开/私有数据 |
| 可观测性 | 返回请求 ID；身份验证失败与写入均记录审计日志但不记录 Bearer 密钥；对异常失败/滥用率设置告警 |

公开接口仍须经过 `AnonymousViewCheckMidware` 或 v3 等价中间件，保留既有黑名单与验证码防护。每次内容查询序列化前均需执行文章匿名查看、讨论可见性与用户隐私规则。现有 `DesensitizeUtil` 可作为基线，但 v3 必须使用端点 DTO 字段白名单，不能仅依赖删除已知敏感字段。

## 实施顺序

1. 执行 `node tools/api-route-inventory.mjs`，审查新建或变更的有效路由，并在本文标注其预期 v3 归属。
2. 定义 v3 DTO 和响应/错误渲染器；编写序列化测试，证明敏感字段不会出现。
3. 新增 v3 Token 存储、哈希/轮换/撤销逻辑、Bearer 中间件、scope、请求 ID 与审计日志。
4. 交付文章、用户、标签、领域、专栏和信息流公开读接口；验证匿名、隐藏内容、验证码/黑名单及响应字段白名单链路。
5. 交付带 scope 的 `/me` 读取接口；验证用户不能读取他人的私有列表、通知、草稿、背包或积分。
6. 开放任何写 scope 前，先设计 OAuth PKCE 与幂等机制；每个写接口都使用事务并完成端到端授权测试。
7. 将 v3 专属文档写入 `API.md`，并在 `UPDATE_LOG.md` 增加带日期的第三方开发者更新记录；旧 API 保持文档直到另行公告废弃。

## 复用前需要解决的审计结论

以下是基于路由注册和中间件审查得出的设计结论，不代表每个处理器都存在漏洞。

| 结论 | 影响 | v3 必须采取的措施 |
| --- | --- | --- |
| `/api` 混合公开、用户、后台、内部和回调路由 | 不能通过 URL 前缀判断安全边界或调用方 | 使用独立 Processor 和 v3 命名空间；逐接口列明 scope 与调用方 |
| `loginCheck` 接受 session 与 apiKey，而 `apiCheck` 只接受 apiKey | 表面相近的路由可能有不同的浏览器/API 行为 | v3 只接受 Bearer，并与页面 CSRF 分离 |
| 部分旧写接口使用 GET 或没有统一写入中间件 | 缓存、预加载器和浏览器行为可能触发风险 | 变更只能使用 POST/PATCH/DELETE；要求 Content-Type、Token scope、所有权、限流，必要时幂等 |
| 现有 API 使用多种旧响应结构 | 客户端需要理解每个处理器的特殊约定 | 使用一种 v3 信封，独立版本演进 |
| 页面数据模型和 Repository JSON 比外部需求更丰富 | 复用时容易泄露敏感字段 | 建立窄 DTO 映射器，并对省略字段做回归测试 |
| 公开路由逐条挂载校验 | 新匿名接口可能绕过既有反扫描逻辑 | 要求 v3 公开读中间件，并以路由测试确保它不可缺失 |

## 人工验证清单

后续实现 v3 后，在 `http://localhost:8080` 完整验证以下用户链路：

1. 无凭据打开一篇公开文章并取得其 v3 表示；再访问关闭匿名查看的文章。确认公开文章仅返回文档字段，受限文章被拒绝且不泄露私有元数据。
2. 创建两个普通账号，为第一个账号创建仅有 `profile.read` 的 Token。调用 `/api/v3/me`，随后尝试读取第一个账号的私有资源与第二个账号的资源。确认 scope 与所有权失败可区分，且不出现敏感字段。
3. 从不可信 Origin 带浏览器 session Cookie 尝试 v3 写入；再以有效 Bearer Token 与幂等键发送同一写入。确认前者被拒绝，后者只成功一次，重放安全。
4. 测试非法 ID、大页大小、错误 JSON、HTML/Markdown 载荷、过期/撤销 Token、短时间突发调用。确认返回受控 4xx 和请求 ID，无服务端 500，无响应体或日志泄露。
5. 每条新增公开接口均测试匿名首次访问、黑名单 IP 跳转/验证码、验证码通过后释放、已登录访问，以及携带旧 apiKey 的访问。确认只接受设计中的 v3 凭据路径。

## 写入事务与幂等落地

`api_v3_request` 以 user + operation + Idempotency-Key 的哈希为主键，只保存规范化请求哈希和成功响应 DTO。请求行、领域写入、成功结果在一个数据库事务中提交；同键竞争通过主键及行锁串行化，存储冲突使用新事务有限重试。失败操作不留下业务写入或已占用的键；结果已提交但确认丢失时，重试读取原结果。

通用执行器的回调必须只进行当前事务内的数据库操作。后续文章、评论、通知等会产生推送/邮件/其他外部效果的业务必须另行接入事务后事件或 outbox，禁止把带独立事务或外部效果的旧服务直接包进执行器。当前草稿没有外部事件，已通过真实管理服务加事务测试适配器验证回滚、所有权和重放；MySQL 锁行为及 HTTP 仍待联调。

2026-10-19 授权码补充：已新增注册回调与hash-only授权码表、同事务签码/消费换Token对核心及重放家族撤销；回调范围已获用户确认：HTTPS、开发HTTP回环、原生登记的专用scheme。原生按RFC8252采用反向域名scheme和单斜线路径，完整地址（包括端口和查询）精确匹配，不采用动态端口例外。迁移见sql/20261019_api_v3_oauth.sql；授权请求记录、来源/CSRF/明确同意页、注册管理、HTTP换码/刷新与清理尚未接入，仍不能作为第三方OAuth上线完成证据。旧/openid链路不变，不能混用凭据。

2026-10-19 同意状态补充：授权请求表及owner-bound一次性批准/拒绝服务已实现，批准与签码同事务；Cookie同意页所需配置站点精确Origin/Referer和非空Session CSRF校验工具已隔离验证，但尚未接HTTP或模板。state可空、上限1024 UTF-8字节，回调ASCII表示上限2048且禁止预置code/state/error/iss等响应键。不能将工具测试作为浏览器同意或CSRF联调完成证据；下一步必须接Cookie-only身份、HTTP限流/清理及PC/移动页面。

### 2026-10-19 OAuth HTTP 接线

ApiV3OAuthProcessor 新增 GET authorize、POST consent/token/revoke。协议返回 HTML、303 或直接 OAuth JSON。Cookie 专用身份解析避免 apiKey 提升；两端授权模板与 SCSS 同步，日志过滤器须重启加载并在路由注册前检查。维护任务清理过期请求与码，保留活跃家族的重放检测依据。总路由701（HTTP691、WS10），浏览器引用398。69组核心/HTTP/模板/清理测试及Cookie专项1组通过；真实MySQL、完整IoC和生产匿名链路未验证。登记管理与凭据作为端点登录证明的策略尚待完成。实际文件与人工测试链路见 development-plan 最新记录。

2026-10-19 登记核心补充：ApiV3ClientRegistrationService 提供已批准应用的同事务原子登记，随机client_id、已实现scope词表、完整回调校验；无HTTP入口，身份/审批/幂等归调用方。管理员登记或自助待审的业务选择已提问，未答复前不开放登记。新增3组登记测试，合计72组通过；新应用真实S256换码参与测试，MySQL/管理入口尚未验证。源路由重新核对仍701，完整目标未完成。

### 2026-10-19 账号入口继续

新增GET /api/v3/auth/registration-options（anonymous），只供表单展示，不是注册证明。原始注册开关直查数据库投影，邀请资格复用角色读取，响应白名单不带用户对象。新增AccountVerificationService及VerifycodeRepository绑定查询：当前用户/手机号/类型/用途/有效期锁定消费，与后续账号变更同事务，重复同值码全部消费；没有公共验证/激活HTTP，旧register2/reset-pwd兼容变更尚待答复。注册选项4组与原专项扩展77组通过；源路由702（HTTP692、WS10），398浏览器引用。真实MySQL、短信、风控及IDEA重启后联调未执行。

完整目标中私信不能因早期“不直接搬旧接口”的建议被遗漏：需要专用权限、参与者可见性、事务化发送/已读/撤回和专用实时授权；当前没有v3私信入口。账号注册/恢复同样继续保留为必做，不把注册选项或内部验证primitive当完成证据。详情见development-plan最新记录。


### 2026-10-19 密码重置完成

新增 POST /api/v3/auth/password-reset/complete，经anonymous处理后以当前手机号及找回短信证明完成严格事务；AccountPasswordResetService也接入旧POST/reset-pwd，显式userId，不回退身份，提交后才旧Cookie登录。密码、绑定短信消费、全部client的v3 Token撤销和待决OAuth清理同提交；缓存仅事务结束后失效。旧注册2/重置兼容已获用户同意，注册2激活/奖励事务及两端code字段尚未实现，无需重复确认。旧reset部署也须既有v3/OAuth迁移与完整重启日志过滤器。

最新隔离回归共用82组、注册选项4组通过；重置5组实际SQL/根事务/故障/入口解析，SQLite和登录缓存替代实现不等于真实MySQL/IoC/Cookie/短信/全局风控。盘点703路由（693HTTP、10WS），398浏览器引用；未运行Maven/应用HTTP/浏览器，重启尚未确认。具体文件、权限/反漏扫检查及完整人工路径见development-plan最新记录。全量v3目标继续，短信申请/注册激活、应用管理、鱼游OAuth、私信、红包、支付等不能由本批测试推定完成。


### 2026-10-19 注册完成接线

新增POST /api/v3/auth/registration/complete（anonymous），与旧register2共用AccountRegistrationService严格根：预投影并锁定完整sys账户对、namespace/当前开关/排序用户/邀请码及绑定注册短信，激活/初始与邀请奖励/邀请码消费/通知同提交；缓存、索引、刷新与天降红包只在提交后。旧必传code已获同意并落实，PC/mobile隐藏字段及verify.js/生成产物同步。短信申请v3尚未实现，旧首次申请仍原链。旧register2/reset-pwd缺少统一anonymous验证的兼容变化另待用户答复，未接线且不能宣称旧首访检查通过。

共用专项91组、注册选项4组通过；前端yarn build通过；实际浏览器隔离fixture四项（PC/mobile×源码/min）通过，AJAX拦截，无真实注册。FTL完整语法/隐藏字段片段渲染通过，完整应用宏未运行。JS版本审计155文件0命中；路由704（694HTTP+10WS）、398浏览器引用。没有Maven/真实短信/MySQL/8080/完整IoC，重启未确认。源码文件清单、安全结论及完整人工链见development-plan最新记录；所有未完v3模块保持原范围。


### 2026-10-19 找回短信与旧提交匿名验证

用户已批准旧POST/register2、POST/reset-pwd接入anonymous；现已在旧业务前执行，无viewSkips变更。新增GET /api/v3/auth/captcha-options只返geetest4/public ID，POST /api/v3/auth/password-reset/request接受手机号与有界四字段极验证明，复用同步后的原SMS预算和固定发送服务。通用accepted不披露账号可用性/服务商结果；服务商受理后在严格根当前锁用户再绑定保存RESET_PWD码，远程期间无SQL事务，保持既有码。物理发送/SQL不是原子提交，无恒定时间或可靠队列声明。专用腾讯受理核对一项目标/+86/Ok，旧提供商正常返回契约保持；生产日志不写手机号/码/完整回执。

共用专项100组、注册选项4组通过；新增申请→现有完成核心、故障/变更/输入/额度/受理及公开组件；真实Dispatcher校验提取的旧路由顺序，匿名策略/SDK传输/Cookie为适配器。源码路由706（696HTTP+10WS），398浏览器引用。无新增表或前端，无Maven/真实短信/极验/MySQL/8080/完整IoC；重启仍未确认。文件清单与完整手验及安全/反漏扫限制见development-plan最新记录。v3注册短信及私信/红包/支付等完整目标继续，不把恢复链当全量完成。


### 2026-10-19 注册待验证账号核心

新增AccountPendingRegistrationService当前事务primitive及共享UserRegistrationDefaults，旧addUser复用纯默认字段。外层须已有严格根/ArticlePublicationCaches及完整短信/极验/邀请授权；namespace先锁、当前开关、排序有界用户锁，pending手机号复用/自然月注销等待期/会员编号与新记录同保存点写入；缓存按全部别名根结束后失效。新注册短信HTTP尚未接入，邀请码预留与极验条件待用户选择；证明还须绑定请求时名称，不能让旧重发改名后激活不对应名称。

共用107组、注册选项4组通过；实际服务/默认字段/metadata全列SQLite/严格提交/保存点/故障/边界及缓存适配器。没有新路由/表/前端/公开字段，仍706路由（696HTTP+10WS）、398浏览器引用。未运行Maven/真实MySQL/短信/头像/完整IoC/8080，重启未确认；API.md不虚构未开放的申请接口。具体文件清单、安全限制及人工回归见development-plan最新记录。完整v3范围不因内部primitive测试而缩减。

### 2026-10-20 私信写入进展

私信当前已有五个messages.read读取入口，以及messages.write下的发送、指定/全部已读三入口；独立Bearer及幂等，不能照搬旧apiKey WS。用户已确认发送接收者仅当前激活的其他用户及固定文件助手，已有历史仍按参与者读取。消息/未读/旧会话列表同事务，旧异步列表改为重读实际最新消息；旧整个发送仍非原子。迁移见20261020_api_v3_private_message_writes.sql，内容TEXT、索引及精确默认权限须先更新。专用撤回/实时、真实MySQL/应用运行与其他模块仍待完成；具体文件、测试适配边界和完整手验见development-plan最新记录，不将早期“不能直接搬旧接口”的建议当作删掉私信目标。


### 2026-10-20 私信撤回兼容入口与变更标记

v3 DELETE本人私信已接线，用户批准旧GET /chat/revoke共用严格事务核心；只清messageId/fromId/toId准确关联未读，保留未知历史记录，身份取ApiCheck认证attr，提交后旧推送失败不覆盖成功。旧GET无幂等回执。新增api_v3_private_revision（20261020_api_v3_private_realtime.sql及repository.json）在v3发送/已读/撤回和旧原子撤回同事务写owner标记。当前尚未覆盖旧普通发送/已读，未接专用私信Socket票据/授权复核/worker，不能宣称实时完成；标记不是历史游标。实际文件、人工完整链路和运行验证限制见development-plan最新记录。


### 2026-10-20 旧私信标记覆盖完成

旧普通/手动/认证申请私信三组写入现经ChatInfoRepository/ChatUnreadRepository.addLegacy单步严格根，消息标记双方、未读标记接收者；旧指定/全部已读用owner-aware删除。PrivateMessageListStateService.refreshLegacy最终列表提交再标双方，防止前两步刷新后遗漏列表；各旧步骤仍独立，不能称整个旧发送原子或幂等。chatLog提交后只记messageId，不重读删除目标。此前“旧普通发送/已读未覆盖”已更新；私信专用Socket票据/连接/授权复核/worker仍待接线。新表/元数据迁移仍须先部署，完整文件、故障边界与人工链见development-plan最新记录。


### 2026-10-20 私信实时取票与空变更提示

新增POST /api/v3/me/messages/realtime-ticket，messages.read+write gate，复用现有WS首帧一次性票据。ApiV3SocketTicketService绑定actor/父令牌/messages/空target，当前token/user/client/scope逐次复核；ApiV3RealtimeService.pollMessages读取已提交owner标记，sessions只向该owner的messages频道发送messages.changed/data={}。首次/重连ready后主动HTTP同步，提示合并/重复/丢失，不当逐条消息/已读回执/可靠游标。此前“私信Socket未接线”当前已更新；真实MySQL、多实例、IDEA/应用/浏览器及完整反漏扫待验收，完整v3目标仍active。文件和完整人工链见development-plan最新记录。


2026-10-20：头像GET/PUT /api/v3/me/settings/avatar已接入，读取profile.read、写settings.write+幂等事务。新写接受合法HTTPS（外部、无后缀及带query），255 UTF-16；GET返回保存地址，旧页面显示来源和缩放规则保持。参数/身份/回滚测试及限制见development-plan最新头像记录；其他账号设置和完整运行验收仍待完成。


2026-10-20：MBTI本人读取和选项目录GET /api/v3/me/settings/personality、/personality-options已接入profile.read；仅当前有效owner的mbti和16类型/A或T目录。写入无效值规则已询问，尚未开放新PUT，不改旧资料/注册行为。测试、安全限制及完整人工步骤见development-plan最新记录。


2026-10-20：MBTI写入规则已获用户确认并接入PUT /api/v3/me/settings/personality，严格16类型/可选-A或-T、大小写与首尾空白规范化、空字符串清空/无效422。settings.write、当前用户锁、原ProfileWriteQuota、settings.personality幂等事务，仅更新mbti/时间；旧资料及注册规则保持。上一条待确认状态现已关闭，具体测试与人工链路见development-plan最新记录。


2026-10-20并行整理：当前可达v3入口181条（180HTTP+1WS），完整METHOD/path、源码/测试/运行验收状态及剩余用户能力以[api-v3-progress.md](api-v3-progress.md)为当前索引。新接文章热度、表情4GET和红包类型目录，具体实现/验证限制见development-plan本批记录及两个work文档；不能再沿用历史矩阵漏项或把声明/测试适配器计为完整应用验收。

2026-10-20第二轮子代理已接线职业、勋章、会员6个GET；当前可达v3注册187条（186 HTTP+1 WS），最新逐项清单与剩余写入范围见 [api-v3-progress.md](api-v3-progress.md)。OAuth登记已由用户确认仅管理员登记维护，管理HTTP尚待实现。
