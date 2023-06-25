package net.orange.game;

import net.orange.game.tools.DrawTool;
import net.orange.game.tools.Scaler;
import net.orange.game.page.Page;
import net.orange.game.page.TitlePage;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class MainWindow extends JFrame {
    private static final int time_check_interval = 3;
    public Timer timer;
    public Page page;
    private long lasttime;
    private int delay;
    private int cnt;
    private final BufferedImage image;
    public final BufferedImage backgroundImage;
    private final Graphics2D g2;

    public int getSpeedrate() {
        return speedrate;
    }

    public void setSpeedrate(int speedrate) {
        if (this.speedrate != speedrate) {
            timer.setDelay(delay=delay*this.speedrate/speedrate);
            this.speedrate = speedrate;
            lasttime = System.currentTimeMillis();
            cnt = 0;
            //System.out.println("delay="+delay+" speedrate="+speedrate);
        }
    }

    private int speedrate = 1;
    private int fcnt = 0;
    public void refresh(){
        setSize(Toolkit.getDefaultToolkit().getScreenSize());
        repaint();
    }
    public MainWindow(){
        Main.mainWindow = this;
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setUndecorated(true);
        setSize(Toolkit.getDefaultToolkit().getScreenSize());
        this.setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Scaler.init(this.getWidth(), this.getHeight());
        delay = 30;
        cnt = 0;
        timer = new Timer(delay, this::onTick);
        timer.start();
        lasttime = System.currentTimeMillis();
        Runtime.getRuntime().addShutdownHook(new Thread(this::onClose));
        image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        g2 = image.createGraphics();
        Main.renderContext = g2.getFontRenderContext();
        backgroundImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = backgroundImage.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(),getHeight());
        g.dispose();
        this.changePage(new TitlePage());
    }
    public void onTick(ActionEvent event) {
        //long t = System.currentTimeMillis();
        if (page!=null) {
            page.onTick();
        }
        if(isActive()) {
            fcnt++;
            if(fcnt>=speedrate) {
                repaint();
                fcnt = 0;
            }
        }
        cnt++;
        if (cnt>=time_check_interval){
            long timeelapsed = (System.currentTimeMillis()-lasttime)*Main.fps;
            //System.out.println("time elapsed "+ timeelapsed);
            long ms = 1000/speedrate;
            //System.out.println("wanted "+ time_check_interval*ms);
            if (timeelapsed>=(ms+Main.fps)*time_check_interval && delay>1){
                timer.setDelay(--delay);
            }
            if (timeelapsed<=(ms-Main.fps)*time_check_interval){
                timer.setDelay(++delay);
            }
            lasttime = System.currentTimeMillis();
            cnt = 0;
        }
        //System.out.println("all "+(System.currentTimeMillis()-t));
        //System.out.println("delay "+delay);
    }
    @Override
    public void paint(@NotNull Graphics g){
        if (g2 != null) {
        //long t = System.nanoTime();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
            g2.drawImage(backgroundImage, 0, 0, null);
            if (alpha!=1){
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            }
            if (page != null) {
                page.paint(g2);
            }
            g.drawImage(image, 0, 0, null);
        }
    }
    public void changePage(Page page){
        if (this.page != null) {
            removeMouseListener(this.page);
            removeKeyListener(this.page);
            removeMouseMotionListener(this.page);
            removeMouseMotionListener(this.page);
        }
        this.page = page;
        addMouseListener(this.page);
        addKeyListener(this.page);
        addMouseMotionListener(this.page);
        addMouseWheelListener(this.page);
        setSpeedrate(1);
        setAlpha(1);
    }
    public void onClose(){
        Main.log("onclose");
        Main.userDataList.save();
    }
    public BufferedImage getBuffer(){
        return DrawTool.cloneImage(image);
    }
    private float alpha = 1;
    public void setAlpha(float alpha){
        this.alpha = alpha;
    }
}
