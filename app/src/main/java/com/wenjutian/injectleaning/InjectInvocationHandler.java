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
    // 拦截的方法名列表,里面存的就是我们注解的方法名字,关键字是OnClick
    private Map<String, Method> map = new HashMap<>();
    //    在这里实际上是MainActivity
    private Object target;

    public InjectInvocationHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (target != null) {
//      获取方法名,因为我们是要截获OnClick方法,所以我们要拿到OnClick方法
            String name = method.getName();
            Log.e("自定义标签", "invoke: " + method.getName());
            Method m = map.get(name);
            //如果method的方法名字是onClick,我们就能取得我们注解的方法,从而直接调用
            Log.e("自定义标签", "invoke: activity" + m.getName());
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
