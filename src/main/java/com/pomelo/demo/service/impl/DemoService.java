package com.pomelo.demo.service.impl;

import com.pomelo.demo.service.IDemoService;
import com.pomelo.mvcframework.annotation.PomeloService;

/**
 * @program: FakeSpringFramework
 * @description: 核心业务逻辑
 * @author: zhengxu
 **/
@PomeloService
public class DemoService implements IDemoService {
    @Override
    public String get(String name) {
        return "My name is " + name;
    }
}
