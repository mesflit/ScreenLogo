package github.mesflit.screenlogo;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class OverlayService extends Service {
    private WindowManager windowManager;
    private View overlayView;
    private TextView overlayButton;
    private ImageView imageOverlay;
    private int animationDuration = 3000; // Animasyon süresi (milisaniye cinsinden)
    private WindowManager.LayoutParams layoutParams;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // WindowManager'ı başlat
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        // Eklenecek görünümü oluştur
        overlayView = LayoutInflater.from(this).inflate(R.layout.overlay_layout, null);

        // SharedPreferences'tan kayıtlı metni alın
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String kayitliMetin = sharedPreferences.getString("kayitliMetin", ""); // İlk parametre anahtar, ikinci parametre varsayılan değer

        // overlayButton'u bulun
        overlayButton = overlayView.findViewById(R.id.overlayButton);
        imageOverlay = overlayView.findViewById(R.id.imageOverlay);

        // Metni ayarla
        overlayButton.setText(kayitliMetin);
        overlayButton.setTextSize(23); // Yazı tipi boyutunu ayarlayın (örneğin 24)


        // Görünümü konumlandırın ve ekleyin
        layoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, // Önemli: Bu tip kullanılır
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, // Görünüm odaklanabilir olmamalı
                PixelFormat.TRANSLUCENT);

        layoutParams.gravity = Gravity.CENTER;
        windowManager.addView(overlayView, layoutParams);

        // Renk animasyonunu başlat
        startTextColorAnimation();

        // Uzun basmaya tepki ver
        overlayButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // Uzun basıldığında taşınabilir hale getir
                makeViewDraggable(overlayButton);
                return true; // true döndürerek olayın tüketildiğini belirtin
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (overlayView != null) {
            windowManager.removeView(overlayView);
        }
    }

    // Metin rengini yavaşça soldan sağa doğru değiştiren bir animasyon
    private void startTextColorAnimation() {
        int startColor = Color.RED; // Başlangıç rengi
        int endColor = Color.BLUE; // Bitiş rengi

        // ValueAnimator oluştur
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), startColor, endColor);
        colorAnimation.setDuration(animationDuration);

        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                int color = (int) animator.getAnimatedValue();
                overlayButton.setTextColor(color);
            }
        });

        colorAnimation.setRepeatCount(ValueAnimator.INFINITE); // Sonsuz döngü
        colorAnimation.setRepeatMode(ValueAnimator.REVERSE); // Değişim yönünü tersine çevir

        colorAnimation.start();
    }

    // Yazıyı sürüklenebilir hale getirme
    private void makeViewDraggable(final View view) {
        view.setOnTouchListener(new View.OnTouchListener() {
            private int offsetX, offsetY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        offsetX = (int) event.getRawX() - layoutParams.x;
                        offsetY = (int) event.getRawY() - layoutParams.y;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        layoutParams.x = (int) event.getRawX() - offsetX;
                        layoutParams.y = (int) event.getRawY() - offsetY;
                        windowManager.updateViewLayout(overlayView, layoutParams);
                        break;
                }
                return true;
            }
        });
    }
}
