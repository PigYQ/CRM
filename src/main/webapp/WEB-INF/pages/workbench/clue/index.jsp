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
		//给日期输入框绑定日历
		$(".my-date").datetimepicker({
			language:'zh-CN',
			format:'yyyy-mm-dd',
			minView:'month',
			initialDate:new Date(),
			autoclose:true
		});

		//给全选框添加单击方式
		$("#checkAll").click(function (){
			$("#tbody input[type='checkbox']").prop('checked',this.checked);
		});

		//给线索复选框添加单击事件
		$("#tbody").on("click","input[type='checkbox']",function (){
			if($("#tbody input[type='checkbox']").size()===$("#tbody input[type='checkbox']:checked").size()){
				$("#checkAll").prop('checked',true);
			}else {
				$("#checkAll").prop('checked',false);
			}
		});

		//页面初始化
		queryClueByConditionForPage(1,10);

		//创建线索按钮单击事件
		$("#create-clueBtn").click(function (){
			//清空数据
			$("#createClueForm").get(0).reset();
			//弹出模态窗口
			$("#createClueModal").modal("show");
		});

		//保存线索单击事件
		$("#saveClueBtn").click(function (){
			//收集参数
			let owner = $("#create-clueOwner").val();
			let company = $.trim($("#create-company").val());
			let appellation = $.trim($("#create-call").val());
			let fullname = $.trim($("#create-surname").val());
			let job = $.trim($("#create-job").val());
			let email = $.trim($("#create-email").val());
			let phone = $.trim($("#create-phone").val());
			let website = $.trim($("#create-website").val());
			let mphone = $.trim($("#create-mphone").val());
			let state = $.trim($("#create-status").val());
			let source = $.trim($("#create-source").val());
			let description = $.trim($("#create-describe").val());
			let contactSummary = $.trim($("#create-contactSummary").val());
			let nextContactTime = $.trim($("#create-nextContactTime").val());
			let address = $.trim($("#create-address").val());
			//表单验证
			if (owner===""){
				alert("所有者不能为空");
				return;
			}
			if (fullname===""){
				alert("姓名不能为空");
				return;
			}
			if (company===""){
				alert("公司不能为空");
				return;
			}
			if (appellation===""){
				alert("称呼不能为空");
				return;
			}
			if (phone===""){
				alert("公司座机不能为空");
				return;
			}
			if (mphone===""){
				alert("手机不能为空");
				return;
			}
			//发送请求
			$.ajax({
				url:'workbench/clue/createClue.do',
				data:{
					owner:owner,
					company:company,
					appellation:appellation,
					fullname:fullname,
					job:job,
					email:email,
					phone:phone,
					website:website,
					mphone:mphone,
					state:state,
					source:source,
					description:description,
					contactSummary:contactSummary,
					nextContactTime:nextContactTime,
					address:address
				},
				type:'post',
				datatype:'json',
				success:function (data){
					if (data.code==="1"){
						//关闭模特窗口，刷新列表
						$("#createClueModal").modal("hide");
						queryClueByConditionForPage(1,$("#pages").bs_pagination('getOption','rowsPerPage'));
					}else {
						//提示信息
						alert(data.msg);
					}
				}
			});
		});

		//查询按钮单击事件
		$("#query-clue").click(function (){
			queryClueByConditionForPage(1,$("#pages").bs_pagination('getOption','rowsPerPage'));
		});

		//删除按钮添加单击事件
		$("#delete-clue").click(function (){
			//获取勾选的线索并且验证
			let checkedIds = $("#tbody input[type='checkbox']:checked");
			if (checkedIds.size()===0){
				alert("请选择要删除的线索");
				return;
			}
			window.confirm("确定删除选中市场活动吗");
			//收集要删除的市场活动id集
			let ids='';
			$.each(checkedIds,function (){
				ids+= "id="+this.value+"&";
			});
			ids = ids.substr(0,ids.length-1);
			alert(ids);
			//发送请求
			$.ajax({
				url:'workbench/clue/deleteClueByIds.do',
				data:ids,
				type:'post',
				dataType: 'json',
				success:function (data){
					if(data.code==="0"){
						alert(data.msg);
					}else {
						queryClueByConditionForPage($("#pages").bs_pagination('getOption','currentPage'),$("#pages").bs_pagination('getOption','rowsPerPage'));
					}
				}
			});
		});

		//修改按钮单击事件
		$("#edit-clue").click(function (){
			//获取选中线索
			let checkedIds = $("#tbody input[type='checkbox']:checked");
			//验证
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
			//发送请求，查询数据
			$.ajax({
				url:'workbench/clue/queryClueById.do',
				data:{
					id:id
				},
				type:'post',
				typeType:'json',
				success:function (data){
					//填充数据
					$("#edit-id").val(data.id);
					$("#edit-clueOwner").val(data.owner);
					$("#edit-company").val(data.company);
					$("#edit-call").val(data.appellation);
					$("#edit-surname").val(data.fullname);
					$("#edit-job").val(data.job);
					$("#edit-email").val(data.email);
					$("#edit-phone").val(data.phone);
					$("#edit-website").val(data.website);
					$("#edit-mphone").val(data.mphone);
					$("#edit-status").val(data.state);
					$("#edit-source").val(data.source);
					$("#edit-describe").val(data.description);
					$("#edit-contactSummary").val(data.contactSummary);
					$("#edit-nextContactTime").val(data.nextContactTime);
					$("#edit-address").val(data.address);
				}
			});
			//展示模态窗口
			$("#editClueModal").modal("show");
		});

		//更新按钮单击事件
		$("#update-clue").click(function (){
			//收集参数
			let id = $("#edit-id").val();
			let owner = $("#edit-clueOwner").val();
			let company = $.trim($("#edit-company").val());
			let appellation = $.trim($("#edit-call").val());
			let fullname = $.trim($("#edit-surname").val());
			let job = $.trim($("#edit-job").val());
			let email = $.trim($("#edit-email").val());
			let phone = $.trim($("#edit-phone").val());
			let website = $.trim($("#edit-website").val());
			let mphone = $.trim($("#edit-mphone").val());
			let state = $.trim($("#edit-status").val());
			let source = $.trim($("#edit-source").val());
			let description = $.trim($("#edit-describe").val());
			let contactSummary = $.trim($("#edit-contactSummary").val());
			let nextContactTime = $.trim($("#edit-nextContactTime").val());
			let address = $.trim($("#edit-address").val());
			//表单验证
			if (owner===""){
				alert("所有者不能为空");
				return;
			}
			if (fullname===""){
				alert("姓名不能为空");
				return;
			}
			if (company===""){
				alert("公司不能为空");
				return;
			}
			if (appellation===""){
				alert("称呼不能为空");
				return;
			}
			if (phone===""){
				alert("公司座机不能为空");
				return;
			}
			if (mphone===""){
				alert("手机不能为空");
				return;
			}
			//发送请求
			$.ajax({
				url:'workbench/clue/updateClueById.do',
				data:{
					id:id,
					owner:owner,
					company:company,
					appellation:appellation,
					fullname:fullname,
					job:job,
					email:email,
					phone:phone,
					website:website,
					mphone:mphone,
					state:state,
					source:source,
					description:description,
					contactSummary:contactSummary,
					nextContactTime:nextContactTime,
					address:address
				},
				type:'post',
				typeType: 'json',
				success:function (data){
					if (data.code==="1"){
						//关闭模态窗口
						$("#editClueModal").modal("hide");
						//刷新列表
						queryClueByConditionForPage($("#pages").bs_pagination('getOption','currentPage'),$("#pages").bs_pagination('getOption','rowsPerPage'));
					}else {
						alert(data.msg);
						$("#editClueModal").modal("show");
					}
				}
			});
		});

	});

	function queryClueByConditionForPage(pageNo,pageSize){
		//收集参数
		let fullname = $.trim($("#query-name").val());
		let company = $.trim($("#query-company").val());
		let phone = $.trim($("#query-phnoe").val());
		let source = $("#query-source").val();
		let owner = $.trim($("#query-owner").val());
		let mphone = $.trim($("#query-mphone").val());
		let state = $("#query-state").val();
		//发送请求
		$.ajax({
			url:'workbench/clue/queryClue.do',
			data:{
				fullname:fullname,
				company:company,
				phone:phone,
				source:source,
				owner:owner,
				mphone:mphone,
				state:state,
				pageNo:pageNo,
				pageSize:pageSize
			},
			type:'post',
			dataType:'json',
			success:function (data){
				//遍历所有市场活动
				let htmlStr = "";
				$.each(data.clueList,function (index,obj){
					htmlStr+="<tr>"
					htmlStr+="<td><input value='"+obj.id+"' type=\"checkbox\" /></td>"
					htmlStr+="<td><a style=\"text-decoration: none; cursor: pointer;\" onclick=\"window.location.href='workbench/clue/toDetail.do?id="+obj.id+"';\">"+obj.fullname+"</a></td>"
					htmlStr+="<td>"+obj.company+"</td>"
					htmlStr+="<td>"+obj.phone+"</td>"
					htmlStr+="<td>"+obj.mphone+"</td>"
					htmlStr+="<td>"+obj.source+"</td>"
					htmlStr+="<td>"+obj.owner+"</td>"
					htmlStr+="<td>"+obj.state+"</td>"
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
						queryClueByConditionForPage(obj.currentPage,obj.rowsPerPage);
					}
				});
			}
		})
	}
	
</script>
</head>
<body>

	<!-- 创建线索的模态窗口 -->
	<div class="modal fade" id="createClueModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 90%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel">创建线索</h4>
				</div>
				<div class="modal-body">
					<form class="form-horizontal" role="form" id="createClueForm">
					
						<div class="form-group">
							<label for="create-clueOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="create-clueOwner">
								  <c:forEach items="${userList}" var="user">
									  <option value="${user.id}">${user.name}</option>
								  </c:forEach>
								</select>
							</div>
							<label for="create-company" class="col-sm-2 control-label">公司<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="create-company">
							</div>
						</div>
						
						<div class="form-group">
							<label for="create-call" class="col-sm-2 control-label">称呼</label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="create-call">
								  <c:forEach items="${appellation}" var="obj">
									  <option value="${obj.id}">${obj.value}</option>
								  </c:forEach>
								</select>
							</div>
							<label for="create-surname" class="col-sm-2 control-label">姓名<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="create-surname">
							</div>
						</div>
						
						<div class="form-group">
							<label for="create-job" class="col-sm-2 control-label">职位</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="create-job">
							</div>
							<label for="create-email" class="col-sm-2 control-label">邮箱</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="create-email">
							</div>
						</div>
						
						<div class="form-group">
							<label for="create-phone" class="col-sm-2 control-label">公司座机</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="create-phone">
							</div>
							<label for="create-website" class="col-sm-2 control-label">公司网站</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="create-website">
							</div>
						</div>
						
						<div class="form-group">
							<label for="create-mphone" class="col-sm-2 control-label">手机</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="create-mphone">
							</div>
							<label for="create-status" class="col-sm-2 control-label">线索状态</label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="create-status">
									<c:forEach items="${clueState}" var="obj">
										<option value="${obj.id}">${obj.value}</option>
									</c:forEach>
								</select>
							</div>
						</div>
						
						<div class="form-group">
							<label for="create-source" class="col-sm-2 control-label">线索来源</label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="create-source">
									<c:forEach items="${source}" var="obj">
										<option value="${obj.id}">${obj.value}</option>
									</c:forEach>
								</select>
							</div>
						</div>
						

						<div class="form-group">
							<label for="create-describe" class="col-sm-2 control-label">线索描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<textarea class="form-control" rows="3" id="create-describe"></textarea>
							</div>
						</div>
						
						<div style="height: 1px; width: 103%; background-color: #D5D5D5; left: -13px; position: relative;"></div>
						
						<div style="position: relative;top: 15px;">
							<div class="form-group">
								<label for="create-contactSummary" class="col-sm-2 control-label">联系纪要</label>
								<div class="col-sm-10" style="width: 81%;">
									<textarea class="form-control" rows="3" id="create-contactSummary"></textarea>
								</div>
							</div>
							<div class="form-group">
								<label for="create-nextContactTime" class="col-sm-2 control-label">下次联系时间</label>
								<div class="col-sm-10" style="width: 300px;">
									<input type="text" class="form-control my-date" id="create-nextContactTime">
								</div>
							</div>
						</div>
						
						<div style="height: 1px; width: 103%; background-color: #D5D5D5; left: -13px; position: relative; top : 10px;"></div>
						
						<div style="position: relative;top: 20px;">
							<div class="form-group">
                                <label for="create-address" class="col-sm-2 control-label">详细地址</label>
                                <div class="col-sm-10" style="width: 81%;">
                                    <textarea class="form-control" rows="1" id="create-address"></textarea>
                                </div>
							</div>
						</div>
					</form>
					
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" id="saveClueBtn">保存</button>
				</div>
			</div>
		</div>
	</div>
	
	<!-- 修改线索的模态窗口 -->
	<div class="modal fade" id="editClueModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 90%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title">修改线索</h4>
				</div>
				<div class="modal-body">
					<form class="form-horizontal" role="form">
						<input type="hidden" id="edit-id">
						<div class="form-group">
							<label for="edit-clueOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="edit-clueOwner">
									<c:forEach items="${userList}" var="user">
										<option value="${user.id}">${user.name}</option>
									</c:forEach>
								</select>
							</div>
							<label for="edit-company" class="col-sm-2 control-label">公司<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-company" value="动力节点">
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-call" class="col-sm-2 control-label">称呼</label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="edit-call">
									<c:forEach items="${appellation}" var="obj">
										<option value="${obj.id}">${obj.value}</option>
									</c:forEach>
								</select>
							</div>
							<label for="edit-surname" class="col-sm-2 control-label">姓名<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-surname" value="李四">
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-job" class="col-sm-2 control-label">职位</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-job" value="CTO">
							</div>
							<label for="edit-email" class="col-sm-2 control-label">邮箱</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-email" value="lisi@bjpowernode.com">
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-phone" class="col-sm-2 control-label">公司座机</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-phone" value="010-84846003">
							</div>
							<label for="edit-website" class="col-sm-2 control-label">公司网站</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-website" value="http://www.bjpowernode.com">
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-mphone" class="col-sm-2 control-label">手机</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-mphone" value="12345678901">
							</div>
							<label for="edit-status" class="col-sm-2 control-label">线索状态</label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="edit-status">
									<c:forEach items="${clueState}" var="obj">
										<option value="${obj.id}">${obj.value}</option>
									</c:forEach>
								</select>
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-source" class="col-sm-2 control-label">线索来源</label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="edit-source">
									<c:forEach items="${source}" var="obj">
										<option value="${obj.id}">${obj.value}</option>
									</c:forEach>
								</select>
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-describe" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<textarea class="form-control" rows="3" id="edit-describe">这是一条线索的描述信息</textarea>
							</div>
						</div>
						
						<div style="height: 1px; width: 103%; background-color: #D5D5D5; left: -13px; position: relative;"></div>
						
						<div style="position: relative;top: 15px;">
							<div class="form-group">
								<label for="edit-contactSummary" class="col-sm-2 control-label">联系纪要</label>
								<div class="col-sm-10" style="width: 81%;">
									<textarea class="form-control" rows="3" id="edit-contactSummary">这个线索即将被转换</textarea>
								</div>
							</div>
							<div class="form-group">
								<label for="edit-nextContactTime" class="col-sm-2 control-label">下次联系时间</label>
								<div class="col-sm-10" style="width: 300px;">
									<input type="text" class="form-control my-date" id="edit-nextContactTime" value="2017-05-01">
								</div>
							</div>
						</div>
						
						<div style="height: 1px; width: 103%; background-color: #D5D5D5; left: -13px; position: relative; top : 10px;"></div>

                        <div style="position: relative;top: 20px;">
                            <div class="form-group">
                                <label for="edit-address" class="col-sm-2 control-label">详细地址</label>
                                <div class="col-sm-10" style="width: 81%;">
                                    <textarea class="form-control" rows="1" id="edit-address">北京大兴区大族企业湾</textarea>
                                </div>
                            </div>
                        </div>
					</form>
					
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" id="update-clue">更新</button>
				</div>
			</div>
		</div>
	</div>

	<div>
		<div style="position: relative; left: 10px; top: -10px;">
			<div class="page-header">
				<h3>线索列表</h3>
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
				      <input id="query-name" class="form-control" type="text">
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">公司</div>
				      <input id="query-company" class="form-control" type="text">
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">公司座机</div>
				      <input id="query-phnoe" class="form-control" type="text">
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">线索来源</div>
					  <select id="query-source" class="form-control">
						  <option></option>
						  <c:forEach items="${source}" var="obj">
							  <option value="${obj.id}">${obj.value}</option>
						  </c:forEach>
					  </select>
				    </div>
				  </div>
				  
				  <br>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">所有者</div>
				      <input id="query-owner" class="form-control" type="text">
				    </div>
				  </div>

				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">手机</div>
				      <input id="query-mphone" class="form-control" type="text">
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">线索状态</div>
					  <select id="query-state" class="form-control">
						  <option></option>
						  <c:forEach items="${clueState}" var="obj">
							  <option value="${obj.id}">${obj.value}</option>
						  </c:forEach>
					  </select>
				    </div>
				  </div>

				  <button id="query-clue" type="button" class="btn btn-default">查询</button>
				  
				</form>
			</div>
			<div class="btn-toolbar" role="toolbar" style="background-color: #F7F7F7; height: 50px; position: relative;top: 40px;">
				<div class="btn-group" style="position: relative; top: 18%;">
				  <button type="button" class="btn btn-primary" id="create-clueBtn"><span class="glyphicon glyphicon-plus"></span> 创建</button>
				  <button type="button" class="btn btn-default" id="edit-clue"><span class="glyphicon glyphicon-pencil"></span> 修改</button>
				  <button type="button" class="btn btn-danger" id="delete-clue"><span class="glyphicon glyphicon-minus"></span> 删除</button>
				</div>

			</div>
			<div style="position: relative;top: 50px;">
				<table class="table table-hover">
					<thead>
						<tr style="color: #B3B3B3;">
							<td><input id="checkAll" type="checkbox" /></td>
							<td>名称</td>
							<td>公司</td>
							<td>公司座机</td>
							<td>手机</td>
							<td>线索来源</td>
							<td>所有者</td>
							<td>线索状态</td>
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