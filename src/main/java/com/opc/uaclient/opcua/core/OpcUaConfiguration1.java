package com.opc.uaclient.opcua.core;

import com.opc.uaclient.opcua.exception.OpcUaClientException;
import com.opc.uaclient.opcua.pojo.Relation;
import com.opc.uaclient.opcua.pojo.UaClientPOJO;
import com.opc.uaclient.opcua.util.OpcUaUtil;
import com.opc.uaclient.opcua.util.YamlConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/**
 * 客户端配置入口
 * 版本1.已经配置好的版本
 * @author fujun
 */
@Configuration
@Slf4j
public class OpcUaConfiguration1 {

    @Autowired
    private OpcUaProperties properties;

    @Autowired
    private RetryTemplate retryTemplate;

    @Autowired
    private OpcUaTemplate template;

    @Autowired
    private Subscriber subscriber;

    /**
     * 在构造器中读取配置文件，创建opcUaClientFactory
     */
    @PostConstruct
    public void InitOpcUaConfiguration() throws InterruptedException{
        try {
            ListenerBinder.bind(properties);
        }catch (OpcUaClientException e){
            log.error(e.getMessage());
            return;
        }
        template.getConnection(subscriber);
        //等待连接上所有plc后释放线程池资源
        /**
         * todo:是否应该启用一个线程执行下面步骤？
         */
        OpcUaUtil.latch.await();
        OpcUaUtil.release();
    }

    @PreDestroy
    public void destroy(){
        template.disconnect();
    }

    /**
     * 读取配置文件并且转换为opcUaProperties
     * 配置文件建议放在src目录下（resources下）
     * @return
     */
    @Bean
    public OpcUaProperties convertYAML2Properties() throws Exception{
        OpcUaProperties properties =  YamlConverter.getInstance().readAndConvert("opcua.yml",OpcUaProperties.class);
        List<Map<String, String>>  plcList= properties.getPlcList();
        for (Map<String, String> plc : plcList) {
            ClientCreator.create(plc);
        }
        return properties;
    }


    @Bean
    public OpcUaTemplate createOpcUaClientTemplate(OpcUaProperties properties, RetryTemplate retryTemplate){
        return new OpcUaTemplate(properties,retryTemplate);
    }

    @Bean
    public RetryTemplate createRetryTemplate(){
        // 重试策略
        SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy();
        simpleRetryPolicy.setMaxAttempts(properties.getRetry().getMaxAttempts());
        // 设置间隔策略
        FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
        fixedBackOffPolicy.setBackOffPeriod(properties.getRetry().getBackOffPeriod());
        // 初始化重试模板
        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setBackOffPolicy(fixedBackOffPolicy);
        retryTemplate.setRetryPolicy(simpleRetryPolicy);
        return retryTemplate;
    }

    @Bean
    public Subscriber createUaClientSubscribe(OpcUaTemplate opcUaTemplate){
        return new Subscriber(opcUaTemplate);
    }
}
