package com.rommelosc.kalpaper;

import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;
 
public abstract class AnimationWallpaper extends WallpaperService {
 	
   protected abstract class AnimationEngine extends Engine {
      private Handler mHandler = new Handler();
 
      private Runnable mIteration = new Runnable() {
         public void run() {
            iteration();
            drawFrame();
         }
      };
 
      private boolean mVisible;
 
      @Override
      public void onDestroy() {
         super.onDestroy();
         // stop the animation
         mHandler.removeCallbacks(mIteration);
      }
 
      @Override
      public void onVisibilityChanged(boolean visible) {
         mVisible = visible;
         if (visible) {
            iteration();
            drawFrame();
         } else {
            // stop the animation
            mHandler.removeCallbacks(mIteration);
         }
      }
 
      @Override
      public void onSurfaceChanged(SurfaceHolder holder, int format,
            int width, int height) {
         iteration();
         drawFrame();
      }
 
      @Override
      public void onSurfaceDestroyed(SurfaceHolder holder) {
         super.onSurfaceDestroyed(holder);
         mVisible = false;
         // stop the animation
         mHandler.removeCallbacks(mIteration);
      }
 
      @Override
      public void onOffsetsChanged(float xOffset, float yOffset,
            float xOffsetStep, float yOffsetStep, int xPixelOffset,
            int yPixelOffset) {
         iteration();
         drawFrame();
      }
 
      protected abstract void drawFrame();
 
      protected void iteration() {
         // Reschedule the next redraw in 40ms
         mHandler.removeCallbacks(mIteration);
         if (mVisible) {
            mHandler.postDelayed(mIteration, delay );
         }
      }
      
      protected int delay = 1300;
   }
}