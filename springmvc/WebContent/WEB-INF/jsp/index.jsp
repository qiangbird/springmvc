<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
    String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
					+ request.getServerName() + ":" + request.getServerPort()
					+ path + "/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<base href="<%=basePath%>">    
    <title></title>
    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="expires" content="0">  
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <script src="resource/js/upload-avatar/jquery-1.6.4.js"></script>
    <script src="resource/js/upload-avatar/jquery.Jcrop.js"></script> 
    <script src="resource/js/upload-avatar/upload-avatar.js"></script> 
    <script type="text/javascript" src="resource/js/upload-avatar/jquery-ui-1.8.18.custom.js"></script>
    <link rel="stylesheet" href="resource/css/upload-avatar/jquery.Jcrop.css" type="text/css" />
    <link rel="stylesheet" href="resource/css/upload-avatar/upload-avatar.css" type="text/css" />
    <link type="text/css" href="resource/css/upload-avatar/jquery-ui-1.8.18.custom.css" rel="stylesheet" /> 
</head>
<body >
    <input type="hidden" id="errorFileType" value="${errorFileType}">
    <input type="hidden" id="path" value="<%=request.getContextPath()%>">
    <div id="content_body">
        <div align="center">
            <form action="<%=request.getContextPath()%>/uploadImg.do"
                     name="imgForm1" enctype="multipart/form-data" method="POST">
                <img id="defaultImg" src="resource/images/upload-avatar/defaultImg.jpg" />
                <input type="hidden" id="avatar" value="${avatarPicture}">
                <input type="file" name="imgFile" id="imgFile" onchange="checkFileType();" style="display:none">
                <input type="button" id="upbutton"  Onclick="imgFile.click();">
            </form>
        </div>
        <div id="bg" class="bg" style="display: none;"></div>
        <div id="uploadWindow" class="uploadWindow" style="display: none;">
            <div id="left_border"></div>
            <div id="right_border"></div>
            <div id="bottom_border"></div>
            <input type="hidden" id="imgSrcWidth"  value="${imgWidth}"></input>
            <input type="hidden" id="imgSrcHeight"  value="${imgHeight}"></input>
            <div id="uploadWindowTile">上传头像
                <div id="uploadWindowCloseButton">X</div>
            </div>
            <div class="srcImgJcrop">
                <div id="srcImg_box">
                    <div id="imgAround">
                        <img id="srcImg" src="${imgSrc}" />
                    </div>
                </div>
                <div id="tip_words">拖拽、缩放左侧图片，生成满意的头像。</div>
                <div id="preview_box150" class="crop_preview150">
                    <img id="preview150" src="${imgSrc}" />
                </div>
                <div id="size150">150 x 150</div>
                <div id="preview_box65" class="crop_preview65">
                    <img id="preview65" src="${imgSrc}" />
                </div>
                <div id="size65">65 x 65</div>
                <div id="preview_box30" class="crop_preview30">
                    <img id="preview30" src="${imgSrc}" />
                </div> 
                <div id="size30">30 x 30</div>
             </div>
		     <form action="<%=request.getContextPath()%>/saveCutImg.do"
                     name="imgForm" method="POST">
                <input type="hidden" name="imgName" id="imgName" value="${imgName}" /><label>
                <input type="hidden" size="4" id="x" name="x" /></label> <label>
                <input type="hidden" size="4" id="y" name="y" /></label> <label>
                <input type="hidden" size="4" id="width" name="width" /></label> <label>
                <input type="hidden" size="4" id="height" name="height" /></label> <label>
                <input type="hidden" size="4" id="srcChangedWidth" name="srcChangedWidth" /></label>
                <input type="button" id="save" name="save" value="保存头像"/>
                <input type="button" id="rechoose" name="rechoose" value="重新选择" Onclick="imgFile.click();" />
             </form>
        </div>
    </div>
</body>
</html>