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

import java.util.*;

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
        ArrayList<String> cacheList = new ArrayList<String>();
        try {
            cacheInstance = cacheService.getCache("jsAssetsCache", true);

            Iterator<Object> iterator = cacheInstance.getKeys().iterator();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                cacheList.add(key+":"+cacheInstance.get(key));

            }

        } catch (JahiaInitializationException e) {
            e.printStackTrace();
        }
        renderContext.getRequest().setAttribute("cacheList", cacheList);
        return null;

    }
}
