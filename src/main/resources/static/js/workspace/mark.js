/**
 * 为mark标注提供加载requirement和提交标记的服务
 */

function setRequirement(){
	//加载要求
	var projectId = getCookie("projectId");
	var publisherId = getCookie("publisherId");
	$.ajax({
		url:  "/myProjects/getProjectRequirement/"+publisherId+"/"+projectId,
		type: "get",
		success: function(data){
			$("#requirementArea").append(data);
		}
	});
}

function submitTag(){
	//提交标签
	//这种标记只有图片没有标签
	var type = "area";
	var userId = getCookie("userId");
	var projectId = getCookie("projectId");
	var publisherId = getCookie("publisherId");
	var pictureId = getCookie("pictureId");
	var remark = "";
	var width = getCookie("pictureWidth");
	var height = getCookie("pictureHeight");
	var canvas = document.getElementById("penal");
	var tag = canvas.toDataURL("image/png");
	var base64 = tag.substring(22);

	$.ajax({
		url: "/workspace/submit/"+type+"/"+userId+"/"+projectId+"/"+publisherId+"/"+pictureId,
		type: "post",
		data: {
			'base64' : base64,
			'remark' : remark,
			'width' : width,
			'height' : height,
		},
		success: function(){
			alert("上传成功");
			clearCanvas();
			getNewPicture();//这个方法在getPicture.js里面
		}
	});
}

function submitTag_edit(){
	//提交修改的标记
	var type = "area";
	var userId = getCookie("userId");
	var projectId = getCookie("projectId");
	var publisherId = getCookie("publisherId");
	var pictureId = getCookie("pictureId");
	var remark = "";
	var width = getCookie("pictureWidth");
	var height = getCookie("pictureHeight");
	var canvas = document.getElementById("penal");
	var tag = canvas.toDataURL("image/png");
	var base64 = tag.substring(22);

	$.ajax({
		url: "/workspace/submit/"+type+"/"+userId+"/"+projectId+"/"+publisherId+"/"+pictureId,
		type: "post",
		data: {
			'base64' : base64,
			'remark' : remark,
			'width' : width,
			'height' : height,
		},
		success: function(){
			alert("修改成功");
			history.back();//返回上个界面
		}
	});
}

function setPreviousTag(){
	//设置以前的图片
	var userId = getCookie("userId");
	var projectId =getCookie("projectId");
	var publisherId = getCookie("publisherId");
	var projectType = getCookie("projectType");
	var pictureId = getCookie("pictureId");
	$.ajax({
		url: "/workspace/getPictureTag/"+projectType+"/"+userId+"/"+projectId+"/"+publisherId+"/"+pictureId,
			type:"post",
		    async:true, //同步
	    	cache:false,
			success: function(data){
				
			}
	     });
	
	var canvas = document.getElementById("penal");
	var ctx = canvas.getContext("2d");
	var u = "/workspace/getPictureTag/"+projectType+"/"+userId+"/"+projectId+"/"+publisherId+"/"+pictureId;
	preImage(u, function(){
		ctx.drawImage(this,0,0);
	})
}

function preImage(url , callback){
	var img = new Image();
	img.src = url;
	if(img.complete){
		callback.call(img);
		return;
	}
	img.onload = function(){
		callback.call(img);
	}
}

function clearCanvas(){
	//清空画板
	clearAllMark();//这个方法在canvas.js里
}