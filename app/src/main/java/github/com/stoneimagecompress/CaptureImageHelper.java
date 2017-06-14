package github.com.stoneimagecompress;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.util.List;

/**
 * Email  1562363326@qq.com
 * Github https://github.com/skcodestack
 * Created by sk on 2017/6/8
 * Version  1.0
 * Description:
 */

public class CaptureImageHelper {

    private static String mAuthority = "github.com.androidadvanced_ndk.fileprovider";

    public static void setAuthority(String authority){
        mAuthority=authority;
    }


    public static void startCamera(Activity activity, File imagefile, int requestCode){

        Uri uriForFile = FileProvider.getUriForFile(activity, mAuthority, imagefile);

        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ClipData clip =
                    ClipData.newUri(activity.getContentResolver(), "A photo", uriForFile);
            intent.setClipData(clip);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }else {
            List<ResolveInfo> resInfoList =
                    activity.getPackageManager()
                            .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                activity.grantUriPermission(packageName, uriForFile,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
        }

        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriForFile);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        activity.startActivityForResult(intent, requestCode);
    }

}
