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
<template:addResources type="javascript" resources="jquery.js,jquery.tablersorter.widget.js,Chart.js,jquery.tablesorter.js,performancePanel.js"/>
<template:addResources type="css" resources="bootstrap/bootstrap.css,content-report-style.css,tableSorter.css"/>
<c:url value="${url.baseEdit}${currentResource.node.path}" var="detailDisplayUrl" />


<div style="margin: 3%" >
    <template:addResources>
        <script>
            $(document).ready(function()
                {
                    //Let the table pager and orderable
                    $("#perfTable")
                        .tablesorter( {sortList: [[4,1]]} )
                        .tablesorterPager({container: $(".pager"), output:'{page}/{totalPages}'});
                    change('pie','${detailDisplayUrl}');

                    //Let show the number in the language property
                    $('.number').each(function () {
                        var item = $(this).text();
                        var num = Number(item).toLocaleString('fr');
                        $(this).text(num);
                    });
                });
        </script>
    </template:addResources>

    <!-- include tree folder picker -->
    <template:include view="folderPicker"/>
    <!-- Set variable -->
    <c:set value="-" var="pageName"/>
    <c:if test="${not empty PageNameCachePerf}">
        <c:set var="pageName" value="${PageNameCachePerf}"/>
    </c:if>
    <c:set value="${renderContext.site.path}" var="pagePath"/>
    <c:if test="${not empty PagePathCachePerf}">
        <c:set var="pagePath" value="${PagePathCachePerf}"/>
    </c:if>

    <!-- Title and page selector  -->
    <div class="jumbotron">
        <h1 style="text-align: center;"><fmt:message key="performancePanelPerformanceAnalyser"/></h1>
        <div class="selectPage">
            <p><fmt:message key="pagePerformanceAnalyser.infoPage"/></p>

            <div style="display: inline-block">
                <button type="button" class="btn-small" onclick="callTreeView('pathTxtRDA')">
                    <span class="glyphicon glyphicon-folder-open"></span>
                    &nbsp;<fmt:message key="pagePerformanceAnalyser.choosePageToAnalyse"/>
                </button>
                <input type="text" id="pathTxtRDA" name="pathTxtRDA" class="pageInput" readonly="true" value="${pagePath}" >
            </div>
            <!-- Flush Cache Checkbox, check the box if empty  -->
            <div class="boxes">
                <c:choose >
                    <c:when test="${flushCachePerf}">
                        <input type="checkbox" id="flushCacheCheck"  checked >
                    </c:when>
                    <c:otherwise>
                        <input type="checkbox" id="flushCacheCheck" >
                    </c:otherwise>
                </c:choose>

                <label for="flushCacheCheck">Flush cache</label>
            </div>
            </br>
            <button type="button" class="btn btn-lg btn-success" onclick="runPerformancePanel($('#pathTxtRDA').val(), '${renderContext.site.path}', $('#flushCacheCheck').is(':checked'))">
                <fmt:message key="pagePerformanceAnalyser.launch"/>&nbsp;
                <span class="glyphicon glyphicon-ok"></span>
            </button>
        </div>
    </div>

    <!-- Card elements -->
    <div class="card card-stats" style="width: 25%; margin-left: 2%">
        <div class="card-header" data-background-color="blue">
            <i class="glyphicon glyphicon-time"></i>
        </div>
        <div class="card-content">
            <p class="category"> <fmt:message key="pagePerformanceAnalyser.title.totalTime"/></p>
            <h3 class="card-title"><span class="number">${TotalTimeSpent2}</span> <fmt:message key="pagePerformanceAnalyser.title.ms"/> <p class="category"> </h3>
        </div>
        <div class="card-footer">
            <div class="stats">
            </div>
        </div>
    </div>
    <div class="card card-stats" style="width: 25%; margin-left: 10%">
        <div class="card-header" data-background-color="blue">
            <i class="glyphicon glyphicon-info-sign"></i>
        </div>
        <div class="card-content">
            <p class="category"><fmt:message key="pagePerformanceAnalyser.title.elementLoad"/></p>
            <h3 class="card-title"><span class="number">${totalCacheElements}</span></h3>
        </div>
        <div class="card-footer">
            <div class="stats">
            </div>
        </div>
    </div>
    <div class="card card-stats" style="width: 25%; margin-left: 10%">
        <div class="card-header" data-background-color="blue">
            <i class="glyphicon glyphicon-file"></i>
        </div>
        <div class="card-content">
            <p class="category"><fmt:message key="pagePerformanceAnalyser.page"/> </p>
            <h3 class="card-title">${pageName}</h3>
        </div>
        <div class="card-footer">
            <div class="stats">
            </div>
        </div>
    </div>

    <!-- Table -->
    <div class="stylePager pager">
        <form>
            <label  class="labels" for="pagesize"><fmt:message key="pagePerformanceAnalyser.elementsPerPage"/> </label>
            <select class="pagesize" id="pagesize" style="width: auto">
                <option value="10">10</option>
                <option value="20">20</option>
                <option value="30">30</option>
                <option value="50">50</option>
            </select>
        </form>
    </div>
    <div id="table" class="">
        <table id="perfTable" class="table table-bordered table-striped table-hover table-sortable tablesorter">
            <thead>
                <tr>
                    <th><fmt:message key="pagePerformanceAnalyser.UUID"/> </th>
                    <th><fmt:message key="pagePerformanceAnalyser.path"/></th>
                    <th><fmt:message key="pagePerformanceAnalyser.primaryNodeType"/> </th>
                    <th><fmt:message key="pagePerformanceAnalyser.name"/></th>
                    <th data-sortinitialorder="desc"><fmt:message key="pagePerformanceAnalyser.time"/> </th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${cacheList}" var="item">
                    <c:set var="splitString" value="${fn:split(item,'__')}" />
                    <tr>
                        <td>${splitString[1]}</td>
                        <td>${splitString[0]}</td>
                        <td>${splitString[2]}</td>
                        <td>${splitString[3]}</td>
                        <td class="number">${splitString[4]}</td>
                    </tr>
                </c:forEach>
            </tbody>

        </table>
    </div>
    <!-- pager for the table -->
    <div class="pager">
        <form>
            <span class="btn-small first"><<</span>
            <span class="btn-small prev" alt="<"><</span>
            <input type="text" class="pagedisplay pageInfo" style="text-align: center" disabled>
            <span class="btn-small next" alt=">">></span>
            <span class="btn-small last" alt=">>">>></span>
        </form>
    </div>


    <!-- Chart To show all the last 20 elements -->
    <div id="charts" class="card">
        <div class="card-header" data-background-color="blue">
            <i class="glyphicon glyphicon-stats" style="font-size: 34px; margin-left: 9px"></i>
        </div>
        <div class="card-content">
            <div>
                <h1 id="titleChart"></h1>
                <div class="category-chart" style="text-align: right;">
                    <button id='pie' onclick="change('pie','${detailDisplayUrl}')" class="btn-small"><fmt:message key="pagePerformanceAnalyser.pie"/> </button>
                    <button id='bar' onclick="change('horizontalBar','${detailDisplayUrl}')" class="btn-small"><fmt:message key="pagePerformanceAnalyser.bar"/></button>
                    <label class="labels"><fmt:message key="pagePerformanceAnalyser.elements"/> </label>
                    <select id="numberOfElement" style="width: auto">
                        <option value="5">5</option>
                        <option value="10">10</option>
                        <option value="15">15</option>
                        <option value="20" selected="selected">20</option>
                    </select>
                </div>
            </div>

            <canvas id="myChart" style=" min-height: 100%"></canvas>
        </div>
    </div>
</div>