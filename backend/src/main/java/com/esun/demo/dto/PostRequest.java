package com.esun.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

/**
 * PostRequest 用於接收新增或更新貼文時的請求資料。
 * 支援 multipart/form-data 格式以進行檔案上傳。
 */
@Data
@NoArgsConstructor // 無參數建構子 (Spring 解析 form-data 必要)
@AllArgsConstructor // 全參數建構子
public class PostRequest {

    private String content; // 貼文內容

    /**
     * 貼文配圖檔案。
     * 前端 Postman 的 Key 必須與此變數名稱 "image" 完全一致。
     */
    private MultipartFile image;

}