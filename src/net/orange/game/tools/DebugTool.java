package net.orange.game.tools;

import net.orange.game.Main;
import net.orange.game.data.exception.JsonAnalyzeException;
import net.orange.game.data.exception.JsonKeyException;
import net.orange.game.data.json.JsonAnalyze;
import net.orange.game.data.json.JsonObj;
import net.orange.game.data.json.JsonObject;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Arrays;
import java.util.Scanner;

public class DebugTool {
    public static void mainloop(){
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        while(running){
            System.out.print("DEBUG >> ");
            String line = scanner.nextLine();
            String[] args = line.split(" ");
            if (args.length==0) continue;
            String cmd = args[0];
            args = Arrays.copyOfRange(args,1,args.length);
            switch(cmd){
                case "exit" -> running = false;
                case "refresh" ->{
                    Main.mainWindow.refresh();
                    log("refreshed");
                }
                case "get" -> {
                    if (args.length<1){
                        log("args not enough");
                        break;
                    }
                    JsonObject obj = query(args[0]);
                    if (obj == null){
                        log("key \"" + args[0] + "\" not found");
                        break;
                    }
                    if (args.length<2){
                        log("full value of \"" + args[0] + "\" is:\n"+obj);
                    }else{
                        String path = args[1];
                        JsonObj o = obj.read("."+path);
                        if (o == null){
                            log("path \""+path+"\" of \"" + args[0] + "\" not found");
                        }
                        log("path \""+path+"\" of \"" + args[0] + "\" is:\n"+o);
                    }
                }
                case "write"->{
                    if (args.length<3){
                        log("args not enough");
                        break;
                    }
                    JsonObject obj = query(args[0]);
                    if (obj == null){
                        log("key \"" + args[0] + "\" not found");
                        break;
                    }
                    String path = args[1];
                    String value = args[2];
                    JsonObj o;
                    try {
                        o = JsonAnalyze.analyze(JsonAnalyze.simply(value));
                    }catch (JsonAnalyzeException e){
                        log("value \"" + value + "\" is invalid");
                        break;
                    }
                    try{
                        obj.write("."+path,o);
                    }catch (JsonKeyException e){
                        log("path \"" + path + "\" of \"" + args[0] + "\" not found");
                        break;
                    }
                    edit(args[0],obj);
                    log("path \""+path+"\" of \"" + args[0] + "\" is changed to :\n"+o);
                }
            }
        }
    }
    private static void log(String message){
        System.out.println("[DEBUG] "+message);
    }
    @Contract(pure = true)
    private static @Nullable JsonObject query(@NotNull String key){
        if (key.equals("userdata")){
            if (Main.userData != null){
                return Main.userData.getData();
            }
        }
        if (Main.exists(key)){
            return Main.read(key);
        }
        return null;
    }
    private static void edit(@NotNull String key, JsonObject obj){
        if (Main.exists(key)){
            Main.write(key,obj);
        }
    }
}
