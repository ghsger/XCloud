package cn.zf233.xcloud.entity;

/**
 * Created by zf233 on 11/28/20
 */
public class RequestBody {
    private User user;
    private String sortFlag;
    private String sortType;
    private String matchCode;
    private String inviteCode;

    public RequestBody() {
    }

    public RequestBody(User user, String sortFlag, String sortType, String matchCode, String inviteCode) {
        this.user = user;
        this.sortFlag = sortFlag;
        this.sortType = sortType;
        this.matchCode = matchCode;
        this.inviteCode = inviteCode;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getSortFlag() {
        return sortFlag;
    }

    public void setSortFlag(String sortFlag) {
        this.sortFlag = sortFlag;
    }

    public String getSortType() {
        return sortType;
    }

    public void setSortType(String sortType) {
        this.sortType = sortType;
    }

    public String getMatchCode() {
        return matchCode;
    }

    public void setMatchCode(String matchCode) {
        this.matchCode = matchCode;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }
}