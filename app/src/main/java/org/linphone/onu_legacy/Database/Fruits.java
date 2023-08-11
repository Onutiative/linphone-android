package org.linphone.onu_legacy.Database;

/**
 * Created by mamba on 3/14/2016.
 */
public class Fruits {

    private String name;
    private String age;
    private String game;
    private String stat;


    public Fruits(String name, String age, String game,String stat){
        this.setName(name);
        this.setAge(age);
        this.setGame(game);
        this.setStat(stat);

    }

    public String getName() {
        return name;
    }
    public String getStat() {
        return stat;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setStat(String Stat) {
        this.stat = Stat;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }




}
