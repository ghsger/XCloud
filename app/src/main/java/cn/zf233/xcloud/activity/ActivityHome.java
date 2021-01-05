package cn.zf233.xcloud.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import cn.zf233.xcloud.R;
import cn.zf233.xcloud.common.Const;
import cn.zf233.xcloud.common.RequestTypeENUM;
import cn.zf233.xcloud.common.RequestURL;
import cn.zf233.xcloud.common.ResponseCodeENUM;
import cn.zf233.xcloud.common.ServerResponse;
import cn.zf233.xcloud.entity.AbsolutePath;
import cn.zf233.xcloud.entity.File;
import cn.zf233.xcloud.entity.User;
import cn.zf233.xcloud.exception.OpenFileException;
import cn.zf233.xcloud.service.UserService;
import cn.zf233.xcloud.service.impl.UserServiceImpl;
import cn.zf233.xcloud.util.FileUtil;
import cn.zf233.xcloud.util.JumpActivityUtil;
import cn.zf233.xcloud.util.RequestUtil;
import cn.zf233.xcloud.util.ToastUtil;

public class ActivityHome extends AppCompatActivity {

    private final UserService userService = new UserServiceImpl();
    private final Map<String, Integer> typesMap = new HashMap<>();

    private static final List<Map<String, Object>> items = new ArrayList<>();
    public static List<File> fileList;
    public static Integer parentId = -1;
    public static Stack<Integer> grandpaStack = new Stack<>();
    public static List<AbsolutePath> absolutePath;
    @SuppressLint("StaticFieldLeak")
    public static ActivityHome activityHome;

    private Spinner spinner;
    private File choiceFile;
    private User currentUser;
    private ListView listFileView;
    private EditText searchStringEditText;
    private ImageView xcloudLogo;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView absolutePathText;
    private Animation clickAnimation;
    private Animation waitAnimation;

    {
        // File extensions correspond to ICONS
        typesMap.put("doc", R.mipmap.doc);
        typesMap.put("docx", R.mipmap.doc);
        typesMap.put("csv", R.mipmap.csv);
        typesMap.put("eps", R.mipmap.eps);
        typesMap.put("gif", R.mipmap.gif);
        typesMap.put("mov", R.mipmap.mov);
        typesMap.put("mp4", R.mipmap.mp4);
        typesMap.put("mp3", R.mipmap.mp3);
        typesMap.put("svg", R.mipmap.svg);
        typesMap.put("html", R.mipmap.html);
        typesMap.put("jar", R.mipmap.jar);
        typesMap.put("js", R.mipmap.js);
        typesMap.put("jpg", R.mipmap.jpg);
        typesMap.put("java", R.mipmap.java);
        typesMap.put("rar", R.mipmap.rar);
        typesMap.put("jpeg", R.mipmap.jpg);
        typesMap.put("pdf", R.mipmap.pdf);
        typesMap.put("png", R.mipmap.png);
        typesMap.put("ppt", R.mipmap.ppt);
        typesMap.put("pptx", R.mipmap.ppt);
        typesMap.put("txt", R.mipmap.txt);
        typesMap.put("xls", R.mipmap.xls);
        typesMap.put("xlsx", R.mipmap.xlsx);
        typesMap.put("zip", R.mipmap.zip);
        typesMap.put("xml", R.mipmap.xml);
        typesMap.put("conf", R.mipmap.conf);
        typesMap.put("unknown", R.mipmap.unknow);
        typesMap.put("avi", R.mipmap.avi);
        typesMap.put("json", R.mipmap.json);
        typesMap.put("sql", R.mipmap.sql);
        typesMap.put("folder", R.mipmap.folder);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        init();
        // delay refreshing the file list
        new Handler().postDelayed(() -> new Thread(new InitFileListRunnable(null, null, ActivityHome.parentId)).start(), 500);
    }

    // start this activity
    @Override
    protected void onStart() {
        super.onStart();
        String msg = this.getIntent().getStringExtra(Const.MSG.getDesc());
        if (StringUtils.isNotBlank(msg)) {
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
    @SuppressLint("ClickableViewAccessibility")
    private void init() {
        // animation
        clickAnimation = AnimationUtils.loadAnimation(this, R.anim.click);
        waitAnimation = AnimationUtils.loadAnimation(this, R.anim.wait);

        // controls
        currentUser = FileUtil.inputShared(this, Const.CURRENT_USER.getDesc(), User.class);
        ActivityHome.activityHome = ActivityHome.this;
        spinner = findViewById(R.id.spinnerUp);
        listFileView = findViewById(R.id.listFileView);
        searchStringEditText = findViewById(R.id.searchFileText);
        xcloudLogo = findViewById(R.id.xcloudLogo);
        swipeRefreshLayout = findViewById(R.id.fileListRefresh);
        absolutePathText = findViewById(R.id.absolutePathText);
        ImageView xcloudTitle = findViewById(R.id.xcloudTitle);
        ImageView currentUserHeadImage = findViewById(R.id.currentUserHeadImage);
        ImageView spinnerShow = findViewById(R.id.spinnerShow);
        ImageView createFolder = findViewById(R.id.createFolderImage);
        LinearLayout goBackFolderLayout = findViewById(R.id.goBackFolderLayout);
        FloatingActionButton floatingActionButton = findViewById(R.id.uploadFile);

        // go back folder
        goBackFolderLayout.setOnClickListener(v -> {
            if (ActivityHome.grandpaStack.isEmpty()) {
                goBackFolderLayout.setAnimation(clickAnimation);
                ToastUtil.showLongToast("已经是根");
            } else {
                ActivityHome.parentId = ActivityHome.grandpaStack.pop();
                new Thread(new InitFileListRunnable(null, null, ActivityHome.parentId)).start();
            }
        });


        // binding move file list view event
        listFileView.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    floatingActionButton.setEnabled(false);
                    floatingActionButton.setVisibility(View.GONE);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    floatingActionButton.setEnabled(true);
                    floatingActionButton.setVisibility(View.VISIBLE);
                    break;
            }
            return false;
        });

        // the drop-down refresh
        swipeRefreshLayout.setColorSchemeResources(R.color.file_list_refresh);
        // binding drop-down event
        swipeRefreshLayout.setOnRefreshListener(() -> new Thread(new InitFileListRunnable(null, null, ActivityHome.parentId)).start());

        // binding event-upload file
        floatingActionButton.setOnClickListener(v -> {
            findViewById(R.id.uploadFile).startAnimation(clickAnimation);
            // open file selector
            openFileSelector();
        });

        // sort method drop-down list adapter
        List<Map<String, Object>> upListItems = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("iconImage", R.mipmap.sort);
        map.put("sortTypeText", "选排序方式");
        upListItems.add(map);
        map = new HashMap<>();
        map.put("iconImage", R.mipmap.time);
        map.put("sortTypeText", "按时间排序");
        upListItems.add(map);
        map = new HashMap<>();
        map.put("iconImage", R.mipmap.name);
        map.put("sortTypeText", "按名称排序");
        upListItems.add(map);
        map = new HashMap<>();
        map.put("iconImage", R.mipmap.size);
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
                        new Thread(new InitFileListRunnable(null, 3, ActivityHome.parentId)).start();
                        break;
                    case 2:
                        new Thread(new InitFileListRunnable(null, 0, ActivityHome.parentId)).start();
                        break;
                    case 3:
                        new Thread(new InitFileListRunnable(null, 2, ActivityHome.parentId)).start();
                        break;
                    default:
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // file list operation dialog
        AlertDialog.Builder fileOperationAlertDialog = new AlertDialog.Builder(ActivityHome.this);
        fileOperationAlertDialog.setIcon(R.mipmap.choice);
        fileOperationAlertDialog.setTitle("操作");
//        alterDialog.setPositiveButton("下载并打开", (dialog, which) -> new Thread(new FileDownloadRunnable(choiceFile)).start());
        fileOperationAlertDialog.setNegativeButton("删除", (dialog, which) -> new Thread(new FileRemoveRunnable(choiceFile)).start());
        AlertDialog fileOperationDialog = fileOperationAlertDialog.create();

        // folder operation dialog
        AlertDialog.Builder folderOperationAlertDialog = new AlertDialog.Builder(ActivityHome.this);
        folderOperationAlertDialog.setIcon(R.mipmap.choice);
        folderOperationAlertDialog.setTitle("操作");
//        alterDialog03.setPositiveButton("打开", (dialog03, which) -> {
//            ActivityHome.grandpaStack.push(ActivityHome.parentid);
//            ActivityHome.parentid = choiceFile.getId();
//            new Thread(new InitFileListRunnable(null, null, ActivityHome.parentid)).start();
//        });
        folderOperationAlertDialog.setNegativeButton("删除", (dialog03, which) -> new Thread(new FileRemoveRunnable(choiceFile)).start());
        AlertDialog folderOperationDialog = folderOperationAlertDialog.create();

        // create new folder dialog
        final EditText folderNameEditText = new EditText(this);
        folderNameEditText.setSingleLine();
//        folderNameEditText.set
        AlertDialog.Builder createFolderAlertDialog = new AlertDialog.Builder(ActivityHome.this).setView(folderNameEditText);
        createFolderAlertDialog.setIcon(R.mipmap.createfolder);
        createFolderAlertDialog.setTitle("新建文件夹");
        createFolderAlertDialog.setPositiveButton("确认", (dialog1, which) -> {
            String folderName = folderNameEditText.getText().toString();
            if ("".equals(folderName)) {
                ToastUtil.showLongToast("文件夹名称为空");
            } else {
                File file = new File();
                file.setFileName(folderName);
                file.setParentId(ActivityHome.parentId == -1 ? null : ActivityHome.parentId);
                new Thread(new CreateFolderRunnable(file)).start();
            }
            folderNameEditText.setText("");
        });
        AlertDialog createFolderDialog = createFolderAlertDialog.create();

        // file list click listener
        listFileView.setOnItemClickListener((parent, view, position, id) -> {
            choiceFile = ActivityHome.fileList.get(position);
            // this is a file
            if (choiceFile.getFolder() == 0) {
                new Thread(new FileDownloadRunnable(choiceFile)).start();
            } else { // this is a folder
                ActivityHome.grandpaStack.push(ActivityHome.parentId);
                ActivityHome.parentId = choiceFile.getId();
                new Thread(new InitFileListRunnable(null, null, ActivityHome.parentId)).start();
            }
        });

        // file list long click listener
        listFileView.setOnItemLongClickListener((parent, view, position, id) -> {
            choiceFile = ActivityHome.fileList.get(position);
            // this is a file
            if (choiceFile.getFolder() == 0) {
                fileOperationDialog.show();
                Window dialogWindow = fileOperationDialog.getWindow();
                WindowManager.LayoutParams p = dialogWindow.getAttributes();
                p.alpha = 0.9f;
                dialogWindow.setAttributes(p);
            } else { // this is a folder
                folderOperationDialog.show();
                Window dialogWindow = folderOperationDialog.getWindow();
                WindowManager.LayoutParams p = dialogWindow.getAttributes();
                p.alpha = 0.9f;
                dialogWindow.setAttributes(p);
            }
            return true;
        });

        // show create folder dialog
        createFolder.setOnClickListener(v -> {
            createFolder.setAnimation(clickAnimation);
            createFolderDialog.show();
            Window dialogWindow = createFolderDialog.getWindow();
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
        findViewById(R.id.searchFileEnter).setOnClickListener(v -> new Thread(new InitFileListRunnable(searchStringEditText.getText().toString(), null, ActivityHome.parentId)).start());
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
                bundle.putInt(Const.ERROR.getDesc(), Const.ERROR.getCode());
                bundle.putString(Const.MSG.getDesc(), "上传文件选择失败");
                requestPromptHandler.sendMessage(message);
                return;
            }
            String filePathByUri = FileUtil.getFilePathByUri(ActivityHome.this, uri);
            if (StringUtils.isBlank(filePathByUri)) {
                bundle.putInt(Const.ERROR.getDesc(), Const.ERROR.getCode());
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
        private final Integer sortFlag;
        private final RequestUtil requestUtil;
        private final Integer parentid;

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
                    ServerResponse<List<File>> serverResponse = userService.home(requestUtil, user, parentid, searchString, sortFlag);
                    if (serverResponse.isSuccess()) {
                        assembleFileList(serverResponse.getData());
                        ActivityHome.absolutePath = serverResponse.getAbsolutePath();
//                        bundle.putString(Const.MSG.getDesc(), "获取/刷新成功");
                    } else {
                        ActivityHome.parentId = -1;
                        ActivityHome.grandpaStack.clear();
                        bundle.putInt(Const.ERROR.getDesc(), serverResponse.getStatus());
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

        public InitFileListRunnable(String searchString, Integer sortFlag, Integer parentid) {
            this.searchString = searchString;
            this.sortFlag = sortFlag;
            this.requestUtil = RequestUtil.getRequestUtil();
            this.parentid = parentid;
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
                    ServerResponse serverResponse = requestUtil.fileRemove(RequestURL.REMOVE_FILE_URL.getDesc(), currentUser, file.getId());
                    if (serverResponse.isSuccess()) {
                        bundle.putString(Const.MSG.getDesc(), "删除成功");
                    } else {
                        bundle.putInt(Const.ERROR.getDesc(), serverResponse.getStatus());
                        bundle.putString(Const.MSG.getDesc(), serverResponse.getMsg());
                    }
                    requestUtil.setIsUsed(false);
                    requestUtil.setRequestType(RequestTypeENUM.UNKNOWN_TYPE.getCode());
                    requestUtil.setFileName(null);
                    message.setData(bundle);
                    xcloudLogo.clearAnimation();
                    new Thread(new InitFileListRunnable(null, null, ActivityHome.parentId)).start();
                }
                requestPromptHandler.sendMessage(message);
            }
        }

        public FileRemoveRunnable(File file) {
            this.file = file;
            this.requestUtil = RequestUtil.getRequestUtil();
        }
    }

    // create folder
    class CreateFolderRunnable implements Runnable {

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
                    requestUtil.setRequestType(RequestTypeENUM.CREATE_FOLDER.getCode());
                    requestUtil.setFileName(file.getFileName());
                    xcloudLogo.startAnimation(waitAnimation);
                    ServerResponse serverResponse = requestUtil.createFolder(RequestURL.CREATE_FOLDER.getDesc(), currentUser, file.getFileName(), parentId);
                    if (serverResponse.isSuccess()) {
                        bundle.putString(Const.MSG.getDesc(), "创建成功");
                    } else {
                        bundle.putInt(Const.ERROR.getDesc(), serverResponse.getStatus());
                        bundle.putString(Const.MSG.getDesc(), serverResponse.getMsg());
                    }
                    requestUtil.setIsUsed(false);
                    requestUtil.setRequestType(RequestTypeENUM.UNKNOWN_TYPE.getCode());
                    requestUtil.setFileName(null);
                    message.setData(bundle);
                    xcloudLogo.clearAnimation();
                    new Thread(new InitFileListRunnable(null, null, ActivityHome.parentId)).start();
                }
                requestPromptHandler.sendMessage(message);
            }
        }

        public CreateFolderRunnable(File file) {
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
                    ServerResponse serverResponse = requestUtil.uploadFile(RequestURL.UPLOAD_FILE_URL.getDesc(), currentUser, file, ActivityHome.parentId);
                    if (serverResponse.isSuccess()) {
                        bundle.putString(Const.MSG.getDesc(), "上传成功");
                    } else {
                        bundle.putInt(Const.ERROR.getDesc(), serverResponse.getStatus());
                        bundle.putString(Const.MSG.getDesc(), serverResponse.getMsg());
                    }
                    requestUtil.setIsUsed(false);
                    requestUtil.setRequestType(RequestTypeENUM.UNKNOWN_TYPE.getCode());
                    requestUtil.setFileName(null);
                    message.setData(bundle);
                    xcloudLogo.clearAnimation();
                    new Thread(new InitFileListRunnable(null, null, ActivityHome.parentId)).start();
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
                    java.io.File readyOpenFile = requestUtil.fileDownload(RequestURL.DOWNLOAD_URL.getDesc(), currentUser, file.getId());
                    if (readyOpenFile != null) {
                        bundle.putString(Const.MSG.getDesc(), "下载完成");
                        try {
                            FileUtil.openFile(readyOpenFile.getAbsolutePath(), ActivityHome.this);
                        } catch (OpenFileException e) {
                            bundle.putInt(Const.ERROR.getDesc(), Const.ERROR.getCode());
                            bundle.putString(Const.MSG.getDesc(), e.getMessage());
                            e.printStackTrace();
                        }
                    } else {
                        bundle.putInt(Const.ERROR.getDesc(), Const.ERROR.getCode());
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
                    ActivityHome.items,
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

            // set absolutePath start
            absolutePathText.setText("");
            if (ActivityHome.absolutePath != null) {
                for (AbsolutePath folder : ActivityHome.absolutePath) {
                    absolutePathText.setText(absolutePathText.getText().toString() + folder.getFolderName() + "/");
                }
            }
            absolutePathText.setText("/" + absolutePathText.getText().toString());
            // set absolutePath end

            String msgString = msg.getData().getString(Const.MSG.getDesc());
            int errorCode = msg.getData().getInt(Const.ERROR.getDesc());
            if (errorCode == Const.ERROR.getCode() || errorCode == ResponseCodeENUM.ERROR.getCode()) {
                if (StringUtils.isNotBlank(msgString)) {
                    ToastUtil.showLongToast(msgString);
                }
            } else {
                if (StringUtils.isNotBlank(msgString)) {
                    ToastUtil.showShortToast(msgString);
                }
            }
            swipeRefreshLayout.setRefreshing(false);
            searchStringEditText.setText("");
            return false;
        }
    });

    // request or prompt handler
    private final Handler requestPromptHandler = new Handler(msg -> {
        String msgString = msg.getData().getString(Const.MSG.getDesc());
        int errorCode = msg.getData().getInt(Const.ERROR.getDesc());
        if (errorCode == Const.ERROR.getCode() || errorCode == ResponseCodeENUM.ERROR.getCode()) {
            if (StringUtils.isNotBlank(msgString)) {
                ToastUtil.showLongToast(msgString);
            }
        } else {
            if (StringUtils.isNotBlank(msgString)) {
                ToastUtil.showShortToast(msgString);
            }
        }
        return false;
    });


    // assemble File List item data
    private void assembleFileList(List<File> files) {
        ActivityHome.items.clear();
        if (!files.isEmpty()) {
            ActivityHome.fileList = files;
            for (File file : files) {
                if (file.getFolder() == 0) {
                    file.setFileType(file.getFileType().toLowerCase());
                    Integer fileExLogoID = typesMap.get(file.getFileType());
                    if (fileExLogoID != null) {
                        file.setLogoID(fileExLogoID);
                    } else {
                        file.setLogoID(typesMap.get("unknown"));
                    }
                } else {
                    file.setLogoID(typesMap.get("folder"));
                }
            }
        }
        Map<String, Object> map;
        for (File file : files) {
            map = new HashMap<>();
            map.put("logoID", file.getLogoID());
            map.put("fileName", file.getFileName());
            map.put("fileSize", file.getFileSize());
            map.put("fileUploadTime", file.getUploadTime());
            ActivityHome.items.add(map);
        }
    }
}