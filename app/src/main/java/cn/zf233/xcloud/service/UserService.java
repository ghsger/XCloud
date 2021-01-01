package cn.zf233.xcloud.service;

import java.util.List;

import cn.zf233.xcloud.common.ServerResponse;
import cn.zf233.xcloud.entity.File;
import cn.zf233.xcloud.entity.User;
import cn.zf233.xcloud.util.RequestUtil;

/**
 * Created by zf233 on 11/28/20
 */
public interface UserService {
    ServerResponse<User> login(RequestUtil requestUtil, User user);

    ServerResponse<List<File>> home(RequestUtil requestUtil, User user,Integer folderid, String searchString, Integer sortFlag);

    ServerResponse<User> regist(RequestUtil requestUtil, User user, String code);

    ServerResponse<User> update(RequestUtil requestUtil, User user);
}
