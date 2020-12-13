package cn.zf233.xcloud.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;

import cn.zf233.xcloud.R;
import cn.zf233.xcloud.common.Const;
import cn.zf233.xcloud.util.PermisionUtils;
import cn.zf233.xcloud.util.JumpActivityUtil;
import cn.zf233.xcloud.util.ToastUtil;

public class MainActivity extends AppCompatActivity {

    @SuppressLint("StaticFieldLeak")
    public static MainActivity mainActivity;

    private View loginLayout;
    private View registLayout;
    private Animation clickAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginLayout = findViewById(R.id.loginLayout);
        registLayout = findViewById(R.id.regist);
        clickAnimation = AnimationUtils.loadAnimation(this, R.anim.click);
        mainActivity = this;

        // set Thread Policy
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        PermisionUtils.verifyStoragePermissions(this);

        loginLayout.setOnClickListener(v -> {
            loginLayout.startAnimation(clickAnimation);
            Intent intent = new Intent(MainActivity.this, ActivityLogin.class);
            JumpActivityUtil.jumpActivity(this, intent, 100L, false);
        });
        registLayout.setOnClickListener(v -> {
            registLayout.startAnimation(clickAnimation);
            Intent intent = new Intent(MainActivity.this, ActivityRegist.class);
            JumpActivityUtil.jumpActivity(this, intent, 100L, false);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        String msg = this.getIntent().getStringExtra(Const.MSG.getDesc());
        if (null != msg && !"".equals(msg)) {
            ToastUtil.showLongToast(msg);
        }
    }
}