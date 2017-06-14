package github.com.stoneimagecompress;

import android.Manifest;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import github.com.stoneimagecompress.itemdecoration.GridLayoutItemDecoration;


public class ImageSelectActivity extends AppCompatActivity implements OnItemSelectedChangedListener<String>, View.OnClickListener, SelectImageListAdapter.OnCaptureImageListener {

    //多选
    public static final int MODE_MULTI=0x00012;
    //单选
    public static final int MODE_SIGNAL=0x00011;
    //单选还是多选key
    public static final String EXTRA_SELECT_MODE="EXTRA_SELECT_MODE";
    //最大选择张数key
    public static final String EXTRA_SELECT_COUNT="EXTRA_SELECT_COUNT";
    //已经选中的图片key
    public static final String EXTRA_DEFAULT_SELECTED_LIST="EXTRA_DEFAULT_SELECTED_LIST";
    //是否可以拍照key
    public static final String EXTRA_SHOW_CAMERA="EXTRA_SHOW_CAMERA";
    //选中图片返回列表的key
    public static final String EXTRA_RESULT="EXTRA_RESULT";



    //是单选还是多选
    private int  mMode = MODE_MULTI;
    //最大选择张数
    private int mMaxCount = 8;
    //已经选中的图片
    private ArrayList<String> mResultList;
    //是否可以拍照
    private boolean mShowCamera = true;


    private int LOADER_TYPE = 1;
    private RecyclerView mRecyclerView;
    private TextView btn_preview;
    private TextView tv_count_hint;
    private String mImagePath;
    private Activity mActivity;
    File mTemFile;
    private static final int CAPTURE_IMAGE_REQUEST=0X0021;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_select);
        mActivity = this;
        init();

        initDataAndEvent();
    }



    private void init() {

        TextView btn_query = (TextView) findViewById(R.id.btn_query);
        btn_query.setOnClickListener(this);

        btn_preview = (TextView) findViewById(R.id.btn_preview);
        tv_count_hint = (TextView) findViewById(R.id.tv_count_hint);

        mRecyclerView = (RecyclerView) findViewById(R.id.image_list_rv);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this,3));
        mRecyclerView.addItemDecoration(new GridLayoutItemDecoration(this,R.drawable.linearlayout_item));


        mMaxCount = getIntent().getIntExtra(EXTRA_SELECT_COUNT,mMaxCount);
        mMode = getIntent().getIntExtra(EXTRA_SELECT_MODE,mMode);
        mResultList = getIntent().getStringArrayListExtra(EXTRA_DEFAULT_SELECTED_LIST);
        mShowCamera = getIntent().getBooleanExtra(EXTRA_SHOW_CAMERA,true);

        if(mResultList == null){
            mResultList = new ArrayList<>();
        }
        if(mMode == MODE_SIGNAL){
            mMaxCount = 1;
        }
        selectedChangedListener("");



    }

    private void initDataAndEvent() {
        //保存图片的路径
        File picFile = new File(Environment.getExternalStorageDirectory(),"images");
        if(!picFile.exists()){
            picFile.mkdirs();
        }
        mImagePath = picFile.getAbsolutePath();
        Log.e("ImageSelect", "OOOOOOOOOOOOOOOO=====>"+mImagePath);


        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},100);

        }else {

            getLoaderManager().initLoader(LOADER_TYPE, null, mLoaderCallback);
        }
    }

    LoaderManager.LoaderCallbacks<Cursor> mLoaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {

        private final String[] IMAGE_COLUMNS={
                MediaStore.Images.Media.DATA,//图片路径
                MediaStore.Images.Media.DISPLAY_NAME,//显示的名字
                MediaStore.Images.Media.DATE_ADDED,//添加时间
                MediaStore.Images.Media.MIME_TYPE,//图片扩展类型
                MediaStore.Images.Media.SIZE,//图片大小
                MediaStore.Images.Media._ID,//图片id
        };

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {

            CursorLoader cursorLoader = new CursorLoader(ImageSelectActivity.this,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,IMAGE_COLUMNS,
                    IMAGE_COLUMNS[4] + " > 0 AND "+IMAGE_COLUMNS[3] + " =? OR " +IMAGE_COLUMNS[3] + " =? ",
                    new String[]{"image/jpeg","image/png"},IMAGE_COLUMNS[2] + " DESC");
            return cursorLoader;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

            if(data != null && data.getCount() > 0){
                ArrayList<String> imageList = new ArrayList<>();

                if(mShowCamera){
                    imageList.add("");
                }

                while (data.moveToNext()){
                    String path = data.getString(data.getColumnIndexOrThrow(IMAGE_COLUMNS[0]));
                    imageList.add(path);
                    Log.e("ImageSelect", "IIIIIIIIIIIIIIIIIIII=====>"+path);
                }
                //显示数据
                showListData(imageList);
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };

    /**
     * 显示数据到界面上
     * @param imageList
     */
    private void showListData(ArrayList<String> imageList) {

        SelectImageListAdapter adapter = new SelectImageListAdapter(this,imageList,mResultList,mMaxCount);
        mRecyclerView.setAdapter(adapter);
        adapter.setOnItemSelectListener(this);
        adapter.setOnCaptureImageListener(this);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==100){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){

                getLoaderManager().initLoader(LOADER_TYPE, null, mLoaderCallback);
            }else {
                Toast.makeText(ImageSelectActivity.this, "权限被拒接！", Toast.LENGTH_SHORT).show();
            }
        }else if(requestCode == 200){

            for (int result : grantResults) {
                if(result != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(ImageSelectActivity.this, "权限被拒接！", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            startCamera();


        }


    }

    @Override
    public void selectedChangedListener(String itemData) {
        if(mResultList != null ){
            int size = mResultList.size();
            if(size > 0){
                btn_preview.setEnabled(true);
                btn_preview.setOnClickListener(this);
            }else {
                btn_preview.setEnabled(false);
                btn_preview.setOnClickListener(null);
            }

            tv_count_hint.setText(size+"/"+mMaxCount);
        }
    }
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_query){
            //确定
            sureSelect();
        }else if(v.getId() == R.id.btn_preview){
            //预览

        }
    }


    /**
     * 拍照
     */
    @Override
    public void captureImage() {
        //检测是否有相机和读写文件权限
        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity,new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 200);
        } else {
            startCamera();
        }



    }

    private void startCamera(){
        File imagefile=new File(mImagePath,"IMG_"+System.currentTimeMillis()+".png");
        mTemFile=imagefile;
        CaptureImageHelper.setAuthority("github.com.androidadvanced_ndk.fileprovider");
        CaptureImageHelper.startCamera(this,imagefile,CAPTURE_IMAGE_REQUEST);


//        Uri uriForFile = FileProvider.getUriForFile(mActivity, "github.com.androidadvanced_ndk.fileprovider", imagefile);
//
//        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//            ClipData clip =
//                    ClipData.newUri(getContentResolver(), "A photo", uriForFile);
//            intent.setClipData(clip);
//            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//        }else {
//            List<ResolveInfo> resInfoList =
//                    getPackageManager()
//                            .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
//            for (ResolveInfo resolveInfo : resInfoList) {
//                String packageName = resolveInfo.activityInfo.packageName;
//                grantUriPermission(packageName, uriForFile,
//                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//            }
//        }
//
////        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imagefile));
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriForFile);
//
//        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
//        ImageSelectActivity.this.startActivityForResult(intent, CAPTURE_IMAGE_REQUEST);
    }


    /**
     * 确定
     */
    private void sureSelect() {
        Intent intent = new Intent();
        intent.putStringArrayListExtra(EXTRA_RESULT,mResultList);
        setResult(RESULT_OK,intent);

        finish();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == CAPTURE_IMAGE_REQUEST){

                mResultList.add(mTemFile.getAbsolutePath());
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(mTemFile)));
                sureSelect();

            }
        }

   }



}
