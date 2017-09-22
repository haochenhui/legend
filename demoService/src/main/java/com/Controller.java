package com;

import com.legend.service.MyRedisConn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by hch on 2017/9/22.
 */
@RestController
public class Controller {
    @Autowired
    MyRedisConn myRedisConn;
    @GetMapping("test")
    public String test(){
        System.out.println(myRedisConn);
        myRedisConn.set("hello","123");
//        myRedisConn.del("hello");
        return "success";
    }
}
