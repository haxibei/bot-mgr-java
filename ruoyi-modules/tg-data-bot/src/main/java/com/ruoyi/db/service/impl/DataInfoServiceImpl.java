package com.ruoyi.db.service.impl;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.core.utils.SpringUtils;
import com.ruoyi.common.mp.service.impl.BaseServiceImpl;
import com.ruoyi.config.BotClientConfig;
import com.ruoyi.db.domain.AgentBindInfo;
import com.ruoyi.db.domain.DataInfo;
import com.ruoyi.db.domain.DomainInfo;
import com.ruoyi.db.mapper.DataInfoMapper;
import com.ruoyi.db.service.IAgentBindInfoService;
import com.ruoyi.db.service.IDataInfoService;
import com.ruoyi.db.service.IDomainInfoService;
import com.ruoyi.handlers.handlerImpl.DataInfoHandler;
import com.ruoyi.model.*;
import com.ruoyi.services.BotConfigService;
import com.ruoyi.utils.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.time.LocalDate;
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

    @Autowired
    private IAgentBindInfoService agentBindInfoService;

    @Override
    public void spiderData(String date) {
        List<DomainInfo> list = domainInfoService.list();
        for (DomainInfo domainInfo : list) {

            CompletableFuture.runAsync(() -> {
                spiderDomainAllData(domainInfo, date);
            });

            //是否需要获取1级代理的数据
//            if(domainInfo.getNeedAgentLevel1() == 1) {
//                CompletableFuture.runAsync(() -> {
//                    spiderDomainSubData(domainInfo, date);
//                });
//            }
        }
    }

    @Override
    public void spiderDomainSubData(DomainInfo domainInfo, String date) {
        AgentBindInfo query = new AgentBindInfo();
        query.setDomain(domainInfo.getDomain());
        query.setLevel(0);//顶级代理
        List<AgentBindInfo> agents = agentBindInfoService.selectList(query);//代理

//        if(CollectionUtils.isNotEmpty(agents)) {
//            agents.forEach(agentBindInfo -> {
//                List<DataInfo> dataInfos = loadRemoteSubDataLevel1(domainInfo, agentBindInfo.getAgentCode(), date);
//                log.info(domainInfo.getDomain()  + "获取代理" + agentBindInfo.getAgentCode()+ ":" + date + "对应下级代理的数据数量 " + dataInfos.size());
//                if(CollectionUtils.isNotEmpty(dataInfos)) {
//                    for(DataInfo dataInfo : dataInfos) {
//                        dataInfo.setLevel(1);
//                        dataInfo.setParentAgentCode(agentBindInfo.getAgentCode());
//                        dataInfo.setBotId(domainInfo.getBotId());
//                        insertOrUpdate(dataInfo);
//                    }
//                }
//            });
//        }
    }

    @Override
    public void spiderDomainAllData(DomainInfo domainInfo, String date) {
        List<DataInfo> dataInfos = loadRemoteData(domainInfo, "", date);
        log.info(domainInfo.getDomain() + ":" + date + "数据数量 " + dataInfos.size());
        if(CollectionUtils.isNotEmpty(dataInfos)) {
            for(DataInfo dataInfo : dataInfos) {
                dataInfo.setBotId(domainInfo.getBotId());
                insertOrUpdate(dataInfo);
            }
        }
    }

    @Override
    public List<DataInfo> loadRemoteDataLevel1(DomainInfo domainInfo, String agentCode, String date) {
        //有下级的代理code 就使用 查询当前的 代理数据  否则是查询 下级所有的代理数据
        String reqUrl = domainInfo.getReqLv1Url1();
        int pageSize = botConfigService.getRemotePageSize();

        boolean isMonth = DateUtils.isMonth(date);
        if(isMonth) {
            reqUrl = reqUrl.replace("dateOption=DATE", "dateOption=MONTH");
        }
        reqUrl = String.format(reqUrl, pageSize, date, date, agentCode);
        log.info("req lv1 url is {}", reqUrl);
        HttpHeaders headers = new HttpHeaders();

        Map<String, String> headerMap = JSON.parseObject(domainInfo.getHeader(), HashMap.class);
        for(String key : headerMap.keySet()) {
            headers.add(key, headerMap.get(key));
        }
        List<DataInfo> dataInfos = new ArrayList<DataInfo>();
        try {
            HttpEntity<String> entity = new HttpEntity<>(headers);

            RemoteRowData remoteData = getRowData(domainInfo, reqUrl, entity);
            if(remoteData != null) {
                //查询单个代理的时候  查询一次 留存数据
                RemoteConvertData convertData = null;
                if(!isMonth) {
                    reqUrl = String.format(domainInfo.getReqLv1Url3(), date, date, agentCode);
                    log.info("req lv1 convert url is {}", reqUrl);
                    convertData = getConvertData(domainInfo, reqUrl, entity);
                }

                DataInfo dataInfo = new DataInfo();
                dataInfo.setDataDate(date);
                dataInfo.setAgentCode(remoteData.getAgentName().replace(domainInfo.getMerchantCode()+"@", ""));
                dataInfo.setRegisterNum(remoteData.getRegisterCount());

                //下级代理以 首充作为转换人数
                Integer firstDepositCount = remoteData.getFirstDepositCount();
                dataInfo.setEffectiveNum(firstDepositCount);

//                Integer depositCount = remoteData.getDepositCount();
//                int repeatCnt = (depositCount == null?0:depositCount) - (firstDepositCount == null?0:firstDepositCount);

                dataInfo.setRepeatNum(convertData == null?0:convertData.getRetentionCount());
                dataInfo.setDomain(domainInfo.getDomain());
                dataInfo.setBotId(domainInfo.getBotId());
                dataInfo.setLevel(1);

                dataInfos.add(dataInfo);

            }
        } catch (Exception e) {
            String mgrId = botConfigService.getMgrUserId();
            SendMessage msg = new SendMessage(mgrId, domainInfo.getDomain() + " 下级代理数据获取失败, 请尽快处理！");
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

    private RemoteConvertData getConvertData(DomainInfo domainInfo, String reqUrl, HttpEntity<String> entity) {
        RemoteConvertData remoteData = null;
        ResponseEntity<RemoteResp<RemoteConvertData>> resp = appRestTemplate.exchange(
                reqUrl,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<RemoteResp<RemoteConvertData>>() {}
        );
        RemoteResp<RemoteConvertData> remote = resp.getBody();
        if(remote.invalidTk()) {//登录已经失效
            notifyInvalidTk(domainInfo);
        }else {
            List<RemoteConvertData> datas = remote.getValue().getList();
            if(CollectionUtils.isNotEmpty(datas)) {
                remoteData = datas.get(0);
            }
        }
        return remoteData;
    }

    private RemoteRowData getRowData(DomainInfo domainInfo, String reqUrl, HttpEntity<String> entity) {
        RemoteRowData remoteData = null;
        ResponseEntity<RemoteLv1Resp> resp = appRestTemplate.exchange(
                reqUrl,
                HttpMethod.GET,
                entity,
                RemoteLv1Resp.class
        );
        RemoteLv1Resp remote = resp.getBody();
        if(remote.invalidTk()) {//登录已经失效
            notifyInvalidTk(domainInfo);
        }else {
            remoteData = remote.getValue();
        }
        return remoteData;
    }

//    @Override
//    public List<DataInfo> loadRemoteSubDataLevel1(DomainInfo domainInfo, String agentCode, String date) {
//        String reqUrl = domainInfo.getReqLv1Url2();
//        int pageSize = botConfigService.getRemotePageSize();
//
//        if(DateUtils.isMonth(date)) {
//            reqUrl = reqUrl.replace("dateOption=DATE", "dateOption=MONTH");
//        }
//        reqUrl = String.format(reqUrl, pageSize, date, date, agentCode);
//        log.info("req lv1 url is {}", reqUrl);
//        HttpHeaders headers = new HttpHeaders();
//
//        Map<String, String> headerMap = JSON.parseObject(domainInfo.getHeader(), HashMap.class);
//        for(String key : headerMap.keySet()) {
//            headers.add(key, headerMap.get(key));
//        }
//        List<DataInfo> dataInfos = new ArrayList<DataInfo>();
//        try {
//            HttpEntity<String> entity = new HttpEntity<>(headers);
//
//            List<RemoteRowData> remoteData = null;
//            ResponseEntity<RemoteResp> resp = appRestTemplate.exchange(
//                    reqUrl,
//                    HttpMethod.GET,
//                    entity,
//                    RemoteResp.class
//            );
//            RemoteResp remote = resp.getBody();
//
//            if(remote.invalidTk()) {//登录已经失效
//                notifyInvalidTk(domainInfo);
//            }else {
//                remoteData = remote.getValue().getList();
//            }
//
//            if(CollectionUtils.isNotEmpty(remoteData)) {
//                for(RemoteRowData rowData : remoteData) {
//                    DataInfo dataInfo = new DataInfo();
//                    dataInfo.setDataDate(date);
//                    dataInfo.setAgentCode(rowData.getAgentName().replace(domainInfo.getMerchantCode()+"@", ""));
//                    dataInfo.setRegisterNum(rowData.getRegisterCount());
//
//                    //下级代理以 首充作为转换人数
//                    Integer firstDepositCount = rowData.getFirstDepositCount();
//                    dataInfo.setEffectiveNum(firstDepositCount);
//
//                    Integer depositCount = rowData.getDepositCount();
//                    int repeatCnt = (depositCount == null?0:depositCount) - (firstDepositCount == null?0:firstDepositCount);
//
//                    dataInfo.setRepeatNum(Math.max(repeatCnt, 0));
//                    dataInfo.setDomain(domainInfo.getDomain());
//                    dataInfo.setBotId(domainInfo.getBotId());
//                    dataInfo.setParentAgentCode(parentAgentCode);
//                    dataInfo.setLevel(1);
//                    dataInfos.add(dataInfo);
//                }
//            }
//        } catch (Exception e) {
//            String mgrId = botConfigService.getMgrUserId();
//            SendMessage msg = new SendMessage(mgrId, domainInfo.getDomain() + " 下级代理数据获取失败, 请尽快处理！");
//            try {
//                TelegramClient client = botClientConfig.getClient(domainInfo.getBotId());
//                client.execute(msg);
//            } catch (TelegramApiException e1) {
//                log.error("send exception tk msg err", e1);
//            }
//            log.error("get data err", e);
//
//        }
//        return dataInfos;
//    }

    private void notifyInvalidTk(DomainInfo domainInfo) {
        String mgrId = botConfigService.getMgrUserId();
        SendMessage msg = new SendMessage(mgrId, domainInfo.getDomain() + "登录已失效, 请及时操作，避免数据获取失败！");
        try {
            TelegramClient client = botClientConfig.getClient(domainInfo.getBotId());
            client.execute(msg);
        } catch (TelegramApiException e) {
            log.error("send invalid tk msg err", e);
        }
    }

    @Override
    public List<DataInfo> loadRemoteData(DomainInfo domainInfo, String agentCode, String date) {
        String reqUrl = domainInfo.getReqUrl();
        int pageSize = botConfigService.getRemotePageSize();

        String startDate = date, endDate = date;
        if(DateUtils.isMonth(date)) {
            startDate = startDate + "-01";
            endDate = endDate + "-" +LocalDate.parse(startDate).getDayOfMonth();
        }

        reqUrl = String.format(reqUrl, pageSize, startDate, endDate, agentCode == null?"":agentCode);
        log.info("req url is {}", reqUrl);
        HttpHeaders headers = new HttpHeaders();

        Map<String, String> headerMap = JSON.parseObject(domainInfo.getHeader(), HashMap.class);
        for(String key : headerMap.keySet()) {
            headers.add(key, headerMap.get(key));
        }
        List<DataInfo> dataInfos = new ArrayList<DataInfo>();
        try {
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<RemoteResp<RemoteRowData>> resp = appRestTemplate.exchange(
                    reqUrl,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<RemoteResp<RemoteRowData>>() {}
            );
            RemoteResp<RemoteRowData> remote = resp.getBody();
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

    @Override
    public List<AgentBindInfo> getAllAgentCode(String domain) {
        return this.getBaseMapper().getAllAgentCode(domain, 0);
    }
}
