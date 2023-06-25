package net.orange.game;

import net.orange.game.data.DataTool;
import net.orange.game.data.json.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class CopyTool {
    public static void main(String[] args) throws IOException {
        Path path = Paths.get("resources/data_test/").toAbsolutePath();
        System.out.println(path);
        try(Stream<Path> paths = Files.walk(path)){
            paths.filter(Files::isRegularFile).filter((p)->p.toString().endsWith(".json")).forEach(CopyTool::run);
        }
    }
    public static void run(@NotNull Path path){
        File target = new File(path.toString().replace("resources\\data_test\\","resources\\data\\").replace(".json",".data"));
        if (!target.getParentFile().isDirectory()){
            target.getParentFile().mkdirs();
        }
        System.out.println(path+" -> "+target);
        JsonObject object = DataTool.read_textfile(path.toString());
        DataTool.write_zipfile(target.toString(),object);
    }
}
