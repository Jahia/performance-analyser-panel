package org.jahia.modules.pageperformanceanalyser.filters;

import org.apache.commons.lang.StringUtils;
import org.jahia.exceptions.JahiaInitializationException;
import org.jahia.services.cache.Cache;
import org.jahia.services.cache.CacheHelper;
import org.jahia.services.cache.CacheService;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRPropertyWrapper;
import org.jahia.services.content.JCRValueWrapper;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.filter.AbstractFilter;
import org.jahia.services.render.filter.RenderChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import java.util.*;

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
            //First thing to do get the date to be able to calculate the timer before doing anything
            Long dateTime = new Date().getTime();

            Map<String, Map<String, Object>> responseTimeStack = (Map<String, Map<String, Object>>) renderContext.getRequest().getAttribute("responseTimeStack");

            //To make sure to not remove anything from the store elements
            int element = 1;
            if(renderContext.getRequest().getAttribute("NumberofElement")!=null){
                element = (Integer) renderContext.getRequest().getAttribute("NumberOfElement");
            }

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
                if(Integer.parseInt(depth)==Integer.parseInt(currentDepth)){
                    long timeSpent = (dateTime - date.getTime());
                    totalTimeSpent += timeSpent;
                    //Remove children time if there is any
                    if(renderContext.getRequest().getAttribute("ChildrenTimeSpent") != null ){
                        if( Integer.parseInt(currentDepth) < (Integer) renderContext.getRequest().getAttribute("ChildrenLevel")){
                            timeSpent -= (Long)renderContext.getRequest().getAttribute("ChildrenTimeSpent");
                            totalTimeSpent -= (Long)renderContext.getRequest().getAttribute("ChildrenTimeSpent");
                            //Reset ChildrenTime if there were one
                            if(Integer.parseInt(currentDepth)==1){
                                renderContext.getRequest().removeAttribute("ChildrenTimeSpent");
                            }
                        }

                    }

                    renderContext.getRequest().setAttribute("totalTimeSpent", totalTimeSpent);
                    logger.info("timespent: " + timeSpent + " - totalTimeSpent - " + totalTimeSpent + "ms");

                    //All the infos needs to show:
                    Map<String,String> infos = getInfos(resource.getNode(), timeSpent);

                    //Remove double entry the hidden element; if uncomment, you should comment the storeInCache(resource.getPath()+element, infos);
                    /*

                    String ressourcePath = resource.getPath();

                    if(resource.getPath().contains(".hidden.")){
                        String startPath = ressourcePath.substring(0,ressourcePath.indexOf(".hidden."));
                        if(!keysStartWith(startPath,ressourcePath, listCacheToFlush.keySet())){
                            storeInCache(resource.getPath()+element, infos);
                        }
                    }else {
                        storeInCache(resource.getPath()+element, infos);
                    }*/

                    //Store element
                    storeInCache(resource.getPath()+element, infos);

                    storeInCache("TotalTimeSpent", totalTimeSpent);
                    element++;
                    renderContext.getRequest().setAttribute("NumberOfElement",element);
                    int currentDepthInt = Integer.parseInt(currentDepth);
                    //If the depth is over 1 had the
                    if( currentDepthInt > 1){
                        int childrenLevel = 1;
                        if(null != renderContext.getRequest().getAttribute("ChildrenLevel")){
                            childrenLevel = (Integer) renderContext.getRequest().getAttribute("ChildrenLevel");
                        }
                        if (currentDepthInt <= childrenLevel) {
                            long ChildrenTotalTimeSpent = timeSpent;

                            if (renderContext.getRequest().getAttribute("ChildrenTimeSpent") != null) {
                                ChildrenTotalTimeSpent += (Long) renderContext.getRequest().getAttribute("ChildrenTimeSpent");
                            }
                            renderContext.getRequest().setAttribute("ChildrenTimeSpent", ChildrenTotalTimeSpent);
                            renderContext.getRequest().setAttribute("ChildrenLevel", Integer.parseInt(currentDepth));
                        }
                    }
                }

                logger.info("FINALIZE (" + depth + ") - stopping counter for " + resource.getPath());

            }


        }
        super.finalize(renderContext, resource, renderChain);
    }

    private void storeInCache(String key, Object entry) {
        Cache<Object, Object> cacheInstance = null;
        try {
            cacheInstance = cacheService.getCache("performanceRecordCache", true);
            if(cacheInstance.get(key)!=null){
                cacheInstance.remove(key);
            }
            cacheInstance.put(key,entry);


        } catch (JahiaInitializationException e) {
            e.printStackTrace();
        }

    }

    /**
     * To Create a Map with all the infos need. If more elements are need just add them here and change the PerformancePanelFilter and the jsp to add show them.
     * @param node
     * @param timeSpent
     * @return
     */
    private Map<String, String> getInfos(JCRNodeWrapper node, long timeSpent){
        Map<String,String> infos = new HashMap<String,String>();
        //Add all the infos need, if you want more you will have to add it manually
        try {
            infos.put("jcr:uuid", node.getIdentifier());
            infos.put("jcr:primaryType",node.getPrimaryNodeTypeName());
            infos.put("j:nodename",node.getName());
            infos.put("timeSpent", (String.valueOf(timeSpent)));
            infos.put("path", node.getPath());
        } catch (RepositoryException e) {
            e.printStackTrace();
        }

        return infos;
    }

    private boolean keysStartWith(String startWith,String pathToCompare, Set<String> keys){
        for(String key:keys){
            if(key.startsWith(startWith) && !key.equals(pathToCompare)){
                return true;
            }
        }
        return false;
    }

}
