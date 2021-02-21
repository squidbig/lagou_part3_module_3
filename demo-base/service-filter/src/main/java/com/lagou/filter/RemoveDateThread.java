package com.lagou.filter;

import com.lagou.bean.MethodInfo;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class RemoveDateThread implements Runnable {
    private Map<String, List<MethodInfo>> methodInfos;

    public RemoveDateThread(Map<String, List<MethodInfo>> methodInfos) {
        this.methodInfos = methodInfos;
    }

    @Override
    public void run() {
        for(Map.Entry<String, List<MethodInfo>> entrys:methodInfos.entrySet()) {
            List<MethodInfo> list=entrys.getValue();
            Iterator<MethodInfo> iterator =list.iterator();
            Long removeCon = System.currentTimeMillis() - 60000;
            while(iterator.hasNext()) {
                MethodInfo info=iterator.next();
                if (info.getEndTimes() < removeCon) {
                    iterator.remove();
                }
            }
        }
    }
}
