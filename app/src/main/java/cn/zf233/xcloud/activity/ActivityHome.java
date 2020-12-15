package cn.zf233.xcloud.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.zf233.xcloud.R;
import cn.zf233.xcloud.common.Const;
import cn.zf233.xcloud.common.RequestTypeENUM;
import cn.zf233.xcloud.common.RequestURL;
import cn.zf233.xcloud.common.ResponseCodeENUM;
import cn.zf233.xcloud.common.ServerResponse;
import cn.zf233.xcloud.entity.File;
import cn.zf233.xcloud.entity.User;
import cn.zf233.xcloud.exception.OpenFileException;
import cn.zf233.xcloud.service.UserService;
import cn.zf233.xcloud.service.impl.UserServiceImpl;
import cn.zf233.xcloud.util.DateTimeUtil;
import cn.zf233.xcloud.util.FileUtil;
import cn.zf233.xcloud.util.JumpActivityUtil;
import cn.zf233.xcloud.util.RequestUtil;
import cn.zf233.xcloud.util.ToastUtil;

public class ActivityHome extends AppCompatActivity {

    private final UserService userService = new UserServiceImpl();
    private final Map<String, Integer> typesMap = new HashMap<>();

    private static final List<Map<String, Object>> items = new ArrayList<>();
    public static List<File> fileList;
    @SuppressLint("StaticFieldLeak")
    public static ActivityHome activityHome;

    private Spinner spinner;
    private File choiceFile;
    private User currentUser;
    private ListView listFileView;
    private EditText searchStringEditText;
    private ImageView xcloudLogo;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Animation clickAnimation;
    private Animation waitAnimation;

    {
        // File extensions correspond to ICONS
        typesMap.put("doc", R.drawable.doc);
        typesMap.put("csv", R.drawable.csv);
        typesMap.put("eps", R.drawable.eps);
        typesMap.put("gif", R.drawable.gif);
        typesMap.put("mov", R.drawable.mov);
        typesMap.put("mp4", R.drawable.mp4);
        typesMap.put("svg", R.drawable.svg);
        typesMap.put("docx", R.drawable.docx);
        typesMap.put("html", R.drawable.html);
        typesMap.put("jar", R.drawable.jar);
        typesMap.put("js", R.drawable.js);
        typesMap.put("jpg", R.drawable.jpg);
        typesMap.put("rar", R.drawable.rar);
        typesMap.put("jpeg", R.drawable.jpg);
        typesMap.put("pdf", R.drawable.pdf);
        typesMap.put("png", R.drawable.png);
        typesMap.put("ppt", R.drawable.ppt);
        typesMap.put("pptx", R.drawable.pptx);
        typesMap.put("txt", R.drawable.txt);
        typesMap.put("xls", R.drawable.xls);
        typesMap.put("xlsx", R.drawable.xlsx);
        typesMap.put("zip", R.drawable.zip);
        typesMap.put("xml", R.drawable.xml);
        typesMap.put("conf", R.drawable.conf);
        typesMap.put("unknown", R.drawable.unknow);
        typesMap.put("avi", R.drawable.avi);
        typesMap.put("json", R.drawable.json);
        typesMap.put("sql", R.drawable.sql);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // get disk access
        boolean equals = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (!equals) {
            ToastUtil.showLongToast("未赋予相应权限，部分功能将无法使用");
        }

        init();
        // delay refreshing the file list
        new Handler().postDelayed(() -> new Thread(new InitFileListRunnable(null, null)).start(), 500);
    }

    // start this activity
    @Override
    protected void onStart() {
        super.onStart();
        String msg = this.getIntent().getStringExtra(Const.MSG.getDesc());
        if (null != msg && !"".equals(msg)) {
            ToastUtil.showShortToast(msg);
            getIntent().removeExtra(Const.MSG.getDesc());
        }
    }

    // restart this activity
    @Override
    protected void onRestart() {
        super.onRestart();
        if (RequestUtil.getRequestUtil().getIsUsed()) {
            xcloudLogo.startAnimation(waitAnimation);
        }
    }

    // init
    private void init() {
        // animation
        clickAnimation = AnimationUtils.loadAnimation(this, R.anim.click);
        waitAnimation = AnimationUtils.loadAnimation(this, R.anim.wait);

        // controls
        currentUser = FileUtil.inputShared(this, Const.CURRENT_USER.getDesc(), User.class);
        activityHome = ActivityHome.this;
        spinner = findViewById(R.id.spinnerUp);
        listFileView = findViewById(R.id.listFileView);
        searchStringEditText = findViewById(R.id.searchFileText);
        xcloudLogo = findViewById(R.id.xcloudLogo);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.fileListRefresh);
        ImageView xcloudTitle = findViewById(R.id.xcloudTitle);
        ImageView currentUserHeadImage = findViewById(R.id.currentUserHeadImage);
        ImageView spinnerShow = findViewById(R.id.spinnerShow);

        // the drop-down refresh
        swipeRefreshLayout.setColorSchemeResources(R.color.file_list_refresh);
        swipeRefreshLayout.setOnRefreshListener(() -> new Thread(new InitFileListRunnable(null, null)).start());

        // binding event-upload file
        findViewById(R.id.uploadFile).setOnClickListener(v -> {
            findViewById(R.id.uploadFile).startAnimation(clickAnimation);
            // open file selector
            openFileSelector();
        });

        // sort method drop-down list adapter
        List<Map<String, Object>> upListItems = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("iconImage", R.drawable.sort);
        map.put("sortTypeText", "选排序方式");
        upListItems.add(map);
        map = new HashMap<>();
        map.put("iconImage", R.drawable.time);
        map.put("sortTypeText", "按时间排序");
        upListItems.add(map);
        map = new HashMap<>();
        map.put("iconImage", R.drawable.name);
        map.put("sortTypeText", "按名称排序");
        upListItems.add(map);
        map = new HashMap<>();
        map.put("iconImage", R.drawable.size);
        map.put("sortTypeText", "按大小排序");
        upListItems.add(map);
        SimpleAdapter adapter = new SimpleAdapter(
                ActivityHome.this,
                upListItems,
                R.layout.activity_item_up_list,
                new String[]{"iconImage", "sortTypeText"},
                new int[]{R.id.iconImage, R.id.sortTypeText});
        spinner.setAdapter(adapter);

        // click listener, click ImageView to event
        spinnerShow.setOnClickListener(v -> {
            spinnerShow.startAnimation(clickAnimation);
            spinner.performClick();
        });

        // spinner selected listener
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        break;
                    case 1:
                        new Thread(new InitFileListRunnable(null, "3")).start();
                        break;
                    case 2:
                        new Thread(new InitFileListRunnable(null, "0")).start();
                        break;
                    case 3:
                        new Thread(new InitFileListRunnable(null, "2")).start();
                        break;
                    default:
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // file list items-create binding
        AlertDialog.Builder alterDialog = new AlertDialog.Builder(ActivityHome.this);
        alterDialog.setIcon(R.drawable.choice);
        alterDialog.setTitle("操作");
        alterDialog.setPositiveButton("下载并打开", (dialog, which) -> new Thread(new FileDownloadRunnable(choiceFile)).start());
        alterDialog.setNegativeButton("删除", (dialog, which) -> new Thread(new FileRemoveRunnable(choiceFile)).start());
        AlertDialog dialog = alterDialog.create();

        // file list click listener
        listFileView.setOnItemClickListener((parent, view, position, id) -> {
            choiceFile = ActivityHome.fileList.get(position);
            dialog.show();
            Window dialogWindow = dialog.getWindow();
            WindowManager.LayoutParams p = dialogWindow.getAttributes();
            p.alpha = 0.9f;
            dialogWindow.setAttributes(p);
        });

        // event binding
        currentUserHeadImage.setOnClickListener(v -> {
            currentUserHeadImage.startAnimation(clickAnimation);
            Intent intent = new Intent(ActivityHome.this, ActivityUser.class);
            JumpActivityUtil.jumpActivity(this, intent, 100L, false);
        });
        xcloudLogo.setOnClickListener(v -> {
            xcloudLogo.startAnimation(clickAnimation);
            Intent intent = new Intent(ActivityHome.this, ActivityXCloudDetial.class);
            JumpActivityUtil.jumpActivity(this, intent, 100L, false);
        });
        xcloudTitle.setOnClickListener(v -> {
            xcloudTitle.startAnimation(clickAnimation);
            Intent intent = new Intent(ActivityHome.this, ActivityXCloudDetial.class);
            JumpActivityUtil.jumpActivity(this, intent, 100L, false);
        });
        findViewById(R.id.searchFileEnter).setOnClickListener(v -> new Thread(new InitFileListRunnable(searchStringEditText.getText().toString(), null)).start());
    }

    // callback function（selecting the file）
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 666 && resultCode == Activity.RESULT_OK) {
            Message message = new Message();
            Bundle bundle = new Bundle();
            message.setData(bundle);
            Uri uri = data != null ? data.getData() : null;
            if (uri == null) {
                bundle.putString(Const.MSG.getDesc(), "上传文件选择失败");
                requestPromptHandler.sendMessage(message);
                return;
            }
            String filePathByUri = FileUtil.getFilePathByUri(ActivityHome.this, uri);
            if (null == filePathByUri || "".equals(filePathByUri)) {
                bundle.putString(Const.MSG.getDesc(), "兼容问题,非nexus请选择最近文件夹");
                requestPromptHandler.sendMessage(message);
                return;
            } else {
                bundle.putString(Const.MSG.getDesc(), "开始上传");
                requestPromptHandler.sendMessage(message);
            }
            java.io.File uploadFile = new java.io.File(filePathByUri);
            new Thread(new FileUploadRunnable(uploadFile)).start();
        }
    }

    // open file selector
    private void openFileSelector() {
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent, 666);
        }, 100L);
    }

    // refresh file list runnable
    class InitFileListRunnable implements Runnable {

        private final String searchString;
        private final String sortFlag;
        private final RequestUtil requestUtil;

        @Override
        public void run() {
            Bundle bundle = new Bundle();
            Message message = new Message();
            if (requestUtil.getIsUsed()) {
                bundle.putString(Const.MSG.getDesc(), "已有任务:" + RequestTypeENUM.getDescByCode(requestUtil.getRequestType()) + "\"" + requestUtil.getFileName() + "\"");
                message.setData(bundle);
            } else {
                requestUtil.setIsUsed(true);
                requestUtil.setRequestType(RequestTypeENUM.FLUSH_LISTVIEW.getCode());
                requestUtil.setFileName(null);
                xcloudLogo.startAnimation(waitAnimation);
                User user = FileUtil.inputShared(ActivityHome.this, Const.CURRENT_USER.getDesc(), User.class);
                if (user != null) {
                    ServerResponse<List<File>> serverResponse = userService.home(requestUtil, user, searchString, sortFlag);
                    if (serverResponse.getStatus() == ResponseCodeENUM.SUCCESS.getCode()) {
                        assembleFileList(serverResponse.getData());
                        bundle.putString(Const.MSG.getDesc(), "获取/刷新成功");
                    } else {
                        bundle.putString(Const.MSG.getDesc(), serverResponse.getMsg());
                    }
                } else {
                    bundle.putString(Const.MSG.getDesc(), "未登陆");
                }
                requestUtil.setIsUsed(false);
                requestUtil.setRequestType(RequestTypeENUM.UNKNOWN_TYPE.getCode());
                requestUtil.setFileName(null);
                message.setData(bundle);
                xcloudLogo.clearAnimation();
            }
            flushListViewHandler.sendMessage(message);
        }

        public InitFileListRunnable(String searchString, String sortFlag) {
            this.searchString = searchString;
            this.sortFlag = sortFlag;
            this.requestUtil = RequestUtil.getRequestUtil();
        }
    }

    // file remove runnable
    class FileRemoveRunnable implements Runnable {

        private final File file;
        private final RequestUtil requestUtil;

        @Override
        public void run() {
            if (file != null) {
                Bundle bundle = new Bundle();
                Message message = new Message();
                if (requestUtil.getIsUsed()) {
                    bundle.putString(Const.MSG.getDesc(), "已有任务:" + RequestTypeENUM.getDescByCode(requestUtil.getRequestType()) + "\"" + requestUtil.getFileName() + "\"");
                    message.setData(bundle);
                } else {
                    requestUtil.setIsUsed(true);
                    requestUtil.setRequestType(RequestTypeENUM.UPLOAD_TYPE.getCode());
                    requestUtil.setFileName(file.getFileName());
                    xcloudLogo.startAnimation(waitAnimation);
                    ServerResponse serverResponse = requestUtil.fileRemove(RequestURL.REMOVE_FILE_URL.getDesc(), currentUser, file.getFileId());
                    if (serverResponse.getStatus() == ResponseCodeENUM.SUCCESS.getCode()) {
                        bundle.putString(Const.MSG.getDesc(), "删除成功");
                    } else {
                        bundle.putString(Const.MSG.getDesc(), serverResponse.getMsg());
                    }
                    requestUtil.setIsUsed(false);
                    requestUtil.setRequestType(RequestTypeENUM.UNKNOWN_TYPE.getCode());
                    requestUtil.setFileName(null);
                    message.setData(bundle);
                    xcloudLogo.clearAnimation();
                    new Thread(new InitFileListRunnable(null, null)).start();
                }
                requestPromptHandler.sendMessage(message);
            }
        }

        public FileRemoveRunnable(File file) {
            this.file = file;
            this.requestUtil = RequestUtil.getRequestUtil();
        }
    }

    // file upload runnable
    class FileUploadRunnable implements Runnable {

        private final java.io.File file;
        private final RequestUtil requestUtil;

        @Override
        public void run() {
            if (file != null) {
                Bundle bundle = new Bundle();
                Message message = new Message();
                if (requestUtil.getIsUsed()) {
                    bundle.putString(Const.MSG.getDesc(), "已有任务:" + RequestTypeENUM.getDescByCode(requestUtil.getRequestType()) + "\"" + requestUtil.getFileName() + "\"");
                    message.setData(bundle);
                } else {
                    requestUtil.setIsUsed(true);
                    requestUtil.setRequestType(RequestTypeENUM.UPLOAD_TYPE.getCode());
                    requestUtil.setFileName(file.getName());
                    xcloudLogo.startAnimation(waitAnimation);
                    ServerResponse serverResponse = requestUtil.uploadFile(RequestURL.UPLOAD_FILE_URL.getDesc(), currentUser, file);
                    if (serverResponse.getStatus() == ResponseCodeENUM.SUCCESS.getCode()) {
                        bundle.putString(Const.MSG.getDesc(), "上传成功");
                    } else {
                        bundle.putString(Const.MSG.getDesc(), serverResponse.getMsg());
                    }
                    requestUtil.setIsUsed(false);
                    requestUtil.setRequestType(RequestTypeENUM.UNKNOWN_TYPE.getCode());
                    requestUtil.setFileName(null);
                    message.setData(bundle);
                    xcloudLogo.clearAnimation();
                    new Thread(new InitFileListRunnable(null, null)).start();
                }
                requestPromptHandler.sendMessage(message);
            }
        }

        public FileUploadRunnable(java.io.File file) {
            this.file = file;
            this.requestUtil = RequestUtil.getRequestUtil();
        }
    }

    // file download runnable
    class FileDownloadRunnable implements Runnable {

        private final File file;
        private final RequestUtil requestUtil;

        @Override
        public void run() {
            if (file != null) {
                Bundle bundle = new Bundle();
                Message message = new Message();
                if (requestUtil.getIsUsed()) {
                    bundle.putString(Const.MSG.getDesc(), "已有任务:" + RequestTypeENUM.getDescByCode(requestUtil.getRequestType()) + "\"" + requestUtil.getFileName() + "\"");
                    message.setData(bundle);
                } else {
                    requestUtil.setIsUsed(true);
                    requestUtil.setRequestType(RequestTypeENUM.DOWNLOAD_TYPE.getCode());
                    requestUtil.setFileName(file.getFileName());
                    xcloudLogo.startAnimation(waitAnimation);
//                    ToastUtil.showShortToast("开始下载" + "\"" + requestUtil.getFileName() + "\"");
                    java.io.File readyOpenFile = requestUtil.fileDownload(RequestURL.DOWNLOAD_URL.getDesc(), currentUser, file.getFileId());
                    if (readyOpenFile != null) {
                        bundle.putString(Const.MSG.getDesc(), "下载完成");
                        try {
                            FileUtil.openFile(readyOpenFile.getAbsolutePath(), ActivityHome.this);
                        } catch (OpenFileException e) {
                            bundle.putString(Const.MSG.getDesc(), e.getMessage());
                            e.printStackTrace();
                        }
                    } else {
                        bundle.putString(Const.MSG.getDesc(), "下载失败");
                    }
                    requestUtil.setIsUsed(false);
                    requestUtil.setRequestType(RequestTypeENUM.UNKNOWN_TYPE.getCode());
                    requestUtil.setFileName(null);
                    message.setData(bundle);
                    xcloudLogo.clearAnimation();
                }
                requestPromptHandler.sendMessage(message);
            }
        }

        public FileDownloadRunnable(File file) {
            this.file = file;
            this.requestUtil = RequestUtil.getRequestUtil();
        }
    }

    // ui refresh handler
    private final Handler flushListViewHandler = new Handler(new Handler.Callback() {
        @SuppressLint("SetTextI18n")
        @Override
        public boolean handleMessage(Message msg) {
            @SuppressLint("HandlerLeak")
            SimpleAdapter adapter = new SimpleAdapter(
                    ActivityHome.this,
                    items,
                    R.layout.activity_file_list_item,
                    new String[]{"logoID", "fileName", "fileSize", "fileUploadTime"},
                    new int[]{R.id.fileLogoImageView, R.id.fileNameText, R.id.fileSizeText, R.id.fileUploadTimeText}) {

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    view.setForegroundGravity(Gravity.CENTER_HORIZONTAL);
                    return view;
                }
            };
            listFileView.setAdapter(adapter);
            String messageString = msg.getData().getString(Const.MSG.getDesc());
            ToastUtil.showShortToast(messageString);
            swipeRefreshLayout.setRefreshing(false);
            searchStringEditText.setText("");
            return false;
        }
    });

    // request or prompt handler
    private final Handler requestPromptHandler = new Handler(msg -> {
        String messageString = msg.getData().getString(Const.MSG.getDesc());
        ToastUtil.showShortToast(messageString);
        return false;
    });


    // assemble File List item data
    private void assembleFileList(List<File> files) {
        items.clear();
        if (!files.isEmpty()) {
            ActivityHome.fileList = files;
            for (File file : files) {
                file.setFileType(file.getFileType().toLowerCase());
                Integer fileExLogoID = typesMap.get(file.getFileType());
                if (fileExLogoID != null) {
                    file.setLogoID(fileExLogoID);
                } else {
                    file.setLogoID(typesMap.get("unknown"));
                }
            }
        }
        Map<String, Object> map;
        for (File file : files) {
            map = new HashMap<>();
            map.put("logoID", file.getLogoID());
            map.put("fileName", file.getFileName());
            map.put("fileSize", file.getFileSize());
            String timeString = DateTimeUtil.timerToString(file.getUploadTime());
            timeString = timeString.substring(0, timeString.lastIndexOf(":"));
            map.put("fileUploadTime", timeString);
            items.add(map);
        }
    }
}