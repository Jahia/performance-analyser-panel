<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="jcr" uri="http://www.jahia.org/tags/jcr" %>
<%@ taglib prefix="ui" uri="http://www.jahia.org/tags/uiComponentsLib" %>
<%@ taglib prefix="functions" uri="http://www.jahia.org/tags/functions" %>
<%@ taglib prefix="query" uri="http://www.jahia.org/tags/queryLib" %>
<%@ taglib prefix="utility" uri="http://www.jahia.org/tags/utilityLib" %>
<%@ taglib prefix="s" uri="http://www.jahia.org/tags/search" %>
<%--@elvariable id="currentNode" type="org.jahia.services.content.JCRNodeWrapper"--%>
<%--@elvariable id="out" type="java.io.PrintWriter"--%>
<%--@elvariable id="script" type="org.jahia.services.render.scripting.Script"--%>
<%--@elvariable id="scriptInfo" type="java.lang.String"--%>
<%--@elvariable id="workspace" type="java.lang.String"--%>
<%--@elvariable id="renderContext" type="org.jahia.services.render.RenderContext"--%>
<%--@elvariable id="currentResource" type="org.jahia.services.render.Resource"--%>
<%--@elvariable id="url" type="org.jahia.services.render.URLGenerator"--%>
<%--@elvariable id="vfsFactory" type="org.jahia.modules.external.vfs.factory.VFSMountPointFactory"--%>
<template:addResources type="javascript" resources="bootstrap/bootstrap-treeview.js" />
<c:url value="${url.base}${docPath}${renderContext.mainResource.node.path}" var="currentNodePath"/>

<template:addResources>
    <script type="text/javascript">
        function callTreeView(targetId) {
            $('#windowPathPicker').modal({
                show: 'true'
            });
            var actionUrl = "${currentNodePath}" + ".PathPicker.do";
            $.getJSON( actionUrl, function( data ) {
                $('#treeviewpath').treeview({
                    color: "#428bca",
                    expandIcon: 'glyphicon glyphicon-chevron-right',
                    collapseIcon: 'glyphicon glyphicon-chevron-down',
                    nodeIcon: 'glyphicon glyphicon-folder-close',
                    data:  [data],
                    onNodeSelected: function(event, node) {
                        $("#" + targetId + "").val(node.href);
                        $('#windowPathPicker').modal('toggle');;
                    },
                });
            });
        }
    </script>
</template:addResources>

<div id="windowPathPicker" class="nModal fade window-detail-nModal" role="dialog">
    <div class="nModal-dialog">

        <!-- nModal content-->
        <div class="nModal-content">
            <div class="nModal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="nModal-body">
                <div id="treeviewpath"></div>
            </div>
            <div class="nModal-footer" style="text-align: center">
                <button type="button" class="btn-small" data-dismiss="modal">Close</button>
            </div>
        </div>

    </div>
</div>