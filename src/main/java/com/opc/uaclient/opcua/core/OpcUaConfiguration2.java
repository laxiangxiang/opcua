package com.opc.uaclient.opcua.core;

import com.opc.uaclient.opcua.exception.OpcUaClientException;
import com.opc.uaclient.opcua.pojo.Relation;
import com.opc.uaclient.opcua.pojo.UaClientPOJO;
import com.opc.uaclient.opcua.util.OpcUaUtil;
import com.opc.uaclient.opcua.util.YamlConverter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * 客户端配置入口
 * 版本2：未配置，把当前类配置到spring容器中即可
 * @author fujun
 */
@Slf4j
@Data
public class OpcUaConfiguration2 {

    private Relation relation = Relation.getInstance();

    private OpcUaProperties properties;

    private RetryTemplate retryTemplate;

    private OpcUaTemplate opcUaTemplate;

    private Subscriber subscriber;

    /**
     * 在构造器中读取配置文件，创建opcUaClientFactory
     */
    @PostConstruct
    public void InitOpcUaConfiguration() {
        try {
            properties = convertYAML2Properties();
            retryTemplate = createRetryTemplate();
            opcUaTemplate = createOpcUaClientTemplate(properties,retryTemplate);
            subscriber = createUaClientSubscribe(opcUaTemplate);
            ListenerBinder.bind(properties);
        }catch (OpcUaClientException e){
            log.error(e.getMessage());
            return;
        }
        getConnection();
    }

    /**
     * 客户端连接plc
     */
    private void getConnection(){
        List<UaClientPOJO> uaClientPOJOS = relation.getUaClientPOJOS();
        ExecutorService executorService = OpcUaUtil.createThreadPool(uaClientPOJOS.size());
        for (UaClientPOJO uaClientPOJO : uaClientPOJOS) {
            executorService.execute(
                    new Connector(uaClientPOJO, subscriber)
            );
        }
    }

    /**
     * 读取配置文件并且转换为opcUaProperties
     * 配置文件建议放在src目录下（resources下）
     * @return
     */
    public OpcUaProperties convertYAML2Properties(){
        OpcUaProperties properties =  YamlConverter.getInstance().readAndConvert("opcua.yml",OpcUaProperties.class);
        List<Map<String, String>>  plcList= properties.getPlcList();
        for (Map<String, String> plc : plcList) {
            ClientCreator.create(plc);
        }
        return properties;
    }


    public OpcUaTemplate createOpcUaClientTemplate(OpcUaProperties properties, RetryTemplate retryTemplate){
        return new OpcUaTemplate(properties,retryTemplate);
    }

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

    public Subscriber createUaClientSubscribe(OpcUaTemplate opcUaTemplate){
        return new Subscriber(opcUaTemplate);
    }
}
