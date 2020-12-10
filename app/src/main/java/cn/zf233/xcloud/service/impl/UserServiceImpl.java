package cn.zf233.xcloud.service.impl;

import com.google.gson.reflect.TypeToken;

import java.util.List;

import cn.zf233.xcloud.common.RequestURL;
import cn.zf233.xcloud.common.ResponseCodeENUM;
import cn.zf233.xcloud.common.ServerResponse;
import cn.zf233.xcloud.entity.File;
import cn.zf233.xcloud.entity.RequestBody;
import cn.zf233.xcloud.entity.User;
import cn.zf233.xcloud.service.UserService;
import cn.zf233.xcloud.util.RequestUtil;

/**
 * Created by zf233 on 11/29/20
 */
public class UserServiceImpl implements UserService {

    @Override
    public ServerResponse<User> login(RequestUtil requestUtil, User user) {
        RequestBody body = new RequestBody();
        body.setUser(user);
        ServerResponse<User> response = requestUtil.requestUserXCloudServer(RequestURL.LOGIN_URL.getDesc(), body, new TypeToken<ServerResponse<User>>() {
        });
        if (response != null) {
            if (response.getStatus() == ResponseCodeENUM.SUCCESS.getCode()) {
                response.getData().setPassword(user.getPassword());
                return response;
            }
            response.setData(null);
        }
        return response;
    }

    @Override
    public ServerResponse<List<File>> home(RequestUtil requestUtil, User user, String searchString, String sortFlag) {
        RequestBody body = new RequestBody();
        body.setUser(user);
        body.setMatchCode(searchString);
        if (sortFlag != null && !"".equals(sortFlag)) {
            body.setSortFlag(sortFlag);
            body.setSortType("asc");
        }
        ServerResponse<List<File>> response = requestUtil.requestUserXCloudServer(RequestURL.HOME_URL.getDesc(), body, new TypeToken<ServerResponse<List<File>>>() {
        });
        if (response != null) {
            if (response.getStatus() == ResponseCodeENUM.SUCCESS.getCode()) {
                return response;
            }
        }
        return null;
    }

    @Override
    public ServerResponse<User> regist(RequestUtil requestUtil, User user, String code) {
        RequestBody body = new RequestBody();
        body.setInviteCode(code);
        body.setUser(user);
        ServerResponse<User> response = requestUtil.requestUserXCloudServer(RequestURL.REGIST_URL.getDesc(), body, new TypeToken<ServerResponse<User>>() {
        });
        if (response != null) {
            if (response.getStatus() == ResponseCodeENUM.SUCCESS.getCode()) {
                response.getData().setPassword(user.getPassword());
                return response;
            }
            response.setData(null);
        }
        return response;
    }

    @Override
    public ServerResponse<User> update(RequestUtil requestUtil, User user) {
        RequestBody body = new RequestBody();
        body.setUser(user);
        ServerResponse<User> response = requestUtil.requestUserXCloudServer(RequestURL.UPDATE_URL.getDesc(), body, new TypeToken<ServerResponse<User>>() {
        });
        if (response != null) {
            if (response.getStatus() == ResponseCodeENUM.SUCCESS.getCode()) {
                response.getData().setPassword(user.getPassword());
                return response;
            }
            response.setData(null);
        }
        return response;
    }
}
