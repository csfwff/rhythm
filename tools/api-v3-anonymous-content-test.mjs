#!/usr/bin/env node
/** Isolated Java 25 + SQLite exercise of actual anonymous content SQL/controller/auth/routes; no server/database access. */
import fs from 'node:fs';import path from 'node:path';import os from 'node:os';import {spawnSync} from 'node:child_process';
const ide='/Applications/Android Studio.app/Contents',home=process.env.JAVA_HOME||`${ide}/jbr/Contents/Home`;
const cp=['src/main/resources/lib/latke-core.jar',process.env.API_V3_JSOUP_JAR||`${ide}/lib/intellij.libraries.jsoup.jar`,process.env.API_V3_TESTNG_JAR||`${ide}/plugins/testng/lib/testng.jar`,process.env.API_V3_SQLITE_JAR||`${ide}/plugins/android/lib/sqlite-jdbc-3.51.1.0.jar`].join(path.delimiter);
const dir=fs.mkdtempSync(path.join(os.tmpdir(),'rhythm-v3-anonymous-content-')),shims=[];
function shim(name,body){const file=path.join(dir,name+'.java');fs.mkdirSync(path.dirname(file),{recursive:true});fs.writeFileSync(file,body);shims.push(file);}
function method(source,signature){const begin=source.indexOf(signature);if(begin<0)throw Error(signature);let depth=0;for(let i=source.indexOf('{',begin);i<source.length;i++){if(source[i]==='{')depth++;if(source[i]==='}'&&--depth===0)return source.slice(begin,i+1);}throw Error('Unclosed method');}
function run(bin,args){const r=spawnSync(bin,args,{stdio:'inherit'});if(r.error)throw r.error;if(r.status!==0)throw Error('Anonymous content tests failed');}
try{
 shim('org/b3log/symphony/util/Symphonys','package org.b3log.symphony.util;public class Symphonys {public static final String[] RESERVED_USER_NAMES={};}');
 for(const [name,table] of [['UserRepository','user'],['ArticleRepository','article'],['CommentRepository','comment']])shim('org/b3log/symphony/repository/'+name,`package org.b3log.symphony.repository;import org.b3log.latke.repository.AbstractRepository;public class ${name} extends AbstractRepository {public ${name}(){super("${table}");}}`);
 const tokens=fs.readFileSync('src/main/java/org/b3log/symphony/service/ApiV3TokenMgmtService.java','utf8');
 shim('org/b3log/symphony/service/ApiV3TokenMgmtService',`package org.b3log.symphony.service;import org.json.JSONObject;import org.apache.commons.lang.StringUtils;import java.util.*;public class ApiV3TokenMgmtService {public JSONObject authenticate(String value){return null;}${method(tokens,'public static boolean hasScope(')}}`);
 shim('org/b3log/symphony/processor/ApiV3Processor','package org.b3log.symphony.processor;public class ApiV3Processor {public static final String IDENTITY="apiV3Identity";public static String requestId(org.b3log.latke.http.RequestContext c){return "fixture-anonymous-content";}public static void success(org.b3log.latke.http.RequestContext c,Object data){c.attr("test.status",200);c.attr("test.data",data);}public static void error(org.b3log.latke.http.RequestContext c,int status,String code,String msg){c.attr("test.status",status);c.attr("test.code",code);}}');
 const routes=fs.readFileSync('src/main/java/org/b3log/symphony/processor/ApiV3AnonymousContentProcessor.java','utf8').split('\n').filter(line=>line.includes('Dispatcher.get('));
 if(routes.length!==2||routes.some(line=>!line.includes('auth::contentRead')))throw Error('Review anonymous-content route authentication');
 shim('org/b3log/symphony/processor/AnonymousContentRouteBindings',`package org.b3log.symphony.processor;import org.b3log.latke.http.Dispatcher;import org.b3log.symphony.processor.middleware.ApiV3AuthMidware;public class AnonymousContentRouteBindings {public static void register(ApiV3AnonymousContentProcessor p,ApiV3AuthMidware auth){${routes.join('\n')}}}`);
 const real=['model/UserExt','model/Role','model/Article','model/Comment','util/ApiV3AnonymousContentDto','util/ApiV3Requests','util/ApiV3Exception','util/ApiV3Json','util/UserMentionCandidates','service/ApiV3AnonymousContentQueryService','processor/ApiV3AnonymousContentProcessor','processor/middleware/ApiV3AuthMidware'];
 run(path.join(home,'bin','javac'),['--release','25','-proc:none','-cp',cp,'-d',dir,...shims,...real.map(s=>'src/main/java/org/b3log/symphony/'+s+'.java'),'src/test/java/org/b3log/symphony/service/ApiV3AnonymousContentQueryTest.java']);
 run(path.join(home,'bin','java'),['-cp',[dir,cp].join(path.delimiter),'org.b3log.symphony.service.ApiV3AnonymousContentQueryTest']);
}finally{fs.rmSync(dir,{recursive:true,force:true});}
