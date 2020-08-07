package com.opc.uaclient.opcua.util;

import lombok.extern.slf4j.Slf4j;
import org.opcfoundation.ua.builtintypes.DataValue;
import org.opcfoundation.ua.builtintypes.NodeId;
import org.opcfoundation.ua.builtintypes.Variant;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * OpcUa相关的工具类
 * @author fujun
 */
@Slf4j
public final class OpcUaUtil {
    private  static final ConcurrentHashMap<String,Object> LAST_NODE_VALUE_MAP = new ConcurrentHashMap<String,Object>();
    private final static AtomicInteger THREAD_NO = new AtomicInteger(1);
    private static int needConnectClientNum;
    public static ExecutorService executorService;
    public static CountDownLatch latch;

    /**
     * @param plcIndex  opcUA对应的服务的url
     * @param id  节点Id
     * @param oldDataValue 原来值
     * @param newDataValue 最新读取值
     * @return  该值是否更新,更新为true,未更新为false(排除掉服务器启动时更新值)
     */
    public static boolean  isNewNodeValueValid(String plcIndex, NodeId id, DataValue oldDataValue, DataValue newDataValue){
        String nodeId = plcIndex + "." + id.toString();
        String statusCode = newDataValue.getStatusCode().getName();
        if ("BAD".equals(statusCode)){
            log.error("Check whether the PLC is connected to the network,because the dataValue statusCode is 'BAD (0x80000000)' from opc UA server.");
            return false;
        }
        log.debug("current dataValue statusCode is {}.",statusCode);
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
                return true;
            }
        } else {
            if (newVariant.getValue().equals(oldVariant.getValue())
                || (LAST_NODE_VALUE_MAP.get(nodeId) != null
                && LAST_NODE_VALUE_MAP.get(nodeId).equals(newVariant.getValue()))) {
                log.debug("--->>" + nodeId + " from " + oldVariant + " to " + newVariant + "<<---");
                log.error("data change error: listener YOU DU!!!");
                return true;
            }
        }
        log.info("--->>" + nodeId + " from " + oldVariant + " to " + newVariant + "<<--- go.");
        LAST_NODE_VALUE_MAP.put(nodeId, newVariant.getValue());
        return false;
    }

    /**
     * 创建线程池，一个线程对应连接一个ua server
     */
    public static ExecutorService createThreadPool(int num){
        if (executorService == null){
            executorService = Executors.newFixedThreadPool(needConnectClientNum, r -> {
                SecurityManager s = System.getSecurityManager();
                ThreadGroup group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
                Thread th = new Thread(group,r,"pool-"+ THREAD_NO.getAndIncrement()+"-thread");
                //判断如果线程优先级被修改，那么改变优先级状态
                if(th.getPriority() != Thread.NORM_PRIORITY) {
                    th.setPriority(Thread.NORM_PRIORITY);
                }
                if (th.isDaemon()){
                    th.setDaemon(false);
                }
                return th;
            });
        }
        return executorService;
    }

    public static void settings(int num){
        needConnectClientNum = num;
        latch = new CountDownLatch(needConnectClientNum);
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

