#!/usr/bin/env node
/** Isolated Java 25 + SQLite exercise of actual emoji read SQL/controller/auth/routes; no server/database access. */
import {createHash} from 'node:crypto';import fs from 'node:fs';import path from 'node:path';import os from 'node:os';import {spawnSync} from 'node:child_process';
const ide='/Applications/Android Studio.app/Contents',home=process.env.JAVA_HOME||`${ide}/jbr/Contents/Home`;
const cp=['src/main/resources/lib/latke-core.jar',process.env.API_V3_TESTNG_JAR||`${ide}/plugins/testng/lib/testng.jar`,process.env.API_V3_SQLITE_JAR||`${ide}/plugins/android/lib/sqlite-jdbc-3.51.1.0.jar`].join(path.delimiter);
const dir=fs.mkdtempSync(path.join(os.tmpdir(),'rhythm-v3-emotion-')),shims=[];
function shim(name,body){const file=path.join(dir,name+'.java');fs.mkdirSync(path.dirname(file),{recursive:true});fs.writeFileSync(file,body);shims.push(file);}
function method(source,signature){const begin=source.indexOf(signature);if(begin<0)throw Error(signature);let depth=0;for(let i=source.indexOf('{',begin);i<source.length;i++){if(source[i]==='{')depth++;if(source[i]==='}'&&--depth===0)return source.slice(begin,i+1);}throw Error('Unclosed method');}
function run(bin,args){const r=spawnSync(bin,args,{stdio:'inherit'});if(r.error)throw r.error;if(r.status!==0)throw Error('Emoji settings tests failed');}
try{
 shim('org/b3log/symphony/util/Symphonys','package org.b3log.symphony.util;public class Symphonys {public static final String[] RESERVED_USER_NAMES={};}');
 shim('org/b3log/symphony/repository/UserRepository','package org.b3log.symphony.repository;public class UserRepository extends org.b3log.latke.repository.AbstractRepository {public UserRepository(){super("user");}}');
 const tokens=fs.readFileSync('src/main/java/org/b3log/symphony/service/ApiV3TokenMgmtService.java','utf8');
 shim('org/b3log/symphony/service/ApiV3TokenMgmtService',`package org.b3log.symphony.service;import org.json.JSONObject;import org.apache.commons.lang.StringUtils;import java.util.*;public class ApiV3TokenMgmtService {public JSONObject authenticate(String value){return null;}${method(tokens,'public static boolean hasScope(')}}`);
 shim('org/b3log/symphony/processor/ApiV3Processor','package org.b3log.symphony.processor;public class ApiV3Processor {public static final String IDENTITY="apiV3Identity";public static String requestId(org.b3log.latke.http.RequestContext c){return "fixture-emotion";}public static void success(org.b3log.latke.http.RequestContext c,Object data){c.attr("test.status",200);c.attr("test.data",data);}public static void error(org.b3log.latke.http.RequestContext c,int status,String code,String msg){c.attr("test.status",status);c.attr("test.code",code);}}');
 const routes=fs.readFileSync('src/main/java/org/b3log/symphony/processor/ApiV3EmotionProcessor.java','utf8').split('\n').filter(line=>line.includes('Dispatcher.get('));
 if(routes.length!==4||routes.some(line=>!line.includes('auth::profileRead')))throw Error('Review emotion route authentication');
 shim('org/b3log/symphony/processor/EmotionRouteBindings',`package org.b3log.symphony.processor;import org.b3log.latke.http.Dispatcher;import org.b3log.symphony.processor.middleware.ApiV3AuthMidware;public class EmotionRouteBindings {public static void register(ApiV3EmotionProcessor p,ApiV3AuthMidware auth){${routes.join('\n')}}}`);
 const settings=fs.readFileSync('src/main/java/org/b3log/symphony/processor/SettingsProcessor.java','utf8');
 const options=fs.readFileSync('src/main/java/org/b3log/symphony/util/ApiV3EmotionOptions.java','utf8');
 if(!settings.includes('ApiV3EmotionOptions.legacyGroups()')) {
  const old=settings.slice(settings.indexOf('private static final String[][] emojiLists = ')).split(';')[0].replace('private static final String[][] emojiLists','private static final String[][] GROUPS');
  if(!options.includes(old))throw Error('Classic picker and v3 catalog diverged');
 }
 // Frozen ordered aliases from HEAD's classic picker before extraction (159 entries incl. EOF).
 const aliases=[...options.split('private static final String[][] GROUPS = ')[1].matchAll(/"([^"\n]+)"|Emotion\.(EOF_EMOJI)/g)].map(match=>match[1]||match[2]);
 if(aliases.length!==159||createHash('sha256').update(aliases.join('\n')).digest('hex')!=='13cf198dbf10b7fdac6efe766bdaab7f721c427e034f7338bfe71253fc8d6e1e')throw Error('Review intentional classic picker catalog change');
 const defaults=fs.readFileSync('src/main/java/org/b3log/symphony/service/EmotionQueryService.java','utf8').match(/COMMON_USED = "([^"]+)"/)[1];
 if(!options.includes(`DEFAULTS = "${defaults}"`))throw Error('Legacy/v3 default favorites diverged');
 const real=['model/UserExt','model/Role','model/Emotion','model/Emoji','model/EmojiGroup','model/EmojiGroupItem','model/FishGame','model/FishGameVote','model/Milestone','repository/EmotionRepository','repository/EmojiRepository','repository/EmojiGroupRepository','repository/EmojiGroupItemRepository','util/ApiV3EmotionOptions','util/ApiV3CommunityDto','util/ApiV3Requests','util/ApiV3Exception','service/ApiV3EmotionQueryService','processor/ApiV3EmotionProcessor','processor/middleware/ApiV3AuthMidware'];
 run(path.join(home,'bin','javac'),['--release','25','-proc:none','-cp',cp,'-d',dir,...shims,...real.map(s=>'src/main/java/org/b3log/symphony/'+s+'.java'),'src/test/java/org/b3log/symphony/service/ApiV3EmotionQueryTest.java']);
 run(path.join(home,'bin','java'),['-cp',[dir,cp].join(path.delimiter),'org.b3log.symphony.service.ApiV3EmotionQueryTest']);
}finally{fs.rmSync(dir,{recursive:true,force:true});}
