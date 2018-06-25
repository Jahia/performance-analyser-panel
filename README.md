# Performance Analyser Panel
The performance panel analyser let you analyse a page of your Jahia DX website, to find out what is the reason for the slowdowns. All times, you see are the server's time only.

## Getting Started
To get started, you need to add the module to your webSite. Then you will find a new element on your "Site settings" page called "Performance Panel".

![Title](https://github.com/Jahia/performance-analyser-panel/blob/master/readmeImg/1.png "Title")

### First Select a Page
To select a page you should click on: 
![Button Page](https://github.com/Jahia/performance-analyser-panel/blob/master/readmeImg/2.png "ButtonPage")

Then just select the page you want from your webSite on the tree:
![Pages Selector](https://github.com/Jahia/performance-analyser-panel/blob/master/readmeImg/3.png "PagesSelector")

### Run & Analyse
To run the page analyse, you can choose to flush the cache or not, you have to press on :
![Launch Button ](https://github.com/Jahia/performance-analyser-panel/blob/master/readmeImg/4.png "LaunchButton")

Then you will see on the top all the information, that corresponds to the total time take to load all the elements, the number of elements and the name of the page.

![Top Infos](https://github.com/Jahia/performance-analyser-panel/blob/master/readmeImg/5.png "TopInfos")

Then you will see a table with all the elements information. You can order the table, and change the number of element in one page.

![Table Infos](https://github.com/Jahia/performance-analyser-panel/blob/master/readmeImg/6.png "TableInfos")

You can now see on the bottom of the page, a pie that shows you the 20 slowest elements of the page:

![Bottom Infos](https://github.com/Jahia/performance-analyser-panel/blob/master/readmeImg/7.png "BottomInfos")

These graphs can be show as a bar charts, and the number of elements to show can change between 5 and 20.

![Bar Infos](https://github.com/Jahia/performance-analyser-panel/blob/master/readmeImg/8.png "BarInfos")


## Changelog
Version | Required DX version | Changes
------------ | -------------| -------------
1.0.0 | 7.1.0.0 | Initial version
1.0.1 | 7.1.0.0 | Allow users to pick contents, not only pages
1.1 | 7.1.0.0 | Improved the performances of the page picker (UI)<br/>Fixed a bug preventing to use the panel with another user than root.<br/>Correctly handle the case when the selected page can't be tested (usually because not published)
1.2 | 7.1.0.0 | Fixed a display bug with fragments for which the currentNode has a double underscore in its path

