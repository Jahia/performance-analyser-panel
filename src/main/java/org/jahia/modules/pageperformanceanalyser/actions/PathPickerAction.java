package org.jahia.modules.pageperformanceanalyser.actions;

import org.apache.commons.lang.StringUtils;
import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.services.content.JCRContentUtils;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class PathPickerAction extends Action {

    /* the logger for the class. */
    private static Logger logger = LoggerFactory.getLogger(PathPickerAction.class);

    @Override
    public ActionResult doExecute(HttpServletRequest req, RenderContext renderContext, Resource resource, JCRSessionWrapper session, Map<String, List<String>> parameters, URLResolver urlResolver) throws Exception {
        logger.debug("doExecute: begins the PathPickerAction action.");
        int level = Integer.parseInt(getParameter(parameters, "level")); //No value will be 0
        String path = getParameter(parameters, "pagePath");
        String sitePath = renderContext.getSite().getPath();

        if (path.contains(sitePath)) {
            JCRNodeWrapper node = session.getNode(path);
            try {
                return new ActionResult(HttpServletResponse.SC_OK, null, getSitePathJson(node, renderContext, new AtomicBoolean(true), level));
            } catch (Exception ex) {
                logger.error("doExecute() PathPickerAction, Error,", ex);
                return new ActionResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } else {
            logger.debug("The path : " + path + "is not part of the site path : " + sitePath);
            return new ActionResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * Replace Single Quote by ""
     *
     * @param toReplace
     * @return
     */
    private String replaceSingleQuotes(String toReplace) {
        if (!StringUtils.isBlank(toReplace)) {
            return toReplace.replaceAll("'", "");
        } else {
            return toReplace;
        }

    }

    /**
     * getSitePathJson
     * <p>The method returns a json with the tree under a specific node.</p>
     *
     * @param node @JCRNodeWrapper
     * @return jsonString @String
     * @throws RepositoryException
     */
    protected JSONObject getSitePathJson(JCRNodeWrapper node, RenderContext renderContext, AtomicBoolean displayable, int level) throws RepositoryException {
        if (level > 0 || level == -1) {
            if (JCRContentUtils.isADisplayableNode(node, renderContext)) {
                displayable.set(true);
            }
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("title", replaceSingleQuotes(node.getDisplayableName()));
                jsonObject.put("href", node.getPath());
                if (!JCRContentUtils.isADisplayableNode(node, renderContext)) {
                    jsonObject.put("noSelect", true);
                } else {
                    jsonObject.put("noSelect", false);
                }
                List<JCRNodeWrapper> childNodeList = new ArrayList<JCRNodeWrapper>();

                /* getting the folder child nodes */
                childNodeList = JCRContentUtils.getChildrenOfType(node, "nt:base");


                if (childNodeList.size() > 0) {
                    level--;
                    boolean hasChildren = false;
                    JSONArray childs = new JSONArray();
                    jsonObject.put("lazy", true);
                    for (int index = 0; index < childNodeList.size(); index++) {

                        AtomicBoolean isChildDisplayed = new AtomicBoolean(false);
                        // We only push the child to the jsonBuilder is possesses a content template
                        JSONObject temp = getSitePathJson(childNodeList.get(index), renderContext, isChildDisplayed, level);
                        if (isChildDisplayed.get()) {

                            // if the child is displayed, then the parent must be displayed too
                            displayable.set(true);
                            if (null != temp) {
                                childs.put(temp);
                                if (!hasChildren) {
                                    jsonObject.put("children", childs);
                                }
                                hasChildren = true;
                            }
                        }

                    }
                }
            } catch (JSONException e) {
                logger.error(e.toString());
            }
            return jsonObject;
        } else {
            if (JCRContentUtils.isADisplayableNode(node, renderContext)) {
                displayable.set(true);
            }
            return null;
        }

    }
}
