package com.view.fps;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;

public class MainActivity extends Activity {
    private static final String CHANNEL_ID = "fps_channel";
    private static final int NOTIFICATION_ID = 1;
    private static final int FPS_UPDATE_INTERVAL = 1000; // 1 second
    
    @Override
    protected void onStart() {
        super.onStart();
        startForegroundService(new Intent(this, FPSForegroundService.class));
        finish(); // Close the activity immediately
    }
    
    public static class FPSForegroundService extends Service {
        private WindowManager windowManager;
        private TextView fpsTextView;
        private Handler handler = new Handler();
        private long lastFrameTime = 0;
        private int frameCount = 0;
        private int lastFPS = 0;
        
        @Override
        public void onCreate() {
            super.onCreate();
            createNotificationChannel();
            startForeground(NOTIFICATION_ID, createNotification());
            setupFPSView();
            startFPSCounter();
        }
        
        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            return START_STICKY;
        }
        
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
        
        private void createNotificationChannel() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "FPS Service",
                    NotificationManager.IMPORTANCE_MIN
                );
                channel.setShowBadge(false);
                NotificationManager manager = getSystemService(NotificationManager.class);
                manager.createNotificationChannel(channel);
            }
        }
        
        private Notification createNotification() {
            return new Notification.Builder(this, CHANNEL_ID)
                .setContentTitle("FPS Service")
                .setContentText("Displaying FPS in status bar")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setPriority(Notification.PRIORITY_MIN)
                .build();
        }
        
        private void setupFPSView() {
            windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            
            fpsTextView = new TextView(this);
            fpsTextView.setTextColor(0xFFFFFFFF); // White
            fpsTextView.setTextSize(12);
            fpsTextView.setText("FPS: --");
            
            WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                    WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT
            );
            
            params.gravity = Gravity.TOP | Gravity.END;
            params.y = 10;
            params.x = 10;
            
            windowManager.addView(fpsTextView, params);
        }
        
        private void startFPSCounter() {
            getMainThreadHandler().post(new Runnable() {
                private long lastUpdateTime = SystemClock.elapsedRealtime();
                
                @Override
                public void run() {
                    long currentTime = SystemClock.elapsedRealtime();
                    frameCount++;
                    
                    if (currentTime - lastUpdateTime >= FPS_UPDATE_INTERVAL) {
                        lastFPS = (int) (frameCount * 1000 / (currentTime - lastUpdateTime));
                        fpsTextView.setText("FPS: " + lastFPS);
                        frameCount = 0;
                        lastUpdateTime = currentTime;
                    }
                    
                    getMainThreadHandler().postDelayed(this, 16); // ~60fps
                }
            });
        }
        
        private Handler getMainThreadHandler() {
            return handler;
        }
        
        @Override
        public void onDestroy() {
            super.onDestroy();
            if (fpsTextView != null && windowManager != null) {
                windowManager.removeView(fpsTextView);
            }
            handler.removeCallbacksAndMessages(null);
        }
    }
}