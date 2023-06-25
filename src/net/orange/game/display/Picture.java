package net.orange.game.display;

import net.orange.game.Main;
import net.orange.game.tools.Scaler;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Picture{
    protected BufferedImage image;
    protected BufferedImage display_image;

    public boolean isLoad_success() {
        return load_success;
    }

    private boolean load_success = true;

    public Pos getSize() {
        return size;
    }

    protected Pos size;

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        if (this.alpha != alpha){
            this.alpha = alpha;
            refreshAlpha();
        }
    }
    protected void refreshAlpha() {
        if (alpha == 1) display_image = image;
        else {
            display_image = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = display_image.createGraphics();
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g.drawImage(image,0,0,null);
            g.dispose();
        }
    }

    private float alpha = 1;
    public Picture(String path){
        this(path,0,0);
    }
    public Picture(String path, int width, int height){
        path = Main.image_path + path;
        //System.out.println("read " + path);
        try{
            BufferedImage image = ImageIO.read(new File(path));
            this.size = new Pos(width>0?width:image.getWidth(),height>0?height:image.getHeight());
            Pos size = Scaler.scaleSize(this.size);
            if(size.equals(new Pos(image.getWidth(), image.getHeight()))){
                this.image = image;
            }else {
                Image scaled_image = image.getScaledInstance((int) size.x(), (int) size.y(), BufferedImage.SCALE_AREA_AVERAGING);
                this.image = new BufferedImage((int) size.x(), (int) size.y(), BufferedImage.TYPE_INT_ARGB);
                this.image.getGraphics().drawImage(scaled_image, 0, 0, null);
            }
            this.display_image = this.image;
        } catch (IOException e) {
            System.err.println("error reading image from \""+path+"\"");
            e.printStackTrace();
            load_success = false;
        }
    }
    private Picture(@NotNull BufferedImage image){
        this.size = new Pos(image.getWidth(),image.getHeight());
        Pos size = Scaler.scaleSize(this.size);
        Image scaled_image = image.getScaledInstance((int) size.x(), (int) size.y(), BufferedImage.SCALE_AREA_AVERAGING);
        this.image = new BufferedImage((int) size.x(), (int) size.y(), BufferedImage.TYPE_INT_ARGB);
        this.image.getGraphics().drawImage(scaled_image, 0, 0, null);
        this.display_image = this.image;
    }
    private Picture(@NotNull BufferedImage image, int __) {
        this.image = image;
        this.size = Scaler.unscaleSize(new Pos(image.getWidth(),image.getHeight()));
        this.display_image = this.image;
    }
    public static @NotNull Picture fromText(@NotNull String text, @NotNull Font font) {
        return fromText(text, font,Color.BLACK,null);
    }
    @Contract("_, _, _ -> new")
    public static @NotNull Picture rect(int width, int height, Color color){
        BufferedImage image = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setColor(color);
        g.fillRect(0,0,width,height);
        g.dispose();
        return new Picture(image);
    }
    public static @NotNull Picture rect(int width, int height, Color inner, Color border, int border_width){
        BufferedImage image = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setColor(inner);
        g.fillRect(0,0,width,height);
        g.setStroke(new BasicStroke(border_width));
        g.setColor(border);
        g.drawRect(0,0,width,height);
        g.dispose();
        return new Picture(image);
    }
    public static @NotNull Picture rect(int width, int height, Color border, int border_width){
        BufferedImage image = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setStroke(new BasicStroke(border_width));
        g.setColor(border);
        g.drawRect(0,0,width,height);
        g.dispose();
        return new Picture(image);
    }
    public static @NotNull Picture fromText(@NotNull String text, @NotNull Font font, @NotNull Color color) {
        return fromText(text, font,color,null);
    }
    public static @NotNull Picture fromText(@NotNull String text, @NotNull Font font, @NotNull Color color, @Nullable Color background){
        FontMetrics fm = Main.graphics.getFontMetrics(font);
        int width = fm.charsWidth(text.toCharArray(),0,text.length());fm.getHeight();
        int height = fm.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        if (background != null){
            g.setColor(background);
            g.fillRect(0, 0, width, height);
        }
        g.setColor(color);
        g.setFont(font);
        int ascent = fm.getAscent();
        int descent = fm.getDescent();
        int y = (height - (ascent + descent)) / 2 + ascent;
        g.drawString(text, 0, y);
        g.dispose();
        return new Picture(image);
    }
    public void paint(Pos pos,Graphics2D g) {
        if (load_success){
            pos = Scaler.scale(pos);
            g.drawImage(display_image,(int)pos.x(),(int)pos.y(), null);
        }
    }
    public void paintCenter(@NotNull Pos center, Graphics2D g) {
        paint(center.sub(getSize().mul(0.5)),g);
    }

    public Picture clone() {
        return new Picture(image,0);
    }
    public Paintable paint(Pos pos){
        return (g)->paint(pos,g);
    }
    public void draw(@NotNull Iterable<Paintable> paintables){
        Graphics2D g = image.createGraphics();
        for(Paintable paintable : paintables){
            paintable.paint(g);
        }
        g.dispose();
        refreshAlpha();
    }
    public void draw(Paintable @NotNull ... paintables){
        Graphics2D g = image.createGraphics();
        for(Paintable paintable : paintables){
            paintable.paint(g);
        }
        g.dispose();
        refreshAlpha();
    }
    public Part toPart(Pos pos){
        Part r = new Part(pos);
        r.setPicture(this);
        return r;
    }
    public Part toPartCenter(@NotNull Pos center){
        return toPart(center.sub(getSize().mul(0.5)));
    }
}
