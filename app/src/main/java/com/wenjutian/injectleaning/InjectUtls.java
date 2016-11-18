package com.wenjutian.injectleaning;

import android.app.Activity;
import android.view.View;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by V.Wenju.Tian on 2016/11/18.
 */

public class InjectUtls {


    public static void inject(Activity activity) {
        injectView(activity);
        InjectOnClick(activity);
    }

    private static void InjectOnClick(Activity activity) {

        Class<? extends Activity> aClass = activity.getClass();
        Method[] declaredMethods = aClass.getDeclaredMethods();
        for (Method declaredMethodd : declaredMethods) {
            //1.拿到带注解的onclick事件,并取得先关值
            OnClick aOnClick = declaredMethodd.getAnnotation(OnClick.class);
            if (aOnClick != null) {
                int[] ids = (int[]) aOnClick.value();
                //2.生成一个Onclick的代理类
                InjectInvocationHandler handler = new InjectInvocationHandler(activity);
                handler.add("onClick", declaredMethodd);
                Object o = Proxy.newProxyInstance(View.OnClickListener.class.getClassLoader(), new Class[]{View.OnClickListener.class}, handler);
                for (int id : ids) {
                    //3.拿到对应的控件
                    View view = activity.findViewById(id);
                    try {
                        //4.通过控件拿到方法
                        Method listener = view.getClass().getMethod("setOnClickListener", View.OnClickListener.class);
                        listener.setAccessible(true);
                        //5方法调用
                        listener.invoke(view, o);
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }

                }
            }

        }


    }

    private static void injectView(Activity activity) {

        //1.拿到aClass对象
        Class<? extends Activity> aClass = activity.getClass();
        //2.我们控件声明一般是字段,那么我们就要拿到字段
        Field[] declaredFields = aClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            if (declaredField.isAnnotationPresent(InjectView.class)) {
                //3 .拿到InjectView注解的值
                InjectView annotation = declaredField.getAnnotation(InjectView.class);
                int id = annotation.value();
                try {
                    //4.用activity.findViewById方法,返回我们要的那个View
                    Method findViewById = aClass.getMethod("findViewById", int.class);

                    Object view = findViewById.invoke(activity, id);
                    declaredField.setAccessible(true);
                    //然后在给activity的字段赋值
                    declaredField.set(activity, view);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

            }
        }

    }

    private static void injectViews(Activity activity) {

    }
}
