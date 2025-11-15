package com.meetingminutes.app;

import android.os.Bundle;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 配置 WebView 以支持音频录制和语音识别
        WebView webView = getBridge().getWebView();
        if (webView != null) {
            WebSettings settings = webView.getSettings();

            // 启用 JavaScript（Capacitor 默认已启用，但确保开启）
            settings.setJavaScriptEnabled(true);

            // 启用媒体播放
            settings.setMediaPlaybackRequiresUserGesture(false);

            // 允许访问文件
            settings.setAllowFileAccess(true);
            settings.setAllowContentAccess(true);

            // 启用数据库和本地存储
            settings.setDatabaseEnabled(true);
            settings.setDomStorageEnabled(true);

            // 设置 WebChromeClient 以处理权限请求
            webView.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onPermissionRequest(final PermissionRequest request) {
                    // 自动授予音频录制权限
                    if (request.getResources() != null) {
                        for (String resource : request.getResources()) {
                            if (resource.equals(PermissionRequest.RESOURCE_AUDIO_CAPTURE)) {
                                request.grant(new String[]{PermissionRequest.RESOURCE_AUDIO_CAPTURE});
                                return;
                            }
                        }
                    }
                    // 如果不是音频权限，使用默认处理
                    super.onPermissionRequest(request);
                }
            });
        }
    }
}
