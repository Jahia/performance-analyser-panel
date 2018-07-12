package org.jahia.modules.pageperformanceanalyser.actions;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.modules.pageperformanceanalyser.HttpClient;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public class RequestPageAction extends Action {

    @Override
    public ActionResult doExecute(HttpServletRequest httpServletRequest, RenderContext renderContext, Resource resource, JCRSessionWrapper jcrSessionWrapper, Map<String, List<String>> map, URLResolver urlResolver) throws Exception {
        String url = resource.getNode().getAbsoluteUrl(httpServletRequest)+"?perfAnalyse";
        OkHttpClient client =  HttpClient.getClient();
        Request request = new Request.Builder().url(HttpClient.buildUrl(url,null).build()).get().build();
        Response resp = client.newCall(request).execute();
        if (resp.isSuccessful() && url.equals(resp.networkResponse().request().url().toString())) {
            resp.body().close();
            return new ActionResult(HttpServletResponse.SC_OK);
        }
        return new ActionResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
}
