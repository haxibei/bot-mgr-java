package com.ruoyi.system.service;

import com.ruoyi.common.constant.DictSpace;
import com.ruoyi.common.core.domain.DbModifyMsg;
import com.ruoyi.common.core.utils.SpringUtils;
import com.ruoyi.common.redis.service.AbstractDbMsgReceiver;
import com.ruoyi.web.dict.serialize.DictSerializer;
import com.ruoyi.web.dict.service.DictService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class DbMsgReceiver extends AbstractDbMsgReceiver {

    @Autowired
    private InitService initService;
    @Override
    protected void doMsg(Message message, DbModifyMsg param) {
        log.info("=========> {}", param);
        List<DictSpace> spaces = initService.getSpaces();
        DictService dictService = SpringUtils.getBean("dictService");

        for(DictSpace space : spaces) {
            if(space.getTable().equals(param.getTableName())) {
                log.info("数据变动，刷新字典 {}", space);
                //这里延时 2秒 等数据事务提交
                try {
                    Thread.sleep(2000);
                    dictService.reloadData(space);
                    break;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        DictSpace[] values = DictSpace.values();
        for(DictSpace space : values) {
            if(space.getTable().equals(param.getTableName())) {
                DictSerializer.clearData(space);
            }
        }
    }
}
