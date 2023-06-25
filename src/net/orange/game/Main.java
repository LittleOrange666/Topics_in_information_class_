package net.orange.game;

import net.orange.game.data.DataTool;
import net.orange.game.data.NetworkDataManager;
import net.orange.game.data.json.JsonObject;
import net.orange.game.data.object.UserData;
import net.orange.game.data.object.UserDataList;
import net.orange.game.display.Pos;
import net.orange.game.tools.DebugTool;
import net.orange.game.tools.Scaler;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Random;

public class Main {
    public static final boolean DEBUG = true;
    public static final String image_path = "resources/images/";
    public static final String data_path = "resources/data"+(DEBUG?"_test":"")+"/";
    public static final String font_path = "resources/fonts/";
    public static final String data_type = DEBUG?".json":".data";
    public static final int default_width = 1920;
    public static final int default_height = 1080;
    public static final int block_size = 120;
    public static final int deployment_size = 80;
    public static final Pos default_size = new Pos(default_width, default_height);
    public static final Pos character_size = new Pos(65,65);
    public static final int fps = 30;
    public static final double hindrance = 1;
    public static final int slowdownrate = 5;
    public static MainWindow mainWindow;
    public static final Random rand = new Random();
    public static final Font text_font;
    public static final Font emoji_font;
    public static final Font number_font;
    public static FontRenderContext renderContext = null;
    public static final Graphics2D graphics;
    public static UserData userData = new UserData();
    public static NetworkDataManager dataManager = null;
    public static final UserDataList userDataList = new UserDataList();
    private static boolean startdebug = false;
    public static <T extends Number> int sign(@NotNull T value){
        return value.doubleValue() > 0 ? 1 : value.doubleValue() < 0 ? -1 : 0;
    }

    static {
        try {
            text_font = Font.createFont(Font.TRUETYPE_FONT, new File(font_path+"微軟正黑體-1.ttf"));
            emoji_font = Font.createFont(Font.TRUETYPE_FONT, new File(font_path+"segoe-ui-emoji.ttf"));
            number_font = Font.createFont(Font.TRUETYPE_FONT, new File(font_path+"Consolas.ttf"));
        } catch (FontFormatException | IOException e) {
            throw new RuntimeException(e);
        }
        graphics = new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB).createGraphics();
    }
    public static @NotNull Font textFont(float size){
        return text_font.deriveFont((float) Math.floor(size*Scaler.scale));
    }
    public static @NotNull Font textFont(float size, int style){
        return text_font.deriveFont(style, (float) Math.floor(size*Scaler.scale));
    }
    public static @NotNull Font emojiFont(float size){
        return emoji_font.deriveFont((float) Math.floor(size*Scaler.scale));
    }
    public static @NotNull Font emojiFont(float size, int style){
        return emoji_font.deriveFont(style, (float) Math.floor(size*Scaler.scale));
    }
    public static @NotNull Font numberFont(float size){
        return number_font.deriveFont((float) Math.floor(size*Scaler.scale));
    }
    public static @NotNull Font numberFont(float size, int style){
        return number_font.deriveFont(style, (float) Math.floor(size*Scaler.scale));
    }
    public static void trylogin(){
        dataManager = NetworkDataManager.create();
    }
    public static void logout(){
        if (dataManager != null){
            NetworkDataManager.logout();
            dataManager = null;
        }
    }
    public static boolean hasLoggedIn(){
        return dataManager != null;
    }

    public static void main(String[] args) {
        if (NetworkDataManager.check()){
            trylogin();
        }
        if (hasLoggedIn()){
            userDataList.onlogin();
        }
        /*
        if (exists("user/default")){
            userData.read("user/default");
        }
         */
        mainWindow = new MainWindow();
        if (DEBUG){
            startdebug = true;
            DebugTool.mainloop();
        }
    }
    public static boolean exists(String name){
        String path = Main.data_path + name + data_type;
        return new File(path).isFile();
    }

    public static @NotNull JsonObject read(String name){
        String path = Main.data_path + name + data_type;
        log("read " + path);
        JsonObject r;
        if (DEBUG) {
            r = DataTool.read_textfile(path);
        }else{
            r = DataTool.read_zipfile(path);
        }
        r.setPath("\""+name+"\"");
        return r;
    }

    public static void write(String name, JsonObject object){
        String path = Main.data_path + name + data_type;
        log("write " + path);
        if (DEBUG) {
            DataTool.write_textfile(path,object);
        }else{
            DataTool.write_zipfile(path,object);
        }
    }
    public static void log(String text){
        text = "[INFO "+new Timestamp(System.currentTimeMillis())+"] "+text;
        if (startdebug) {
            System.out.print("\r"+text+"\nDEBUG >> ");
        }else{
            System.out.println(text);
        }
    }
}