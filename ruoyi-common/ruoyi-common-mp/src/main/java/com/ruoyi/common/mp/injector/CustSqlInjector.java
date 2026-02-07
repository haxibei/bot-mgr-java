package com.ruoyi.common.mp.injector;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.ruoyi.common.mp.injector.method.DeleteDbByIdMethod;
import com.ruoyi.common.mp.injector.method.InsertOrUpdateMethod;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CustSqlInjector extends DefaultSqlInjector  {

    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass, TableInfo tableInfo) {

        // 这里很重要，先要通过父类方法，获取到原有的集合，不然会自带的通用方法会失效的
        List<AbstractMethod> methodList = super.getMethodList(mapperClass, tableInfo);
        // 添加自定义方法类
        methodList.add(new InsertOrUpdateMethod());
        methodList.add(new DeleteDbByIdMethod());
        return methodList;
    }
}
