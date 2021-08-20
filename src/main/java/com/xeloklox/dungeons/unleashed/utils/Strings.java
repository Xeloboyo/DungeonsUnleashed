package com.xeloklox.dungeons.unleashed.utils;

import java.io.*;

public class Strings{
    public static int parseInt(String s){
        if(s==null||s.length()==0){return 0;}
        return Integer.parseInt(s);
    }

    public static String resourceAsString(String uri){
        String format = uri.replace("src/main/resources","");
        System.out.println(format);
        InputStream in = Strings.class.getResourceAsStream(format);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();
        String s;
        try{
            while((s = reader.readLine()) != null){
                sb.append(s);
            }
        }catch(IOException e){
            e.printStackTrace();
        }
        return sb.toString();
    }
}
