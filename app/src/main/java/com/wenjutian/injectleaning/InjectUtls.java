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
        injects(activity);
        //非动态代理实现
        // InjectOnClick(activity);
        //动态代理实现
        InjectProxyOnClick(activity);
    }
//    @Target(ElementType.FIELD)
//    @Retention(RetentionPolicy.RUNTIME)
//    public @interface InjectView {
//        int value();
//    }
    // activity.findViewById(id);
    //@InjectView(R.id.text)
    //TextView textView;

    /**
     * 对于注解字段的,是要实现字段的赋值,怎么复制呢?
     * 1.实例化对象
     * 1.1  通过拿字段的注解我们能够拿到id
     * 1.2 findviewById()
     * 2.对象复制
     * 2.1  declaredField.set(activity,viewById);
     *
     * @param activity
     */
    public static void injects(Activity activity) {

        Class<? extends Activity> aClass = activity.getClass();

        Field[] declaredFields = aClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {

            declaredField.setAccessible(true);
            if (declaredField.isAnnotationPresent(InjectView.class)) {
                declaredField.setAccessible(true);
                InjectView annotation = declaredField.getAnnotation(InjectView.class);
                int value = annotation.value();
                View viewById = activity.findViewById(value);//当然也可以是反射
                try {
                    declaredField.set(activity, viewById);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }


            }
        }

    }
    /**
     * 注解事件
     * 原理:我们知道点击事件的监听.是通过 view.setOnclickListener(new onClickListener())的方法实现的,问题是我们怎么调用
     * 注解事件的方法
     * 1.拿到对象View
     * 2.实现点击事件
     *
     * @param activity
     */
    private static void injectOnlick(final Activity activity) {
        Class<? extends Activity> aClass = activity.getClass();
        Method[] declaredMethods = aClass.getDeclaredMethods();
        for (final Method declaredMethod : declaredMethods) {
            {

                OnClick annotation = declaredMethod.getAnnotation(OnClick.class);
                if (annotation != null) {
                    int[] value = annotation.value();
                    for (int j : value) {
                        final View viewById = activity.findViewById(j);

                        viewById.setOnClickListener(new View.OnClickListener() {
                            private Method method;

                            @Override
                            public void onClick(View v) {
                                try {
                                    declaredMethod.invoke(activity, v);
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                } catch (InvocationTargetException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }


            }
        }
    }
    //动态代理实现方式
    /**
     * 1.拿到View对象
     * 2.监听对象我们可以用动态代理实现.通过代理View.OnClickListener对象,当触发onClick事件的时候调用InjectInvocationHandler的Invoke方法,从而我们可以再Invoke方法中调用
     * 我们注解的方法
     *
     * @param activity
     */
    private static void InjectProxyOnClick(Activity activity) {
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

//    private static void injectView(Activity activity) {
//
//        //1.拿到aClass对象
//        Class<? extends Activity> aClass = activity.getClass();
//        //2.我们控件声明一般是字段,那么我们就要拿到字段
//        Field[] declaredFields = aClass.getDeclaredFields();
//        for (Field declaredField : declaredFields) {
//            if (declaredField.isAnnotationPresent(InjectView.class)) {
//                //3 .拿到InjectView注解的值
//                InjectView annotation = declaredField.getAnnotation(InjectView.class);
//                int id = annotation.value();
//                try {
//                    //4.用activity.findViewById方法,返回我们要的那个View
//                    Method findViewById = aClass.getMethod("findViewById", int.class);
//
//                    Object view = findViewById.invoke(activity, id);
//                    declaredField.setAccessible(true);
//                    //然后在给activity的字段赋值
//                    declaredField.set(activity, view);
//                } catch (NoSuchMethodException e) {
//                    e.printStackTrace();
//                } catch (InvocationTargetException e) {
//                    e.printStackTrace();
//                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }
//
//    }
//
}
