package com.whaleread.flutter.plugin.adnet_qq;

import android.util.Log;

import com.qq.e.ads.interstitial2.UnifiedInterstitialAD;
import com.qq.e.ads.interstitial2.UnifiedInterstitialADListener;
import com.qq.e.comm.util.AdError;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

public class FlutterUnifiedInterstitial implements MethodChannel.MethodCallHandler, UnifiedInterstitialADListener {
    private static final String TAG = FlutterUnifiedInterstitial.class.getSimpleName();
    private UnifiedInterstitialAD iad;
    private final MethodChannel methodChannel;

    private String posId;

    public FlutterUnifiedInterstitial(String posId, BinaryMessenger messenger) {
        Log.d(TAG, "creating " + FlutterUnifiedInterstitial.class.getName());
        this.posId = posId;
        this.methodChannel = new MethodChannel(messenger, PluginSettings.UNIFIED_INTERSTITIAL_ID + "_" + posId);
        this.methodChannel.setMethodCallHandler(this);
    }

    @Override
    public void onMethodCall(MethodCall methodCall, MethodChannel.Result result) {
        switch (methodCall.method) {
            case "load":
                Log.d(TAG, "load");
                getIAD().loadAD();
                result.success(true);
                break;
            case "show":
                Log.d(TAG, "show");
                showAD();
                result.success(true);
                break;
            case "popup":
                Log.d(TAG, "popup");
                showAsPopup();
                result.success(true);
                break;
            case "close":
                Log.d(TAG, "close");
                closeAd();
                result.success(true);
                break;
            default:
                result.notImplemented();
        }
    }

    public void closeAd() {
        if (iad != null) {
            iad.destroy();
            iad = null;
        }
        methodChannel.setMethodCallHandler(null);
        AdnetQqPlugin.removeInterstitial(posId);
    }

    private UnifiedInterstitialAD getIAD() {
        if (iad != null) {
            return iad;
        }
        iad = new UnifiedInterstitialAD(AdnetQqPlugin.getActivity(), posId, this);
        return iad;
    }

    private void showAD() {
        getIAD().showAsPopupWindow();
    }

    private void showAsPopup() {
        getIAD().showAsPopupWindow();
    }

    @Override
    public void onNoAD(AdError adError) {
        Log.d(TAG, String.format("onNoAD，eCode = %d, eMsg = %s", adError.getErrorCode(), adError.getErrorMsg()));
        iad = null;
        methodChannel.invokeMethod("onNoAd", adError.getErrorCode());
    }

    @Override
    public void onADReceive() {
        Log.d(TAG, "onADReceive");
        methodChannel.invokeMethod("onAdReceived", null);
    }

    @Override
    public void onADExposure() {
        Log.d(TAG, "onADExposure");
        methodChannel.invokeMethod("onAdExposure", null);
    }

    @Override
    public void onADClosed() {
        Log.d(TAG, "onADClosed");
        iad = null;
        methodChannel.invokeMethod("onAdClosed", null);
    }

    @Override
    public void onADClicked() {
        Log.d(TAG, "onADClicked");
        methodChannel.invokeMethod("onAdClicked", null);
    }

    @Override
    public void onADLeftApplication() {
        Log.d(TAG, "onADLeftApplication");
        methodChannel.invokeMethod("onAdLeftApplication", null);
    }

    @Override
    public void onADOpened() {
        Log.d(TAG, "onADOpened");
        methodChannel.invokeMethod("onAdOpened", null);
    }

    @Override
    public void onVideoCached() {
        methodChannel.invokeMethod("onVideoCached", null);
    }
}
