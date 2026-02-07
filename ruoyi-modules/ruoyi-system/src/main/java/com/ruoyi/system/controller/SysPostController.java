package com.ruoyi.system.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.poi.ExcelUtil;
import com.ruoyi.common.entity.PageData;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.security.annotation.RequiresPermissions;
import com.ruoyi.system.domain.SysPost;
import com.ruoyi.system.service.ISysPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 岗位信息操作处理
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/post")
public class SysPostController
{
    @Autowired
    private ISysPostService postService;

    /**
     * 获取岗位列表
     */
    @RequiresPermissions("system:post:list")
    @GetMapping("/list")
    public R<PageData<SysPost>> list(SysPost post)
    {
        post.setOrdseg("post_sort");
        PageData<SysPost> list = postService.selectPostPage(post);
        return R.ok(list);
    }

    @Log(title = "岗位管理", businessType = BusinessType.EXPORT)
    @RequiresPermissions("system:post:export")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysPost post)
    {
        post.setOrdseg("post_id");
        List<SysPost> list = postService.selectList(post);
        ExcelUtil<SysPost> util = new ExcelUtil<SysPost>(SysPost.class);
        util.exportExcel(response, list, "岗位数据");
    }

    /**
     * 根据岗位编号获取详细信息
     */
    @RequiresPermissions("system:post:query")
    @GetMapping(value = "/{postId}")
    public R getInfo(@PathVariable Long postId)
    {
        return R.ok(postService.selectPostById(postId));
    }

    /**
     * 新增岗位
     */
    @RequiresPermissions("system:post:add")
    @Log(title = "岗位管理", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@Validated @RequestBody SysPost post)
    {
        if (!postService.checkPostNameUnique(post))
        {
            return R.fail("新增岗位'" + post.getPostName() + "'失败，岗位名称已存在");
        }
        else if (!postService.checkPostCodeUnique(post))
        {
            return R.fail("新增岗位'" + post.getPostName() + "'失败，岗位编码已存在");
        }
        return R.ok(postService.insertPost(post));
    }

    /**
     * 修改岗位
     */
    @RequiresPermissions("system:post:edit")
    @Log(title = "岗位管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@Validated @RequestBody SysPost post)
    {
        if (!postService.checkPostNameUnique(post))
        {
            return R.fail("修改岗位'" + post.getPostName() + "'失败，岗位名称已存在");
        }
        else if (!postService.checkPostCodeUnique(post))
        {
            return R.fail("修改岗位'" + post.getPostName() + "'失败，岗位编码已存在");
        }
        return R.ok(postService.updatePost(post));
    }

    /**
     * 删除岗位
     */
    @RequiresPermissions("system:post:remove")
    @Log(title = "岗位管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{postIds}")
    public R remove(@PathVariable Long[] postIds)
    {
        return R.ok(postService.deletePostByIds(postIds));
    }

    /**
     * 获取岗位选择框列表
     */
    @GetMapping("/optionselect")
    public R optionselect()
    {
        List<SysPost> posts = postService.selectPostAll();
        return R.ok(posts);
    }
}
