package com.pomelo.demo;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: FakeSpringFramework
 * @description: 测试
 * @author: zhengxu
 **/

public class Test {
    @org.junit.Test
    public void text(){
         Map<String ,Object> mapping = new HashMap<String , Object>();
        InputStream is = null;
        is = this.getClass().getResourceAsStream("/application.properties");
        System.out.println(is);
        String str = "/" + "com.pomelo.demo".replace(".","/");
        System.out.println(str);
        System.out.println("\\.");
        mapping.put("com.pomelo.demo.mvc.action.DemoAction",null);
        //mapping.entrySet().getValue().getClass().getDeclaredFields()
    }
}
