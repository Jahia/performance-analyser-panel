package org.jahia.modules.pageperformanceanalyser.filters;

import org.jahia.exceptions.JahiaInitializationException;
import org.jahia.services.cache.Cache;
import org.jahia.services.cache.CacheService;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.filter.AbstractFilter;
import org.jahia.services.render.filter.RenderChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PerformancePanelFilter extends AbstractFilter {

    private static final Logger logger = LoggerFactory.getLogger(PerformanceAnalyserFilterEnd.class);
    private CacheService cacheService;


    public CacheService getCacheService() {
        return cacheService;
    }

    public void setCacheService(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @Override
    public String prepare(RenderContext renderContext, Resource resource, RenderChain chain) throws Exception {
        Cache<Object, Object> cacheInstance = null;
        final ArrayList<FragmentInfos> cacheList = new ArrayList<FragmentInfos>();
        Map<String, Long> data = new HashMap<String, Long>();
        int totalElement = 0;
        int totalTimeSpent = 0;
        try {
            cacheInstance = cacheService.getCache("performanceRecordCache", true);


            Iterator<Object> iterator = cacheInstance.getKeys().iterator();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                if (key.equals("PageNameCachePerf")) {
                    renderContext.getRequest().setAttribute("PageNameCachePerf", (String) cacheInstance.get(key));
                } else if (key.equals("PagePathCachePerf")) {
                    renderContext.getRequest().setAttribute("PagePathCachePerf", (String) cacheInstance.get(key));
                } else if (key.equals("TotalTimeSpent")) {
                    renderContext.getRequest().setAttribute("TotalTimeSpent2", (Long) cacheInstance.get(key));
                } else if (key.equals("flushCachePerf")) {
                    renderContext.getRequest().setAttribute("flushCachePerf", (boolean) cacheInstance.get(key));
                } else {
                    Map<String, String> infos = (Map<String, String>) cacheInstance.get(key);
                    if (infos.get("timeSpent") != null) {
                        totalTimeSpent += Long.parseLong(infos.get("timeSpent"));
                    }
                    totalElement++;
                    if (infos.get("timeSpent") != null) {
                        data.put(infos.get("j:nodename"), Long.parseLong(infos.get("timeSpent")));
                    }
                    cacheList.add(new FragmentInfos(infos.get("path"), infos.get("jcr:uuid"), infos.get("jcr:primaryType"), infos.get("j:nodename"), infos.get("timeSpent")));
                }

            }

        } catch (JahiaInitializationException e) {
            e.printStackTrace();
        }
        renderContext.getRequest().setAttribute("cacheList", cacheList);
        renderContext.getRequest().setAttribute("totalCacheTimeSpent", totalTimeSpent);
        renderContext.getRequest().setAttribute("totalCacheElements", totalElement);
        renderContext.getRequest().setAttribute("dataCacheElement", data);

        return null;


    }

    public class FragmentInfos {
        private String path, identifier, primaryType, nodename, timeSpent;

        FragmentInfos(String path, String identifier, String primaryType, String nodename, String timeSpent) {
            this.path = path;
            this.identifier = identifier;
            this.primaryType = primaryType;
            this.nodename = nodename;
            this.timeSpent = timeSpent;
        }

        public String getPath() {
            return path;
        }

        public String getIdentifier() {
            return identifier;
        }

        public String getPrimaryType() {
            return primaryType;
        }

        public String getNodename() {
            return nodename;
        }

        public String getTimeSpent() {
            return timeSpent;
        }
    }
}
