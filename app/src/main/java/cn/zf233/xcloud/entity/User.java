package cn.zf233.xcloud.entity;

/**
 * Created by zf233 on 11/28/20
 */
public class User {

    private Integer id;
    private String username;
    private String nickname;
    private String password;
    private Integer role;
    private Integer useCapacity;
    private Integer grade;
    private Integer growthValue;

    public User() {
    }

    public User(Integer id, String username, String nickname, String password, Integer role, Integer useCapacity, Integer grade, Integer growthValue) {
        this.id = id;
        this.username = username;
        this.nickname = nickname;
        this.password = password;
        this.role = role;
        this.useCapacity = useCapacity;
        this.grade = grade;
        this.growthValue = growthValue;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public Integer getUseCapacity() {
        return useCapacity;
    }

    public void setUseCapacity(Integer useCapacity) {
        this.useCapacity = useCapacity;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public Integer getGrowthValue() {
        return growthValue;
    }

    public void setGrowthValue(Integer growthValue) {
        this.growthValue = growthValue;
    }
}
