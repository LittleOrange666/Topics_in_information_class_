package net.orange.game.tools;

import net.orange.game.display.BlockPos;
import net.orange.game.display.Paintable;
import net.orange.game.display.Picture;
import net.orange.game.display.Pos;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;

public class DrawTool {
    public static void drawString(@NotNull Graphics2D g,Pos pos, String text, Color color, Font font){
        drawString(pos, text, color, font).paint(g);
    }
    public static @NotNull Paintable drawString(Pos pos, String text, Color color, Font font){
        BlockPos textpos = Scaler.scale(pos).toBlockPos();
        return (g)->{
            g.setFont(font);
            g.setColor(color);
            g.drawString(text, textpos.x(), textpos.y());
        };
    }
    public static @NotNull Paintable drawStringNotScreen(Pos pos, String text, Color color, Font font){
        BlockPos textpos = Scaler.scaleSize(pos).toBlockPos();
        return (g)->{
            g.setFont(font);
            g.setColor(color);
            g.drawString(text, textpos.x(), textpos.y());
            System.out.println(text+" "+textpos);
        };
    }
    public static @NotNull Paintable rectNotScreen(Pos pos, Pos size, Color color){
        BlockPos p = Scaler.scaleSize(pos).toBlockPos();
        BlockPos s = Scaler.scaleSize(size).toBlockPos();
        return (g)->{
            g.setColor(color);
            g.fillRect(p.x(),p.y(),s.x(),s.y());
        };
    }
    public static @NotNull Paintable rectNotScreen(Pos pos, Pos size, Color color, Color border, int border_width){
        BlockPos p = Scaler.scaleSize(pos).toBlockPos();
        BlockPos s = Scaler.scaleSize(size).toBlockPos();
        Stroke stroke = new BasicStroke(border_width);
        return (g)->{
            g.setColor(color);
            g.fillRect(p.x(),p.y(),s.x(),s.y());
            g.setStroke(stroke);
            g.setColor(border);
            g.drawRect(p.x(),p.y(),s.x(),s.y());
        };
    }
    public static @NotNull Paintable rectNotScreen(Pos pos, Pos size, Color border, int border_width){
        BlockPos p = Scaler.scaleSize(pos).toBlockPos();
        BlockPos s = Scaler.scaleSize(size).toBlockPos();
        Stroke stroke = new BasicStroke(border_width);
        return (g)->{
            g.setStroke(stroke);
            g.setColor(border);
            g.drawRect(p.x(),p.y(),s.x(),s.y());
        };
    }
    public static @NotNull Paintable rect(Pos pos, Pos size, Color color){
        BlockPos p = Scaler.scale(pos).toBlockPos();
        BlockPos s = Scaler.scaleSize(size).toBlockPos();
        return (g)->{
            g.setColor(color);
            g.fillRect(p.x(),p.y(),s.x(),s.y());
        };
    }
    public static void drawRect(@NotNull Graphics2D g,Pos pos, Pos size, Color color){
        rect(pos, size, color).paint(g);
    }
    public static @NotNull BufferedImage cloneImage(@NotNull BufferedImage source){
        BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        Graphics g = b.getGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return b;
    }
}
