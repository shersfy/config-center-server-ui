var winwidth = $(window).width()-80;
var scale = winwidth/1200;

$(function(){
    
	Index.init();

	$(":input[name='repo']").change(function(){
		if ($(this).val()=='git'){
			$(".easyui-accordion").hide();
		} else {
			$(".easyui-accordion").show();
		}
	});

	$("#reboot").bind("click", function(){
		Index.reboot();
	});

	$("#addapp").bind("click", function(){
		Index.addApplication();
	});

	$("#reboot-app").bind("click", function(){
		Index.rebootApp();
	});

	$("#refresh-app").bind("click", function(){
		Index.refreshApp();
	});

	$('#label').combobox({onChange: function(newValue, oldValue){
		Index.getProfiles(newValue);
	}
	});

	$('#profile').combobox({onChange: function(newValue, oldValue){
		var label = $('#label').combobox("getValue");
		Index.getApplications(label, newValue);
	}
	});

	$('#applications').combobox({onChange: function(newValue, oldValue){
		Index.reloadProperties();
	}
	});

	$('#properties').datagrid({onClickCell: function(rowIndex, field, value){
		Datagrid.clickCell(rowIndex, field);
	}
	});

	$("#copy").tooltip({
		content: "复制到剪贴板"
	});

	$("#upload").bind("click", function(){
		Index.upload();
	});

	$("#upload").tooltip({
		content: "支持上传*.yml或*.properties"
	});

	$("#download").tooltip({
		content: "下载到文件application.yml"
	});
	
	$(".easyui-accordion .panel-title").tooltip({
		content: "点击展开",
		trackMouse: true
	});
	
	$(".block").panel({
		width: scale*1200+40
	});
	
	$("#easyui-accordion").accordion({
		selected: -1,
		width: scale*1200
	});
	
	$("#searchbox").searchbox({
		searcher:function(value, name){
			Index.reloadProperties();
		},
		prompt:'输入关键字',
		width: scale*420
	});

	var clipboard = new ClipboardJS("#copy", {
		text: function(trigger){
			return Datagrid.copy();
		}
	});

	clipboard.on('success', function(e) {
		success('已复制到剪贴板');
	});

});

var Index = {
		init: function(){
			$.each($('form'), function(index, item){
				$(item).form('reset');
			});

			Index.initRepo();
			Index.initApplications();
		},

		initRepo: function(){
			$.ajax({ 
				url: basePath+"/config/server/repo", 
				type: "GET", 
				data: {}, 
				success:function (data){
					if (data.code!=0){
						$.messager.alert('错误', data.msg,'error');
						return;
					}
					$("#gitUri").attr("href", data.model.gitUri);
					$("#gitUri").html(data.model.gitUri);
					if (data.model.active[0]=='git'){
						$("#git").attr("checked", "checked");
						$("#jdbc").removeAttr("checked");
					} else {
						$("#git").removeAttr("checked");
						$("#jdbc").attr("checked", "checked");
					}
				}, 
				error:function(error){
					$.messager.alert('错误', '网络异常','error');
				} 
			});
		},

		initApplications: function(){
			Index.getLabels();
		},

		addApplication: function(){
			var label = $(":input[name='label']:first").val();
			var profile = $(":input[name='profile']:first").val();
			var application = $(":input[name='application']:first").val();

			label = $.trim(label);
			profile = $.trim(profile);
			application = $.trim(application);

			// check
			if (label.length==0){
				$.messager.alert('提示', '仓库标签不能为空', 'info');
				return;
			}
			if (profile.length==0){
				$.messager.alert('提示', '环境标签不能为空', 'info');
				return;
			}
			if (application.length==0){
				$.messager.alert('提示', '服务ID不能为空', 'info');
				return;
			}

			$.ajax({
				url: basePath+"/config/server/add", 
				type: "POST", 
				data: {
					'label': label,
					'profile': profile,
					'application': application
				}, 
				success:function (data){
					if (data.code!=0){
						$.messager.alert('错误', data.msg,'error');
						return;
					}
					success('添加成功');
					location.reload();
				}, 
				error:function(error){
					$.messager.alert('错误', '网络异常','error');
				}
			});

		},

		getLabels: function(){
			$.ajax({ 
				url: basePath+"/config/server/labels", 
				type: "GET", 
				data: {}, 
				success:function (data){
					if (data.code!=0){
						$.messager.alert('错误', data.msg,'error');
						return;
					}
					var opts = new Array();
					$.each(data.model, function(index, item){
						var opt = {
								id: item,
								text: item
						};
						if (index==0){
							opt.selected=true;
						}
						opts.push(opt);
					});
					$('#label').combobox({
						data: opts,
						valueField: 'id',
						textField: 'text'
					});
				}, 
				error:function(error){
					$.messager.alert('错误', '网络异常','error');
				} 
			});
		},

		getProfiles: function(label){
			$.ajax({ 
				url: basePath+"/config/server/profiles", 
				type: "GET", 
				data: {
					'label': label
				}, 
				success:function (data){
					if (data.code!=0){
						$.messager.alert('错误', data.msg,'error');
						return;
					}
					var opts = new Array();
					$.each(data.model, function(index, item){
						var opt = {
								id: item,
								text: item
						};
						if (index==0){
							opt.selected=true;
						}
						opts.push(opt);
					});
					$('#profile').combobox({
						data: opts,
						valueField: 'id',
						textField: 'text'
					});
				}, 
				error:function(error){
					$.messager.alert('错误', '网络异常','error');
				} 
			});
		},

		getApplications: function(label, profile){
			$.ajax({ 
				url: basePath+"/config/server/applications", 
				type: "GET", 
				data: {
					'label': label,
					'profile': profile
				}, 
				success:function (data){
					if (data.code!=0){
						$.messager.alert('错误', data.msg,'error');
						return;
					}
					var opts = new Array();
					$.each(data.model, function(index, item){
						var opt = {
								id: item,
								text: item
						};
						if (index==0){
							opt.selected=true;
						}
						opts.push(opt);
					});
					$('#applications').combobox({
						data: opts,
						editable: true,
						valueField: 'id',
						textField: 'text'
					});
				}, 
				error:function(error){
					$.messager.alert('错误', '网络异常','error');
				} 
			});
		},

		reloadProperties: function(){

			var label = $('#label').combobox("getValue");
			var profile = $('#profile').combobox("getValue");
			var application = $('#applications').combobox("getValue");
			var keyword = $('#searchbox').textbox("getValue");

			$.ajax({ 
				url: basePath+"/config/server/properties", 
				type: "GET", 
				data: {
					'label': label,
					'profile': profile,
					'application': application,
					'keyword': keyword
				}, 
				beforeSend: function () {
					//异步请求时spinner出现
					var target = $("#content").get(0);
					spinner.spin(target); 
				},
				success:function (data){
					if (data.code!=0){
						$.messager.alert('错误', data.msg,'error');
						//关闭spinner
						spinner.spin();
						return;
					}

					$.each($('.easyui-linkbutton'), function(index, item){
						$(item).linkbutton('enable');
					});

					var isGit = data.msg=='git';
					if (isGit){
						var nogit = $('.no-git');
						$.each(nogit, function(index, item){
							$(item).linkbutton('disable');
						});
					}

					$('#properties').datagrid({
						title: "配置项",
						rownumbers: true,
						width: 1200*scale,
						toolbar: "#toolbar",
						data: data.model,
						columns:[[
							{field:'checked', checkbox:true},
							{field:'key', title:'键', resizable:true, width:(isGit?600:300)*scale, editor:isGit?null:'text'},
							{field:'value', title:'值', resizable:true, width:(isGit?570:300)*scale, editor:isGit?null:'textarea'},
							{field:'comment', title:'说明', hidden:isGit, width:240*scale, editor:isGit?null:'text'},
							{field:'createTimestamp', title:'创建时间', hidden:isGit, width:150*scale, formatter: function(value){
								return isGit?"-":formatTime(value);
							}},
							{field:'updateTimestamp', title:'更新时间', hidden:isGit, width:150*scale, formatter: function(value){
								return isGit?"-":formatTime(value);
							}}
							]]
					});
					$('td .datagrid-cell').tooltip({
						content: function(){
							var text = $(this).text();
							return $.trim(text).length==0?'无内容':text;
						},
						showDelay: 500,
						hideDelay: 200
					});	
					//关闭spinner
					spinner.spin();
				}, 
				error:function(error){
					$.messager.alert('错误', '网络异常', 'error');
					//关闭spinner
					spinner.spin();
				} 
			});

		},

		upload: function(){

			var app = $('#applications').combobox("getValue");
			if (app==""){
				$.messager.alert('提示', '请选择应用ID或添加应用配置', 'info');
				return ;
			}

			var selector = $.messager.prompt('选择文件', '', function(ok){});

			var html = '<form id="upload-form"><input id="upfile" name="file" type="text" style="width:250px"></form>';
			$(".messager-input").parent().append(html);
			$(".messager-input").parent().siblings('.messager-icon').hide();
//			.removeClass('messager-question');

			$(".messager-input").hide();
			$(".messager-input").val('true');

			$('#upfile').filebox({
				buttonText: '选择文件',
				prompt: "支持*.yml或*.properties",
				buttonAlign: 'left',
				onChange: function(newValue, oldValue){
					// 创建Form
					var form = $('#upload-form');
					form.attr('method', 'post');
					form.attr('enctype', 'multipart/form-data');

					var formdata = new FormData($('#upload-form')[0]);

					var label = $('#label').combobox("getValue");
					var profile = $('#profile').combobox("getValue");
					var application = $('#applications').combobox("getValue");

					formdata.append('label', label);
					formdata.append('profile', profile);
					formdata.append('application', application);

					$.ajax({
						url: basePath+"/config/server/upload", 
						type: "POST", 
						data: formdata, 
						processData: false,
						contentType: false,
						beforeSend: function () {
							$('#upfile').textbox('disable');
							//异步请求时spinner出现
							var target = $("#content").get(0);
							spinner.spin(target);
						},
						success:function (data){
							if (data.code!=0){
								$.messager.alert('错误', data.msg,'error');
								$('#upfile').textbox('enable');
								return;
							}

							selector.window('close');
							Index.saveSuccessful(data);
							//关闭spinner
							spinner.spin();
						}, 
						error:function(error){
							$.messager.alert('错误', '网络异常', 'error');
							//关闭spinner
							spinner.spin();
							$('#upfile').textbox('enable');
						}
					});

				}
			});
			$(".messager-window").children(".dialog-button").hide();
			$(".window-shadow").height("auto");
		},

		downloadYaml: function(){
			var label = $('#label').combobox("getValue");
			var profile = $('#profile').combobox("getValue");
			var application = $('#applications').combobox("getValue");
			var url = basePath+"/config/server/download";
			url += "?label="+label;
			url += "&profile="+profile;
			url += "&application="+application;

			location.href = url;
		},

		webscanYaml: function(){
			var label = $('#label').combobox("getValue");
			var profile = $('#profile').combobox("getValue");
			var application = $('#applications').combobox("getValue");
			var url = basePath+"/config/server";
			url += "/"+label;
			url += "/"+application+"-"+profile+".yml";
			window.open(url);
		},

		/**
		 * 重启服务
		 */
		reboot: function(){
			$.messager.confirm('确认', '是否重启【配置中心】服务？', function(ok){
				if (ok){
					success("重启成功");
					//location.reload();
				}
			});
		},

		/**
		 * 重启服务
		 */
		rebootApp: function(){
			var app = $('#applications').combobox("getValue");
			$.messager.confirm('确认', '是否重启应用服务，使配置生效？<p/>应用ID：'+app, function(ok){
				if (ok){
					success('重启成功');
				}
			});
		},

		/**
		 * 刷新配置
		 */
		refreshApp: function(){
			var app = $('#applications').combobox("getValue");
			$.messager.prompt('确认', '通知应用刷新配置，使配置生效？<p/>应用ID：'+app, function(destination){
				if (destination==undefined){
					return;
				}
				destination = $.trim(destination);
				$.ajax({
					url: basePath+"/config/server/refresh", 
					type: "POST", 
					data: {
						'destination': destination
					}, 
					beforeSend: function () {
						//异步请求时spinner出现
						var target = $("#content").get(0);
						spinner.spin(target);
					},
					success:function (data){
						if (data.code!=0){
							$.messager.alert('错误', data.msg,'error');
							//关闭spinner
							spinner.spin();
							return;
						}
						success('刷新成功');
						//关闭spinner
						spinner.spin();
					}, 
					error:function(error){
						$.messager.alert('错误', '网络异常','error');
						//关闭spinner
						spinner.spin();
					}
				});
			});

			$(".messager-input").attr("placeholder", "刷新指定节点, 空白表示刷新全部");
		},
		/**
		 * 删除记录
		 */
		deleteRecords: function(ids){
			$.ajax({
				url: basePath+"/config/server/delete", 
				type: "POST", 
				data: {
					'ids': ids.join(',')
				}, 
				success:function (data){
					if (data.code!=0){
						$.messager.alert('错误', data.msg,'error');
						return;
					}
				}, 
				error:function(error){
					$.messager.alert('错误', '网络异常','error');
				}
			});
		},

		saveRecords: function(rows){
			$.ajax({
				url: basePath+"/config/server/save", 
				type: "POST", 
				data: {
					'data': JSON.stringify(rows)
				}, 
				beforeSend: function () {
					//异步请求时spinner出现
					var target = $("#content").get(0);
					spinner.spin(target);
				},
				success:function (data){
					if (data.code!=0){
						$.messager.alert('错误', data.msg,'error');
						//关闭spinner
						spinner.spin();
						return;
					}

					Index.saveSuccessful(data);
				}, 
				error:function(error){
					$.messager.alert('错误', '网络异常','error');
					//关闭spinner
					spinner.spin();
				}
			});
		},

		saveSuccessful: function (data){
			var content = "";
			content += "新增："+data.model.insert+"<p/>";
			content += "更新："+data.model.update+"<p/>";
			if (data.model.error>0){
				content += "<font color='red'>";
				content += "错误："+data.model.error+"<p/>";
				content += "错误信息：<br/>"+JSON.stringify(data.model.errorMsg)+"<p/>";
				content += "错误数据：<br/>"+JSON.stringify(data.model.errorData)+"<p/>";
				content += "</font>";
			}
			$.messager.show({
				title:'提示',
				width: 400,
				height: 300,
				msg:content,
				showType:'fade',
				timeout: 3000,
				style:{
					right:'',
					bottom:''
				}
			});

			Datagrid.refresh();
		}
}

var editIndex = undefined;
var Datagrid = {
		endEditing: function (){
			if (editIndex == undefined){
				return true
			}
			if ($('#properties').datagrid('validateRow', editIndex)){
				$('#properties').datagrid('endEdit', editIndex);
				$('#properties').datagrid('refreshRow', editIndex);
				editIndex = undefined;
				return true;
			} else {
				return false;
			}
		},

		clickCell: function(index, field){
			if (Datagrid.endEditing()){
				$('#properties').datagrid('selectRow', index).datagrid('editCell', {index:index,field:field});
				editIndex = index;
			}
		},
		/**
		 * 保存记录
		 */
		save: function(){
			var label = $('#label').combobox("getValue");
			var profile = $('#profile').combobox("getValue");
			var application = $('#applications').combobox("getValue");

			var info = label+"/"+application+"-"+profile;
			$.messager.confirm('确认', '是否保存？<p/>'+info, function(ok){
				if (ok){

					var isEnd = Datagrid.endEditing();
					var rows  = $('#properties').datagrid('getChanges');
					if (isEnd){
						$('#properties').datagrid('acceptChanges');
					}
					if (rows.length==0){
						return;
					}

					var target = $("#content").get(0);
					spinner.spin(target);
					// check
					for(i=0; i<rows.length; i++){
						if ($.trim(rows[i].key).length==0){
							$.messager.alert('提示', '键不能为空', 'info');
							return;
						}
						if ($.trim(rows[i].label).length==0){
							rows[i].label = label;
						}
						if ($.trim(rows[i].profile).length==0){
							rows[i].profile = profile;
						}
						if ($.trim(rows[i].application).length==0){
							rows[i].application = application;
						}
						rows[i].createTime = null;
						rows[i].updateTime = null;
					}

					Index.saveRecords(rows);
				}
			});

		},

		reject: function(){
			if (editIndex == undefined){
				return;
			}
			$.messager.confirm('确认', '是否撤销修改?', function(ok){
				if (ok){
					$('#properties').datagrid('rejectChanges');
					editIndex = undefined;
				}
			});
		},

		append: function(){
			if (Datagrid.endEditing()){
				$('#properties').datagrid('appendRow',{
					createTimestamp: new Date().getTime(),
					updateTimestamp: new Date().getTime()
				});
				editIndex = $('#properties').datagrid('getRows').length-1;
				$('#properties').datagrid('selectRow', editIndex).datagrid('beginEdit', editIndex);
			}
		},

		removeit: function(){
			// 删除行
			var rows = $('#properties').datagrid('getSelections');
			if (rows.length==0){
				$.messager.alert('提示', '请选择要删除的行', 'info');
				return;
			}
			$.messager.confirm('确认', '是否永久删除已选择的行?<p/>已选择：'+
					rows.length+"行", function(ok){
				if (ok){
					var ids = new Array();
					$.each(rows, function(index, item){
						if (item.id!= undefined){
							ids.push(item.id);
						}
						var rowIndex = $('#properties').datagrid('getRowIndex', item);
						$('#properties').datagrid('deleteRow', rowIndex);
					});

					editIndex = undefined;
					if (ids.length!=0){
						Index.deleteRecords(ids);
						Datagrid.refresh();
					}
				}
			});

		},

		copy: function(){
			var rows = $('#properties').datagrid('getSelections');
			if (rows.length==0){
				$.messager.alert('提示', '请选择要复制的行', 'info');
				return;
			}
			var content = '';
			$.each(rows, function(index, row){
				if ($.trim(row.comment).length!=0){
					content += "# "+row.comment+"\n";
				}
				content += row.key+"=";
				content += row.value;
				content += "\n";
			});
			return content;
		},

		refresh: function(){
			Index.reloadProperties();
		},

		download: function(){
			Index.downloadYaml();
		},

		webscan: function(){
			Index.webscanYaml();
		}
}

$.extend($.fn.datagrid.methods, {
	editCell: function(jq,param){
		return jq.each(function(){
			var opts = $(this).datagrid('options');
			var fields = $(this).datagrid('getColumnFields',true).concat($(this).datagrid('getColumnFields'));
			for(var i=0; i<fields.length; i++){
				var col = $(this).datagrid('getColumnOption', fields[i]);
				col.editor1 = col.editor;
				if (fields[i] != param.field){
					col.editor = null;
				}
			}
			$(this).datagrid('beginEdit', param.index);
			for(var i=0; i<fields.length; i++){
				var col = $(this).datagrid('getColumnOption', fields[i]);
				col.editor = col.editor1;
			}
		});
	}
});

var opts = {            
		lines: 13, // 花瓣数目
		length: 20, // 花瓣长度
		width: 5, // 花瓣宽度
		radius: 20, // 花瓣距中心半径
		corners: 1, // 花瓣圆滑度 (0-1)
		rotate: 0, // 花瓣旋转角度
		direction: 1, // 花瓣旋转方向 1: 顺时针, -1: 逆时针
		color: '#5882FA', // 花瓣颜色
		speed: 1, // 花瓣旋转速度
		trail: 60, // 花瓣旋转时的拖影(百分比)
		shadow: false, // 花瓣是否显示阴影
		hwaccel: false, //spinner 是否启用硬件加速及高速旋转            
		className: 'spinner', // spinner css 样式名称
		zIndex: 2e9, // spinner的z轴 (默认是2000000000)
		top: 50, // spinner 相对父容器Top定位 单位 px
		left: 'auto'// spinner 相对父容器Left定位 单位 px
};

var spinner = new Spinner(opts);

/**
 * 时间格式化
 * @param time
 * @returns
 */
function formatTime(time){
	var datetime = new Date(time);
	var year = datetime.getFullYear();
	var month = datetime.getMonth() + 1 < 10 ? "0" + (datetime.getMonth() + 1) : datetime.getMonth() + 1;
	var date = datetime.getDate() < 10 ? "0" + datetime.getDate() : datetime.getDate();
	var hour = datetime.getHours()< 10 ? "0" + datetime.getHours() : datetime.getHours();
	var minute = datetime.getMinutes()< 10 ? "0" + datetime.getMinutes() : datetime.getMinutes();
	var second = datetime.getSeconds()< 10 ? "0" + datetime.getSeconds() : datetime.getSeconds();
	return year + "-" + month + "-" + date+" "+hour+":"+minute+":"+second;
}

function success(msg){
	$.messager.show({
		title:'提示',
		msg: msg,
		showType:'fade',
		timeout: 1000,
		width: 200,
		height: 50,
		style:{
			right:'',
			bottom:''
		}
	});
}