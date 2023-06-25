package net.orange.game.page;

import net.orange.game.Main;
import net.orange.game.display.Part;
import net.orange.game.display.Pos;
import net.orange.game.display.TheButton;
import net.orange.game.tools.Scaler;
import org.jetbrains.annotations.NotNull;

import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

public class Page implements KeyListener, MouseInputListener, MouseWheelListener {
    private final ArrayList<Part> parts = new ArrayList<>();

    private BufferedImage buffer = Main.mainWindow.backgroundImage;
    private Page parent = null;
    public void hover(@NotNull Page page) {
        page.parent = this;
        buffer = Main.mainWindow.getBuffer();
        Main.mainWindow.changePage(page);
    }
    public void back(){
        if (parent != null){
            Main.mainWindow.changePage(parent);
        }else{
            throw new UnsupportedOperationException("this is not a child page");
        }
    }
    public void paint(Graphics2D g){
        if (parent!=null) g.drawImage(parent.buffer,0,0,null);
        for(Part part : parts) part.paint(g);
    }
    public void onTick(){
    }
    public void addPart(Part... part){
        parts.addAll(Arrays.asList(part));
    }
    public void removePart(Part part){
        parts.remove(part);
    }

    @Override
    public void mouseClicked(@NotNull MouseEvent e) {
        if(e.getButton() != MouseEvent.BUTTON1) return;
        Pos pos = Scaler.unscale(new Pos(e.getPoint()));
        for(Part part : parts){
            if (part.include(pos)) part.mouseClicked(e);
        }
    }

    @Override
    public void mousePressed(@NotNull MouseEvent e) {
        //System.out.println("mousePressed");
        Pos pos = Scaler.unscale(new Pos(e.getPoint()));
        for(Part part : parts){
            if (part.include(pos)) part.mousePressed(e);
        }
    }

    @Override
    public void mouseReleased(@NotNull MouseEvent e) {
        //System.out.println("mouseReleased");
        Pos pos = Scaler.unscale(new Pos(e.getPoint()));
        for(Part part : parts){
            if (part.include(pos)) part.mouseReleased(e);
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        //System.out.println("mouseEntered");
    }

    @Override
    public void mouseExited(MouseEvent e) {
        //System.out.println("mouseExited");
    }

    @Override
    public void keyTyped(KeyEvent e) {
        //System.out.println("keyTyped");
        for(Part part : parts){
            part.keyTyped(e);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        //System.out.println("keyPressed");
        for(Part part : parts){
            part.keyPressed(e);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        //System.out.println("keyReleased");
        for(Part part : parts){
            part.keyReleased(e);
        }
    }

    @Override
    public void mouseDragged(@NotNull MouseEvent e) {
        //System.out.println("mouseDragged");
        Pos pos = Scaler.unscale(new Pos(e.getPoint()));
        for(Part part : parts){
            if (part.include(pos)) part.mouseDragged(e);
        }
    }

    @Override
    public void mouseMoved(@NotNull MouseEvent e) {
        //System.out.println("mouseMoved");
        Pos pos = Scaler.unscale(new Pos(e.getPoint()));
        for(Part part : parts){
            if (part.include(pos)) part.mouseMoved(e);
        }
    }

    @Override
    public void mouseWheelMoved(@NotNull MouseWheelEvent e) {
        Pos pos = Scaler.unscale(new Pos(e.getPoint()));
        for(Part part : parts){
            if (part.include(pos)) part.mouseWheelMoved(e);
        }

    }
}
