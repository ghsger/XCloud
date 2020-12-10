package cn.zf233.xcloud.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import cn.zf233.xcloud.R;
import cn.zf233.xcloud.util.JumpActivityUtil;

public class ActivityXCloudDetial extends AppCompatActivity {

    private Animation clickAnimation;
    private View xcloudDetailEnterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initXCloudDetailImageView();
        xcloudDetailEnterButton.setOnClickListener(v -> {
            xcloudDetailEnterButton.startAnimation(clickAnimation);
            Intent intent = new Intent(ActivityXCloudDetial.this, ActivityHome.class);
            ActivityHome.activityHome.finish();
            JumpActivityUtil.jumpActivity(this, intent, 100L, true);
        });
    }

    // init
    private void initXCloudDetailImageView() {
        setContentView(R.layout.activity_xcloud_detial);
        xcloudDetailEnterButton = findViewById(R.id.xcloudDetialEnterButton);
        clickAnimation = AnimationUtils.loadAnimation(this, R.anim.click);
        ImageView imageView01 = findViewById(R.id.detialImage01);
        ImageView imageView02 = findViewById(R.id.detialImage02);
        ImageView imageView03 = findViewById(R.id.detialImage03);
        ImageView imageView04 = findViewById(R.id.detialImage04);
        imageView01.setImageResource(R.drawable.github);
        imageView02.setImageResource(R.drawable.qq);
        imageView03.setImageResource(R.drawable.email);
        imageView04.setImageResource(R.drawable.author);
    }
}