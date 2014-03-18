// check whether the pattern of the upload picture rigtht.
function checkFileType() {
	var file = $("#imgFile").val();
	if (file == "") {
		alert("Please upload a picture.");
		return;
	} else {
		// get the file type.
		var fileType = file.substring(file.lastIndexOf(".") + 1);

		// check whether exist the file type.
		if ("" == fileType || null == fileType) {
			alert("The pattern of file is not correct.");
			return;
		}
		// allowed upload picture pattern.
		var fileTypes = [ "jpg", "jpeg", "png", "gif", "bmp" ];
		var typeLength = fileTypes.length;
		var isRightType = 0;

		// chec whether the uploaded picture pattern is within tbe scope
		// of the allowed picture pattern.
		for ( var i = 0; i < typeLength; i++) {
			if (fileTypes[i] == fileType.toLowerCase()) {
				isRightType = 1;
			}
		}
		if (0 == isRightType) {
			alert("The pattern of file is not correct.");
			return;
		} else {
			imgForm1.submit();
		}
	}
}

$(document).ready(function() {

	// check whether a picture have been uploaded by check whether
		// the 'srcImg' ID is null, then show the upload avatar dialog
		// and transient background.
		var cropbox = $("#srcImg").attr("src");
		if ("" != cropbox) {
			var content = $("#uploadWindow");
			var bg = jQuery("#bg");
			bg.show();
			content.show();
		}

		// close the upload avatar dialog by click the close button.
		// when close the dialog it will delete the original upload
		// picure by the picture's name which is restored on the server.
		$("#uploadWindowCloseButton").click(function(event) {
			var imgName = $("#imgName").val();
			var path = $("#path").val();
			$("#uploadWindow").html();
			$("#uploadWindow").css("display", "none");
			$("#bg").css("display", "none");
			$.get(path + "/deleteImg.do", {
				"imgName" : imgName
			});
		});

		$("#uploadWindow").click(function(event) {
			event.stopPropagation();
		});

		$("#save").click(function() {
			imgForm.submit();
		});

		// to re-elected another picture if the user don't like
		// the current uploaded one, he/she can click the 're-election'
		// button, and the system will delete by the picture name
		// the pre one which restored on the server.
		$("#rechoose").click(function() {
			var imgName = $("#imgName").val();
			var path = $("#path").val();
			$.get(path + "/deleteImg.do", {
				"imgName" : imgName
			});
		});

		// if user haven't upload a avatar before, system will display
		// the default one, otherwise it will display the avater of the
		// user.
		if ("" != $("#avatar").val()) {
			$("#defaultImg").attr("src", $("#avatar").val());
		} else {
			$("#defaultImg").attr("src",
					"resource/images/upload-avatar/defaultImg.jpg");
		}
		// the tip message that the file type comes from the background
		// is not correct.
		var errorMassage = $("#errorFileType").val();
		if ("" != errorMassage) {
			alert("The pattern of file is not correct.");
		}

	});

// Remember to invoke within jQuery(window).load(...)
// If you don't, Jcrop may not initialize properly
$(document)
		.ready(function() {
			// the initiate position of the cut picture.
				var x0 = 0;
				var y0 = 0;
				var x1 = 0;
				var y1 = 0;

				// if the source picture is bigger than the panel where,
				// to diplay the uploaded picture, reduce the source
				// picture at the same proportion and then put it on the
				// dispaly panel.
				if ("" != $("#imgSrcWidth").val()
						|| null != $("#imgSrcWidth").val()) {

					// get the width and height of the original upload
					// picture.
					var imgSrcWidth = $("#imgSrcWidth").val();
					var imgSrcHeight = $("#imgSrcHeight").val();

					// get the width and height of the picture display
					// panel.
					var srcImgBoxWidth = $("#srcImg_box").width();
					var srcImgBoxHeight = $("#srcImg_box").height();

					// the initiate of the tailor area is a square,
					// and it width is base on the area of the
					// uploaded picture.
					var tailorWidth;

					// when width > height of the original uploaded picture.
					if (parseInt(imgSrcWidth) > parseInt(imgSrcHeight)) {

						// when the width of the original picture is larger
						// than the width of the picture display panel.
						if (parseInt(imgSrcWidth) > parseInt(srcImgBoxWidth)) {

							// when the height of the original picture is larger
							// than the height of the picture display panel.
							if (parseInt(imgSrcHeight) > parseInt(srcImgBoxHeight)) {

								// the rate of the width divide the height of
								// the original picture.
								var srcRate = parseInt(imgSrcWidth)
										/ parseInt(imgSrcHeight);

								// the rate of the width divide the height of
								// the picture display panel.
								var boxRate = parseInt(srcImgBoxWidth)
										/ parseInt(srcImgBoxHeight);

								// call the accSub method to get the pricise
								// result of 'srcRate' minus 'boxRate'.
								var sub = accSub(srcRate, boxRate);

								// when the rate of the width divide the height
								// of the original picture is larger than the 
								// rate of picture display panel.
								if (0 > sub) {

									// change the css of the upload picture.
									$("#srcImg").css("height",
											$("#srcImg_box").height());
									
									// reduce the value of the imgSrcWidth and imgSrcHeight
									imgSrcWidth = Math.round(parseInt(imgSrcWidth)
													* ((parseInt(imgSrcHeight) - (parseInt(imgSrcHeight) - parseInt(srcImgBoxHeight))) 
															/ parseInt(imgSrcHeight)));
									imgSrcHeight = Math.round($("#srcImg_box").height());
									
									// make the tailor area width 80% of the smallest border
									// of the re-size uploaded picture.
									tailorWidth = (parseInt(imgSrcHeight) * 8) / 10;
								} 
								
								// when the rate of the width divide the height
								// of the original picture is less-than-or-equal-to 
								// the rate of picture display panel.
								else {
									$("#srcImg").css("width",
											$("#srcImg_box").width());
									imgSrcHeight = Math.round(parseInt(imgSrcHeight)
													* ((parseInt(imgSrcWidth) - (parseInt(imgSrcWidth) - parseInt(srcImgBoxWidth))) 
															/ parseInt(imgSrcWidth)));
									imgSrcWidth = Math.round($("#srcImg_box").width());
									
									// make the tailor area width 80% of the smallest border
									// of the re-size uploaded picture.
									tailorWidth = (parseInt(imgSrcHeight) * 8) / 10;
								}

							} 
							
							// when the height of the original picture is 
							// less-than-or-equal-to the height of the 
							// picture display panel.
							else {
								$("#srcImg").css("width",
										$("#srcImg_box").width());
								imgSrcHeight = Math
										.round(parseInt(imgSrcHeight)
												* ((parseInt(imgSrcWidth) - (parseInt(imgSrcWidth) - parseInt(srcImgBoxWidth))) 
														/ parseInt(imgSrcWidth)));
								imgSrcWidth = Math.round($("#srcImg_box")
										.width());
								
								// make the tailor area width 80% of the smallest border
								// of the re-size uploaded picture.
								tailorWidth = (parseInt(imgSrcHeight) * 8) / 10;
							}
						} 
						
						// when the width of the original picture is 
						// less-than-or-equal-to the width of
						// the picture display panel.
						else {
							
							// make the tailor area width 80% of the smallest border
							// of the re-size uploaded picture.
							tailorWidth = (parseInt(imgSrcHeight) * 8) / 10;
						}

					}
					
					// when width is less-than-or-equal-to the height of the 
					// original uploaded picture.
					else {
						if (parseInt(imgSrcHeight) > parseInt(srcImgBoxHeight)) {
							$("#srcImg").css("height",
									$("#srcImg_box").height());
							imgSrcWidth = Math
									.round(parseInt(imgSrcWidth)
											* ((parseInt(imgSrcHeight) - (parseInt(imgSrcHeight) - parseInt(srcImgBoxHeight))) 
													/ parseInt(imgSrcHeight)));
							imgSrcHeight = Math
									.round($("#srcImg_box").height());
							
							// make the tailor area width 80% of the smallest border
							// of the re-size uploaded picture.
							tailorWidth = (parseInt(imgSrcWidth) * 8) / 10;
							
						} else {
							
							// make the tailor area width 80% of the smallest border
							// of the re-size uploaded picture.
							tailorWidth = (parseInt(imgSrcWidth) * 8) / 10;
						}
					}
					
					// restore the value of imgSrcWidth and imgSrcHeight
					// after they be changed.
					$("#srcChangedWidth").val(imgSrcWidth);
					
					// calculate the top size when the re-size uploaded picture
					// be diaplay in the center of the picture display panel.
					var top = (parseInt(srcImgBoxHeight) - parseInt(imgSrcHeight)) / 2;
					$("#imgAround").css("margin-top",
							top + "px");

					// calculate the initiate select position.
					x0 = (parseInt(imgSrcWidth) - parseInt(tailorWidth)) / 2;
					y0 = (parseInt(imgSrcHeight) - parseInt(tailorWidth)) / 2;
					x1 = parseInt(x0) + parseInt(tailorWidth);
					y1 = parseInt(y0) + parseInt(tailorWidth);
				}

				// call the function of jcrop.
				// need add the jquery-1.6.4.js,
				// jquery.Jcrop.js,jquery.Jcrop.css.
				$('#srcImg').Jcrop( {
					setSelect : [ x0, y0, x1, y1 ], // initiate the select area.
					onChange : showPreview, // call the showPreview method.
					aspectRatio : 1, // the rate of width/height of select
					// area.
					allowSelect : false
				// not allow to new a select area.
						});
			});

// Our simple event handler, called from onChange and onSelect
// event handlers, as per the Jcrop invocation above
function showPreview(coords) {

	// get the tailor position of the picture.
	$('#x').val(coords.x);
	$('#y').val(coords.y);
	$('#width').val(coords.w);
	$('#height').val(coords.h);

	if (parseInt(coords.w) > 0) {

		// Calculate the reduce rate of the preview picture.
		// it comes from the calculate result of the width and height of display
		// area and tailor area.
		var rx = $("#preview_box150").width() / coords.w;
		var ry = $("#preview_box150").height() / coords.h;

		// control the css and show style fo the preview picture by the reduce
		// rate.
		$("#preview150").css( {

			// the width of the preview picture equals the result of
			// the reduce rate plus width of the origianl upload picture.
			width : Math.round(rx * $("#srcImg").width()) + "px",

			// the height of the preview picture equals the result of
			// the reduce rate plus height of the origianl upload picture.
			height : Math.round(rx * $("#srcImg").height()) + "px",

			marginLeft : "-" + Math.round(rx * coords.x) + "px",
			marginTop : "-" + Math.round(ry * coords.y) + "px"
		});

		// Calculate the reduce rate of the preview picture.
		// it comes from the calculate result of the width and height of display
		// area and tailor area.
		var rx = $("#preview_box65").width() / coords.w;
		var ry = $("#preview_box65").height() / coords.h;

		// control the css and show style fo the preview picture by the reduce
		// rate.
		$("#preview65").css( {

			// the width of the preview picture equals the result of
			// the reduce rate plus width of the origianl upload picture.
			width : Math.round(rx * $("#srcImg").width()) + "px",

			// the height of the preview picture equals the result of
			// the reduce rate plus height of the origianl upload picture.
			height : Math.round(rx * $("#srcImg").height()) + "px",
			marginLeft : "-" + Math.round(rx * coords.x) + "px",
			marginTop : "-" + Math.round(ry * coords.y) + "px"
		});

		// Calculate the reduce rate of the preview picture.
		// it comes from the calculate result of the width and height of display
		// area and tailor area.
		var rx = $("#preview_box30").width() / coords.w;
		var ry = $("#preview_box30").height() / coords.h;

		// control the css and show style fo the preview picture by the reduce
		// rate.
		$("#preview30").css( {

			// the width of the preview picture equals the result of
			// the reduce rate plus width of the origianl upload picture.
			width : Math.round(rx * $("#srcImg").width()) + "px",

			// the height of the preview picture equals the result of
			// the reduce rate plus height of the origianl upload picture.
			height : Math.round(rx * $("#srcImg").height()) + "px",
			marginLeft : "-" + Math.round(rx * coords.x) + "px",
			marginTop : "-" + Math.round(ry * coords.y) + "px"
		});
	}
}

// drag the div window which display the upload picture.
// specify the id 'requestDetailTile' to be the drag handle.
// need add the jquery-ui-1.8.18.custom.min.js, jquery-ui-1.8.18.custom.css.
$(function() {
	$("#uploadWindow").draggable( {
		handle : "#uploadWindowTile"
	});
});

// calculate the precise methmetic result of (arg2 - arg1)
function accSub(arg1, arg2) {
	var r1, r2, m, n;

	try {
		r1 = arg1.toString().split(".")[1].length;
	} catch (e) {
		r1 = 0;
	}

	try {
		r2 = arg1.toString().split(".")[1].length;
	} catch (e) {
		r2 = 0;
	}
	m = Math.pow(10, Math.max(r1, r2));
	n = (r1 > r2) ? r1 : r2;

	return ((arg1 * m - arg2 * m) / m).toFixed(n);
}
