package com.lagou.filter;

import com.lagou.bean.MethodInfo;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.SimpleFormatter;

@Activate(group = {CommonConstants.CONSUMER})
public class DubboInvokeFilter implements Filter, Runnable {

    Map<String, List<MethodInfo>> methodTimes =new ConcurrentHashMap<>();

    public DubboInvokeFilter() {
        Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(this,5,5, TimeUnit.SECONDS);
        Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(new Thread(new RemoveDateThread(methodTimes)),10,60, TimeUnit.SECONDS);
    }

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        Result result=null;
        Long takeTime=0L;

        try {
            Long startTime=System.currentTimeMillis();
            result=invoker.invoke(invocation);
            if(result.getException() instanceof Exception) {
                throw new Exception(result.getException());
            }
            takeTime=System.currentTimeMillis()-startTime;
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }

        String methodName=invocation.getMethodName();
        List<MethodInfo> methodInfos=methodTimes.get(methodName);
        if(null==methodInfos) {
            methodInfos =new ArrayList<>();
            methodTimes.put(methodName,methodInfos);
        }
        methodInfos.add(new MethodInfo(methodName,takeTime,System.currentTimeMillis()));
        return result;
    }

    private Long getTP(List<MethodInfo> methodInfos,double rate) {
        List<MethodInfo> tmpInfo =new ArrayList<>();
        long endTime =System.currentTimeMillis();
        long starttime =endTime -6000;

        for (MethodInfo mi:methodInfos) {
            if(mi.getEndTimes()>=starttime && mi.getEndTimes()<=endTime) {
                tmpInfo.add(mi);
            }
        }

        tmpInfo.sort(new Comparator<MethodInfo>() {
            @Override
            public int compare(MethodInfo o1, MethodInfo o2) {
                if(o1.getTimes() > o2.getTimes()) {
                    return 1;
                } else  if(o1.getTimes() < o2.getTimes()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });

        int index=(int)(tmpInfo.size()*rate);
        return tmpInfo.get(index).getTimes();
    }

    @Override
    public void run() {
        Date date =new Date();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        for(Map.Entry<String, List<MethodInfo>> entrys:methodTimes.entrySet()) {
            List<MethodInfo> li=entrys.getValue();
            System.out.println(sdf.format(date)+entrys.getKey()+"TP90:"+getTP(li,0.90)+"TP99:"+getTP(li,0.99));
        }
    }
}
