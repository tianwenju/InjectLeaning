package com.wenjutian.injectleaning;

import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by V.Wenju.Tian on 2016/11/18.
 */
public class InjectInvocationHandler implements InvocationHandler {
   // 拦截的方法名列表,也就是加了Onclick
    private Map<String, Method> map = new HashMap<>();
    //    在这里实际上是MainActivity
    private Object target;

    public InjectInvocationHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (target != null) {
//            获取方法名
            String name = method.getName();
            Log.e("自定义标签", "invoke: "+method.getName());
            Method m = map.get(name);
            Log.e("自定义标签", "invoke: activity"+m.getName());
            //其实调用的是Activity的加了Onclick的方法
            if (m != null) {
                return m.invoke(target, args);
            }
        }
        return null;
    }

    /**
     * 向拦截列表里添加拦截的方法
     */
    public void add(String name, Method method) {
        map.put(name, method);
    }
}
