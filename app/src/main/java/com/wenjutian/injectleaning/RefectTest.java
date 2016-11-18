package com.wenjutian.injectleaning;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by V.Wenju.Tian on 2016/11/18.
 */

public class RefectTest {
    private static String invoke;

    public static void main(String[] args) {

        People people = new People();
        people.setName("sdff");
        Class<? extends People> aClass = people.getClass();
        try {

            /**
             * 方法参数为空的情况
             */
            Method getName = aClass.getMethod("getName",null);
            invoke = ((String) getName.invoke(people, null));
            System.out.println(invoke);

            /**
             * 方法参数为多个的情况
             */
            Method setName = aClass.getMethod("setName",new Class[]{String.class, int.class});
            setName.invoke(people,new Object[]{"zzzz",3});
            invoke = ((String) getName.invoke(people, null));
            System.out.println(invoke);

            /**
             * method 获取方法的参数
             *
             */
            Class<?>[] parameterTypes = setName.getParameterTypes();
            for (Class<?> parameterType : parameterTypes) {
                System.out.println(parameterType.getName());

            }

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
