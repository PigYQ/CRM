<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
	<%
	String basePath=request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
	%>
	<base href="<%=basePath%>">
<meta charset="UTF-8">

<link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet" />
<link href="jquery/bootstrap-datetimepicker-master/css/bootstrap-datetimepicker.min.css" type="text/css" rel="stylesheet" />
<link rel="stylesheet" type="text/css" href="jquery/bs_pagination-master/css/jquery.bs_pagination.min.css">

<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/js/bootstrap-datetimepicker.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/locale/bootstrap-datetimepicker.zh-CN.js"></script>
<script type="text/javascript" src="jquery/bs_pagination-master/js/jquery.bs_pagination.min.js"></script>
<script type="text/javascript" src="jquery/bs_pagination-master/localization/en.js"></script>
<script type="text/javascript">

	$(function(){
		//当市场活动主页面加载完成，查询一页市场活动
		queryActivityByConditionForPage(1,10);

		//给全选框添加单击方式
		$("#checkAll").click(function (){
			$("#tbody input[type='checkbox']").prop('checked',this.checked);
		});

		//给市场活动复选框添加单击事件
		$("#tbody").on("click","input[type='checkbox']",function (){
			if($("#tbody input[type='checkbox']").size()===$("#tbody input[type='checkbox']:checked").size()){
				$("#checkAll").prop('checked',true);
			}else {
				$("#checkAll").prop('checked',false);
			}
		});

		//给日期输入框绑定日历
		$(".my-date").datetimepicker({
			language:'zh-CN',
			format:'yyyy-mm-dd',
			minView:'month',
			initialDate:new Date(),
			autoclose:true
		});

		//给创建市场活动按钮添加单击事件
		$("#createActivityBtn").click(function (){
			//清空窗口数据
			$("#createActivityForm").get(0).reset();
			//弹出创建市场活动模态窗口
			$("#createActivityModal").modal("show");
			//给保存按钮添加单击事件
			$("#saveActivity").click(function (){
				//收集参数
				let owner = $("#create-marketActivityOwner").val();
				let name = $.trim($("#create-marketActivityName").val());
				let startDate = $("#create-startTime").val();
				let endDate = $("#create-endTime").val();
				let cost = $.trim($("#create-cost").val());
				let description = $.trim($("#create-describe").val());
				//表单验证
				if(owner===""){
					alert("所有者不能为空！");
					return;
				}
				if (name===""){
					alert("名称不能为空！");
					return;
				}
				if (startDate!=="" && endDate!==""){
					if (startDate>endDate){
						alert("结束日期不能比开始日期小！");
						return;
					}
				}
				let regExp = /^(([0-9]\d*)|0)$/;
				if (!regExp.test(cost)){
					alert("成本只能是非负整数");
					return;
				}
				$.ajax({
					url:'workbench/activity/saveCreateActivity.do',
					data:{
						owner:owner,
						name:name,
						startDate:startDate,
						endDate:endDate,
						cost:cost,
						description:description
					},
					type:'post',
					dataType:'json',
					success:function (data){
						if (data.code==="1"){
							//关闭模态窗口
							$("#createActivityModal").modal("hide");
							//刷新市场活动列表
							queryActivityByConditionForPage(1,$("#pages").bs_pagination('getOption','rowsPerPage'));
						}else {
							//提示信息
							alert(data.msg);
						}
					}
				});
			});
		});

		//删除市场活动
		$("#deleteActivityBtn").click(function (){
			//获取所有选中的市场活动集
			let checkedIds = $("#tbody input[type='checkbox']:checked");
			if (checkedIds.size()===0){
				alert("请选择要删除的市场活动");
				return;
			}
			window.confirm("确定删除选中市场活动吗");
			//收集要删除的市场活动id集
			let ids='';
			$.each(checkedIds,function (){
				ids+= "id="+this.value+"&";
			});
			ids = ids.substr(0,ids.length-1);
			//发送请求
			$.ajax({
				url:'workbench/activity/deleteActivityByIds.do',
				data:ids,
				type:'post',
				dataType:'json',
				success:function (data){
					if(data.code==="0"){
						alert(data.msg);
					}else {
						queryActivityByConditionForPage($("#pages").bs_pagination('getOption','currentPage'),$("#pages").bs_pagination('getOption','rowsPerPage'));
					}
				}
			});
		});

		//修改按钮单击事件
		$("#updateActivity").click(function (){
			//验证
			let checkedIds = $("#tbody input[type='checkbox']:checked");
			if (checkedIds.size()===0){
				alert("请选择要修改的市场活动");
				return;
			}
			if (checkedIds.size()>1){
				alert("只能选择一个市场活动");
				return;
			}
			//获取id
			let id = checkedIds[0].value;
			//发送请求
			$.ajax({
				url:'workbench/activity/queryActivityById.do',
				data:{
					id:id
				},
				type:'post',
				dataType:'json',
				success:function (data){
					$("#edit-id").val(data.id);
					$("#edit-marketActivityOwner").val(data.owner);
					$("#edit-marketActivityName").val(data.name);
					$("#edit-startTime").val(data.startDate);
					$("#edit-endTime").val(data.endDate);
					$("#edit-cost").val(data.cost);
					$("#edit-describe").val(data.description);
				}
			});
			//显示模态窗口
			$("#editActivityModal").modal("show");
		});

		//更新按钮单击事件
		$("#edit-update").click(function (){
			//收集参数
			let id = $("#edit-id").val();
			let owner = $("#edit-marketActivityOwner").val();
			let name = $.trim($("#edit-marketActivityName").val());
			let startDate = $("#edit-startTime").val();
			let endDate = $("#edit-endTime").val();
			let cost = $.trim($("#edit-cost").val());
			let description = $.trim($("#edit-describe").val());
			//表单验证
			if(owner===""){
				alert("所有者不能为空！");
				return;
			}
			if (name===""){
				alert("名称不能为空！");
				return;
			}
			if (startDate!=="" && endDate!==""){
				if (startDate>endDate){
					alert("结束日期不能比开始日期小！");
					return;
				}
			}
			let regExp = /^(([0-9]\d*)|0)$/;
			if (!regExp.test(cost)){
				alert("成本只能是非负整数");
				return;
			}
			//发送请求
			$.ajax({
				url:'workbench/activity/updateActivityById.do',
				data:{
					id:id,
					owner:owner,
					name:name,
					startDate:startDate,
					endDate:endDate,
					cost:cost,
					description:description
				},
				type:'post',
				dataType:'json',
				success:function (data){
					if (data.code==="0"){
						alert(data.msg);
					}else {
						$("#editActivityModal").modal("hide");
						queryActivityByConditionForPage($("#pages").bs_pagination('getOption','currentPage'),$("#pages").bs_pagination('getOption','rowsPerPage'));
					}
				}
			});
		});

		//给查询按钮添加单击事件
		$("#queryActivityBtn").click(function (){
			queryActivityByConditionForPage(1,$("#pages").bs_pagination('getOption','rowsPerPage'));
		});

		//给批量导出添加单击事件
		$("#exportActivityAllBtn").click(function (){
			window.location.href='workbench/activity/exportAllActivity.do';
		});

		//给选择导出按钮添加单击事件
		$("#exportActivityXzBtn").click(function (){
			//获取所有选中的市场活动
			let checkedIds = $("#tbody input[type='checkbox']:checked");
			//验证
			if (checkedIds.size()===0){
				alert("请选择要导出的市场活动");
				return;
			}
			//获取id集
			let ids='';
			$.each(checkedIds,function (){
				ids+="id="+this.value+"&";
			});
			ids = ids.substr(0,ids.length-1);
			window.location.href="workbench/activity/exportActivityByIds.do?"+ids;
			/*$.ajax({
				url:"workbench/activity/exportActivityByIds.do",
				data:ids,
				type:'post',
				dataType:'json',
				async:false,
				success:function (resp){}
			});*/
		});

		//给导入按钮添加单击事件
		$("#importActivityBtn").click(function (){
			//获取文件名
			let activityFileName = $("#activityFile").val();
			let suffix = activityFileName.substr(activityFileName.lastIndexOf(".")+1).toLocaleLowerCase();
			//文件格式验证
			if (suffix!=="xls"){
				alert("只支持xls文件");
				return;
			}
			//获取文件
			let activityFile = $("#activityFile")[0].files[0];
			console.log("获取文件成功");
			//验证文件大小
			let fileSize = activityFile.size;
			if (fileSize>5*1024*1024){
				alert("文件大小不能超过5MB");
				return;
			}
			//FormData是ajax提供的接口，可以模拟键值对向后台提交数据
			//它最大的优势是不但能提交文本数据，还能提交二进制数据
			let formData = new FormData();
			formData.append("activityFile",activityFile);
			//发送请求
			$.ajax({
				url:"workbench/activity/importActivity.do",
				data:formData,
				processData:false,//是否把参数统一转换成字符串，默认为true
				contentType:false,//是否将参数统一按urlencoded编码，默认为true
				type:'post',
				dataType:'json',
				success:function (data){
					if (data.code==="1"){
						alert(data.msg);
						$("#importActivityModal").modal("hide");
						queryActivityByConditionForPage(1,$("#pages").bs_pagination('getOption','rowsPerPage'));
					}else {
						alert(data.msg);
						$("#importActivityModal").modal("show");
					}
				}
			});
		});
	});
	//查询市场活动
	function queryActivityByConditionForPage(pageNo,pageSize){
		var owner = $.trim($("#query-owner").val());
		var name = $.trim($("#query-name").val());
		var startDate = $.trim($("#startTime").val());
		var endDate = $.trim($("#endTime").val());
		$.ajax({
			url:'workbench/activity/queryActivityNums.do',
			data:{
				owner:owner,
				name:name,
				startDate:startDate,
				endDate:endDate,
				pageNo:pageNo,
				pageSize:pageSize
			},
			type:'post',
			dataType:'json',
			success:function (data){
				//遍历所有市场活动
				var htmlStr = "";
				$.each(data.activityList,function (index,obj){
					htmlStr+="<tr class=\"active\">"
					htmlStr+="<td><input type=\"checkbox\" value=\""+obj.id+"\"/></td>"
					htmlStr+="<td><a style=\"text-decoration: none; cursor: pointer;\" onclick=\"window.location.href='workbench/activity/detailActivity.do?id="+obj.id+"'\">"+obj.name+"</a></td>"
					htmlStr+="<td>"+obj.owner+"</td>"
					htmlStr+="<td>"+obj.startDate+"</td>"
					htmlStr+="<td>"+obj.endDate+"</td>"
					htmlStr+="</tr>"
				});
				//显示数据
				$("#tbody").html(htmlStr);
				//全选框设为false
				$("#checkAll").prop('checked',false);
				//显示分页信息
				//计算总共页数
				let totalPages;
				if (data.totalRows%pageSize===0){
					totalPages = data.totalRows/pageSize;
				}else {
					totalPages = data.totalRows/pageSize+1;
				}
				$("#pages").bs_pagination({
					currentPage:pageNo,
					rowsPerPage:pageSize,
					totalRows:data.totalRows,
					totalPages:totalPages,
					visiblePageLinks: 5,
					showGoToPage: true,
					showRowsPerPage: true,
					showRowsInfo: true,
					onChangePage:function (event,obj){
						queryActivityByConditionForPage(obj.currentPage,obj.rowsPerPage);
					}
				});
			}
		})
	}
</script>
</head>
<body>

	<!-- 创建市场活动的模态窗口 -->
	<div class="modal fade" id="createActivityModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 85%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel1">创建市场活动</h4>
				</div>
				<div class="modal-body">
				
					<form class="form-horizontal" role="form" id="createActivityForm">
					
						<div class="form-group">
							<label for="create-marketActivityOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="create-marketActivityOwner">
								  <c:forEach items="${userList}" var="user">
									  <option value="${user.id}">${user.name}</option>
								  </c:forEach>
								</select>
							</div>
                            <label for="create-marketActivityName" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="create-marketActivityName">
                            </div>
						</div>
						
						<div class="form-group">
							<label for="create-startTime" class="col-sm-2 control-label">开始日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control my-date" id="create-startTime" readonly>
							</div>
							<label for="create-endTime" class="col-sm-2 control-label">结束日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control my-date" id="create-endTime" readonly>
							</div>
						</div>
                        <div class="form-group">

                            <label for="create-cost" class="col-sm-2 control-label">成本</label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="create-cost">
                            </div>
                        </div>
						<div class="form-group">
							<label for="create-describe" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<textarea class="form-control" rows="3" id="create-describe"></textarea>
							</div>
						</div>
						
					</form>
					
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" id="saveActivity">保存</button>
				</div>
			</div>
		</div>
	</div>
	
	<!-- 修改市场活动的模态窗口 -->
	<div class="modal fade" id="editActivityModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 85%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel2">修改市场活动</h4>
				</div>
				<div class="modal-body">
				
					<form class="form-horizontal" role="form">
						<input type="hidden" id="edit-id">
						<div class="form-group">
							<label for="edit-marketActivityOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="edit-marketActivityOwner">
									<c:forEach items="${userList}" var="user">
									<option value="${user.id}">${user.name}</option>
									</c:forEach>
								</select>
							</div>
                            <label for="edit-marketActivityName" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="edit-marketActivityName">
                            </div>
						</div>

						<div class="form-group">
							<label for="edit-startTime" class="col-sm-2 control-label">开始日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control my-date" id="edit-startTime">
							</div>
							<label for="edit-endTime" class="col-sm-2 control-label">结束日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control my-date" id="edit-endTime" value="2020-10-20">
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-cost" class="col-sm-2 control-label">成本</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-cost" value="5,000">
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-describe" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<textarea class="form-control" rows="3" id="edit-describe">市场活动Marketing，是指品牌主办或参与的展览会议与公关市场活动，包括自行主办的各类研讨会、客户交流会、演示会、新产品发布会、体验会、答谢会、年会和出席参加并布展或演讲的展览会、研讨会、行业交流会、颁奖典礼等</textarea>
							</div>
						</div>
						
					</form>
					
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" id="edit-update">更新</button>
				</div>
			</div>
		</div>
	</div>
	
	<!-- 导入市场活动的模态窗口 -->
    <div class="modal fade" id="importActivityModal" role="dialog">
        <div class="modal-dialog" role="document" style="width: 85%;">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">
                        <span aria-hidden="true">×</span>
                    </button>
                    <h4 class="modal-title" id="myModalLabel">导入市场活动</h4>
                </div>
                <div class="modal-body" style="height: 350px;">
                    <div style="position: relative;top: 20px; left: 50px;">
                        请选择要上传的文件：<small style="color: gray;">[仅支持.xls]</small>
                    </div>
                    <div style="position: relative;top: 40px; left: 50px;">
                        <input type="file" id="activityFile">
                    </div>
                    <div style="position: relative; width: 400px; height: 320px; left: 45% ; top: -40px;" >
                        <h3>重要提示</h3>
                        <ul>
                            <li>操作仅针对Excel，仅支持后缀名为XLS的文件。</li>
                            <li>给定文件的第一行将视为字段名。</li>
                            <li>请确认您的文件大小不超过5MB。</li>
                            <li>日期值以文本形式保存，必须符合yyyy-MM-dd格式。</li>
                            <li>日期时间以文本形式保存，必须符合yyyy-MM-dd HH:mm:ss的格式。</li>
                            <li>默认情况下，字符编码是UTF-8 (统一码)，请确保您导入的文件使用的是正确的字符编码方式。</li>
                            <li>建议您在导入真实数据之前用测试文件测试文件导入功能。</li>
                        </ul>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                    <button id="importActivityBtn" type="button" class="btn btn-primary">导入</button>
                </div>
            </div>
        </div>
    </div>
	
	
	<div>
		<div style="position: relative; left: 10px; top: -10px;">
			<div class="page-header">
				<h3>市场活动列表</h3>
			</div>
		</div>
	</div>
	<div style="position: relative; top: -20px; left: 0px; width: 100%; height: 100%;">
		<div style="width: 100%; position: absolute;top: 5px; left: 10px;">
		
			<div class="btn-toolbar" role="toolbar" style="height: 80px;">
				<form class="form-inline" role="form" style="position: relative;top: 8%; left: 5px;">
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">名称</div>
				      <input class="form-control" type="text" id="query-name">
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">所有者</div>
				      <input class="form-control" type="text" id="query-owner">
				    </div>
				  </div>


				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">开始日期</div>
					  <input class="form-control" type="text" id="startTime" />
				    </div>
				  </div>
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">结束日期</div>
					  <input class="form-control" type="text" id="endTime">
				    </div>
				  </div>
				  
				  <button type="button" class="btn btn-default" id="queryActivityBtn">查询</button>
				  
				</form>
			</div>
			<div class="btn-toolbar" role="toolbar" style="background-color: #F7F7F7; height: 50px; position: relative;top: 5px;">
				<div class="btn-group" style="position: relative; top: 18%;">
				  <button type="button" class="btn btn-primary" id="createActivityBtn"><span class="glyphicon glyphicon-plus"></span> 创建</button>
				  <button type="button" class="btn btn-default" id="updateActivity"><span class="glyphicon glyphicon-pencil"></span> 修改</button>
				  <button type="button" class="btn btn-danger" id="deleteActivityBtn"><span class="glyphicon glyphicon-minus"></span> 删除</button>
				</div>
				<div class="btn-group" style="position: relative; top: 18%;">
                    <button type="button" class="btn btn-default" data-toggle="modal" data-target="#importActivityModal" ><span class="glyphicon glyphicon-import"></span> 上传列表数据（导入）</button>
                    <button id="exportActivityAllBtn" type="button" class="btn btn-default"><span class="glyphicon glyphicon-export"></span> 下载列表数据（批量导出）</button>
                    <button id="exportActivityXzBtn" type="button" class="btn btn-default"><span class="glyphicon glyphicon-export"></span> 下载列表数据（选择导出）</button>
                </div>
			</div>
			<div style="position: relative;top: 10px;">
				<table class="table table-hover" >
					<thead>
						<tr style="color: #B3B3B3;">
							<td><input id="checkAll" type="checkbox" /></td>
							<td>名称</td>
                            <td>所有者</td>
							<td>开始日期</td>
							<td>结束日期</td>
						</tr>
					</thead>
					<tbody id="tbody">

                	</tbody>
				</table>
				<div id="pages"></div>
			</div>

		</div>
	</div>
</body>
</html>