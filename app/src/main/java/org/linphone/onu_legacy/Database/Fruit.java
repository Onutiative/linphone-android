package org.linphone.onu_legacy.Database;

/**
 * Created by mamba on 3/14/2016.
 */
public class Fruit {

    private String name;
    private String age;
    private String game;
    private String status;  //This for call pop-up logging.


    public Fruit(String name,String age,String game){
        this.setName(name);
        this.setAge(age);
        this.setGame(game);

    }


    // This constructor for call pop-up logging.

    public Fruit(String name, String age, String game, String status) {
        this.name = name;
        this.age = age;
        this.game = game;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
