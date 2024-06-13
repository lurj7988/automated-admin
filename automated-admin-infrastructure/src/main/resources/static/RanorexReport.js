/* TREELIST */
!function (s) {
    s.widget("jv.treeList", {
        options: {selectable: !0}, _create: function () {
            var t = this;
            t._initItem(s(this.element).find("li")), t._initChildList(s(this.element).find("ul"));
            var e = s(this.element).find("li[class*='ui-treeList-open']");
            t.openNode(e), t.openNode(e.parents("li")), s(this.element).addClass("ui-treeList ui-widget ui-widget-content ui-corner-all").bind("click", function (e) {
                var i = s(e.target);
                if (i.hasClass("ui-treeList-toggle")) i.siblings("h2").trigger("click"); else if (i.hasClass("ui-treeList-toggle-child")) i.parent("h2").trigger("click"); else if (i.is("h2")) {
                    i.siblings("ul").is(":visible") ? t.closeNode(i.parents("li:first")) : t.openNode(i.parents("li:first"))
                }
                i.hasClass("ui-treeList-item") && t.selected(i)
            })
        }, destroy: function () {
            s(this.element).unbind("click").removeClass("ui-treeList ui-widget ui-widget-content ui-corner-all").find("li").unbind("mouseenter mouseleave").removeClass("ui-treeList-item ui-widget-content ui-corner-all ui-state-default ui-state-active ui-state-hover").children("div.ui-treeList-toggle").remove().end().find("ul").unbind("mouseenter mouseleave").removeClass("ui-treeList-childs"), s.Widget.prototype.destroy.call(this)
        }, _initItem: function (e) {
            filterValidTreeItems(e).addClass("ui-treeList-item ui-widget-content ui-corner-all ui-state-default")
        }, _initChildList: function (e) {
            filterValidTreeItems(e).addClass("ui-treeList-childs").hide().before('<div class="ui-treeList-toggle ui-widget ui-widget-content ui-corner-all ui-icon ui-icon-plus"></div>')
        }, openNode: function (e) {
            e && (e.children("ul").show().siblings("div.ui-treeList-toggle").removeClass("ui-icon-plus").addClass("ui-icon ui-icon-minus").end().end().find("ul:has(li)").parents("li").removeClass("ui-state-default"), onNodeOpen(e))
        }, closeNode: function (e) {
            e && (e.addClass("ui-state-default").children("ul").hide().siblings("div.ui-treeList-toggle").removeClass("ui-icon-minus").addClass("ui-icon ui-icon-plus"), onNodeClosed(e))
        }, selected: function (e) {
            if (!e) return s(this.element).find("li.ui-state-active");
            s(this.element).find("li").removeClass("ui-state-active"), e.addClass("ui-state-active"), this._trigger("onSelect")
        }
    })
}(jQuery);

var ToolMode = {BROWSER: 0, TOOL: 1, STUDIO: 2};
var rxToolMode = ToolMode.BROWSER;

var cache, src, ss;
var rxCacheLoaded = false;
var rxDataFileNotFound = false;

var chromeCache, chromeStyleSheet, chromeDataFile;
var chromeCacheLoaded = false;
var chromeXsltProcessor;

function DisplayHoverMenu(e) {
    if (rxToolMode != ToolMode.BROWSER) {
        DisplayHoverMenuInternal(e);
    }
}

function HideHoverMenu(e) {
    HideHoverMenuInternal(e);
}

function DisplayHoverMenuInternal(e) {
    if (!document.suppressHoverMenu == true) {
        var target = e;

        if (e.tagName.toLowerCase() == 'li')
            target = $(e).find('h2').first();

        if (e.HoverMenuShown == false || e.HoverMenuShown == undefined) {
            var container = $(target).find("div.controls-container").first();

            var button = $(target).find("a.spy").first();
            var text = $(target).find("span.spytext").first();

            if (text[0] != undefined && button != undefined && container != undefined)
                SetSpyButton(container, button, text[0]);

            if (!window.hoverAlreadyShown) {
                window.hoverAlreadyShown = true;
                $(container).show();
                e.HoverMenuShown = true;
            }
        }
    }
}

function HideHoverMenuInternal(e) {
    if (!document.suppressHoverMenu == true) {
        window.hoverAlreadyShown = false;
        var target = e;

        if (e.tagName.toLowerCase() == 'li')
            target = $(e).find('h2').first();

        if (e.HoverMenuShown == true) {
            var container = $(target).find("div.controls-container").first();
            $(container).hide();

            e.HoverMenuShown = false;
        }
    }
}

function OnFilter(e, classInfo) {
    if (e.checked) {
        for (i = 0; i < classInfo.length; i = i + 1)
            $(classInfo[i]).show();
    } else {
        for (i = 0; i < classInfo.length; i = i + 1)
            $(classInfo[i]).hide();
    }
}

function ShowHideItem(item, checkedState) {
    if (checkedState == true)
        item.hide();
    else
        item.show();
}

function onNodeOpen(node) {
    header = node.children('h2');

    if (header.length == 0)
        return;

    desc = header.find('.description');
    if (desc.length > 0)
        $(desc[0]).hide();
}

function onNodeClosed(node) {
    header = node.children('h2');

    if (header.length == 0)
        return;

    desc = header.find('.description');
    if (desc.length > 0)
        $(desc[0]).show();
}

function OnLoadContentDynamic(rId, objectType, thisObject) {
    if (document.RxProcessMode === "IE") {
        OnLoadContentDynamicIE(rId, objectType, thisObject);
    } else if (document.RxProcessMode === "Chrome") {
        OnLoadContentDynamicChrome(rId, objectType, thisObject);
    }
}

function OnLoadContentDynamicIE(rId, objectType, thisObject) {
    objectName = '#' + objectType + rId;

    if (typeof document[objectName + 'Clicked'] != 'undefined') return;

    initMSXML();

    var processor = cache.createProcessor();
    processor.input = src;
    src.documentElement.setAttribute("dynamic-load", "1");

    if (objectType != 'testmodule') //TEST CASE, ITERATION, ETC...
    {
        processor.addParameter('startMode', 'TestCaseDetail');
        processor.addParameter('testcaserid', rId);
        processor.transform();

        var result = RemoveHeader(processor.output);
        postProcessContainer(objectName, result);
    } else //TEST MODULE
    {
        processor.addParameter('startMode', 'TestModuleDetail');
        processor.addParameter('testcaserid', rId);
        processor.transform();
        var result = RemoveHeader(processor.output);

        postProcessModule(thisObject, result);
    }

    decodeAllHrefAndSrcUris(document.body);
    document[objectName + 'Clicked'] = true;
}

function OnLoadContentDynamicChrome(rId, objectType, thisObject) {
    objectName = '#' + objectType + rId;

    if (typeof document[objectName + 'Clicked'] != 'undefined') return;

    if (objectType != 'testmodule') //TEST CASE, ITERATION, ETC...
    {
        chromeXsltProcessor.setParameter(null, 'startMode', 'TestCaseDetail');
        chromeXsltProcessor.setParameter(null, 'testcaserid', rId);

        var nodeToTransform = chromeDataFile.evaluate("//activity[@rid='" + rId + "']", chromeDataFile.documentElement)
            .iterateNext();

        nodeToTransform.setAttribute("dynamic-load", "1");
        var result = chromeXsltProcessor.transformToFragment(nodeToTransform, document);
        nodeToTransform.removeAttribute("dynamic-load");

        postProcessContainer(objectName, result);
        fixFirefoxMissingOutputEscaping();
    } else //TEST MODULE
    {
        chromeXsltProcessor.setParameter(null, 'startMode', 'TestModuleDetail');
        chromeXsltProcessor.setParameter(null, 'testcaserid', rId);

        var nodeToTransform = chromeDataFile.evaluate("//activity[@rid='" + rId + "']", chromeDataFile.documentElement)
            .iterateNext();
        nodeToTransform.setAttribute("dynamic-load", "1");
        var result = chromeXsltProcessor.transformToFragment(nodeToTransform, document);
        nodeToTransform.removeAttribute("dynamic-load");

        // table row stitching because XSLTProcessor enforces HTML rules on fragment output
        var resultHTML = "";

        result.querySelectorAll("tr").forEach(function (e) {
            resultHTML += e.outerHTML;
        });

        postProcessModule(thisObject, resultHTML);
    }

    decodeAllHrefAndSrcUris(document.body);
    document[objectName + 'Clicked'] = true;
}

function postProcessContainer(objectName, result) {
    filterValidTreeItems($(objectName + ' ul'))
        .first()
        .append(result);

    addModuleToggle($(objectName + ' ul h3.module-title'));

    filterValidTreeItems($(objectName + ' li ul'))
        .addClass('ui-treeList-childs')
        .hide()
        .before('<div class=\'ui-treeList-toggle ui-widget ui-widget-content ui-corner-all ui-icon ui-icon-plus\'></div>');
}

function postProcessModule(thisObject, resultHTML) {
    var tbodyArray = $(thisObject.parentNode).find("tbody");
    if (tbodyArray.length < 1) {
        decodeAllHrefAndSrcUris(document.body);
        document[objectName + 'Clicked'] = true;
        return;
    }

    var table = tbodyArray[0].parentNode.parentNode;

    var tResult = "";
    if (table.innerHTML.indexOf('TBODY') > 0)
        tResult = customReplace(table.innerHTML, resultHTML, "TBODY");
    else
        tResult = customReplace(table.innerHTML, resultHTML, "tbody");

    table.innerHTML = tResult;
    tb_init('a.thickbox, area.thickbox, input.thickbox');
}

function filterValidTreeItems(items) {
    return items.filter(function (index) {
        return $(items[index]).parents('.htmlDescription').length === 0
    })
}

function RemoveHeader(htmlString) {
    if (htmlString.indexOf("<!") >= 0) {
        htmlString = htmlString.substring(htmlString.indexOf("\">") + 2);
    }

    return htmlString;
}

function customReplace(oldString, replacement, tag) {
    var openTag = "<" + tag + ">";
    var closeTag = "</" + tag + ">";

    var i1 = oldString.indexOf(openTag) + openTag.length;
    var i2 = oldString.indexOf(closeTag);

    return oldString.substring(0, i1) + replacement.replace(/<\/?table>/g, "") + oldString.substring(i2);
}

//Switch the "Open" and "Close" state per click then slide up/down (depending on open/close state)
function addModuleToggle(moduletitle) {
    moduletitle.click(function () {
        $(this).toggleClass("active").next().toggle(); // animated toggle: .slideToggle("slow");
        // Scroll down the report
        $(this).next().find(".module-report").attr(
            {
                scrollTop: $(this).next().find(".module-report").attr("scrollHeight")
            });
        $(this).find("span").toggleClass("ui-icon-circle-triangle-s");
        return false;
    });
}

function clickNodes(tag, exceptionTag, exists, searchMode) {
    document.alreadyClicked = new Array();

    do {
        document.goOn = false;

        $(tag).each(function (i, j) {
            if (($.inArray(j, document.alreadyClicked) == -1)) {
                var length = 0;

                if (searchMode == 'next')
                    length = $(j).next(exceptionTag).length;
                if (searchMode == 'nextAll')
                    length = $(j).nextAll(exceptionTag).length;
                if (searchMode == 'children')
                    length = $(j).children('a').children(exceptionTag).length;
                if (searchMode == 'parents') {
                    if ($(j).hasClass(exceptionTag))
                        length = 1;
                }

                var condition = true;

                if (exists == true) condition = length > 0
                else condition = length == 0;

                if (condition == true) {
                    j.click();
                    document.goOn = true;
                    document.alreadyClicked.push(j);
                }
            }
        });
    }
    while (document.goOn)

    document.alreadyClicked = null;
}

function expandDetails() {
    clickNodes("h2", '.ui-icon-minus', false, 'next');
    clickNodes("h3", '.ui-icon-circle-triangle-s', false, 'children');
}

function expandTestcases() {
    clickNodes("h2", '.ui-icon-minus, .pre-testsuite', false, 'nextAll');
}

function expandFailed() {
    clickNodes("h2", 'Failed', true, 'parents');
    clickNodes("h3", 'Failed', true, 'parents');
}

function collapseAll() {
    clickNodes("h3", '.ui-icon-circle-triangle-s', true, 'children');
    $('#treeList').treeList('closeNode', $('#treeList').find('li'));
}

$(document).ready(function () {
    document.browserInfo = getBrowserInformation();
    var b = document.browserInfo.BrowserName;

    if ((b === "Chrome" || b === "Firefox") && XSLTProcessor) {
        initChromeTransform().then(function () {
            chromeXsltProcessor = new XSLTProcessor();
            chromeXsltProcessor.importStylesheet(chromeStyleSheet.documentElement);
            chromeXsltProcessor.setParameter(null, 'ReportPath', ReportPath)
            var outputFragment = chromeXsltProcessor.transformToFragment(chromeDataFile, document);
            document.getElementById('RxContent').innerHTML = "";
            document.getElementById('RxContent').appendChild(outputFragment);
            document.RxProcessMode = "Chrome";

            fixFirefoxMissingOutputEscaping();

            afterRootTransform();
        })
            .catch(function (e) {
                afterRootTransform();
            });

    } else {
        try {
            var test = new ActiveXObject("Msxml2.DOMDocument.6.0");
            initXmlIE();
        } catch (e) {
            // checkForChromeOrEdgeWithoutDisabledWebSecurity();
        }

        afterRootTransform();
    }

});

// workaround for FF not supporting disable-output-escaping in XSL transformations
function fixFirefoxMissingOutputEscaping() {
    if (document.browserInfo.BrowserName === "Firefox") {
        Array.from(document.querySelectorAll(".literalHtml")).forEach(function (e) {
            if (e.textContent && e.textContent.indexOf("<") >= 0) {
                e.innerHTML = e.textContent;
                e.classList.remove("literalHtml");
            }
        });
    }
}

function afterRootTransform() {
    if (rxDataFileNotFound == true) {
        addErrorMessageBox("The data file (" + getDataFilePath() + ") or transformation cannot be accessed. "
            + "Possible reasons include:<br>"
            + "- The file does not exist in the current location.<br>"
            + "- Chrome-based browsers and Firefox cannot load local data files with file:// URLs without allowing file access.<br>"
            + "- The test run is in progress and the data file is locked due to frequent saving operations.<br>"
            + "- Wait until the test run has finished and increase the auto-save interval of the report for future test executions.<br>"
            + "- The report contains invalid XML/XHTML, e.g. unclosed tags."
            + " Check code that uses the Report.LogHtml method to only log well-formed XHTML.");
    } else {
        var treeList = $('#treeList');
        treeList.treeList();
        addModuleToggle($("h3.module-title"));

        tb_init('a.thickbox, area.thickbox, input.thickbox');
        expandFailed();

        ShowChart();
    }

    decodeAllHrefAndSrcUris(document.body);
}

function initXmlIE() {
    document.RxProcessMode = "IE";

    initMSXML();

    var processor = cache.createProcessor();
    processor.input = src;
    processor.transform();

    var d = document.getElementById('RxContent');
    d.innerHTML = processor.output;
}

function ShowChart() {
    try {
        var success = parseInt($("#testCasesPie").attr('totalsuccesstestcasecount'));
        var failed = parseInt($("#testCasesPie").attr('totalfailedtestcasecount'));
        var ignored = parseInt($("#testCasesPie").attr('totalblockedtestcasecount'));

        line1 = [];
        colors = [];

        if (success > 0) {
            line1.push([success + 'x Success', success]);
            colors.push("#42AF6F");
        }

        if (failed > 0) {
            line1.push([failed + 'x Failed', failed]);
            colors.push("#e10000");
        }

        if (ignored > 0) {
            line1.push([ignored + 'x Blocked', ignored]);
            colors.push("#777");
        }

        plot1 = $.jqplot('testCasesPie', [line1],
            {
                title: '',
                seriesDefaults: {
                    renderer: $.jqplot.PieRenderer,
                    rendererOptions: {
                        padding: 10,
                        sliceMargin: 3,
                        shadow: false
                    }
                },
                seriesColors: colors,
                grid: {
                    borderWidth: 0,
                    shadow: false,
                    background: '#ffffff'
                },
                legend: {
                    show: true,
                    location: 'w',
                    xoffset: 12,
                    yoffset: 12
                }
            });
    } catch (e) {
        //throw e;
    }
}

function disableControlsContainer() {
    document.suppress = true;
}


function initMSXML() {
    if (!rxCacheLoaded) {
        var filename = getDataFilePath();

        src = new ActiveXObject("Msxml2.DOMDocument.6.0");
        src.async = false;
        src.load(filename);

        if (src.xml == "")
            rxDataFileNotFound = true;

        ss = new ActiveXObject('MSXML2.FreeThreadedDOMDocument.6.0');
        ss.async = false;
        ss.load("RanorexReport.xsl");

        cache = new ActiveXObject("Msxml2.XSLTemplate.6.0");
        cache.stylesheet = ss;

        rxCacheLoaded = true;
    }
}

function initChromeTransform() {
    if (!chromeCacheLoaded) {
        return Promise.all([
            loadXlsFile()
                .then(function (text) {
                    chromeStyleSheet = (new window.DOMParser()).parseFromString(text, "text/xml")
                }),
            loadDataFile()
                .then(function (text) {
                    chromeDataFile = (new window.DOMParser()).parseFromString(text, "text/xml")
                })
                .then(function () {
                    chromeCacheLoaded = true;
                })
                .catch((function (e) {
                    rxDataFileNotFound = true;
                    throw e;
                }))
        ]);
    }

    return Promise.resolve();
}

function loadXlsFile() {
    return new Promise(loadXlsFilePromiseHelper);
}

// Workaround method because IE does not support (arg1, arg2) => { ... } syntax.
function loadXlsFilePromiseHelper(resolve, reject) {
    loadFile("RanorexReport.xsl", resolve, reject);
}

function loadDataFile() {
    return new Promise(loadDataFilePromiseHelper);
}

// Workaround method because IE does not support (arg1, arg2) => { ... } syntax.
function loadDataFilePromiseHelper(resolve, reject) {
    loadFile(getDataFilePath(), resolve, reject);
}

function loadFile(file, resolve, reject) {
    var xhr = new XMLHttpRequest();
    xhr.open("GET", file, true);
    xhr.onload = function () {
        if (xhr.readyState === 4) {
            if (xhr.status === 200 || xhr.status === 0) {
                resolve(xhr.responseText);
            }
        }

        reject("Failed to load file " + file);
    };
    xhr.onerror = function () {
        reject("Failed to load file " + file);
    };
    xhr.send();
}

function getBrowserInformation() {
    var nVer = navigator.appVersion;
    var nAgt = navigator.userAgent;
    var browserName = navigator.appName;
    var fullVersion = '' + parseFloat(navigator.appVersion);
    var majorVersion = parseInt(navigator.appVersion, 10);
    var nameOffset, verOffset, ix;

    // In Opera, the true version is after "Opera" or after "Version"
    if ((verOffset = nAgt.indexOf("Opera")) != -1) {
        browserName = "Opera";
        fullVersion = nAgt.substring(verOffset + 6);
        if ((verOffset = nAgt.indexOf("Version")) != -1)
            fullVersion = nAgt.substring(verOffset + 8);
    }
    // In MSIE, the true version is after "MSIE" in userAgent
    else if ((verOffset = nAgt.indexOf("MSIE")) != -1) {
        browserName = "Microsoft Internet Explorer";
        fullVersion = nAgt.substring(verOffset + 5);
    }
    // In Chrome, the true version is after "Chrome"
    else if ((verOffset = nAgt.indexOf("Chrome")) != -1) {
        browserName = "Chrome";
        fullVersion = nAgt.substring(verOffset + 7);
    }
    // In Safari, the true version is after "Safari" or after "Version"
    else if ((verOffset = nAgt.indexOf("Safari")) != -1) {
        browserName = "Safari";
        fullVersion = nAgt.substring(verOffset + 7);
        if ((verOffset = nAgt.indexOf("Version")) != -1)
            fullVersion = nAgt.substring(verOffset + 8);
    }
    // In Firefox, the true version is after "Firefox"
    else if ((verOffset = nAgt.indexOf("Firefox")) != -1) {
        browserName = "Firefox";
        fullVersion = nAgt.substring(verOffset + 8);
    }
    // In most other browsers, "name/version" is at the end of userAgent
    else if ((nameOffset = nAgt.lastIndexOf(' ') + 1) <
        (verOffset = nAgt.lastIndexOf('/'))) {
        browserName = nAgt.substring(nameOffset, verOffset);
        fullVersion = nAgt.substring(verOffset + 1);
        if (browserName.toLowerCase() == browserName.toUpperCase()) {
            browserName = navigator.appName;
        }
    }
    // trim the fullVersion string at semicolon/space if present
    if ((ix = fullVersion.indexOf(";")) != -1)
        fullVersion = fullVersion.substring(0, ix);
    if ((ix = fullVersion.indexOf(" ")) != -1)
        fullVersion = fullVersion.substring(0, ix);

    majorVersion = parseInt('' + fullVersion, 10);
    if (isNaN(majorVersion)) {
        fullVersion = '' + parseFloat(navigator.appVersion);
        majorVersion = parseInt(navigator.appVersion, 10);
    }

    var result = {};
    result["BrowserName"] = browserName;
    result["Version"] = fullVersion;
    return result;
}

function checkForChromeOrEdgeWithoutDisabledWebSecurity() {
    if (document.browserInfo["BrowserName"].indexOf("Chrome") == -1)
        return;

    try {
        var doc = $.xsl.load(getDataFilePath());
    } catch (e) {
        if (navigator && navigator.userAgent && navigator.userAgent.indexOf("Edge") != -1) {
            addErrorMessageBox("<b>Error:</b> Microsoft Edge prevents access of local file content.");
        } else {
            addErrorMessageBox("<b>Error:</b> Could not load data file. Please start Chrome with <b>--allow-file-access-from-files</b> flag to allow access to local files.");
        }
    }
}

// function getDataFilePath() {
//     var uri = document.location.toString();
//
//     if (uri.indexOf(".wbn?") > 0) {
//         var path = uri.substring(uri.lastIndexOf(".wbn?") + 5);
//         path = path.substring(path.lastIndexOf('/') + 1);
//         return path + '.data';
//     }
//
//     var path = document.location.pathname;
//     path = path.substring(path.lastIndexOf('/') + 1);
//     return path + '.data';
// }
function getDataFilePath() {
    return ReportPath + 'RanorexReport.html.data';
}

function addErrorMessageBox(message) {
    var html = "<center><table border='1' cellpadding='1' cellspacing='1' style='width: 800px; background-color:#EEEEEE'><tbody><tr><td>";
    html += message;
    html += "</td></tr></tbody></table><center>"
    $('#RxContent').html(html);
}

function SetStudioMode() {
    rxToolMode = ToolMode.STUDIO;
}

function SetToolMode() {
    rxToolMode = ToolMode.TOOL;
}

function SetBrowserMode() {
    rxToolMode = ToolMode.BROWSER;
}

function SetSpyButton(container, button, text) {
    if (text == undefined || button == undefined || container == undefined)
        return;

    if (rxToolMode == ToolMode.STUDIO) {
        if (container.hasClass("three-columns")) {
            container.removeClass("three-columns");
            container.addClass("two-columns");
        } else {
            container.removeClass("two-columns");
        }

        button.css({"height": 0, "width": 0});
        button.hide();
    } else
        text.innerHTML = 'Open in Spy';
}

function showBinding(self) {
    $(self).toggle();
    $(self).parent('.binding').children('table').toggle();
    $(self).parent('.binding').children('.hideBinding').toggle();
}

function hideBinding(self) {
    $(self).toggle();
    $(self).parent('.binding').children('table').toggle();
    $(self).parent('.binding').children('.showBinding').toggle();
}

function decodeAllHrefAndSrcUris(root) {
    decodeAttributeInElements(root.getElementsByTagName('img'), 'src');
    decodeAttributeInElements(root.getElementsByTagName('a'), 'href');
}

var elementsWithDecodedUrls = [];

function decodeAttributeInElements(collection, attributeName) {
    for (i = 0; i < collection.length; i++) {
        var item = collection[i];

        if (!item || $.inArray(item, elementsWithDecodedUrls) !== -1) {
            continue;
        }

        elementsWithDecodedUrls.push(item);

        var value = item.getAttribute(attributeName);
        if (value) {
            try {
                var decodedValue = decodeURIComponent(value);
                if (value != decodedValue)
                    item.setAttribute(attributeName, decodedValue);
            } catch (ex) { /* do nothing */
            }
        }
    }
}