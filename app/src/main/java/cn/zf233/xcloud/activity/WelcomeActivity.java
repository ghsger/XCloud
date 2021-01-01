package cn.zf233.xcloud.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import androidx.appcompat.app.AppCompatActivity;

import cn.zf233.xcloud.R;
import cn.zf233.xcloud.common.Const;
import cn.zf233.xcloud.common.ServerResponse;
import cn.zf233.xcloud.entity.User;
import cn.zf233.xcloud.service.UserService;
import cn.zf233.xcloud.service.impl.UserServiceImpl;
import cn.zf233.xcloud.util.FileUtil;
import cn.zf233.xcloud.util.RequestUtil;

public class WelcomeActivity extends AppCompatActivity {

    private final UserService userService = new UserServiceImpl();

    private View welcomeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        welcomeLayout = findViewById(R.id.welcomeLayout);
        new Thread(new WelcomeRunnable()).start();
    }

    // callback
    private void jumpActivity(Intent intent, String msg) {
        Looper.prepare();
        new Handler().postDelayed(() -> {
            AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
            alphaAnimation.setDuration(300);
            alphaAnimation.setFillAfter(true);
            alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    intent.putExtra(Const.MSG.getDesc(), msg);
                    startActivity(intent);
                    WelcomeActivity.this.finish();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            welcomeLayout.startAnimation(alphaAnimation);

        }, 1500);
        Looper.loop();
    }

    // check user login status
    private class WelcomeRunnable implements Runnable {

        @Override
        public void run() {
            Intent intent;
            String msg;
            User user = FileUtil.inputShared(WelcomeActivity.this, Const.CURRENT_USER.getDesc(), User.class);
            if (user == null) {
                intent = new Intent(WelcomeActivity.this, MainActivity.class);
                jumpActivity(intent, null);
                return;
            }
            ServerResponse<User> response = userService.login(RequestUtil.getRequestUtil(), user);
            if (!response.isSuccess()) {
                FileUtil.removeShared(WelcomeActivity.this, Const.CURRENT_USER.getDesc());
                intent = new Intent(WelcomeActivity.this, MainActivity.class);
                msg = response.getMsg();
            } else if (response.isSuccess()) {
                user.setId(response.getData().getId());
                FileUtil.removeShared(WelcomeActivity.this, Const.CURRENT_USER.getDesc());
                FileUtil.outputShared(WelcomeActivity.this, Const.CURRENT_USER.getDesc(), user);
                intent = new Intent(WelcomeActivity.this, ActivityHome.class);
                msg = response.getMsg();
            } else {
                intent = new Intent(WelcomeActivity.this, MainActivity.class);
                msg = "服务器未响应";
            }
            jumpActivity(intent, msg);
        }
    }
}