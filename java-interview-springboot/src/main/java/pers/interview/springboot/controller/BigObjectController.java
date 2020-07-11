package pers.interview.springboot.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pers.interview.springboot.common.ResponseResult;

import java.util.HashSet;

/**
 * 创建大的对象
 * @description:
 * @author: haochencheng
 * @create: 2020-07-11 00:44
 **/
@RestController
@RequestMapping("/big")
public class BigObjectController {

    private HashSet<byte[]> byteSet=new HashSet<>();

    @GetMapping("/add")
    public ResponseResult addBigObject(){
        // 1m
        byte[] bytes=new byte[1024*1024];
        byteSet.add(bytes);
        return ResponseResult.successful();
    }

}
