package com.croshe.android.base.extend.web;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import com.croshe.android.base.utils.MD5Encrypt;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * webview注入js方法的基本父类
 */
public class CrosheBaseJavaScript {
    protected Gson gson = new Gson();
    protected WebView webView;
    protected Handler handler = new Handler(Looper.getMainLooper());
    private Class<?> targetClass;
    private Map<String, WebResponse> maps = new HashMap<String, WebResponse>();//当客户端执行webjs时回调对象
    private ProgressDialog dialog;
    protected Activity activity;
    protected Context context;

    public CrosheBaseJavaScript(WebView webView, Class<? extends CrosheBaseJavaScript> targetClass) {
        this.webView = webView;
        this.targetClass = targetClass;
        webView.addJavascriptInterface(this, "app");
        String ua = webView.getSettings().getUserAgentString();
        webView.getSettings().setUserAgentString(ua+";CROSHE");
        webView.getSettings().setJavaScriptEnabled(true);
        context = webView.getContext();
        activity=((Activity) context);
    }

    @JavascriptInterface
    public void doJavaScript(final String methodName, final String params) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {

                    List<Object> paramsArray = gson.fromJson(URLDecoder.decode(params, "utf-8"), new TypeToken<List<Object>>() {
                    }.getType());
                   // List<Object> paramsArray= (List<Object>) JSON.parse(URLDecoder.decode(params, "utf-8"));
                    Class<?>[] paramsClass = new Class<?>[paramsArray.size()];
                    Object[] paramsValue = new Object[paramsArray.size()];
                    final String key = paramsArray.get(0).toString();//第一个参数 为key

                    paramsClass[0] = WebResponse.class;
                    paramsValue[0] = new WebResponse() {

                        @Override
                        public void callBack(final boolean success, final Object object) {
                            // TODO Auto-generated method stub
                            handler.post(new Runnable() {

                                @Override
                                public void run() {
                                    // TODO Auto-generated method stub
                                    Map<String, Object> callback = new HashMap<String, Object>();
                                    callback.put("success", success);
                                    callback.put("key", key);
                                    callback.put("result", object);
                                    webView.loadUrl("javascript:appJs.appCallBack('" + key + "','" + gson.toJson(callback) + "');");
                                }
                            });
                        }
                    };

                    for (int i = 1; i < paramsArray.size(); i++) {//从第二个参数开始
                        Object object = paramsArray.get(i);
                        paramsClass[i] = object.getClass();
                        paramsValue[i] = object;
                    }

                    if (methodName.equals("appCallBack")) {
                        appCallBack(paramsValue[1].toString(), paramsValue[2]);
                    } else {
                        Method method = targetClass.getMethod(methodName, paramsClass);
                        method.invoke(CrosheBaseJavaScript.this, paramsValue);
                    }
                } catch (Exception e) {
                    webView.loadUrl("javascript:appJs.error('" + e.getLocalizedMessage() + "');");
                }
            }
        });
    }


    /**
     * toast的消息
     *
     * @param webResponse
     * @param message
     */
    public void toast(WebResponse webResponse, String message) {
        Toast.makeText(webView.getContext(), message, Toast.LENGTH_LONG).show();
    }


    /**
     * 关闭浏览器窗口
     *
     * @param webResponse
     */
    public void closeWindow(WebResponse webResponse) {
        ((Activity) webView.getContext()).finish();
    }

    /**
     * 显示等待对话框
     *
     * @param response
     * @param msg
     */
    public void showLoadingDialog(WebResponse response, String msg) {
        try {
            dialog = new ProgressDialog(this.webView.getContext());
            dialog.setMessage(msg);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        } catch (Exception e) {}
    }

    /**
     * 关闭等待对话框
     *
     * @param response
     */
    public void hideLoadingDialog(WebResponse response) {
        if (null != dialog) {
            dialog.dismiss();
        }
    }


    /**
     * 执行webjs
     *
     * @param methodName 方法名
     * @param params     方法参数
     */
    public void execute(WebResponse response, String methodName, Object... params) {
        try {
            String key = MD5Encrypt.MD5(System.currentTimeMillis() + methodName);
            maps.put(key, response);
            String paramStr = "";
            for (Object obj : params) {
                if (obj instanceof Number) {
                    paramStr += "," + obj.toString() + "";
                } else {
                    paramStr += ",'" + obj.toString() + "'";
                }
            }
            if(paramStr.length()==0)paramStr=",";
            webView.loadUrl("javascript:appJs.androidExecute('" + key + "','" + methodName + "',\"" + paramStr.substring(1) + "\");");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void appCallBack(String key, Object value) {
        maps.get(key).callBack(true, value);
    }


    /**
     * web回调
     *
     * @author Janesen
     */
    public interface WebResponse {
        public void callBack(boolean success, Object object);
    }


}
