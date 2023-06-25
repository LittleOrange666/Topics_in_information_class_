package net.orange.game.tools;

import net.orange.game.Main;

public class TimeChecker {
    private static final double step = 1.0 / Main.fps;
    private double delay;
    private double lasttime;
    private double curtime;
    public TimeChecker(double delay){
        this.delay = delay;
        this.lasttime = 0;
        this.curtime = 0;
    }
    public void changeDelay(long delay){
        this.delay = delay;
    }
    public boolean check(){
        curtime += step;
        if (curtime>=lasttime+delay) {
            lasttime = lasttime+delay;
            return true;
        }
        return false;
    }
    public void activate(){
        this.lasttime = curtime-delay;
    }
    public double getLastTime(){
        return lasttime;
    }
    public double getCurTime(){
        return curtime;
    }
    public void reset(){
        this.lasttime = curtime;
    }
}
