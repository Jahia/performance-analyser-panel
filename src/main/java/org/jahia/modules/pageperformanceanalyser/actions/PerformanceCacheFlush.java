package org.jahia.modules.pageperformanceanalyser.actions;


import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.services.cache.Cache;
import org.jahia.services.cache.CacheHelper;
import org.jahia.services.cache.CacheService;
import org.jahia.services.content.*;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;
import org.json.JSONException;
import org.json.JSONObject;

import javax.jcr.PathNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public class PerformanceCacheFlush extends Action {
    private CacheService cacheService;


    public CacheService getCacheService() {
        return cacheService;
    }

    public void setCacheService(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @Override
    public ActionResult doExecute(HttpServletRequest httpServletRequest, RenderContext renderContext, Resource resource, final JCRSessionWrapper jcrSessionWrapper, Map<String, List<String>> map, URLResolver urlResolver) throws Exception {

        final String path = getParameter(map, "path"); //Path of the page
        boolean flushPage = Boolean.parseBoolean(getParameter(map, "flush"));

        try{
            JCRNodeWrapper page = jcrSessionWrapper.getNode(path);
            String pageName = page.getDisplayableName();

            Cache<Object, Object> cacheInstance = null;

            cacheInstance = cacheService.getCache("performanceRecordCache", true);

            cacheInstance.flush();

            cacheInstance.put("PageNameCachePerf", pageName);
            cacheInstance.put("PagePathCachePerf", path);
            cacheInstance.put("flushCachePerf", flushPage);

            //We need to flush the page to go through the process. What if someone go the page at the same time?
            if (flushPage) {
                CacheHelper.flushOutputCachesForPath(path, true);
            }
            return new ActionResult(HttpServletResponse.SC_OK, null, pageNotPublish(false));

        }catch (PathNotFoundException e){
            return new ActionResult(HttpServletResponse.SC_OK, null, pageNotPublish(true));
        }


    }

    private JSONObject pageNotPublish(boolean notpublish) {
        try {
            JSONObject json = new JSONObject();
            json.put("test","test");
            json.put("PageNotPublish",notpublish);
            return json;
        }catch (JSONException e){
            return  new JSONObject();
        }

    }
}
