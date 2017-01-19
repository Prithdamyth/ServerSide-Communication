

/**
 * Created by krishna on 11/24/16.
 */
public class User {
    String name;
    String password;
    int nfByOthers;//no. of times you were fooled by others
    int nfOthers;//no. of times fooled by others
    private int score;
    User(String name,String password,int nfByOthers,int nfOthers, int score)
    {
        this.name=name;
        this.password=password;
        this.nfByOthers=nfByOthers;
        this.nfOthers=nfOthers;
        this.score = score;
    }
    public String getUsername(){
        return this.name;
    }
    public String getPassword(){
        return this.password;
    }
    public int getCumulative(){
        return score;
    }
    public int getFool(){
        return nfOthers;
    }
    public int getFooled(){
        return nfByOthers;
    }
    public String toString() {
        return getUsername() + ":" + getPassword() + ":" + getCumulative() + ":" + getFool() + ":" + getFooled();
    }
}
