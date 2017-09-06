package net.goeasyway.uploadimage;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.soundcloud.android.crop.Crop;
import com.yalantis.ucrop.UCrop;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;

import java.io.File;
import java.util.List;

import static android.R.attr.maxWidth;

public class HomeActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_CHOOSE = 99;

    ImageView image_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        image_view = (ImageView) findViewById(R.id.image_view);

        Matisse.from(HomeActivity.this)
                .choose(MimeType.allOf())
                .countable(true)
                .maxSelectable(1)
                .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(new GlideEngine())
                .forResult(REQUEST_CODE_CHOOSE);
    }

    private void beginCrop(Uri sourceUri) {
        Uri destinationUri = Uri.fromFile(new File(getCacheDir(), "cropped"));
//        Crop.of(source, destination).asSquare().start(this);

        UCrop.of(sourceUri, destinationUri)
                .withAspectRatio(1, 1)
                .withMaxResultSize(800, 2400)
                .start(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            List<Uri> list = Matisse.obtainResult(data);
            Log.d("Matisse", "mSelected: " + list);
            if (!list.isEmpty()) {
                Glide.with(this).load(list.get(0)).into(image_view);
                beginCrop(list.get(0));
            }
        } else if (requestCode == Crop.REQUEST_CROP && resultCode == RESULT_OK) {
            image_view.setImageURI(Crop.getOutput(data));
        }else if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            image_view.setImageURI(resultUri);
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
        }
    }
}
