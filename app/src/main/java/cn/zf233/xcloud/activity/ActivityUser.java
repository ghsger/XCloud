package cn.zf233.xcloud.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

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

public class ActivityUser extends AppCompatActivity {

    private static final UserService userService = new UserServiceImpl();
    private final List<Integer> userGradeImgID = new ArrayList<>();

    private static User user;

    private View logoutLayout;
    private TextView currentUserUsernameText;
    private ProgressBar levelGroupBar;
    private ProgressBar numberCountBar;
    private TextView levelGroupText;
    private TextView numberCountText;
    private ImageView userGradeImg;
    private Animation clickAnimation;

    {
        userGradeImgID.add(R.drawable.no01);
        userGradeImgID.add(R.drawable.no02);
        userGradeImgID.add(R.drawable.no03);
        userGradeImgID.add(R.drawable.no04);
        userGradeImgID.add(R.drawable.no05);
        userGradeImgID.add(R.drawable.no06);
        userGradeImgID.add(R.drawable.non);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        currentUserUsernameText = findViewById(R.id.currentUserUsernameText);
        levelGroupBar = findViewById(R.id.levelGroupBar);
        numberCountBar = findViewById(R.id.numberCountBar);
        levelGroupText = findViewById(R.id.levelGroupText);
        numberCountText = findViewById(R.id.numberCountText);
        userGradeImg = findViewById(R.id.userGradeImg);
        logoutLayout = findViewById(R.id.logoutLayout);
        clickAnimation = AnimationUtils.loadAnimation(this, R.anim.click);

        new Thread(new UserRunnable()).start();

        logoutLayout.setOnClickListener(v -> {
            logoutLayout.startAnimation(clickAnimation);
            FileUtil.removeShared(ActivityUser.this, Const.CURRENT_USER.getDesc());
            ActivityHome.activityHome.finish();
            Intent intent = new Intent(ActivityUser.this, MainActivity.class);
            intent.putExtra(Const.MSG.getDesc(), "注销成功");
            JumpActivityUtil.jumpActivity(this, intent, 100L, true);
        });
    }

    // flush user detail UI
    private final Handler userHandler = new Handler(new Handler.Callback() {
        @SuppressLint("SetTextI18n")
        @Override
        public boolean handleMessage(Message msg) {
            if (user.getNickname() != null && !"".equals(user.getNickname())) {
                currentUserUsernameText.setText(user.getNickname());
            } else {
                currentUserUsernameText.setText(user.getUsername());
            }
            if (user.getGrade() > 6) {
                userGradeImg.setImageResource(userGradeImgID.get(6));
            } else {
                userGradeImg.setImageResource(userGradeImgID.get(user.getGrade() - 1));
            }
            int downloadCount = user.getDownloadCount() % 100;
            int availableSpace = user.getGrade() * 10;
            int usedSpace = ActivityHome.fileList == null ? 0 : ActivityHome.fileList.size();
            numberCountBar.setMax(availableSpace);
            numberCountBar.setProgress(usedSpace);
            numberCountText.setText(usedSpace + "/" + availableSpace);
            levelGroupBar.setMax(100);
            levelGroupBar.setProgress(downloadCount);
            levelGroupText.setText(downloadCount + "/100");
            return false;
        }
    });

    // callback
    private void jumpActivity(Intent intent) {
        ActivityHome.activityHome.finish();
        JumpActivityUtil.jumpActivity(this, intent, 100L, true);
    }

    // check and get user Detail
    private class UserRunnable implements Runnable {

        @Override
        public void run() {
            Looper.prepare();
            User user = FileUtil.inputShared(ActivityUser.this, Const.CURRENT_USER.getDesc(), User.class);
            ServerResponse<User> response = userService.login(RequestUtil.getRequestUtil(), user);
            if (response.getStatus() == ResponseCodeENUM.ERROR.getCode()) {
                FileUtil.removeShared(ActivityUser.this, Const.CURRENT_USER.getDesc());
                Intent intent = new Intent(ActivityUser.this, MainActivity.class);
                intent.putExtra(Const.MSG.getDesc(), response.getMsg());
                jumpActivity(intent);

            } else if (response.getStatus() == ResponseCodeENUM.SUCCESS.getCode()) {
                ActivityUser.user = response.getData();
                FileUtil.outputShared(ActivityUser.this, Const.CURRENT_USER.getDesc(), user);
                userHandler.sendMessage(new Message());
            }
            Looper.loop();
        }
    }
}