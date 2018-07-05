package org.jahia.modules.pageperformanceanalyser.actions;

import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.modules.pageperformanceanalyser.ValueComparator;
import org.jahia.services.cache.Cache;
import org.jahia.services.cache.CacheService;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class GetElementForGraphs extends Action {
    private CacheService cacheService;


    public CacheService getCacheService() {
        return cacheService;
    }

    public void setCacheService(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @Override
    public ActionResult doExecute(HttpServletRequest httpServletRequest, RenderContext renderContext, Resource resource, JCRSessionWrapper jcrSessionWrapper, Map<String, List<String>> map, URLResolver urlResolver) throws Exception {

        int numberOfElementToShow = Integer.parseInt(getParameter(map, "numberOfElement"));   //Number of Element in the graph
        Map<String, Long> data = new HashMap<>();
        Cache<Object, Object> cacheInstance = null;
        cacheInstance = cacheService.getCache("performanceRecordCache", true);
        Iterator<Object> iterator = cacheInstance.getKeys().iterator();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            if (!key.equals("PageNameCachePerf") && !key.equals("PagePathCachePerf") && !key.equals("TotalTimeSpent") && !key.equals("flushCachePerf")) {
                if (cacheInstance.get(key) != null) {
                    Map<String, String> infos = (Map<String, String>) cacheInstance.get(key);
                    if (infos.get("timeSpent") != null) {
                        data.put(key + "__/__" + infos.get("j:nodename"), Long.parseLong(infos.get("timeSpent")));
                    }
                }

            }
        }

        ValueComparator bvc = new ValueComparator(data);
        TreeMap<String, Long> sorted_map = new TreeMap<String, Long>(bvc);
        //Ordered Map by the longest loading element first:
        sorted_map.putAll(data);                                                                                        //Map order desc
        List<String> keys = new ArrayList<String>(sorted_map.keySet());
        List<String> keyToSend = new ArrayList<String>();
        List<String> keysToDisplay = new ArrayList<String>();
        List<Object> dataToSend = new ArrayList<Object>();

        //Loop to get only the number of element I want
        if (numberOfElementToShow != -1) {
            // we only get the one key (which happens when the cache is activated)
            if (keys.size() == 1){
                String[] tableKey = keys.get(0).split("__/__");
                keyToSend.add(tableKey[0]);
                keysToDisplay.add(tableKey[1]);
                dataToSend.add(data.get(keys.get(0)));
            }else if (numberOfElementToShow < keys.size() - 1) {
                for (int i = keys.size() - 1; i >= keys.size() - 1 - numberOfElementToShow; i--) {
                    String[] tableKey = keys.get(i).split("__/__");
                    keyToSend.add(tableKey[0]);
                    keysToDisplay.add(tableKey[1]);
                    dataToSend.add(data.get(keys.get(i)));
                }
            } else {
                for (int i = 0; i <= keys.size() - 1; i++) {
                    keyToSend.add(keys.get(i));
                    dataToSend.add(data.get(keys.get(i)));
                }
            }
        } else {
            for (int i = 0; i <= keys.size() - 1; i++) {
                keyToSend.add(keys.get(i));
                dataToSend.add(data.get(keys.get(i)));
            }
        }


        JSONObject json = new JSONObject();
        json.put("keys", keyToSend);
        json.put("displayName", keysToDisplay);
        json.put("data", dataToSend);

        return new ActionResult(200, null, json);


    }
}

