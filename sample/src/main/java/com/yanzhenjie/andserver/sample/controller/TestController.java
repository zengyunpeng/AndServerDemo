/*
 * Copyright 2018 Zhenjie Yan.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yanzhenjie.andserver.sample.controller;

import android.util.Log;

import com.yanzhenjie.andserver.annotation.Addition;
import com.yanzhenjie.andserver.annotation.CookieValue;
import com.yanzhenjie.andserver.annotation.FormPart;
import com.yanzhenjie.andserver.annotation.GetMapping;
import com.yanzhenjie.andserver.annotation.PathVariable;
import com.yanzhenjie.andserver.annotation.PostMapping;
import com.yanzhenjie.andserver.annotation.PutMapping;
import com.yanzhenjie.andserver.annotation.RequestBody;
import com.yanzhenjie.andserver.annotation.RequestMapping;
import com.yanzhenjie.andserver.annotation.RequestMethod;
import com.yanzhenjie.andserver.annotation.RequestParam;
import com.yanzhenjie.andserver.annotation.RestController;
import com.yanzhenjie.andserver.http.HttpRequest;
import com.yanzhenjie.andserver.http.HttpResponse;
import com.yanzhenjie.andserver.http.cookie.Cookie;
import com.yanzhenjie.andserver.http.multipart.MultipartFile;
import com.yanzhenjie.andserver.http.session.Session;
import com.yanzhenjie.andserver.sample.App;
import com.yanzhenjie.andserver.sample.component.LoginInterceptor;
import com.yanzhenjie.andserver.sample.model.UserInfo;
import com.yanzhenjie.andserver.sample.util.FileUtils;
import com.yanzhenjie.andserver.sample.util.Logger;
import com.yanzhenjie.andserver.util.MediaType;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Zhenjie Yan on 2018/6/9.
 */
@RestController
@RequestMapping(path = "/user")
class TestController {

    @GetMapping(path = "/get/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    String info(@PathVariable(name = "userId") String userId) {
        return userId;
    }

    @PutMapping(path = "/get/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    String modify(@PathVariable("userId") String userId, @RequestParam(name = "sex") String sex,
                  @RequestParam(name = "age") int age) {
        return String.format(Locale.US, "The userId is %1$s, and the sex is %2$s, and the age is %3$d.", userId, sex,
                age);
    }

    @PostMapping(path = "/login", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    String login(HttpRequest request, HttpResponse response, @RequestParam(name = "account") String account,
                 @RequestParam(name = "password") String password) {
        Session session = request.getValidSession();
        session.setAttribute(LoginInterceptor.LOGIN_ATTRIBUTE, true);

        Cookie cookie = new Cookie("account", account + "=" + password);

        response.addCookie(cookie);
        return "Login successful.";
    }

    @Addition(stringType = "login", booleanType = true)
    @GetMapping(path = "/userInfo", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    UserInfo userInfo(@CookieValue("account") String account) {
        Logger.i("Account: " + account);
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId("123");
        userInfo.setUserName("AndServer");
        return userInfo;
    }

    @PostMapping(path = "/upload", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    String upload(@RequestParam(name = "header") MultipartFile file) throws IOException {
        File localFile = FileUtils.createRandomFile(file);
        //这里替换成写入图片到本地,localFile替换成自己定义的路径
        Log.i("tag", "图片上传接口被调用了");
        File myFile = new File("/storage/emulated/0/testUpload.jpg");
        file.transferTo(myFile);
        return myFile.getAbsolutePath();
    }


    @GetMapping(path = "/consume", consumes = {"application/json", "!application/xml"})
    String consume() {
        return "Consume is successful";
    }

    @GetMapping(path = "/produce", produces = {"application/json; charset=utf-8"})
    String produce() {
        return "Produce is successful";
    }

    @GetMapping(path = "/include", params = {"name=123"})
    String include(@RequestParam(name = "name") String name) {
        return name;
    }

    @GetMapping(path = "/exclude", params = "name!=123")
    String exclude() {
        return "Exclude is successful.";
    }

    @GetMapping(path = {"/mustKey", "/getName"}, params = "name")
    String getMustKey(@RequestParam(name = "name") String name) {
        return name;
    }

    @PostMapping(path = {"/mustKey", "/postName"}, params = "name")
    String postMustKey(@RequestParam(name = "name") String name) {
        return name;
    }

    @GetMapping(path = "/noName", params = "!name")
    String noName() {
        return "NoName is successful.";
    }

    @PostMapping(path = "/formPart")
    UserInfo forPart(@FormPart(name = "user") UserInfo userInfo) {
        return userInfo;
    }

    @PostMapping(path = "/jsonBody")
    UserInfo jsonBody(@RequestBody UserInfo userInfo) {
        return userInfo;
    }

    @PostMapping(path = "/listBody")
    List<UserInfo> jsonBody(@RequestBody List<UserInfo> infoList) {
        return infoList;
    }


    /*自己写的测试代码*/


    @RequestMapping(value = "/uploadTest", method = RequestMethod.POST)
    public String uploadTest(@RequestParam("file") MultipartFile file,
                             @RequestParam(name = "startTime") String startTime,
                             @RequestParam(name = "endTime") String endTime) throws IOException {
        // 判断文件是否为空
        if (file.isEmpty()) {
            return "failed";
        }
        // 获取文件存储路径（绝对路径）
//        String path = request.getServletContext().getRealPath("/WEB-INF/file");
//        System.out.println(path);
//        // 获取文件名称
//        String fileName = file.getOriginalFilename();
//        // 创建文件实例
//        File f = new File(path, fileName);
//        // 判断文件目录是否存在
//        if (!f.getParentFile().exists()) {
//            // 创建目录
//            f.getParentFile().mkdirs();
//            System.out.println("文件已创建");
//        }

        File myFile = new File("/storage/emulated/0/testUpload.jpg");
        // 写入文件
        file.transferTo(myFile);

        Log.i("tag", "startTime: " + startTime);
        Log.i("tag", "endTime: " + endTime);


        return "filesuccess";
    }


}