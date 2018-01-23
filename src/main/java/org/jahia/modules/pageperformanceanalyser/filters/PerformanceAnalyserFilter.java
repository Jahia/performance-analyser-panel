package org.jahia.modules.pageperformanceanalyser.filters;

import org.jahia.exceptions.JahiaInitializationException;
import org.jahia.services.cache.Cache;
import org.jahia.services.cache.CacheHelper;
import org.jahia.services.cache.CacheService;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.filter.AbstractFilter;
import org.jahia.services.render.filter.RenderChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import java.util.*;

public class PerformanceAnalyserFilter extends AbstractFilter {
    private static final Logger logger = LoggerFactory.getLogger(PerformanceAnalyserFilter.class);

    /**
     * @Description  This will get a new date at the moment of the request if there is
     * @param renderContext
     * @param resource
     * @param chain
     * @return
     * @throws Exception
     */

    @Override
    public String prepare(RenderContext renderContext, Resource resource, RenderChain chain) throws Exception {
        if (renderContext.getRequest().getParameter("perfAnalyse") != null) {
            String ressourcePath = resource.getPath();
            Map<String, Map<String, Object>> responseTimeStack = (Map<String, Map<String, Object>>) renderContext.getRequest().getAttribute("responseTimeStack");
            Map<String, Boolean> listCacheAlreadyFlush = (Map<String, Boolean>) renderContext.getRequest().getAttribute("listCacheAlreadyFlush");
            if (responseTimeStack == null) {
                responseTimeStack = new HashMap<>();
                renderContext.getRequest().setAttribute("responseTimeStack", responseTimeStack);
                renderContext.getRequest().setAttribute("depth", "0");
            }

            if(listCacheAlreadyFlush == null){
                listCacheAlreadyFlush = new HashMap<>();
                renderContext.getRequest().setAttribute("listCacheAlreadyFlush",listCacheAlreadyFlush);
            }
            if(!listCacheAlreadyFlush.containsKey(ressourcePath)){
                CacheHelper.flushOutputCachesForPath(ressourcePath,false);
                listCacheAlreadyFlush.put(ressourcePath,true);
            }

            String depth = String.valueOf(Integer.parseInt((String) renderContext.getRequest().getAttribute("depth")) + 1);
            renderContext.getRequest().setAttribute("depth", depth);

            HashMap<String, Object> objectRenderChainMap = new HashMap<String, Object>();
            objectRenderChainMap.put("depth", depth);
            responseTimeStack.put(resource.getPath(), objectRenderChainMap);
            logger.info("PREPARE (" + depth + ") - starting counter for: " + resource.getPath());

            //We do evertyhing before starting the timer
            objectRenderChainMap.put("date", new Date());
            
        }
        return null;
    }
}


