package net.orange.game.game;

public enum GameResult {
    faild("作戰失敗"),
    success("作戰成功"),
    fullsuccess("作戰大成功");
    public final String text;

    GameResult(String text) {
        this.text = text;
    }
}
