package com.ruoyi.system.service.impl;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.ruoyi.common.core.utils.SpringUtils;
import com.ruoyi.common.entity.PageData;
import com.ruoyi.common.mp.service.impl.BaseServiceImpl;
import com.ruoyi.system.domain.SysDictData;
import com.ruoyi.system.utils.DictUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.SysDictDataMapper;
import com.ruoyi.system.service.ISysDictDataService;

/**
 * 字典 业务层处理
 * 
 * @author ruoyi
 */
@Service
public class SysDictDataServiceImpl extends BaseServiceImpl<SysDictData, SysDictDataMapper> implements ISysDictDataService
{
    @Autowired
    private DictUtils dictUtils;

    @Override
    public QueryWrapper<SysDictData> getBaseWrapper(SysDictData sysDictType) {
        sysDictType.setOrdseg("dict_sort");
        QueryWrapper<SysDictData> baseWrapper = super.getBaseWrapper(sysDictType);
        return baseWrapper;
    }

    /**
     * 根据条件分页查询字典数据
     *
     * @param dictData 字典数据信息
     * @return 字典数据集合信息
     */
    @Override
    public PageData<SysDictData> selectDictDataPage(SysDictData dictData)
    {
        return SpringUtils.getAopProxy(this).selectPage(dictData);
    }

    /**
     * 根据条件查询字典数据
     *
     * @param dictData 字典数据信息
     * @return 字典数据集合信息
     */
    @Override
    public List<SysDictData> selectDictDataList(SysDictData dictData)
    {
        return SpringUtils.getAopProxy(this).selectList(dictData);
    }

    /**
     * 根据字典数据ID查询信息
     *
     * @param dictCode 字典数据ID
     * @return 字典数据
     */
    @Override
    public SysDictData selectDictDataById(Long dictCode)
    {
        return this.getById(dictCode);
    }

    /**
     * 批量删除字典数据信息
     *
     * @param dictCodes 需要删除的字典数据ID
     */
    @Override
    public void deleteDictDataByIds(Long[] dictCodes)
    {
        for (Long dictCode : dictCodes)
        {
            SysDictData data = selectDictDataById(dictCode);
            this.removeById(dictCode);

            SysDictData queryData = new SysDictData();
            queryData.setDictType(data.getDictType());
            List<SysDictData> dictDatas = this.selectList(queryData);
            dictUtils.setDictCache(data.getDictType(), dictDatas);
        }
    }

    /**
     * 新增保存字典数据信息
     *
     * @param data 字典数据信息
     * @return 结果
     */
    @Override
    public int insertDictData(SysDictData data)
    {
        int row = this.getBaseMapper().insert(data);
        if (row > 0)
        {
            SysDictData queryData = new SysDictData();
            queryData.setDictType(data.getDictType());
            List<SysDictData> dictDatas = this.selectList(queryData);
            dictUtils.setDictCache(data.getDictType(), dictDatas);
        }
        return row;
    }

    /**
     * 修改保存字典数据信息
     *
     * @param data 字典数据信息
     * @return 结果
     */
    @Override
    public int updateDictData(SysDictData data)
    {
        int row = this.getBaseMapper().updateById(data);
        if (row > 0)
        {
            SysDictData queryData = new SysDictData();
            queryData.setDictType(data.getDictType());
            List<SysDictData> dictDatas = this.selectList(queryData);
            dictUtils.setDictCache(data.getDictType(), dictDatas);
        }
        return row;
    }

    @Override
    public boolean countDictDataByType(String dictType) {
        return SqlHelper.retBool(this.getBaseMapper().countDictDataByType(dictType));
    }

    @Override
    public void updateDictDataType(String oldDictType, String newDictType) {
        this.getBaseMapper().updateDictDataType(oldDictType, newDictType);
    }

    @Override
    public List<SysDictData> selectDictDataByType(String dictType) {
        SysDictData queryData = new SysDictData();
        queryData.setDictType(dictType);
        List<SysDictData> dictDatas = this.selectList(queryData);
        return dictDatas;
    }
}
