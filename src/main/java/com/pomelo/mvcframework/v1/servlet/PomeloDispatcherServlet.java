package com.pomelo.mvcframework.v1.servlet;

import com.pomelo.mvcframework.annotation.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

/**
 * @program: FakeSpringFramework
 * @description:
 * @author: zhengxu
 **/
public class PomeloDispatcherServlet extends HttpServlet {
   private Map<String ,Object> mapping = new HashMap<String , Object>();
   private Properties configContext = new Properties();
   private List<String> classNames = new ArrayList<String>();

   @Override
   protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       this.doPost(req,resp);
   }
   
   @Override
   protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException,IOException{
       doDispatch(req,resp);
   }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) {
       String url = req.getRequestURI();
       String contextPath = req.getContextPath();
       url = url.replace(contextPath,"").replaceAll("/+","/");
    }

    @Override
    public void init(ServletConfig config) throws ServletException{
        //加载配置文件
        doLoadConfig(config.getInitParameter("contextConfigLocation"));

        //扫描相关类
        doScanner(configContext.getProperty("scanPackage"));

        //初始化所有相关的类和ioc容器，并且注入到ioc容器中
        doInstance();

        //完成自动依赖注入
        doAutowired();

        //初始化HandlerMapping
        initHandlerMapping();

        System.out.println("Pomelo Spring init");
    }

    private void initHandlerMapping() {
       if(mapping.isEmpty()){return;}

        for (Map.Entry<String,Object> entry : mapping.entrySet()) {
            Class<?> clazz = entry.getValue().getClass();
            if(!clazz.isAnnotationPresent(PomeloController.class)){continue;}

            Method[] methods = clazz.getMethods();
            for(Method method : methods){
                if(!method.isAnnotationPresent(PomeloRequestParam.class)){continue;}

                PomeloRequestMapping requestMapping = method.getAnnotation(PomeloRequestMapping.class);
                requestMapping.value();
            }
        }

    }

    private void doAutowired() {
       if(mapping.isEmpty()){return;}
       for (Map.Entry<String,Object> entry: mapping.entrySet()) {
           Field[] fields = entry.getValue().getClass().getDeclaredFields();
           for (Field field: fields) {
               if(!field.isAnnotationPresent(PomeloAutowired.class)){continue;}

               PomeloAutowired autowired = field.getAnnotation(PomeloAutowired.class);
               String beanName = autowired.value().trim();
               if("".equals(beanName)){
                   beanName = field.getType().getName();
               }

               field.setAccessible(true);

               try {
                   //利用反射给字段字段自动赋值
                   field.set(entry.getValue(),mapping.get(beanName));
               } catch (IllegalAccessException e) {
                   e.printStackTrace();
               }

           }
       }

    }

    private void doScanner(String scanPackage){
       //找所有.class文件，取文件名，拿到所有className
        URL url = this.getClass().getClassLoader().getResource("/" + scanPackage.replace("\\.","/"));
        File classDir = new File(url.getFile());
        for (File file: classDir.listFiles()) {
            if(file.isDirectory()){
                doScanner(scanPackage + "." + file.getName());
            }else {
                if(!file.getName().endsWith(".class")){ continue; }
                //TODO
                String clazzname = scanPackage + "." + file.getName().replace(".class","");
                mapping.put(clazzname,null);
            }
        }
    }

    private void doLoadConfig(String contextConfigLocation)  {
        InputStream is = null;
        try{
            is = this.getClass().getResourceAsStream(contextConfigLocation);
            this.configContext.load(is);
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if (is!=null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doInstance(){
        if(classNames.isEmpty()){return;}

        try{
            for(String className : classNames){
                Class<?> clazz = Class.forName(className);
                if(clazz.isAnnotationPresent(PomeloController.class)){
                    String beanName = toLoweFirstCase(clazz.getSimpleName());
                    Object instance = clazz.newInstance();
                    mapping.put(beanName,instance);
                }else if(clazz.isAnnotationPresent(PomeloService.class)){
                    //默认小字母小写
                    String beanName = toLoweFirstCase(clazz.getSimpleName());
                    //采用自定义命名 alise
                    PomeloService service = clazz.getAnnotation(PomeloService.class);
                    if("".equals(service.value().trim())){
                        beanName = service.value();
                    }

                    Object instance = clazz.newInstance();
                    mapping.put(beanName,instance);

                    //采用类型命名
                    for(Class<?> i : clazz.getInterfaces()){
                        if(mapping.containsKey(i.getName())){
                            throw new Exception("The beanName is exists !!");
                        }
                        mapping.put(i.getName(),instance);
                    }
                }else{
                    continue;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private String toLoweFirstCase(String simpleName) {
       char[] chars = simpleName.toCharArray();
       chars[0] += 32;
       return String.valueOf(chars);
    }
}
