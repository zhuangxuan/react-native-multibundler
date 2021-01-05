/**
 * Copyright (c) 2015-present, Facebook, Inc.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.react;

import android.app.AlertDialog;
import android.os.Bundle;
import com.facebook.react.bridge.CatalystInstance;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.facebook.react.modules.core.PermissionAwareActivity;
import com.facebook.react.modules.core.PermissionListener;
import com.reactnative_multibundler.R;
import com.reactnative_multibundler.RnBundle;
import com.reactnative_multibundler.ScriptLoadUtil;
import java.io.File;
import javax.annotation.Nullable;

/**
 * 异步加载业务bundle的activity
 */
public abstract class AsyncReactActivity extends androidx.fragment.app.FragmentActivity
        implements DefaultHardwareBackBtnHandler, PermissionAwareActivity {

    public enum ScriptType {ASSET,FILE,NETWORK}

    protected boolean bundleLoaded = false;


    protected @Nullable String getMainComponentName() {
        return null;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ReactInstanceManager manager = getReactNativeHost().getReactInstanceManager();
        if (!manager.hasStartedCreatingInitialContext()
        ||ScriptLoadUtil.getCatalystInstance(getReactNativeHost())==null) {
            manager.addReactInstanceEventListener(new ReactInstanceManager.ReactInstanceEventListener() {
                @Override
                public void onReactContextInitialized(ReactContext context) {
                    loadScript(new LoadScriptListener() {
                        @Override
                        public void onLoadComplete(boolean success,String scriptPath) {
                            bundleLoaded = success;
                            if(success)
                                runApp(scriptPath);
                        }
                    });
                    manager.removeReactInstanceEventListener(this);
                }
            });
            ((ReactApplication)getApplication()).getReactNativeHost().getReactInstanceManager().createReactContextInBackground();
        }else{
            loadScript(new LoadScriptListener() {
                @Override
                public void onLoadComplete(boolean success,String scriptPath) {
                    bundleLoaded = success;
                    if(success)
                        runApp(scriptPath);
                }
            });
        }
        setContentView(R.layout.rn_layout);
        ReactRootView fl_content_rn = findViewById(R.id.fl_content_rn);
        fl_content_rn.startReactApplication(getReactNativeHost().getReactInstanceManager(), getMainComponentName(), null);
    }

    protected abstract RnBundle getBundle();

    protected void runApp(String scriptPath){
        if(scriptPath!=null){
            scriptPath = "file://"+scriptPath.substring(0,scriptPath.lastIndexOf(File.separator)+1);
        }
        final String path = scriptPath;
        final ReactInstanceManager reactInstanceManager = ((ReactApplication)getApplication()).getReactNativeHost().getReactInstanceManager();
        ScriptLoadUtil.setJsBundleAssetPath(
                reactInstanceManager.getCurrentReactContext(),
                path);
    }

    private ReactNativeHost getReactNativeHost(){
        return ((ReactApplication)getApplication()).getReactNativeHost();
    }

    protected void loadScript(final LoadScriptListener loadListener){
        final RnBundle bundle = getBundle();
        /** all buz module is loaded when in debug mode*/
        if(ScriptLoadUtil.MULTI_DEBUG){//当设置成debug模式时，所有需要的业务代码已经都加载好了
            loadListener.onLoadComplete(true,null);
            return;
        }
        ScriptType pathType = bundle.scriptType;
        String scriptPath = bundle.scriptUrl;
        final CatalystInstance instance = ScriptLoadUtil.getCatalystInstance(getReactNativeHost());
        if(pathType== ScriptType.ASSET) {
            ScriptLoadUtil.loadScriptFromAsset(getApplicationContext(),instance,scriptPath,false);
            loadListener.onLoadComplete(true,null);
        }
    }

    @Override
    public void invokeDefaultOnBackPressed() {
        super.onBackPressed();
    }


    @Override
    public void requestPermissions(
            String[] permissions,
            int requestCode,
            PermissionListener listener) {
    }

}
