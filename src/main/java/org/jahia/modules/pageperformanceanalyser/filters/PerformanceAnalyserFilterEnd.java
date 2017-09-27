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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PerformanceAnalyserFilterEnd extends AbstractFilter {
    private static final Logger logger = LoggerFactory.getLogger(PerformanceAnalyserFilterEnd.class);
    private CacheService cacheService;

    public CacheService getCacheService() {
        return cacheService;
    }

    public void setCacheService(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @Override
    public void finalize(RenderContext renderContext, Resource resource, RenderChain renderChain) {
        if (renderContext.getRequest().getParameter("perfAnalyse") != null) {

            Map<String, Map<String, Object>> responseTimeStack = (Map<String, Map<String, Object>>) renderContext.getRequest().getAttribute("responseTimeStack");

            if (responseTimeStack != null) {
                Map<String, Object> objectRenderChainMap = responseTimeStack.get(resource.getPath());
                Date date = (Date) objectRenderChainMap.get("date");
                String depth = (String) objectRenderChainMap.get("depth");

                Long totalTimeSpent = (Long) renderContext.getRequest().getAttribute("totalTimeSpent");
                if (totalTimeSpent == null) {
                    totalTimeSpent = new Long(0);
                }

                String currentDepth = (String) renderContext.getRequest().getAttribute("depth");

                renderContext.getRequest().setAttribute("depth", String.valueOf(Integer.parseInt(depth) - 1));

                if (Integer.parseInt(currentDepth) == Integer.parseInt(depth)) {
                    long timeSpent = (new Date().getTime() - date.getTime());
                    totalTimeSpent = totalTimeSpent.longValue() + timeSpent;
                    renderContext.getRequest().setAttribute("totalTimeSpent", totalTimeSpent);
                    logger.info("timespent: " + timeSpent + " - totalTimeSpent - " + totalTimeSpent + "ms");
                    storeInCache(resource.getPath(), String.valueOf(timeSpent));
                }

                logger.info("FINALIZE (" + depth + ") - stopping counter for " + resource.getPath());

            }


        }
        super.finalize(renderContext, resource, renderChain);
    }

    private void storeInCache(String key, String entry) {
        Cache<Object, Object> cacheInstance = null;
        try {
            cacheInstance = cacheService.getCache("jsAssetsCache", true);
            cacheInstance.put(key, entry);
        } catch (JahiaInitializationException e) {
            e.printStackTrace();
        }

    }


}
