package com.view.fps;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.os.*;
import android.view.*;
import android.widget.*;
import androidx.core.app.NotificationCompat;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startService(new Intent(this, FPSForegroundService.class));

        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);

        finish();
    }

    public static class FPSForegroundService extends Service implements SharedPreferences.OnSharedPreferenceChangeListener {

        private WindowManager windowManager;
        private View overlayView;
        private TextView fpsTextView, ramTextView;
        private int frameCount = 0;
        private long startTime = 0;
        private Choreographer.FrameCallback frameCallback;
        private SharedPreferences prefs;

        @Override
        public void onCreate() {
            super.onCreate();

            prefs = getSharedPreferences("fps_prefs", MODE_PRIVATE);
            prefs.registerOnSharedPreferenceChangeListener(this);

            windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            createOverlay();

            frameCallback = new Choreographer.FrameCallback() {
                @Override
                public void doFrame(long frameTimeNanos) {
                    frameCount++;
                    long currentTime = System.currentTimeMillis();
                    if (startTime == 0) {
                        startTime = currentTime;
                    }
                    if (currentTime - startTime >= 1000) {
                        int fps = frameCount;
                        frameCount = 0;
                        startTime = currentTime;
                        updateOverlay(fps);
                    }
                    Choreographer.getInstance().postFrameCallback(this);
                }
            };
            Choreographer.getInstance().postFrameCallback(frameCallback);
        }

        private void createOverlay() {
            if (overlayView != null) return;

            WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                            WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                    PixelFormat.TRANSLUCENT
            );
            params.gravity = Gravity.TOP | Gravity.START;
            params.x = 100;
            params.y = 100;

            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.HORIZONTAL);
            layout.setPadding(15, 10, 15, 10);
            layout.setBackgroundColor(Color.TRANSPARENT);

            GradientDrawable bg = new GradientDrawable();
            bg.setColor(Color.parseColor("#AA000000")); // Semi-transparent black
            bg.setCornerRadius(10f);
            layout.setBackground(bg);

            fpsTextView = new TextView(this);
            fpsTextView.setTextColor(Color.WHITE);
            fpsTextView.setTextSize(prefs.getInt("overlay_size", 14));
            fpsTextView.setText("FPS: --");

            ramTextView = new TextView(this);
            ramTextView.setTextColor(Color.WHITE);
            ramTextView.setTextSize(prefs.getInt("overlay_size", 14));
            ramTextView.setText("RAM: --");
            ramTextView.setPadding(20, 0, 0, 0);

            layout.addView(fpsTextView);
            layout.addView(ramTextView);

            overlayView = layout;
            overlayView.setOnTouchListener(new OverlayTouchListener(params));
            windowManager.addView(overlayView, params);

            float opacity = prefs.getInt("overlay_opacity", 80) / 100f;
            overlayView.setAlpha(opacity);

            startForeground(1, createNotification());
        }

        private Notification createNotification() {
            String channelId = "fps_overlay";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelId, "FPS Monitor", NotificationManager.IMPORTANCE_LOW);
                getSystemService(NotificationManager.class).createNotificationChannel(channel);
            }
            return new NotificationCompat.Builder(this, channelId)
                    .setContentTitle("FPS Overlay Running")
                    .setSmallIcon(android.R.drawable.ic_menu_info_details)
                    .setOngoing(true)
                    .build();
        }

        private void updateOverlay(int fps) {
            boolean showFPS = prefs.getBoolean("show_fps", true);
            boolean showRAM = prefs.getBoolean("show_ram", true);

            if (fpsTextView != null && ramTextView != null) {
                if (showFPS) {
                    fpsTextView.setVisibility(View.VISIBLE);
                    fpsTextView.setTextSize(prefs.getInt("overlay_size", 14));
                    fpsTextView.setText("FPS: " + fps);
                } else {
                    fpsTextView.setVisibility(View.GONE);
                }

                if (showRAM) {
                    ramTextView.setVisibility(View.VISIBLE);
                    ramTextView.setTextSize(prefs.getInt("overlay_size", 14));
                    ramTextView.setText("RAM: " + getRamUsage() + "%");
                } else {
                    ramTextView.setVisibility(View.GONE);
                }

                float opacity = prefs.getInt("overlay_opacity", 80) / 100f;
                overlayView.setAlpha(opacity);
            }
        }

        private int getRamUsage() {
            ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
            activityManager.getMemoryInfo(memoryInfo);

            long totalMem = memoryInfo.totalMem;
            long availMem = memoryInfo.availMem;
            long usedMem = totalMem - availMem;

            return (int) ((usedMem * 100) / totalMem);
        }

        private class OverlayTouchListener implements View.OnTouchListener {
            private final WindowManager.LayoutParams params;
            private int initialX, initialY;
            private float initialTouchX, initialTouchY;

            OverlayTouchListener(WindowManager.LayoutParams params) {
                this.params = params;
            }

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        windowManager.updateViewLayout(overlayView, params);
                        return true;
                }
                return false;
            }
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            if (overlayView != null) {
                windowManager.removeView(overlayView);
                overlayView = null;
            }
            prefs.unregisterOnSharedPreferenceChangeListener(this);
            Choreographer.getInstance().removeFrameCallback(frameCallback);
        }

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals("show_fps") || key.equals("show_ram") || key.equals("overlay_size") || key.equals("overlay_opacity")) {
                // Overlay UI আপডেট করো SharedPreferences পরিবর্তন হলে
                handlerPostUpdate();
            }
        }

        private void handlerPostUpdate() {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> {
                // force update overlay, fps আর ram দেখানোর জন্য
                updateOverlay(frameCount);
            });
        }
    }
}
