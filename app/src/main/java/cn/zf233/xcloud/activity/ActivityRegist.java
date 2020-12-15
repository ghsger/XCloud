package cn.zf233.xcloud.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import cn.zf233.xcloud.R;
import cn.zf233.xcloud.common.Const;
import cn.zf233.xcloud.common.ResponseCodeENUM;
import cn.zf233.xcloud.common.ServerResponse;
import cn.zf233.xcloud.entity.User;
import cn.zf233.xcloud.service.UserService;
import cn.zf233.xcloud.service.impl.UserServiceImpl;
import cn.zf233.xcloud.util.FileUtil;
import cn.zf233.xcloud.util.RequestUtil;
import cn.zf233.xcloud.util.JumpActivityUtil;
import cn.zf233.xcloud.util.ToastUtil;

public class ActivityRegist extends AppCompatActivity {

    private final UserService userService = new UserServiceImpl();

    private EditText usernameRegistText;
    private EditText passwordRegistText;
    private EditText codeRegistText;
    private View registUserLayout;
    private Animation clickAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);

        usernameRegistText = findViewById(R.id.usernameRegistText);
        passwordRegistText = findViewById(R.id.passwordRegistText);
        codeRegistText = findViewById(R.id.codeRegistText);
        registUserLayout = findViewById(R.id.registUserLayout);
        clickAnimation = AnimationUtils.loadAnimation(this, R.anim.click);

        // regist
        registUserLayout.setOnClickListener(v -> {
            registUserLayout.startAnimation(clickAnimation);
            String username = usernameRegistText.getText().toString().trim();
            String password = passwordRegistText.getText().toString().trim();
            String code = codeRegistText.getText().toString().trim();
            if ("".equals(code)) {
                Toast.makeText(ActivityRegist.this, "邀请码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            if ("".equals(username) || "".equals(password)) {
                Toast.makeText(ActivityRegist.this, "用户名密码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            if (username.length() < 4 || password.length() < 5) {
                Toast.makeText(ActivityRegist.this, "用户名密码格式有误", Toast.LENGTH_SHORT).show();
                return;
            }
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            ServerResponse<User> response = userService.regist(RequestUtil.getRequestUtil(), user, code);
            if (response.getStatus() == ResponseCodeENUM.SUCCESS.getCode()) {
                FileUtil.outputShared(ActivityRegist.this, Const.CURRENT_USER.getDesc(), response.getData());
                MainActivity.mainActivity.finish();
                Intent intent = new Intent(ActivityRegist.this, ActivityHome.class);
                intent.putExtra(Const.MSG.getDesc(), response.getMsg());
                JumpActivityUtil.jumpActivity(this, intent, 100L, true);
                return;
            }
            ToastUtil.showShortToast(response.getMsg());
        });
    }
}