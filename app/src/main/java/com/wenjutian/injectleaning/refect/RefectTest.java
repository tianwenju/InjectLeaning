package com.wenjutian.injectleaning.refect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by V.Wenju.Tian on 2016/11/18.
 */

public class RefectTest {
    private static String invoke;
    private static PAnnotion pAnnotion;

    public static void main(String[] args) {

        People people = new People();
        people.setName("sdff");
        Class<? extends People> aClass = people.getClass();
        try {
            /**
             * 类名
             */
            aClass.getName();
            aClass.getSimpleName();
            /**
             * 无参数的构造方法
             */
            Constructor<? extends People> constructor = aClass.getConstructor(null);
            People people1 = constructor.newInstance(null);
            people1.setName("dsf");
            System.out.println(people1.getName());

            /**
             * 有参数的
             */
            Constructor<? extends People> constructor1 = aClass.getConstructor(String.class, int.class);
            //可变参数最后在编译后，会变成数组，所以也可以传数组
            //Constructor<? extends People> constructor2 = aClass.getConstructor(new Class[]{String.class, int.class});
            constructor1.newInstance("王五", 100);
            System.out.println("有参数的构造方法" + constructor1);
            /**
             * 实现的接口
             */

            Class<?>[] interfaces = aClass.getInterfaces();
            System.out.println("people的接口" + interfaces.length);
            for (Class<?> anInterface : interfaces) {
                System.out.println("people的接口" + anInterface.getName());
            }

            /**
             * 获取公共字段
             */

            Field[] fields = aClass.getFields();
            for (Field field : fields) {
                System.out.println("字段" + field.getName());
            }
            Field nameField = aClass.getField("name");
            nameField.set(people, "liming");
            nameField.get(people);

            /**
             * 获取非公有字段
             */
            Field[] declaredFields = aClass.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                System.out.println("所有字段" + declaredField.getName());
            }
            Field idField = aClass.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(people, 33);
            idField.get(people);
            System.out.println("id字段" + people.getId());
            /**
             *
             */
            Method[] methods = aClass.getMethods();
            for (Method method : methods) {
                System.out.println("公共方法" + method.getName());
            }

            /**
             * 方法参数为空的情况
             */
            Method getName = aClass.getMethod("getName", null);
            invoke = ((String) getName.invoke(people, null));
            System.out.println(invoke);


            /**
             * 方法参数为多个的情况
             */
            Method setName = aClass.getMethod("setName", new Class[]{String.class, int.class});
            aClass.getMethod("setName", String.class, int.class);
            setName.invoke(people, new Object[]{"zzzz", 3});
            //setName.invoke(people,"sdfd",4);
            invoke = ((String) getName.invoke(people, null));
            Method getId = aClass.getMethod("getId", null);
            int id = (int) getId.invoke(people, null);

            System.out.println("方法参数为多个的情况" + invoke + id);

            /**
             * method 获取方法的参数
             *
             */
            Class<?>[] parameterTypes = setName.getParameterTypes();
            for (Class<?> parameterType : parameterTypes) {
                System.out.println(parameterType.getName());

            }
            Method[] declaredMethods = aClass.getDeclaredMethods();
            for (Method declaredMethod : declaredMethods) {
                declaredMethod.setAccessible(true);
                System.out.println("在此类中所有的方法" + declaredMethod.getName());
            }

            Method buyMethod = aClass.getDeclaredMethod("buy", String.class);
            buyMethod.setAccessible(true);
            buyMethod.invoke(people, "bananer");

            Class<?> superclass = aClass.getSuperclass();
            Method eat = superclass.getDeclaredMethod("eat", null);
            eat.setAccessible(true);
            eat.invoke(people, null);

            /**
             * 注解
             */

            Annotation[] annotations = aClass.getAnnotations();

            CAnnotation annotation = aClass.getAnnotation(CAnnotation.class);
            if (annotation != null) {
                System.out.println("类上的注解" + annotation.age() + annotation.name());
            }

            Field field = aClass.getField("name");
            FAnnotaion fannotation = field.getAnnotation(FAnnotaion.class);
            if (fannotation != null) {
                System.out.println(fannotation.name());
                field.set(people, fannotation.name());
            }

            Method msetName = aClass.getMethod("setName", String.class);
            MAnnotaion mannotion = msetName.getAnnotation(MAnnotaion.class);
            if (mannotion != null) {
                msetName.invoke(people, mannotion.name());
                System.out.println("method的注解"+people.getName());
            }else{
                System.out.println("method的注解为空");
            }
            /**
             * 第一个坐标代表有几个参数，第二个坐标代表一个参数有几个注解
             */
            Annotation[][] parameterAnnotations = msetName.getParameterAnnotations();
            for (int i = 0; i < parameterAnnotations.length; i++) {
                Annotation[] parameterAnnotation = parameterAnnotations[i];
                for (int j = 0; j < parameterAnnotation.length; j++) {
                    if (parameterAnnotation[j] != null) {

                        if (parameterAnnotation[j] instanceof PAnnotion) {
                            pAnnotion = ((PAnnotion) parameterAnnotation[j]);
                            msetName.invoke(people, pAnnotion.name());
                            System.out.println("参数注解"+people.getName());
                        }
                    }
                }
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
