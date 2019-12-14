package com.pomelo.demo.mvc.action;

import com.pomelo.demo.service.IDemoService;
import com.pomelo.mvcframework.annotation.PomeloAutowired;
import com.pomelo.mvcframework.annotation.PomeloController;
import com.pomelo.mvcframework.annotation.PomeloRequestMapping;
import com.pomelo.mvcframework.annotation.PomeloRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @program: FakeSpringFramework
 * @description: 配置请求入口
 * @author: zhengxu
 **/
@PomeloController
@PomeloRequestMapping("/demo")
public class DemoAction {
    @PomeloAutowired
    private IDemoService demoService;

    public void query(HttpServletRequest req, HttpServletResponse resp, @PomeloRequestParam("name") String name){
        String result = demoService.get(name);
        try{
            resp.getWriter().write(result);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @PomeloRequestMapping("/add")
    public void add(HttpServletRequest req, HttpServletResponse resp,@PomeloRequestParam("a") Integer a,@PomeloRequestParam("b") Integer b){
        try {
            resp.getWriter().write(a + "a" + "b" + "=" + ( a + b ));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PomeloRequestMapping("/remove")
    public void remove(HttpServletRequest req, HttpServletResponse resp, @PomeloRequestParam("id") Integer id){

    }

}
