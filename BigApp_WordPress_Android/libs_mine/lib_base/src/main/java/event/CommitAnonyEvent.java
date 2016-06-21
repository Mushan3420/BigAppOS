package event;

/**
 * Created by wjwu on 2015/9/7.
 */
public class CommitAnonyEvent {
    public boolean isLogin;
    public String author;
    public String email;
    public String content;

    public CommitAnonyEvent(boolean isLogin, String author, String email, String content) {
        this.isLogin = isLogin;
        this.author = author;
        this.email = email;
        this.content = content;
    }
}
