package com.ruoyi.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.utils.FileUploadUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件请求处理
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/file")
@Slf4j
public class SysFileController
{
    @Value("${file.path}")
    private String localFilePath;

    /**
     * 文件上传请求
     */
    @PostMapping("upload")
    public R<String> upload(MultipartFile file)
    {
        try
        {
            String url = FileUploadUtils.upload(localFilePath, file);
            return R.ok(url);
        }
        catch (Exception e)
        {
            log.error("上传文件失败", e);
            return R.fail(e.getMessage());
        }
    }
}