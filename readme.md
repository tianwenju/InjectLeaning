# View注入框架简单学习(绑定对象,绑定事件)

## 前言
现在有许多针对View的注解框架,例如Xutils的ViewUtis,解决了令我们厌烦的findViewById对象的初始化,以及绑定OnClick事件的问题,提高了开发效率,那么怎么实现的呢?我们来简单学习一下,自己写个框架,此种方法是运行时的注解,而Buttknife是编译时期的注解.
### 绑定View对象
#### 自定义针对View的注解

对注解不熟悉的同学可以参考
[注解](http://note.youdao.com/noteshare?id=5676f9a95bd7f41a49c92749589c8c32)
```
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface InjectView {
    int value();
}
```
该注解修饰字段,里面有value属性,返回值为int用法如下:
```
   @InjectView(R.id.text)
    TextView textView;
```
#### 针对view的注解解释器
上面我们定义了InjectView注解,但是并没有对注解进行解释,从而进行初始化的操作,所以我们要写注解解释器完成初始化工作。
```java
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
```
分析：我们要解决findViewById(int id),我们首先需要什么
1. 首先是该方法的对象是Activity，那么我们就需要传入一个对象activity，然后调用该方法。
2. 我们还需要资源id,怎么拿id呢，还记得我们的注解吗？我们就是通过I注解拿到Id,怎么拿注解的，我们发现该注解修饰的字段，我们就拿字段，通过遍历字段，拿到相应的id,然后调用
activity的findViewById()方法，拿到View对象。
3. 赋值 我们可以通过Field 的set方法进行赋值
#### 调用
```
injects(this);
```
在onCreate方法中调用该方法。也就是说我们初始化是在这个方法中实现的。
### 绑定事件
#### 针对事件的注解
```
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OnClick {
    int[] value();
}
```
事件一般是方法，那么我们就需要注解方法的注解，又因为点击事件可能有好多view,所以我们需要返回值是个数组，那么我们就可以根据这个注解，拿到相应View的id;
用法如下:
```
@OnClick({R.id.bt_1,R.id.bt_2})
    public void showOnclick(View view) {

        switch (view.getId()) {
            case R.id.bt_1:
                Log.e("自定义标签", "onClick: bt1");
                break;
            case R.id.bt_2:
                Log.e("自定义标签", "onClick: bt2 ");
                break;
        }

    }
```
#### 对事件注解的解释器
1. 不用动态代理
```
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
                // 我们拿到带注解的方法，从而拿到value,然后遍历所有的id,拿到对应的View
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
```
思路：我们绑定点击事件，必定要调用view.setOnClickListener(new onClickListener())方法,那我们需要什么呢？
* view

view怎么拿？我们的注解是注解方法的，注解里面有我们需要的值，那么，我们首先拿注解，通过遍历方法，我们拿到带注解的方法，从而拿到value,然后遍历所有的id,拿到对应的View
* 重写OnClick事件，调用我们注解的方法showOnclick();

我们在OnClick事件中通过重写OnClick事件，调用调用注解的method方法，实现了showOnclick()的调用，也就是反射declaredMethod.invoke(activity, v);

2. 动态代理View.OnClickListener对象，实现对OnClick事件的重写。

对于不懂动态代理的同学，请参考：
[动态代理](http://www.cnblogs.com/xiaoluo501395377/p/3383130.html)

```
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
               ** 通过代理方式实现了一个View.OnclickListener对象，**
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

```
```
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

```
* View 

参考不用代理的方式，实现一样

* 重写OnClick事件，调用我们注解的方法showOnclick();
上面的方法是直接是new onClickListener();而这种是通过代理方式实现了一个View.OnclickListener对象，也就是
>  Object o = Proxy.newProxyInstance(View.OnClickListener.class.getClassLoader(),new Class[]{View.OnClickListener.class},handler)，

这个O就是View.OnclickListener代理对象，也就是说View.OnclickListener对象的所有方法都会被我们的代理对象代理，从而回调用  
```
public Object invoke(Object proxy, Method method, Object[] args) 
``` 

那么我们只需要在这里拦截Onclick方法就行了,InjectInvocationHandler里面有个map集合，我们在初始化该对象的时候
```
InjectInvocationHandler handler = new InjectInvocationHandler(activity);
                handler.add("onClick", declaredMethodd);
```
添加了一个关键字是OnClick ，值是对应的注解方法也就是showOnclick（）方法，在invoke方法中里面有个参数method,上面我们说了，我们的o会代理View.OnclickListener的所有方法，那么method就要其中的一个方法OnClick方法，我们通过
```
String name = method.getName();
            
 Method m = map.get(name);
```
是不是拿到了map里面我们存放的我们要调用的方法也就是showOnclick(View view);那我们通过反射方法调用就可以了
```
m.invoke(target, args)
```
也就实现了我们调用我们注解方法showOnclick(View view)的功能。
#### 绑定事件调用
```
InjectProxyOnClick(this);
```
也就是说我们在这个方法中相当于实现了view.SetonClickListener()方法，重写了OnClick（）方法，调用了我们注解的方法。这也就讲完了。

[源码传送](https://github.com/tianwenju/InjectLeaning)