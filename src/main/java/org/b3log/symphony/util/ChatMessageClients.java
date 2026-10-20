package org.b3log.symphony.util;

import java.util.List;

/** Existing chat client-label contract; unknown brands are omitted. */
public final class ChatMessageClients {
    private ChatMessageClients() { }
    private static final List<String> LEGAL_CLIENT = List.of(
            "Web",
            "PC",
            "Mobile",
            "CLI",
            "uTools",
            "Windows",
            "macOS",
            "Linux",
            "IceNet",
            "ElvesOnline",
            "iOS",
            "Android",
            "Extension",
            "IDEA",
            "Chrome",
            "Edge",
            "VSCode",
            "Python",
            "Golang",
            "Bird",
            "Dart",
            "Harmony",
            "Rust",
            "Other");
    public static String normalize(final String input){
        try{
            final String[] parts=input.split("/");
            final String client=parts[0];
            String version=parts[1].replaceAll("[^0-9a-zA-Z\\u4e00-\\u9fa5.\\s\\-]","");
            if(version.length()>32)version=version.substring(0,31);
            return LEGAL_CLIENT.contains(client)?client+"/"+version:"";
        }catch(RuntimeException ignored){return "";}
    }
}
