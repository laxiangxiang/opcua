package com.opc.uaclient.opcua.util;

import lombok.extern.slf4j.Slf4j;
import org.opcfoundation.ua.builtintypes.DataValue;
import org.opcfoundation.ua.builtintypes.NodeId;
import org.opcfoundation.ua.builtintypes.Variant;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * OpcUa相关的工具类
 * @author fujun
 */
@Slf4j
public class OpcUaUtil {
    private  static Map<String,Object>  LAST_NODE_VALUE_MAP=new ConcurrentHashMap();
    private final static AtomicInteger threadNo = new AtomicInteger(1);
    private static int needConnectClientNum;
    public static ExecutorService executorService;
    public static CountDownLatch latch;

    /**
     * @param plcIndex  opcUA对应的服务的url
     * @param id  节点Id
     * @param oldDataValue 原来值
     * @param newDataValue 最新读取值
     * @return  该值是否更新,更新为true,未更新为false(排除掉第一服务器启动更新值)
     */
    public static boolean  isNewNodeValueValid(String plcIndex, NodeId id, DataValue oldDataValue, DataValue newDataValue){
        String nodeId = plcIndex + "." + id.toString();
        if (null == oldDataValue) {
            log.info("The subscription for {} is initialized.", nodeId);
            LAST_NODE_VALUE_MAP.put(nodeId, newDataValue.getValue().getValue());
            return false;
        }
        Variant oldVariant = oldDataValue.getValue();
        Variant newVariant = newDataValue.getValue();
        if (newVariant.isArray()){
            Object[] oldArray = (Object[]) oldVariant.getValue();
            Object[] newArray = (Object[]) newVariant.getValue();
            boolean flag = false;
            for (int i = 0; i < newArray.length; i++) {
                if (oldArray[i].equals(newArray[i])
                    || (LAST_NODE_VALUE_MAP.get(nodeId) != null
                    && ((Object[])LAST_NODE_VALUE_MAP.get(nodeId))[i].equals(newArray[i]))) {
                    continue;
                }
                flag = true;
                break;
            }
            if (!flag){
                log.debug("--->>" + nodeId + " from " + oldVariant + " to " + newVariant + "<<---");
                log.error("data change error: listener YOU DU!!!");
                return false;
            }
        } else {
            if (newVariant.getValue().equals(oldVariant.getValue())
                || (LAST_NODE_VALUE_MAP.get(nodeId) != null
                && LAST_NODE_VALUE_MAP.get(nodeId).equals(newVariant.getValue()))) {

                log.debug("--->>" + nodeId + " from " + oldVariant + " to " + newVariant + "<<---");
                log.error("data change error: listener YOU DU!!!");
                return false;
            }
        }
        log.info("--->>" + nodeId + " from " + oldVariant + " to " + newVariant + "<<--- go.");
        LAST_NODE_VALUE_MAP.put(nodeId, newVariant.getValue());
        return true;
    }

    /**
     * 创建线程池，一个线程对应连接一个plc
     * @return
     */
    public static ExecutorService createThreadPool(int num){
        needConnectClientNum = num;
        if (executorService == null){
            executorService = Executors.newFixedThreadPool(needConnectClientNum, new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    //创建一个线程，定义名称为"order-thread"
                    Thread th = new Thread(r,"conn2plc-thread"+ threadNo.getAndIncrement());
                    //判断如果线程优先级被修改，那么改变优先级状态
                    if(th.getPriority() != Thread.NORM_PRIORITY) {
                        th.setPriority(Thread.NORM_PRIORITY);
                    }
                    th.setDaemon(true);
                    return th;
                }
            });
            latch = new CountDownLatch(needConnectClientNum);
        }
        return executorService;
    }

    public static void release(){
        if (executorService != null){
            executorService.shutdown();
        }
    }

    /**
     * 恢复countDownLatch
     */
    public static void resume(){
        latch = new CountDownLatch(needConnectClientNum);
    }
}

