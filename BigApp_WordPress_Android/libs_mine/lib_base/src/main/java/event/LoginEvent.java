package event;

import model.User;

/**
 * Created by wjwu on 2015/9/5.
 */
public class LoginEvent {
    public User user;
    /***
     * 1修改昵称，2修改头像
     */
    public int modifyType = 0;

    public LoginEvent(User user) {
        this.user = user;
    }

    /***
     * @param user       user
     * @param modifyType 1修改昵称，2修改头像
     */
    public LoginEvent(User user, int modifyType) {
        this.user = user;
        this.modifyType = modifyType;
    }
}
