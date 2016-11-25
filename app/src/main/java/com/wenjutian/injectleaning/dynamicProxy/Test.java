package com.wenjutian.injectleaning.dynamicProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by V.Wenju.Tian on 2016/11/25.
 */

public class Test {
    public static void main(String[] args) {

        Object o = Proxy.newProxyInstance(ISeller.class.getClassLoader(), new Class[]{ISeller.class}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                System.out.println(method.getName()+proxy.getClass().getName());
                for (Object arg : args) {
                    if(arg instanceof String)
                    System.out.println(((String) arg));
                }
                return null;
            }
        });
        System.out.println(o.getClass().getName());
        if(o instanceof ISeller){
            ((ISeller) o).buy("sdfsdfsdf");
        }
    }


}
