package com.ruoyi.db.service.impl;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.core.utils.SpringUtils;
import com.ruoyi.common.mp.service.impl.BaseServiceImpl;
import com.ruoyi.config.BotClientConfig;
import com.ruoyi.db.domain.DataInfo;
import com.ruoyi.db.domain.DomainInfo;
import com.ruoyi.db.mapper.DataInfoMapper;
import com.ruoyi.db.service.IDataInfoService;
import com.ruoyi.db.service.IDomainInfoService;
import com.ruoyi.handlers.handlerImpl.DataInfoHandler;
import com.ruoyi.model.DataScore;
import com.ruoyi.model.RemoteResp;
import com.ruoyi.model.RemoteRowData;
import com.ruoyi.services.BotConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 充值订单Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-06-09
 */
@Service
@Slf4j
public class DataInfoServiceImpl extends BaseServiceImpl<DataInfo, DataInfoMapper> implements IDataInfoService
{
    @Autowired
    private IDomainInfoService domainInfoService;

    @Autowired
    private RestTemplate appRestTemplate;

    @Autowired
    private BotConfigService botConfigService;

    @Autowired
    private BotClientConfig botClientConfig;

    @Override
    public void spiderData(String date) {
        List<DomainInfo> list = domainInfoService.list();
        for (DomainInfo domainInfo : list) {
            CompletableFuture.runAsync(() -> {
                List<DataInfo> dataInfos = loadRemoteData(domainInfo, "", date);
                log.info(domainInfo.getDomain() + ":" + date + "数据数量 " + dataInfos.size());
                if(CollectionUtils.isNotEmpty(dataInfos)) {
                    for(DataInfo dataInfo : dataInfos) {
                        dataInfo.setBotId(domainInfo.getBotId());
                        insertOrUpdate(dataInfo);
                    }
                }
            });
        }
    }

    @Override
    public List<DataInfo> loadRemoteData(DomainInfo domainInfo, String agentCode, String date) {
        String reqUrl = domainInfo.getReqUrl();
        int pageSize = botConfigService.getRemotePageSize();
        reqUrl = String.format(reqUrl, pageSize, date, date, agentCode == null?"":agentCode);
        log.info("req url is {}", reqUrl);
        HttpHeaders headers = new HttpHeaders();

        Map<String, String> headerMap = JSON.parseObject(domainInfo.getHeader(), HashMap.class);
        for(String key : headerMap.keySet()) {
            headers.add(key, headerMap.get(key));
        }
        List<DataInfo> dataInfos = new ArrayList<DataInfo>();
        try {
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<RemoteResp> resp = appRestTemplate.exchange(
                    reqUrl,
                    HttpMethod.GET,
                    entity,
                    RemoteResp.class
            );
            RemoteResp remote = resp.getBody();
//            String d = "{\"success\":true,\"value\":{\"list\":[],\"footer\":{\"regDate\":null,\"agentName\":null,\"registerCount\":26,\"depositCount\":9,\"depositRate\":35,\"notDepositCount\":17,\"notDepositRate\":65,\"deposit2Count\":1,\"deposit2Rate\":11,\"notDeposit2Count\":8},\"total\":26,\"totalPages\":1}}";
//            RemoteResp remote = JSON.parseObject(d, RemoteResp.class);
            if(remote.invalidTk()) {//登录已经失效
                String mgrId = botConfigService.getMgrUserId();
                SendMessage msg = new SendMessage(mgrId, domainInfo.getDomain() + "登录已失效, 请及时操作，避免数据获取失败！");
                try {
                    TelegramClient client = botClientConfig.getClient(domainInfo.getBotId());
                    client.execute(msg);
                } catch (TelegramApiException e) {
                    log.error("send invalid tk msg err", e);
                }
            }else{
                if(remote.getValue().getTotal() > 0) {
                    if(StringUtils.isNotBlank(agentCode)) {
                        RemoteRowData rowData = remote.getValue().getFooter();
                        DataInfo dataInfo = new DataInfo();
                        dataInfo.setDataDate(date);
                        dataInfo.setAgentCode(agentCode);
                        dataInfo.setRegisterNum(rowData.getRegisterCount());
                        dataInfo.setEffectiveNum(rowData.getDepositCount());
                        dataInfo.setRepeatNum(rowData.getDeposit2Count());
                        dataInfo.setDomain(domainInfo.getDomain());
                        dataInfo.setBotId(domainInfo.getBotId());
                        dataInfos.add(dataInfo);
                    }else {
                        for(RemoteRowData rowData : remote.getValue().getList()) {
                            DataInfo dataInfo = new DataInfo();
                            dataInfo.setDataDate(date);
                            dataInfo.setAgentCode(rowData.getAgentName());
                            dataInfo.setRegisterNum(rowData.getRegisterCount());
                            dataInfo.setEffectiveNum(rowData.getDepositCount());
                            dataInfo.setRepeatNum(rowData.getDeposit2Count());
                            dataInfo.setDomain(domainInfo.getDomain());
                            dataInfo.setBotId(domainInfo.getBotId());
                            dataInfos.add(dataInfo);
                        }
                    }
                }
            }
        } catch (Exception e) {
            String mgrId = botConfigService.getMgrUserId();
            SendMessage msg = new SendMessage(mgrId, domainInfo.getDomain() + " 数据获取失败, 请尽快处理！");
            try {
                TelegramClient client = botClientConfig.getClient(domainInfo.getBotId());
                client.execute(msg);
            } catch (TelegramApiException e1) {
                log.error("send exception tk msg err", e1);
            }
            log.error("get data err", e);

        }
        return dataInfos;
    }

    @Override
    public void refreshTk(String date) {
        List<DomainInfo> list = domainInfoService.list();
        for (DomainInfo domainInfo : list) {
            CompletableFuture.runAsync(() -> {
                loadRemoteRefresh(domainInfo, date);
            });
        }
    }

    private void loadRemoteRefresh(DomainInfo domainInfo, String date) {
        String reqUrl = domainInfo.getReqUrl();
        reqUrl = String.format(reqUrl, 1, date, date, "");
        log.info("refresh url is {}", reqUrl);
        HttpHeaders headers = new HttpHeaders();

        Map<String, String> headerMap = JSON.parseObject(domainInfo.getHeader(), HashMap.class);
        for(String key : headerMap.keySet()) {
            headers.add(key, headerMap.get(key));
        }
        HttpEntity<String> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<RemoteResp> resp = appRestTemplate.exchange(
                    reqUrl,
                    HttpMethod.GET,
                    entity,
                    RemoteResp.class
            );
            RemoteResp remote = resp.getBody();
            if(remote.invalidTk()) {//登录已经失效
                String mgrId = botConfigService.getMgrUserId();
                SendMessage msg = new SendMessage(mgrId, domainInfo.getDomain() + "登录已失效, 请及时操作，避免数据获取失败！");
                try {
                    TelegramClient client = botClientConfig.getClient(domainInfo.getBotId());
                    client.execute(msg);
                } catch (TelegramApiException e) {
                    log.error("send invalid tk msg err", e);
                }
            }else{

            }
        } catch (Exception e) {
            String mgrId = botConfigService.getMgrUserId();
            SendMessage msg = new SendMessage(mgrId, domainInfo.getDomain() + " 刷新tk失败, 请尽快处理！");
            try {
                TelegramClient client = botClientConfig.getClient(domainInfo.getBotId());
                client.execute(msg);
            } catch (TelegramApiException e1) {
                log.error("send exception tk msg err", e1);
            }
            log.error("get data err", e);
        }
    }

    @Override
    public void pushDataMsg(String date) {

        List<DataInfo> dataInfos = this.getBaseMapper().loadPushData(date);

        if(CollectionUtils.isNotEmpty(dataInfos)) {
            Map<Long, List<DataInfo>> collect = dataInfos.stream().collect(Collectors.groupingBy(DataInfo::getUserId));

            DataInfoHandler handler = SpringUtils.getBean(DataInfoHandler.class);

            for (Long userId : collect.keySet()) {
                try {
                    handler.sendStatMsg(userId, date, collect.get(userId));
                } catch (Exception e) {
                    log.error("发送统计消息失败, "+ date+" : "+ userId, e);
                }
            }
        }else {
            log.info("没有需要发送统计消息的数据");
        }
    }

    @Override
    public List<DataScore> getConvertConfig(String botId) {
        return this.getBaseMapper().getConvertConfig(botId);
    }

    @Override
    public List<DataScore> getRenewConfig(String botId) {
        return this.getBaseMapper().getRenewConfig(botId);
    }
}
