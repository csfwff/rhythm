#!/usr/bin/env node
/** Actual shared chat validator/service/legacy middleware. Identity, reserved-word storage and clock are explicit adapters; no application DB or HTTP server. */
import fs from 'node:fs';import os from 'node:os';import path from 'node:path';import {spawnSync} from 'node:child_process';
const ide='/Applications/Android Studio.app/Contents',home=process.env.JAVA_HOME||`${ide}/jbr/Contents/Home`;
const cp=['src/main/resources/lib/latke-core.jar',process.env.API_V3_TESTNG_JAR||`${ide}/plugins/testng/lib/testng.jar`].join(path.delimiter),work=fs.mkdtempSync(path.join(os.tmpdir(),'rhythm-chat-validation-')),shims=[];
function shim(name,source){const file=path.join(work,`${name}.java`);fs.mkdirSync(path.dirname(file),{recursive:true});fs.writeFileSync(file,source);shims.push(file);}
function run(cmd,args){const r=spawnSync(cmd,args,{stdio:'inherit'});if(r.error)throw r.error;if(r.status!==0)throw new Error('Chat validation tests failed');}
try{
 const source=fs.readFileSync('src/main/java/org/b3log/symphony/util/Symphonys.java','utf8'),constant=source.match(/public static final long MIN_STEP_CHAT_TIME\s*=\s*\d+;/)?.[0];if(!constant)throw new Error('Review changed chat interval declaration');
 shim('org/b3log/symphony/util/Symphonys',`package org.b3log.symphony.util;public class Symphonys {${constant}public static final String[] RESERVED_USER_NAMES={};}`);
 shim('org/b3log/symphony/util/ReservedWords','package org.b3log.symphony.util;public class ReservedWords {public static int calls;public static String processReservedWord(String input){calls++;return input.replace("RESERVED","❤️");}}');
 shim('org/b3log/symphony/util/Sessions','package org.b3log.symphony.util;public class Sessions {public static org.json.JSONObject current;public static int reads;public static org.json.JSONObject getUser(){reads++;return current;}}');
 shim('org/b3log/symphony/processor/ApiProcessor','package org.b3log.symphony.processor;public class ApiProcessor {public static final java.util.Map<String,org.json.JSONObject> users=new java.util.HashMap<>();public static org.json.JSONObject getUserByKey(String key){var user=users.get(key);if(user==null)throw new NullPointerException();return user;}}');
 const filterSource=fs.readFileSync('src/main/java/org/b3log/symphony/processor/ChatroomProcessor.java','utf8');
 const filters=filterSource.slice(filterSource.indexOf('public static String filterDiscussString('),filterSource.indexOf('/** Called by v3 only'));
 shim('org/b3log/symphony/processor/ChatroomProcessor',`package org.b3log.symphony.processor;public class ChatroomProcessor {${filters}}`);
 const real=['util/ChatMessageClients','model/Role','model/UserExt','model/Common','util/ChatMessageValidation','service/ChatMessageValidationService','processor/middleware/validate/ChatMsgAddValidationMidware'];
 real.push('util/ChatTopicInputs','util/ApiV3Json','util/ApiV3Exception','util/ChatMessageText');
 run(path.join(home,'bin','javac'),['--release','25','-proc:none','-cp',cp,'-d',work,...shims,...real.map(s=>`src/main/java/org/b3log/symphony/${s}.java`),'src/test/java/org/b3log/symphony/util/ChatMessageValidationTest.java']);
 run(path.join(home,'bin','java'),['-cp',[work,cp].join(path.delimiter),'org.b3log.symphony.util.ChatMessageValidationTest']);
}finally{fs.rmSync(work,{recursive:true,force:true});}
