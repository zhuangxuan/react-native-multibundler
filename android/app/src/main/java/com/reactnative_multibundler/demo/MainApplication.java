package com.reactnative_multibundler.demo;

import android.app.Application;
import android.util.Log;

import com.facebook.react.LoadScriptListener;
import com.facebook.react.ReactApplication;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.shell.MainReactPackage;
import com.facebook.soloader.SoLoader;
import com.reactnative_multibundler.BuildConfig;
import com.reactnative_multibundler.ScriptLoadUtil;
import com.smallnew.smartassets.RNSmartassetsPackage;
import com.swmansion.gesturehandler.react.RNGestureHandlerPackage;
import com.swmansion.reanimated.ReanimatedPackage;
import com.swmansion.rnscreens.RNScreensPackage;
import com.th3rdwave.safeareacontext.SafeAreaContextPackage;

import org.reactnative.maskedview.RNCMaskedViewPackage;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

public class MainApplication extends Application implements ReactApplication {

  private final ReactNativeHost mReactNativeHost = new ReactNativeHost(this) {
    @Override
    public boolean getUseDeveloperSupport() {
      return false;//是否是debug模式
    }

    @Override
    protected List<ReactPackage> getPackages() {
      return Arrays.<ReactPackage>asList(
          new MainReactPackage(),
              new RNSmartassetsPackage(),
              new RNCMaskedViewPackage(),
              new RNGestureHandlerPackage(),
              new ReanimatedPackage(),
              new SafeAreaContextPackage(),
              new RNScreensPackage()
      );
    }

    @Nullable
    @Override
    protected String getBundleAssetName() {
      return "platform.android.bundle";
    }

    @Override
    protected String getJSMainModuleName() {
      return "MultiDenugEntry";
    }
  };

  @Override
  public ReactNativeHost getReactNativeHost() {
    return mReactNativeHost;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    SoLoader.init(this, /* native exopackage */ false);
    final ReactInstanceManager reactInstanceManager = getReactNativeHost().getReactInstanceManager();
    if (!reactInstanceManager.hasStartedCreatingInitialContext()) {
      reactInstanceManager.createReactContextInBackground();//这里会先加载基础包platform.android.bundle，也可以不加载
      reactInstanceManager.addReactInstanceEventListener(new ReactInstanceManager.ReactInstanceEventListener() {
        @Override
        public void onReactContextInitialized(ReactContext context) {
          Log.i("zxa","onReactContextInitialized");
          reactInstanceManager.removeReactInstanceEventListener(this);
        }
      });
    }
  }
}
