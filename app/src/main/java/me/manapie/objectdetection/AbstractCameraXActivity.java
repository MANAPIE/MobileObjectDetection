package me.manapie.objectdetection;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Size;
import android.view.TextureView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageAnalysisConfig;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.core.app.ActivityCompat;

public abstract class AbstractCameraXActivity<R> extends BaseModuleActivity {
    private static final int NEXT_FRAME_IN_MS = 100;
//    private static final int TARGET_RESOLUTION_WIDTH = 3840;
//    private static final int TARGET_RESOLUTION_HEIGHT = 2160;
    private static final int TARGET_RESOLUTION_WIDTH = 480;
    private static final int TARGET_RESOLUTION_HEIGHT = 640;

    private static final int REQUEST_CODE_CAMERA_PERMISSION = 200;
    private static final String[] PERMISSIONS = {Manifest.permission.CAMERA};

    private long mLastAnalysisResultTime;

    protected abstract int getContentViewLayoutId();

    protected abstract TextureView getCameraPreviewTextureView();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewLayoutId());

        startBackgroundThread();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                PERMISSIONS,
                REQUEST_CODE_CAMERA_PERMISSION);
        } else {
            setupCameraX();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(
                    this,
                    "You can't use object detection example without granting CAMERA permission",
                    Toast.LENGTH_LONG)
                    .show();
                finish();
            } else {
                setupCameraX();
            }
        }
    }

    private void setupCameraX() {
        final TextureView textureView = getCameraPreviewTextureView();
        final PreviewConfig previewConfig = new PreviewConfig.Builder().build();
        final Preview preview = new Preview(previewConfig);
        preview.setOnPreviewOutputUpdateListener(output -> textureView.setSurfaceTexture(output.getSurfaceTexture()));

        final ImageAnalysisConfig imageAnalysisConfig =
            new ImageAnalysisConfig.Builder()
                .setTargetResolution(new Size(TARGET_RESOLUTION_WIDTH, TARGET_RESOLUTION_HEIGHT))
                .setCallbackHandler(mBackgroundHandler)
                .setImageReaderMode(ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE)
                .build();
        final ImageAnalysis imageAnalysis = new ImageAnalysis(imageAnalysisConfig);

        imageAnalysis.setAnalyzer((image, rotationDegrees) -> {
            if (SystemClock.elapsedRealtime() - mLastAnalysisResultTime < NEXT_FRAME_IN_MS) {
                return;
            }

            final R result = analyzeImage(image, rotationDegrees);
            if (result != null) {
                long realTime = SystemClock.elapsedRealtime();
                long timeGap = realTime - mLastAnalysisResultTime;
                mLastAnalysisResultTime = realTime;
                runOnUiThread(() -> applyToUiAnalyzeImageResult(result, System.currentTimeMillis(), timeGap));
            }
        });

        CameraX.bindToLifecycle(this, preview, imageAnalysis);
    }

    @WorkerThread
    @Nullable
    protected abstract R analyzeImage(ImageProxy image, int rotationDegrees);

    @UiThread
    protected abstract void applyToUiAnalyzeImageResult(R result, long resultTime, long timeGap);
}
