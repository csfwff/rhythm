# 适用

本文主要适用于第三方开发者，更新日志会着重偏向API的更新，以便第三方开发者能够方便、快速的更新客户端。

# 261020

## v3 管理员 OAuth 客户端修改

新增 `PUT /api/v3/admin/oauth/clients/{clientId}`，网站第一方管理员持 settings.write 与 Idempotency-Key 完整替换 name、scope、redirectUris，沿用登记上限。权限集合或回调集合变化会令全部已有令牌、旧授权码、待确认授权失效，用户需重新授权；仅名称或集合顺序变化保留授权。保留当前启用状态，不提供重新启用；同键重试不覆盖后续修改。

## v3 本人匿名内容列表

新增 `GET /api/v3/me/anonymous-articles` 与 `GET /api/v3/me/anonymous-comments`，需要 content.read，分页默认20条、最大50条。返回本人匿名创作概要，评论列表按所属文章当前阅读权限筛选；正文继续通过文章或评论详情接口读取。

## v3 客户端停用与邀请码状态

新增 `DELETE /api/v3/admin/oauth/clients/{clientId}`，仅网站第一方管理员持 settings.write 与 Idempotency-Key 使用。逻辑停用并撤销该客户端已有令牌，保留登记资料和回调，不提供重新启用；请求体可省略或空JSON，最多1024字节，同键重试恢复原结果。新增 `POST /api/v3/invitation-codes/status`，需要 activity.read，查询已知16位字母数字邀请码，返回 used/unused/stopped；请求体最多1024字节，unused仅为存储状态，不保证当前可注册。

## v3 管理员 OAuth 客户端登记

新增第三方客户端列表、详情及登记：`GET /api/v3/admin/oauth/clients`、`GET /api/v3/admin/oauth/clients/{clientId}`、`POST /api/v3/admin/oauth/clients`。仅管理员持网站第一方令牌及 settings.write 使用，登记需要 Idempotency-Key；每个客户端支持 1–20 个回调，每个最多 2048 字符，沿用完整地址精确匹配规则。同键重试恢复原登记结果，客户端停用与修改见本日后续记录。

## v3 主职业与勋章显示设置

新增 `PUT /api/v3/me/professions/primary` 与 `PUT /api/v3/me/medals/{medalId}/display`，均需要 settings.write 和 Idempotency-Key。主职业可选择当前已发布职业，无需既有经验且不收费；勋章显示使用明确布尔值设置，重复请求不会反向切换。同键同请求恢复原结果，不覆盖后续设置；同键不同内容返回 HTTP 409。原职业与勋章入口规则保持。

## v3 职业、勋章和会员读取

新增职业目录 `GET /api/v3/professions` 及本人职业 `GET /api/v3/me/professions`，支持分页，本人读取需要 profile.read，返回选择状态、隐私设置及已产生的职业进度。新增 `GET /api/v3/me/medals` 与 `GET /api/v3/users/{userName}/medals`，分别返回本人有效勋章（需要 profile.read，含隐藏）和用户公开展示的有效勋章。新增 `GET /api/v3/membership/levels`（content.read）及 `GET /api/v3/me/membership`（profile.read），返回档位、已知权益和本人会员状态。职业目录、勋章列表和会员档位分页默认 20 条、最大 50 条；本批未开放对应修改、开通或收费接口。

## v3 表情设置、文章热度与红包目录

新增 4 个个人表情读取接口：常用别名、可选项、本人图片分组及分组图片，均需要 profile.read。图片列表支持分页，无分组时返回空列表，读取不自动创建分组。新增文章热度 GET /api/v3/articles/{articleId}/heat，需要 content.read，返回当前节点的文章连接数近似值，不代表去重或全站人数。新增 GET /api/v3/chat/red-packet-types，需要 content.read，列出 5 种类型及可能损失积分的提示；v3 创建和领取暂未开放。

## v3 MBTI 修改

新增 `PUT /api/v3/me/settings/personality`，需要 settings.write 和 Idempotency-Key。接受 16 种标准类型及可选 -A/-T，忽略大小写和首尾空白，空字符串清空；无效值返回 HTTP 422 并保留旧设置。按规范化后的内容判断幂等，同键重试不覆盖后续修改，同键不同内容返回 HTTP 409。旧资料设置和注册的 MBTI 规范化规则保持。

## v3 MBTI 读取

新增 `GET /api/v3/me/settings/personality` 读取当前账号 MBTI，以及 `GET /api/v3/me/settings/personality-options` 返回 16 种类型和可选 A/T 后缀；均需要 profile.read。未设置或不可用的 MBTI 返回空字符串。

## v3 头像设置

新增 `GET /api/v3/me/settings/avatar` 与 `PUT /api/v3/me/settings/avatar`，分别需要 profile.read、settings.write。写入需要 Idempotency-Key，接受 1–255 个 UTF-16 单位的 HTTPS 图片地址，支持外部来源及带签名查询参数的地址。读取返回保存的地址；同键重试返回原结果，不覆盖后续头像设置，同键不同内容返回 HTTP 409。旧头像设置入口的规则保持。

## v3 私信实时订阅

新增 `POST /api/v3/me/messages/realtime-ticket`，需要 messages.read，返回 channel=messages 的一次性票据。连接 `/api/v3/realtime`，等待 authentication.required 后使用票据认证，ready 后可接收本人 `messages.changed` 空提示。

客户端在首次连接、重连及变更提示后重新查询会话、历史和未读数；提示可能合并、重复或丢失，不包含消息正文、消息ID、对方身份或未读数量，不提供断点续传。已有令牌或账号失效时连接关闭，刷新令牌后须重新取票。

## 旧私信写入兼容维护

旧私信发送和已读保留请求及正常响应字段，改善写入失败时的数据一致性；发生存储错误或响应中断时，请先查询当前历史及未读状态，再决定是否重试。

## 旧私信撤回兼容调整

旧 `GET /chat/revoke` 已同步使用原子撤回，保留 oId、apiKey 请求字段、result 成功格式和原撤回推送，兼容query及JSON请求体中的apiKey。只清理准确关联的本条消息未读，其他消息及无法归属的历史未读保留，不再清空该发送者全部未读；依赖旧行为的客户端请改为明确调用已读接口。

消息、未读及会话列表共同更新，失败不再部分删除；推送失败不把已提交的撤回改报失败。旧接口没有幂等回执，响应中断后先查询目标是否仍存在。v3撤回继续使用原Idempotency-Key重试。

## v3 私信撤回

新增 `DELETE /api/v3/me/messages/{messageId}`，需要 messages.write 和 Idempotency-Key，仅允许发送者撤回本人私信，返回 id、peerId、removed。无需请求体或发送空JSON对象，最多1024字节，不接受查询参数或其他字段。

撤回同步更新会话最新消息，只清理准确关联的本条消息未读，保留其他消息及无法归属的历史未读；历史未读可由接收方使用已读接口处理。回执不返回接收者已读状态。同键重试恢复原回执，不重复删除；新键访问已撤回或非本人消息返回404。原接收者删除或停用不影响撤回本人历史消息，文件传输助手同样支持。客户端应重新查询列表/历史，本次不保证实时推送。

## 私信请求中断后的重试

私信发送在响应中断或暂不可用后，应使用原 Idempotency-Key 和原正文重试；已成功保存的消息返回原回执，不重复发送。重试也会刷新会话与未读显示所需的状态，请勿通过换新键重复提交。旧未读查询的正常响应字段保持。

## v3 私信发送与已读

新增 `POST /api/v3/me/conversations/{peerId}/messages`、`POST /api/v3/me/conversations/{peerId}/read` 和 `POST /api/v3/me/conversations/read`，分别发送私信、标记指定会话或全部私信已读。需要新增 messages.write 权限及 Idempotency-Key；已有令牌须重新登录或获准后重新授权，刷新不会增加权限。

发送接收者限当前已激活的其他用户及文件传输助手，正文为Markdown、最多10240个UTF-16单元，沿用现有保留词处理及首尾空白规则。发送返回 id、peerId、sentAt，同键重试不重复发送。已读返回 changed 记录数，不删除历史，也不提供逐条已读状态；同键重试不会清除后来产生的未读记录。消息读取仍需 messages.read，客户端应查询历史与未读数，本次不保证实时推送。

混用新旧私信入口时，会话列表按实际最新消息显示，延迟到达的旧消息事件不会覆盖较新消息。旧发送及读取请求字段、正常响应保持。

## v3 注册短信申请

新增 `POST /api/v3/auth/registration/request`，接收 userName、userPhone、captcha 及可选 invitecode、referral，返回 data.accepted。请求为JSON，最多8192字节，不接受查询参数。开放注册、邀请链接和邀请码三种方式均须提交极验4结果；显式邀请人须为已激活的其他用户，邀请链接资格另行判断。收到短信后调用既有完成注册接口，再登录获取令牌。

验证码及邀请码预留有效期一天。同手机号重发可续期，其他手机号使用有效预留返回HTTP 409；完成时沿用申请的邀请信息，资料变化后重新申请。accepted 不代表短信已到达；本接口不支持 Idempotency-Key，请勿在超时后自动反复申请。

旧 `POST /register` 同步遵守邀请码预留，冲突沿用原JSON错误格式，正常字段、短信流程和成功响应保持。旧入口可能返回HTTP 302，客户端需按Location完成页面交互后重试。PC/移动注册和找回密码页面在收到非JSON响应或网络错误时保留表单，提供手动页面交互入口，不自动重发；错误提示按文本展示。

# 261019

## 注册资料一致性

完成注册时请沿用申请短信时的邀请信息。名称或邀请信息变化后，请重新获取验证码。申请字段、正常成功响应和奖励金额保持。

## v3 私信读取

新增 GET /api/v3/me/conversations、GET /api/v3/me/conversations/{peerId}、GET /api/v3/me/conversations/{peerId}/messages、GET /api/v3/me/messages/{messageId} 和 GET /api/v3/me/messages/unread-count，提供本人会话、双向游标历史、消息详情与未读记录数。会话按最新消息 ID 倒序分页，size 默认20、最大50；历史每页按消息 ID 升序，before/after 互斥且不包含边界。读取不会自动标记已读。

需要新增 messages.read 权限。已有登录令牌须重新登录；第三方应用获准申请后，用户须重新授权，刷新不会增加权限。消息同时提供原始 Markdown、清洗后的 HTML 与纯文本摘要；sentAt 沿用服务器当地 yyyy-MM-dd HH:mm:ss 字符串。对方账号删除或停用后，本人仍可读已有消息，对方资料以 available=false 表示。未读数是未读记录数量，不提供逐条已读状态。本次仅提供读取能力。

## v3 找回密码短信与账号验证组件

新增 `GET /api/v3/auth/captcha-options`，返回 geetest4 组件的公开 captchaId；完成组件交互后，将四项结果序列化为 captcha 字符串提交 `POST /api/v3/auth/password-reset/request`，同时传 userPhone。申请接口返回 data.accepted，符合条件的手机号收到短信后可调用既有完成密码重置接口；accepted 不代表短信已到达。JSON 请求体最多8192字节，captcha 最多4096个UTF-8字节，验证码有效期一天。请勿在超时后自动反复申请短信。

旧 `POST /register2`、`POST /reset-pwd` 可能返回HTTP 302；客户端需按Location完成页面交互，再向原接口重试。正常请求字段及成功字段保持，register2 仍须携带注册短信 code。

## v3 注册完成与旧客户端调整

新增 `POST /api/v3/auth/registration/complete`，以 userPhone、6位注册短信 code、32位小写 MD5 userPassword 及可选 userAppRole、mbti、referral 完成注册，返回 data.userId、data.registered，之后使用登录接口获取访问令牌。请求为 JSON，不接受查询参数；userAppRole 使用数字，最多4096字节。已激活账号或已使用的验证码不能重复注册，响应丢失时可先尝试登录。

旧 `POST /register2` 必须新增 code 字段，值为注册短信验证码；PC和移动注册页已同步传入。验证码须与该用户当前手机号、注册用途和有效期相符。激活、验证码消费、初始积分、邀请链接及邀请码奖励共同完成，失败不消费有效验证码。正常角色、奖励金额、成功字段及成功后登录行为保持。

## v3 密码重置与旧入口兼容

新增 `POST /api/v3/auth/password-reset/complete`，以 userPhone、6位找回密码短信 code 和32位小写 MD5 userPassword 完成重置，返回 data.changed；成功后需重新登录。本接口不提供 Idempotency-Key 回执，响应丢失时请尝试新密码登录或重新获取验证码。

旧 `POST /reset-pwd` 保留 userId、code、userPassword 及成功后登录行为。新旧入口均要求验证码与用户当前手机号、找回密码用途和有效期相符，成功后一次消费，未完成时不消费。重置成功后，该账号已有的 v3 访问令牌、刷新令牌及尚未完成的授权失效，客户端须重新登录或授权。

## v3 注册选项

新增 `GET /api/v3/auth/registration-options`，返回开放、邀请或关闭状态，邀请码要求、用户名长度和可选角色。可传 referral 查询邀请链接资格；邀请人不存在或不具备资格时不免除邀请码，关闭注册优先。非法邀请人用户名返回 HTTP 422。查询结果仅供注册表单展示，最终以提交时的状态为准。

## v3 第三方 OAuth 授权

新增 GET /api/v3/oauth/authorize、POST /api/v3/oauth/consent、POST /api/v3/oauth/token 和 POST /api/v3/oauth/revoke。已登记应用通过浏览器授权页与 PKCE S256 获取访问令牌，支持刷新轮换及撤销本次授权。回调支持 HTTPS、开发 HTTP 回环地址与预登记的原生专用 scheme，完整地址必须精确匹配。

OAuth 页面返回 HTML 或 303 回调，令牌及撤销直接返回 JSON，不使用业务接口的 code/msg/data 信封；令牌字段使用 access_token、refresh_token、expires_in、refresh_expires_in、token_type、scope。授权码有效 5 分钟，只能使用一次；刷新须串行替换旧令牌。客户端收到 invalid_grant 时重新授权，避免重放已消费凭据。

## v3 聊天室弹幕发送

新增 `POST /api/v3/chat/barrager`，需要 content.write 和 Idempotency-Key，返回 id、createdAt、cost、unit。正文去除HTML后最多32个UTF-16单位，颜色仅接受#RRGGBB，默认白色；无效输入返回 HTTP 422。

扣费与发送回执共同提交，同键重试不再扣费或播放；费用采用发送时当前价格。chat 实时订阅新增 `chat.barrager`，只包含纯文本、颜色、时间及公开作者资料。弹幕不进入聊天历史、不提供离线补播，事件只发往接收请求实例的在线 v3 chat 连接，受理成功不保证动画送达。

两套聊天室弹幕展示同步使用纯文本和编码后的作者链接；旧发送请求及正常响应字段保持。

## v3 聊天室话题修改

新增 `PUT /api/v3/chat/topic`，需要 content.write 和 Idempotency-Key，返回 text、cost、revision。每次成功修改消耗 16 积分；同键同内容重试不重复扣费。文本超过 64 个 UTF-16 单位或过滤后为空返回 HTTP 422。

新旧话题入口共同保存当前话题，重启后保留；无效内容或未提交成功时不扣话题费用。旧正常请求、成功字段和 16 积分费用保持，依赖重启清空话题的客户端需调整。读取仍使用 `GET /api/v3/chat/topic`，实时变更提示见 `chat.topic_changed`。

## v3 登录状态维护

完善 `POST /api/v3/auth/refresh` 的轮换处理和 `POST /api/v3/auth/logout` 的当前登录撤销。登录、刷新和退出的正常请求及响应字段保持不变；请勿重复使用刷新前的登录 Cookie。

# 261018

## v3 聊天室话题提示

实时协议新增 `chat.topic_changed` 类型，data 仅包含字符串 revision；客户端收到后重新查询 `GET /api/v3/chat/topic`。原消息提示字段保持不变，客户端应忽略未知提示类型。

## v3 聊天室撤回、话题与弹幕价格

新增 `GET /api/v3/chat/topic`、`GET /api/v3/chat/barrager/price`，需要 content.read，分别返回话题纯文本和当前价格。读取不会修改话题、发送弹幕或扣分。

聊天室话题变更提示按纯文本显示；撤回消息后同步清理该消息的 emoji reaction，正常费用与成功字段保持。

更正旧撤回接口说明：普通用户每天可多次撤回，当天费用依次为 0、32、64……积分。

新增 `DELETE /api/v3/chat/messages/{messageId}`，需要 content.write 和 Idempotency-Key，仅撤回本人消息，返回 id、removed、cost；同键重试不重复扣费。

新旧撤回统一扣款与删除，未提交成功时不扣分、不计次。每日记录共用，首次启用从零开始，之后改名或重启不再清零；普通用户归属按作者 ID 判断。旧成功字段与管理角色原有撤回权限、免费规则保持，依赖同名认领历史消息或重启清零的客户端需调整。

## v3 聊天室普通消息发送

新增 `POST /api/v3/chat/messages`，需要 content.write 和 Idempotency-Key，支持 Markdown 正文及可选 client 标记，返回消息 ID 和发送时间。相同键与内容重试不重复发送或扣分；相同键换内容返回 HTTP 409。

普通消息中的红包、天气、音乐、话题、弹幕业务包装和执法/zf命令返回 HTTP 422（special_message_required），不执行自动 AI 回复。需要再次确认正文时返回 HTTP 409（confirmation_required），使用原键和原请求体再次发送。服务端错误或断线时也应以原键重试确认结果。

## 聊天室普通消息保存

改进 `POST /chat-room/send` 普通消息保存失败时的处理，正常请求、成功字段及业务语法保持不变。保存错误或连接中断后，请先刷新聊天记录确认结果，再决定是否重发。

## v3 聊天室在线用户

新增 `GET /api/v3/chat/online-users`，需要 content.read，支持 p/size 分页（默认20、最多50）。按用户名排序并合并同一用户的多个连接，遵循在线状态公开设置，返回可见人数及节点采样时间和完整性。

统计范围为当前服务实例的聊天室连接及最近一次配置节点名单；完成认证的 v3 聊天订阅计入，通知/文章订阅及其他服务实例独立建立的 v3 连接不计入。结果不代表全站实时总人数，连接变化时分页结果可能改变。原在线接口保持现有字段。

## v3 聊天室实时订阅

新增 `POST /api/v3/chat/realtime-ticket`，需要 content.read。使用一次性票据连接 `WS /api/v3/realtime`，认证后接收 chat.message.created、chat.message.updated、chat.message.deleted、chat.message.reactions，携带消息 ID 与版本，客户端据此重新读取消息或 reaction。

收到 chat.sync_required 时清除旧缓存并重新同步当前消息窗口；首次连接和重连也应主动读取 HTTP 状态。票据最多60秒可用于认证且只能成功使用一次；不提供按版本断点续传、在线人数、话题或弹幕事件，原通知与文章协议保持。

## v3 聊天室消息读取

新增 `GET /api/v3/chat/messages`、`GET /api/v3/chat/messages/{messageId}` 与 `GET /api/v3/chat/messages/{messageId}/context`，需要 content.read。列表使用 before/after 游标，默认20条、最多50条，返回按消息ID升序排列的结果；上下文默认前后各10条、最多各25条。

普通消息返回原始 Markdown 与清洗后的 HTML；红包、天气、音乐返回对应卡片字段。读取红包卡片只返回摘要，不会领取。不存在或已撤回消息返回HTTP 404；历史消息暂不可读取时返回 unavailable 对象，保留ID供继续翻页。原聊天室接口保持现有字段。

## v3 文章实时订阅

新增 `POST /api/v3/articles/{articleId}/realtime-ticket`，需要 content.read。使用返回的一次性文章票据连接 `WS /api/v3/realtime`，等待 authentication.required 后认证；ready 返回绑定的 articleId，article.changed 提示客户端重新读取文章、评论或 emoji reaction。

一张票据绑定一篇文章，最多60秒可用于认证且只能成功使用一次。失去阅读权限或令牌失效时连接关闭；首次连接、断线重连后应主动重新读取 HTTP 状态。提示可能合并或重复，不提供持久化重放、在线人数或热度事件，原通知连接协议保持。

## 旧改名卡提交结果反馈

改进 `POST /bag/nameCard`、`POST /api/bag/nameCard` 保存失败时的反馈，正常成功字段及一张卡的消耗保持不变。结果暂未确认或断线时，请先刷新资料和背包；v3 改名卡接口使用原 Idempotency-Key 和原请求体重试确认结果。

## v3 写入错误反馈与通知刷新

改进v3写入失败时的错误反馈，正常成功字段保持不变。支持Idempotency-Key的接口遇到服务端错误或断线时，应使用原键和原请求体重试，避免重复执行同一次操作。

通知刷新提示补齐通知新增、已读和删除来源；实时通知服务暂不可用时，取票返回HTTP 503（realtime_unavailable）。

时间字段说明与现有返回值保持一致，默认使用毫秒时间戳；本次未改变字段类型或时间值。

## v3 实时通知连接

新增`POST /api/v3/realtime/tickets`和`WS /api/v3/realtime`，需要notifications.read。先取一次性票据，连接后等待authentication.required，再发送authenticate消息；成功返回ready，本账号通知变化提示为notifications.changed，客户端收到后重新读取通知列表或未读数。

票据最多60秒有效且只能成功使用一次；连接授权到期后需使用新的访问令牌重新取票连接。旧实时通道保持原协议。

## v3 文件上传票据

新增 `POST /api/v3/uploads/tickets`，需要 uploads.write，返回180秒有效的上传票据、完整uploadURL及文件字段名file。客户端携带上传票据向该地址直传文件；每次取票返回新票据，无需Idempotency-Key。

已有登录状态需重新登录获取新权限。旧上传票据及文件代理入口保持原请求和响应结构。

## v3 改名卡使用

新增 `POST /api/v3/me/bag/nameCard/use`，同时需要 activity.write、settings.write 和 Idempotency-Key。用户名规则同注册，每次消耗一张改名卡；同键同内容重试不重复改名或扣卡，同键不同内容返回 HTTP 409。

旧 `/bag/nameCard`、`/api/bag/nameCard` 同步将改名与扣卡一并保存，校验或保存失败不扣卡；正常成功字段和一张卡的消耗保持，旧 API 支持 query/body apiKey。

## v3 补签卡使用

新增 `POST /api/v3/me/bag/patchCheckinCard/use`，需要 activity.write 和 Idempotency-Key。按平台自然日补一天，能接回已有连续记录时接回；同键重试不重复扣卡，不额外发放签到积分。

旧补签入口同步采用相同规则，校验或保存失败保留卡片和补签记录，正常成功字段及消耗保持。每日签到的奖励、连续记录和补签提示一并保存，原基础及连续签到奖励保持。

## 内容删除 emoji reaction 清理

文章和评论删除时同步清理对应的 emoji reaction，保留其他目标的记录。原入口与 v3 删除共用修复，原删除条件、费用及响应字段保持不变。

## 内容删除关联通知清理

改进文章和评论删除后的关联通知清理，处理购买、感谢、采纳、赞踩及收藏订阅等互动消息，并保留无关通知。原入口与 v3 删除共用对应修复，删除条件、费用和响应字段保持不变。

## 文章删除关联记录修复

修复文章删除可能影响其他资源投票及奖励记录的问题。原文章删除入口与 `DELETE /api/v3/articles/{articleId}` 共用修复，原删除条件、请求及成功字段保持不变。

## 原文章打赏正文读取调整

`POST /article/reward` 仅在文章当前可阅读且启用打赏，并确认购买成功、已有购买记录或作者本人时返回 articleRewardContent HTML。失败不再返回打赏正文，正常成功字段和费用保持；apiKey 支持 URL 查询参数与 JSON 请求体。

依赖未购买或未启用打赏状态读取正文的客户端需改为合法购买；本人编辑原始内容请使用本人文章编辑源码接口。

## v3 文章感谢与打赏

新增 `GET/POST /api/v3/articles/{articleId}/thanks`、`GET/POST /api/v3/articles/{articleId}/reward`。读取需要 content.read，操作需要 content.write 和 Idempotency-Key；支持本人状态、感谢次数/费用及打赏价格读取。

感谢和打赏沿用平台费用及匿名免转账规则，同键重试和新键重复操作不再次付款；失败时撤销相关扣款、计数和记录。打赏成功后通过文章详情读取正文，需要 content.read。原感谢/打赏核心同步采用原子事务，旧入口正常费用和成功字段保持不变。

修复 v3 文章投票误将锁定但仍可阅读的文章拒绝的问题，沿用原投票规则。

## v3 文章投票

新增 `GET/PUT/DELETE /api/v3/articles/{articleId}/vote`。读取需要 content.read，设置和清除需要 content.write 与 Idempotency-Key。PUT 只接受 up/down，重复设置不取消；DELETE 清除本人投票。返回 vote、upCount、downCount，同键重放不覆盖后续投票。

原 `/vote/up/article`、`/vote/down/article` 同步保存投票、计数、关联列表排名和点赞通知，失败时撤销整次操作；再次点击取消、切换、自投限制及成功 type 字段保持不变，query/body apiKey 均可使用。

## v3 评论投票

新增 `GET/PUT/DELETE /api/v3/comments/{commentId}/vote`。读取需要 content.read，设置和清除需要 comments.write 与 Idempotency-Key。PUT 只接受 up/down，是设置而非再次点击取消；DELETE 清除本人投票。返回 vote、upCount、downCount。

相同请求键重试返回原结果，新键重复设置或清除不重复计数；重放不会覆盖后续投票，最新状态通过 GET 获取。原评论赞踩入口的取消、切换及 type 字段保持不变。

## v3 评论删除

新增 `DELETE /api/v3/comments/{commentId}`，需要 comments.write 和 Idempotency-Key，仅能删除满足平台条件的本人评论或段评。费用及回复、赞踩、感谢、采纳限制沿用原规则；失败不扣款，同键重试不重复扣费，删除后换新键返回 HTTP 404。

评论发布和编辑提醒按当前可读范围发送，已删除评论不再补出延迟提醒；重复提醒不重复保存。

## 评论投票一致性修复

`POST /vote/up/comment` 与 `POST /vote/down/comment` 同步保存投票与计数，失败时撤销整次操作；保留再次点击取消及赞踩互换行为。成功响应的 type 仍表示操作前的投票，query/body apiKey 均可使用。

## v3 评论采纳

新增 `POST /api/v3/comments/{commentId}/accept`，需要 comments.write 和 Idempotency-Key，仅文章作者可采纳可阅读的他人评论。相同请求键重试、换键重复采纳同一评论均不重复付款；已有其他采纳时返回 HTTP 409。

原 `/comment/accept` 同步将转账、采纳标记、奖励记录和通知一并保存，写入失败时撤销对应付款；成功响应、悬赏积分及匿名免转账规则保持不变，已有其他采纳时仍不再操作。

## v3 评论感谢

新增 `POST /api/v3/comments/{commentId}/thanks`，需要 comments.write 和 Idempotency-Key，只能感谢可阅读的他人评论或段评。相同请求键重试返回原结果，换键重复感谢也不再次扣费；匿名评论沿用免转账规则。

原 `/comment/thank` 同步将扣费、感谢计数、感谢记录和通知一并保存，失败时不扣款；请求字段、成功提示和费用保持不变，query/body apiKey 均可使用。

# 261017

## 评论删除扣费一致性修复

`POST /comment/{评论的oId}/remove` 仅在删除成功时扣除原有费用，删除条件不满足或保存失败时不扣款。请求字段、成功费用和响应字段保持不变；URL 查询参数及 JSON 请求体中的 apiKey 均可使用。

## v3 历史段评标识兼容

修复正文含站内链接时，历史段评可能在 v3 中漏计或无法定位的问题。段落目录、正文锚点、计数、筛选和投稿统一兼容既有标识，并支持可确认对应关系的早期 v3 标识；段评详情返回当前定位链接。文章编辑时关联信息随正文事务迁移，回复关系、正文和历史快照保持。

无法唯一对应的标识返回 HTTP 409，不发表或扣款。v3 新回复不再复制历史段落快照，当前段落摘要从段落目录获取。

## v3 长篇段落与评论详情

新增 `GET /api/v3/articles/{articleId}/paragraphs`、`GET /api/v3/articles/{articleId}/paragraph-comments` 和 `GET /api/v3/comments/{commentId}`。支持段落锚点与摘要、可选的完整锚点正文、有效及失效段评分页、回复和单条评论读取；使用令牌时需要 content.read。段评发布的 paragraphId 建议从 v3 段落列表取得，评论对象不返回历史段落快照。

## 匿名评论实时消息调整

匿名评论实时消息的 commentAuthorId 改为空字符串，字段保留；普通公开作者 ID 保持不变。依赖真实 ID 判断匿名作者的客户端需调整，本人及楼主状态可分别使用 commentIsCurrentUser、commentIsArticleAuthor；有权阅读者的私密正文不变。

## v3 评论发布与回复

新增 `POST /api/v3/articles/{articleId}/comments`，需要 comments.write 和 Idempotency-Key，支持普通评论、回复及长篇段评。正文、积分、版本和回复关系一并保存；相同请求键重试不重复发布或扣款。回复限当前文章内可读的评论，段评锚点取自 v3 长篇段落列表。

仅楼主可见评论的实时消息向无权阅读者返回空 commentContent，接收资格按文章当前可见范围判断；有权阅读者的正文保持不变。评论提醒也遵循当前可读范围。

## 评论发布一致性修复

修复评论正文保存后扣费失败可能只完成部分操作的问题。评论、版本、积分、回复通知已读和自动订阅现随发布结果一并处理；原请求字段、响应、费用、匿名及段评规则保持不变。

## v3 评论编辑

新增 `PUT /api/v3/comments/{commentId}`，需要 comments.write 和 Idempotency-Key，仅能编辑本人评论，支持普通评论与段评。正文必填，不传 visible 时保留原可见范围；作者、匿名状态、回复目标和段落关联保持不变。

评论正文、版本与编辑费用一并保存；相同请求键重试不重复编辑或扣款，重放不覆盖后续编辑。原五分钟和匿名收费规则不变，原评论编辑接口字段与响应保持不变；仅楼主可见评论的编辑提醒遵循正文可见范围。

## 评论客户端鉴权兼容修复

修复评论发布、长篇段评和编辑在仅提供 URL 查询参数 apiKey 时可能无法取得当前用户的问题。JSON 请求体 apiKey 继续可用，原请求字段、响应格式和正文规则保持不变。

## v3 本人文章编辑源码

新增 `GET /api/v3/me/articles/{articleId}/source`，需要 content.read，返回本人文章原始正文、打赏正文、编辑选项和有效专栏章节信息，便于客户端回填编辑器。不存在和他人文章统一返回 HTTP 404；原文章详情接口保持不变。

## v3 文章删除

新增 `DELETE /api/v3/articles/{articleId}`，需要 content.write 和 Idempotency-Key，仅能删除满足原平台条件的本人文章。同键重试返回原结果，已删除后使用新键返回 HTTP 404。

删除时同步清理相关数据；保留满 24 小时、周报及评论/订阅/收藏/赞踩/打赏/感谢限制。原文章删除接口参数与响应不变。

## v3 文章编辑

新增 `PUT /api/v3/articles/{articleId}`，需要 content.write 和 Idempotency-Key，只允许编辑本人文章。支持部分更新，保留未提交字段及章节；正文、费用、版本和段评位置一并保存，同键重试不重复扣款或覆盖后续修改。

保留原有编辑费用、周报、悬赏与打赏规则；历史思绪可维护原记录，其他类型不能转换为思绪。同步修复 PC/移动端思绪回放与预览显示，保留安全格式。

## 文章编辑一致性修复

修复文章编辑保存与扣款可能部分成功的问题，编辑失败时撤销对应扣款；长篇段评位置与正文一起保存。原请求、响应、费用及编辑规则保持不变。

## v3 文章发布

新增 `POST /api/v3/articles`，需要 content.write 和 Idempotency-Key，支持普通帖、讨论帖、同城广播、问答及长篇。支持匿名、打赏、专栏章节与本人草稿发布；成功返回 id、permalink，同键重试不重复发布或扣款，草稿不可访问时整次发布失败。

沿用平台发帖条件；思绪新增仍关闭，内容需通过审核。原文章接口参数、响应与免审权限保持不变。

同时修复 v3 认证的 Cookie 适配问题，登录、刷新和退出接口字段保持不变。

## 周报发布奖励修复

修复周报发布失败后奖励仍可能到账的问题。奖励现在随帖子发布成功后生效，发布条件、奖励金额和响应字段保持不变。

## 文章发布一致性修复

修复正文保存与发帖扣款可能出现部分成功的问题；发布前核对余额能否覆盖全部实际费用，失败时撤销对应扣款。原费率、匿名发布规则、请求及成功响应字段保持不变。

## 首页跳转设置校验

旧功能设置入口与 v3 统一校验首页跳转地址：仅接受当前站点及部署路径内的地址。中文路径、锚点及正常编码文件名保持可用，请求与响应字段不变。

## 写入处理修复

修复文章、资料、清风明月及鱼游部分写入在并发请求下行为不一致的问题，原请求参数和响应字段保持兼容。

## v3 功能、位置公开与语言设置

新增功能、位置公开、语言和时区三组 GET/PUT，以及语言/时区可选项，共 7 条接口。功能设置涵盖列表显示、通知、订阅邮件、快捷键、回复自动订阅、外链确认、聊天室图片模糊和站内首页跳转。位置只允许切换公开状态，不允许修改城市或 IP。读写分别需要 profile.read / settings.write，写入支持幂等与部分更新；旧接口响应字段保持不变。

## v3 个人设置

新增资料、隐私、显示设置的 GET/PUT 子接口，以及可用皮肤目录，共 7 条路由。读取需要 profile.read，修改需要 settings.write 和 Idempotency-Key；仅更新提交字段，重放旧请求不会覆盖后续设置。隐私接口覆盖 13 项开关，皮肤按 PC/移动端校验。原设置汇总和旧接口字段保持不变；升级后请重新登录获取新权限。

## v3 大事记投稿与本人查询

新增 `POST /api/v3/milestones`，需要 content.write 和 Idempotency-Key，Markdown 正文渲染清洗后保存并等待审核，同键重试不重复投稿。

新增 `GET /api/v3/me/milestones` 和 `GET /api/v3/me/milestones/{milestoneId}`，需要 content.read，可查询本人待审核、已通过和已拒绝记录，支持状态筛选与分页。其他账号的投稿详情返回 HTTP 404；公开时间线展示范围保持不变。原投稿接口保留响应字段。

## v3 免签卡使用

新增 `POST /api/v3/me/bag/checkin1day/use` 与 `/api/v3/me/bag/checkin2days/use`，需要 activity.write 和 Idempotency-Key。每次消耗一张卡，分别增加 1/2 天剩余免签天数，并返回使用后的背包；同键重试不重复消耗。旧接口保留原响应结构，扣卡与天数增加改为同时保存，失败时不部分生效。

## v3 昨日活跃度奖励

新增 `GET /api/v3/me/activity/yesterday-reward` 查询奖励与领取状态，及同路径 POST 领取。分别需要 activity.read、activity.write；领取需要 Idempotency-Key，同键重试返回原结果，无可领取奖励或重复领取返回 HTTP 409。积分与领取结果同时保存，原领取接口响应保持兼容。升级后请重新登录获取新权限。日常签到仍由活跃度自动触发，不增加手动签到入口。

## v3 专栏管理

新增专栏改名、封面修改/清空、个人排序和删除空专栏四个接口，均需要 content.write 和 Idempotency-Key。排序支持一次调整 1–200 个本人专栏，未提交的专栏保持原顺序；任一目标不可操作时整次失败。删除含章节专栏或改为冲突名称时返回 HTTP 409，非本人或不存在的专栏返回 HTTP 404。原接口响应保持兼容。

# 261016

## v3 鱼游投票与评论

新增 `PUT /api/v3/fish-games/{gameId}/vote`、`DELETE /api/v3/fish-games/{gameId}/vote` 和 `POST /api/v3/fish-games/{gameId}/comments`。投票需要 reactions.write，评论需要 comments.write，均要求 Idempotency-Key；只允许操作已公开的鱼游。同值重复投票不会取消，换值替换，清票使用 DELETE；评论重试不会重复创建。

原投票接口继续保留 toggle 行为与响应字段。后台编辑和审核更新仅写相应元数据字段，避免覆盖较新的投票计数。

## v3 表态读取、设置与清除

新增文章、评论、聊天室消息的表态读取、PUT 设置、DELETE 清除接口，以及 `/api/v3/reactions/emojis` 可用表情目录，共 10 条路由。写入需要 reactions.write 和 Idempotency-Key；设置相同 value 不会取消表态，换值会替换，清除使用 DELETE。

读取返回汇总、本人选中态与公开展示资料。评论表态遵循文章阅读权限和私密评论权限。相同幂等键重试返回原结果，不会反转或覆盖后续状态；原有 toggle 接口保留其响应与切换行为。

## v3 清风明月编辑与关注流

新增 `PUT /api/v3/breezemoons/{breezemoonId}`，需要 content.write 和编辑权限，支持幂等重试；只允许修改本人有效记录，不改变作者、创建时间或记录状态。新增 `GET /api/v3/me/watching/breezemoons`，需要 content.read，按关注用户的公开内容分页；无关注内容时返回空列表。

鱼游 v3 列表默认采用总投票数、点赞数、时间的排序顺序，显式 `sort=likes` 仍可按点赞数排序。

## v3 清风明月发布、详情与删除

新增 `POST /api/v3/breezemoons`、`GET /api/v3/breezemoons/{breezemoonId}`、`DELETE /api/v3/breezemoons/{breezemoonId}`。写入需要 content.write 和 Idempotency-Key，保留手机绑定、审核及功能权限要求；删除仅限本人。发布和删除支持幂等重试，删除后重试原发布请求不会重新创建记录。

修正 v3 全站清风明月列表，遵循作者的列表公开设置；私密记录仅对有权阅读的账号展示。旧接口字段保持兼容。

## v3 关注、收藏与订阅

新增当前账号关注用户/标签、收藏文章、订阅文章的四类列表，以及对应 PUT 添加、DELETE 取消接口，共 12 条路由。读取需要 profile.read 或 content.read，写入需要 follows.write 和 Idempotency-Key。重复设置同一状态不会重复增加关系或计数；同键重试返回原结果，同一关系添加/取消使用同键返回 HTTP 409。

个人收藏、订阅保留不可访问目标的占位记录，方便取消关系；有权阅读的讨论帖正常展示。旧取消接口同时修正“关系不存在仍减少计数”的问题，响应字段保持兼容。

## v3 主页评论与关注动态

新增用户评论、关注动态与订阅文章读取接口：`/api/v3/users/{userName}/comments`、`/watching`、`/watching/articles`。三者均遵循对应列表公开设置，并支持统一分页。关注动态支持 all/users/tags 来源筛选并去重；订阅文章与收藏列表分别读取。主页评论不收录匿名评论或讨论帖，私密正文按阅读权限展示。旧接口保持兼容。

## v3 排行榜

新增 `GET /api/v3/rankings/{type}`，覆盖 15 类榜单，支持 Evolve 与小冰子榜单及统一分页。记录返回名次、分数、对应指标和公开用户或链接资料；赞助榜另返回汇总。未知类型/子榜单返回 HTTP 422，小冰榜暂不可用返回 HTTP 503。

旧赞助榜与优选榜接口修正查询结果容器兼容问题，恢复正确读取榜单，原有响应字段保持不变。

## v3 社区信息读取

新增鱼游列表/详情/评论、大事记列表/详情、社区统计及同城用户/文章接口。鱼游支持 likes、votes 两种排序，默认总投票数优先；列表使用真实分页。大事记仅返回已审核内容。同城列表遵循公开位置设置，文章列表不包含匿名发表文章。旧接口保持兼容。

## v3 通知读取与状态操作

新增通知列表、详情、分类未读计数，以及单条/分类已读和删除接口，路径位于 `/api/v3/me/notifications*`。列表支持 `type`、`status`、`p`、`size`；当前不可展示的目标返回占位通知。读取不会自动标记已读。

读取需要 `notifications.read`，状态变更需要 `notifications.write` 和 `Idempotency-Key`。相同操作重试返回原结果，批量操作的重试不会影响后来新增的通知。升级后请重新登录获取相应权限。旧接口保持兼容。

## v3 草稿创建、替换与删除

新增 `POST /api/v3/me/drafts`、`PUT /api/v3/me/drafts/{draftId}`、`DELETE /api/v3/me/drafts/{draftId}`，需要 Bearer 访问令牌及 `content.write`。三者均要求 `Idempotency-Key` 请求头；成功操作可使用原键重试，相同操作与键提交不同内容返回 HTTP 409。

创建和替换分别保留编辑器正文、思绪记录及专栏/发帖选项。PUT 为完整替换，省略字段恢复默认值；成功返回草稿摘要。原有草稿接口保持兼容。

## v3 个人资源读取

新增 `GET /api/v3/me/settings`、`/me/checkin`、`/me/bag`、`/me/points`、`/me/drafts`、`/me/drafts/{draftId}`、`/me/columns`。使用 Bearer 访问令牌读取当前账号的数据，分别需要 `profile.read`、`activity.read` 或 `content.read`；具体权限见接口文档。草稿详情同时保留编辑器正文和思绪记录，列表只返回摘要。

## v3 内容发现与公开主页

新增 `/api/v3/search`、标签与领域的目录/详情/文章列表、领域标签列表，以及公开用户资料、用户文章、清风明月、关注用户/标签/收藏文章和粉丝列表。新增 `/api/v3/breezemoons` 全站清风明月列表。列表统一使用 `p`、`size` 和 `data.list/data.pagination`，用户子列表沿用原有隐私设置。文章发现支持 `all/recent/long` 类型与四种排序。

需要登录身份读取时使用 Bearer 访问令牌；用户资料、关注用户与粉丝需要 `profile.read`，其他本批接口需要 `content.read`。旧接口保持兼容。同时修正 v3 登录状态恢复提前失效的问题。

## v3 文章与评论读取

新增 `GET /api/v3/articles`、`GET /api/v3/articles/{articleId}`、`GET /api/v3/articles/{articleId}/comments`。列表支持 `p`、`size` 与排序，文章列表以 `type=recent/long` 区分普通文章和长篇。

详情提供正文源内容、HTML、内容格式及可查看的打赏内容；评论列表提供回复目标、正文可见状态和公开作者资料。需要登录身份读取时使用 Bearer 访问令牌及 `content.read` 权限。原有接口保持兼容。

## v3 分页与认证审计

`/api/v3/columns` 与 `/api/v3/columns/hot` 支持 `p`、`size` 分页参数。v3 登录失败、刷新和退出事件增加不记录凭据明文的服务端审计日志。

## API v3 首批接口

新增 `/api/v3` 版本化 Web API，统一响应结构为 `{ code, msg, data }`。首批提供 website_v3 的登录、登录状态刷新、退出登录、当前用户资料，以及最新/热门专栏和专栏详情读取。

登录后使用 Bearer 访问令牌调用需要授权的接口；旧 `/api` 与 `apiKey` 接口保持兼容，废弃计划将另行公告。

# 261008-2

## 批次二：排行榜、个人主页、关注动态、同城、大事记与统计

**排行榜接口结构调整（破坏性变更）**：所有 `/api/top/*` 接口的 `data` 由"数组"统一调整为对象 `{list, totalData, type}`，请改用 `data.list` 读取榜单列表。同时全部 8 类榜单（签到/在线/财富/消费/优选/邀请/捐赠/游戏）改为匿名可读，无需 apiKey。

**帖子详情增强**：

- `GET /api/article/{id}` 确认匿名可读；仅当作者关闭"匿名查看"时匿名请求返回 401，登录/apiKey 不受影响
- 返回新增 `relevantArticles`（相关帖子）、`previousArticle` / `nextArticle`（上一篇/下一篇，轻量字段）
- 评论排序新增 query 参数 `m`：`0` 按时间正序（默认）、`1` 按时间倒序；不传时沿用登录用户偏好

**新增接口**：

- 个人主页：`GET /api/user/{userName}/comments` 评论列表、`/long` 长文章、`/watching` 关注动态聚合、`/following/articles` 关注用户的帖子、`/following/tags` 关注的标签、`/following/users` 关注的用户、`/followers` 粉丝；以上匿名可读，目标用户隐私设置关闭对应列表时仅本人或管理员可见（返回 `code != 0`）。`/comments/anonymous` 匿名评论、`/articles/anonymous` 匿名帖子、`/points` 积分流水**仅本人**可调（需要 apiKey）
- 关注动态：`GET /api/watch/tags/articles`、`GET /api/watch/users/articles`、`GET /api/watch/breezemoons`（需要 apiKey）；`/api/watch/breezemoons` 无关注或关注无内容时返回空列表，不再回退为全站清风明月
- 专栏管理：`POST /api/columns/{columnId}/rename` 重命名、`POST /api/columns/order` 排序、`POST /api/columns/{columnId}/remove` 删除（有章节的专栏需先移出章节）
- 清风明月：`GET /api/breezemoons` 与 `GET /api/watch/breezemoons` 每条新增 `breezemoonContentRaw` 原始 Markdown 字段
- 同城：`GET /api/city/{cityName}` 同城用户分页（匿名，仅含公开位置的用户）
- 大事记与统计：`GET /api/milestones`、`GET /api/statistic`（匿名）

约定：

- 需登录接口均通过 `apiKey` 鉴权；分页接口参数 `p`（默认 1）/`size`（默认 20，上限 50）
- 列表统一返回 `data.xxx` + `data.pagination = {paginationPageCount, paginationRecordCount}`


# 261008

## 批次一：第三方客户端只读/轻操作 API

为方便第三方 SPA 客户端对接，新增一批 `/api` 前缀接口，并对存量 `/api` 接口做纯增量字段补充。所有现有接口形状不变，FTL 页面对应链路不受影响。

新增接口：

- 鉴权：`GET /api/reset-pwd/meta`，忘记密码时用重置码换取用户 Id（匿名）
- 背包：`GET /api/user/bag` 读取背包；`GET /api/bag/1dayCheckin`、`GET /api/bag/2dayCheckin`、`GET /api/bag/patchCheckin` 使用免签/补签卡；`POST /api/bag/nameCard` 使用改名卡
- 签到：`GET /api/checkin/status`，一次返回今日是否签到、活跃度、自动签到门槛与连续签到天数
- 专栏：`GET /api/columns/latest`、`GET /api/columns/hot` 首页货架；`GET /api/columns/mine` 我的专栏；`GET /api/columns/{columnId}` 专栏详情；`GET /api/columns/{columnId}/articles` 章节分页
- 鱼游：`GET /api/fish-games` 已审核鱼游分页列表
- 帖子：`POST /api/article/stick` 作者置顶自己的帖子（按次扣积分）

字段补充：

- `GET /api/user` 返回新增 `userGuideStep` 字段（新人引导步骤）

约定：

- 需登录接口均通过 `apiKey` 鉴权；列表接口设有 `size` 上限
- 写接口（道具使用、置顶）均需要 apiKey

# 260927

## 图床切换韵图 RhyPic：旧上传接口 `POST /upload` 即将停用（重要）

站点图床已切换为新的 **RhyPic（韵图）** 图床，旧上传接口 `POST /upload` 将在不久后**彻底停用**。请第三方客户端尽快阅读最新对接文档，按「获取上传票据 → 直传韵图」两步流程完成适配：

- 对接文档：https://fishpi.cn/article/1790518451759

新流程的响应结构（`data.errFiles` / `data.succMap`）与旧接口兼容，原有解析逻辑可直接沿用，主要变更为鉴权方式（一次性票据请求头）、上传地址（韵图图床）与文件字段名（`file`）。

# 260920

## 新版勋章接口

新增 `POST /api/medal/admin/holds`，支持查询单个用户或最多 100 个用户是否持有指定勋章。接口返回当前持有状态、过期状态、到期时间和勋章数据。

新增 `POST /api/medal/admin/grant-batch`，支持向单个用户或最多 100 个用户发放指定勋章。批量写入使用同一事务，任一用户校验或写入失败时整批失败；已有勋章会更新到期时间和勋章数据。

原有单用户接口保持兼容。

# 260908

## 鱼游投稿与管理

活动页的固定鱼游列表调整为“摸鱼鱼游”内容库，支持展示已审核鱼游、点赞、点踩和评论。

用户可投稿网站，投稿内容由管理员人工审核后公开。投稿作者可以提交资料修改申请，审核通过后更新公开内容。

新增鱼游管理页，管理员可手动添加、审核、停用、编辑、导入和导出鱼游数据。旧活动页的固定鱼游清单已整理为导入文件。

第三方接口已补充到 API 文档，包括投稿、修改申请、投票、评论和管理员管理接口。

修正鱼游接口的 API key 认证示例；写入接口增加 CSRF 校验和用户频率限制。

## 发帖与优质奖励

普通发帖页改为通过“这是好帖”复选框申请优质奖励，不再在发布时弹出奖励选择窗口。获得奖励的帖子会显示“好帖”标识。

管理员、OP 和超级会员新增免审发帖与免审保存入口。该入口会跳过敏感词过滤和内容审核，服务端会按用户分组校验权限，未授权请求会被拒绝。

发帖和更新帖子接口新增可选参数 `articleBypassCensor`，接口文档已同步更新。

## 页面调整

PC 与移动端同步调整普通发帖和长文发布页的操作区布局。

专栏书城改为“最新专栏”和“热门专栏”分区展示，并调整专栏卡片样式。

职业设置页调整分区间距与预设选项样式。

# 20260519

* 新增“复读机转录站”：首页展示段子、星期四、鱼科普内容，支持随机切换、复制、点赞、登录后上传。
* 新增专栏封面能力：专栏支持自定义封面，长篇发布页可带封面，作者可在 **/column/manage** 管理封面

# 260515

## 评论变革！

评论增加新的接口，按层级模式展示评论，**原接口仍兼容保留，API文档已更新**

![PixPin_2026-05-15_17-29-45.png](https://file.fishpi.cn/2026/05/PixPin20260515172945-d1127e2e.png)

## 发帖/发长文章新增草稿功能

![PixPin_2026-05-15_17-30-27.png](https://file.fishpi.cn/2026/05/PixPin20260515173027-50305ec3.png)

同样已更新到API文档，注意普通帖子和长文章虽然API相同，但是参数（type）不同，开发时请仔细阅读。

# 260429

帖子历史版本 diff 功能重构，历史记录列表与具体版本正文改为按需读取，避免长帖多次更新后一次性返回全部历史正文。

新增两个帖子历史版本接口：

- `GET /article/{id}/revisions/list`
- `GET /article/{id}/revisions/{revisionId}`

原 `GET /article/{id}/revisions` 接口已移除，第三方客户端需改用新的列表接口和详情接口。

帖子历史版本弹窗同步优化，支持选择任意两个历史版本进行标题和正文对比，也可以只显示差异部分，复制内容时不再包含 `+` / `-` 标记。

此功能已适配 API，可参阅 https://fishpi.cn/article/1636516552191 的 【模块：帖子历史版本】章节

补充评论历史版本 API 文档：

- `GET /comment/{id}/revisions`

该接口用于获取指定评论的历史版本列表，返回评论历史正文。评论历史接口为既有接口，本次未拆分为列表接口和详情接口。

# 260422

帖子和表情新增emoji贴纸选项，该贴纸不消耗积分，可反复修改要贴的emoji贴纸，emoji为预置不可自定义。

此功能已适配API，可参阅 https://fishpi.cn/article/1636516552191 的 【模块：贴emoji】章节

![PixPin_2026-04-29_10-52-10.png](https://file.fishpi.cn/2026/04/PixPin20260429105210-725063be.png)
