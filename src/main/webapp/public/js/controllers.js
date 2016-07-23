factoryVehicle.controller('loginManager', function($scope){
	$scope.init = function(){
		$('#mainbody').jParticle({
			'particlesNumber': 160,
			'color': '#001D29',
			'particle': {
				minSize: 2,
				maxSize: 5,
				speed: 120
			}
		});
	};
});

factoryVehicle.controller('topbarManager',function($scope){
	
});

factoryVehicle.controller('adminManager',function($scope,$http){
	$scope.stations = [];	//保存从后台取得的所有站点
	$scope.deleteStaInfo = {
		'marker': null,
		'id': ''
	};		//保存要删除的站点信息
	$scope.updateStaInfo = {
		'name': '',
		'x': '',
		'y': '',
		'id': '',
		'marker': null
	};		//保存要修改的站点信息
	$scope.addStaInfo = {
		'name': '',
		'x': '',
		'y': ''
	};		//保存新增加的站点信息
	$scope.searchCondition = '';		//保存用户搜索的条件
	$scope.searchResult = [];			//保存搜索结果
	$scope.map;		//界面地图控制对象
	$scope.markers = [];		//地图上所有站点marker的集合
	$scope.staStaffList = [{'name': '帅全','eaddress': '杭州电子科技大学生活区','e_id':2015003},{'name': '帅全','eaddress': '杭州电子科技大学生活区','e_id':2015003},{'name': '帅全','eaddress': '杭州电子科技大学生活区','e_id':2015003},{'name': '帅全','eaddress': '杭州电子科技大学生活区','e_id':2015003},{'name': '帅全','eaddress': '杭州电子科技大学生活区','e_id':2015003},{'name': '帅全','eaddress': '杭州电子科技大学生活区','e_id':2015003},{'name': '帅全','eaddress': '杭州电子科技大学生活区','e_id':2015003},{'name': '帅全','eaddress': '杭州电子科技大学生活区','e_id':2015003},{'name': '帅全','eaddress': '杭州电子科技大学生活区','e_id':2015003},{'name': '帅全','eaddress': '杭州电子科技大学生活区','e_id':2015003},{'name': '帅全','eaddress': '杭州电子科技大学生活区','e_id':2015003},{'name': '帅全','eaddress': '杭州电子科技大学生活区','e_id':2015003}];


	//页面加载完成执行的函数
	$scope.$watch('$viewContentLoaded',function(){
		initMap('map-stations');
		//初始化部分功能
		initFunc();
	});

	/**
	 * 初始化地图
	 * @param  {[type]} element [description]
	 * @return {[type]}         [description]
	 */
	var initMap = function(element){
		var map = new BMap.Map(element);
		var point = new BMap.Point(120.164785,30.2453185);
		map.centerAndZoom(point, 12);

		//初始化控件
		map.addControl(new BMap.NavigationControl({anchor: BMAP_ANCHOR_TOP_RIGHT}));
		map.addControl(new BMap.OverviewMapControl());
		$scope.map = map;

		//获取站点数据
		getStations();
		//给地图添加右键菜单
		addMapMenu();
	}

	var initFunc = function(){
		$('#delete-station').find('.agree-delete-station').click(function(){
			deleteMarker();
		});

		$('#update-station').find('.agree-update-station').click(function(){
			updateMarker();
		});

		$('#add-stations').find('.agree-add-station').click(function(){
			addStation();
		});

		$('#sta-staff-date').find('.agree-get-sta-staff').click(function(){
			getStaStaffList();
		})
	};

	/**
	 * 给地图添加右键菜单，添加站点和获取坐标的功能
	 */
	var addMapMenu = function(){
		var mapMenu = new BMap.ContextMenu();

		mapMenu.addItem(new BMap.MenuItem('添加站点', function(point){
			
			$scope.addStaInfo.x = point.lng;
			$scope.addStaInfo.y = point.lat;

			$('#add-stations').openModal();

		}));

		mapMenu.addItem(new BMap.MenuItem('获取坐标', function(point){

			var lng = '经度：'+point.lng;
			var lat = '纬度：'+point.lat;
			$('#show-coordinates').find('.modal-content').find('.lng').text(lng);
			$('#show-coordinates').find('.modal-content').find('.lat').text(lat);
			$('#show-coordinates').openModal();
		}));

		$scope.map.addContextMenu(mapMenu);
	}

	/**
	 * 从后台取得站点数据
	 * @return {[type]} [description]
	 */
	var getStations = function(){
		var url = '/factory_vehicle/api/Station';

		$http.get(url).success(function(data){
			//console.log(data);
			$scope.stations = [];
			for(var i=0;i<data.length;i++){
				var station = {
					'name': data[i].s_name,
					'id': data[i].s_id,
					'x': data[i].longitude,
					'y': data[i].latitude
				}

				$scope.stations.push(station);
			}

			//添加站点marker
			addMarker();
		}).error(function(){
			console.log('从后台获得站点数据失败');
		});
	}

	/**
	 * 添加站点marker
	 */
	var addMarker = function(){
		var point = null;
		$scope.markers = [];
		for(var i=0;i<$scope.stations.length;i++){
			point = new BMap.Point($scope.stations[i].x,$scope.stations[i].y);
			var marker = new BMap.Marker(point);
			$scope.map.addOverlay(marker);

			$scope.markers.push(marker);

			//给marker添加事件
			addEvent(marker,$scope.stations[i].name);
			//给marker添加右键菜单
			addMarkerMenu(marker,$scope.stations[i].id);
		}
	}

	/**
	 * 为marker添加事件
	 * @param {[type]} marker [description]
	 * @param {[type]} name   [description]
	 */
	var addEvent = function(marker,name){
		marker.addEventListener('click',function(e){
			marker.setAnimation(null);
			Materialize.toast(name,4000);
		});
	}

	/**
	 * 为marker添加右键菜单
	 * @param {[type]} marker [站点marker]
	 * @param {int} id [站点的id]
	 */
	var addMarkerMenu = function(marker,id){

		var markerMenu = new BMap.ContextMenu();

		markerMenu.addItem(new BMap.MenuItem('删除',function(){
			$scope.deleteStaInfo = {
				'marker': marker,
				'id': id
			};
			$('#delete-station').openModal();
		}));

		markerMenu.addItem(new BMap.MenuItem('修改',function(){
			$scope.updateStaInfo.marker = marker;
			$scope.updateStaInfo.id = id;

			$('#update-station').openModal();
		}));

		markerMenu.addItem(new BMap.MenuItem('乘车员工',function(){
			$('#sta-staff-date').openModal();
		}));

		marker.addContextMenu(markerMenu);
	};
	
	/**
	 * 删除站点
	 * @param marker 
	 * @param id     [站点id]
	 */
	var deleteMarker = function(){

		//在地图删除该marker
		$scope.map.removeOverlay($scope.deleteStaInfo.marker);

		//在本地的数据里删除该站点
		for(var i=0; i<$scope.stations.length; i++){
			if($scope.stations[i].id  == $scope.deleteStaInfo.id){
				$scope.stations.splice(i,1);
				break;
			}
		}
		//手动进行脏检查
		$scope.$apply($scope.stations);

		//调用后台接口，在数据库中删除该站点，通过发送id
		var url = '/factory_vehicle/api/Station/'+$scope.deleteStaInfo.id;
		$http({
			url: url,
			method: 'DELETE'
		}).success(function(){
			console.log('删除站点成功');
		}).error(function(){
			console.log('删除站点失败');
		});
	};

	/**
	 * 修改站点信息
	 * @param id [description]]
	 */
	var updateMarker = function(){
		console.log($scope.updateStaInfo);
		var url = '/factory_vehicle/api/Station/'+$scope.updateStaInfo.id;

		$http({
			url: url,
			method: 'PUT',
			data: {
				's_name': $scope.updateStaInfo.name,
				'longitude': $scope.updateStaInfo.x,
				'latitude': $scope.updateStaInfo.y
			}
		}).success(function(){
			console.log('修改站点成功');
		}).error(function(){
			console.log('修改站点失败');
		});

		for(var i=0;i<$scope.stations.length;i++){
			if($scope.stations[i].id == $scope.updateStaInfo.id){
				$scope.stations[i].name = $scope.updateStaInfo.name;
				break;
			}
		}

		//是否对marker进行操作，取决于最终版本是否需要由用户输入经纬度
	};

	/**
	 * 添加站点信息
	 */
	var addStation = function(){
		console.log($scope.addStaInfo);

		var url = '/factory_vehicle/api/Station';

		$http({
			url: url,
			method: 'POST',
			data: {
				's_name': $scope.addStaInfo.name,
				'longitude': $scope.addStaInfo.x,
				'latitude': $scope.addStaInfo.y
			}
		}).success(function(){
			console.log('新增站点成功');
			$scope.map.clearOverlays();
			getStations();
		}).error(function(){
			console.log('新增站点失败');
		});

	};

	$scope.reflectMarker = function(id){
		console.log($scope.stations,$scope.markers);
		for(var i=0;i<$scope.markers.length;i++){
			$scope.markers[i].setAnimation(null);
		}
		console.log(id);
		for(i=0;i<$scope.stations.length;i++){
			if($scope.stations[i].id == id){
				break;
			}
		}
		console.log($scope.stations.length,$scope.markers.length,i);
		$scope.markers[i].setAnimation(BMAP_ANIMATION_BOUNCE);
	};

	$scope.searchStation = function(){
		$scope.searchResult = [];
		var url;

		var condition = Number($scope.searchCondition);
		if(!isNaN(condition)){
			//用户输入的是id
			url = '/factory_vehicle/api/Station/'+condition;
		}else{
			//用户输入的名称
			url = '/factory_vehicle/api/Station?name='+$scope.searchCondition;
		}
		console.log($scope.searchCondition,url);
		$http.get(url).success(function(data){
			// console.log(data);
			if(!isNaN(condition)){
				$('#station-table').find('.search-result').find('.search-station-result').find('strong').text(1);
				var station = {
					'name': data.s_name,
					'id': data.s_id,
					'x': data.longitude,
					'y': data.latitude
				}

				$scope.searchResult.push(station);
			}else{
				$('#station-table').find('.search-result').find('.search-station-result').find('strong').text(data.length);
				for(var i=0;i<data.length;i++){
					var station = {
						'name': data[i].s_name,
						'id': data[i].s_id,
						'x': data[i].longitude,
						'y': data[i].latitude
					}

					$scope.searchResult.push(station);
				}
			}
			// console.log($scope.searchResult);
			$('#station-table').find('.search-result').find('.search-station-result').show();
		}).error(function(){
			console.log('查询失败');
		});
	};

	var getStaStaffList = function(){
		$('#station-staff').show();
	};
});




factoryVehicle.controller('adminPeopleManager',function($scope,$http){
	$scope.map;		//保存当前视图的操作地图
	$scope.stations;		//保存当前的站点信息
	$scope.staMarkers = [];		//保存当前的站点marker
	$scope.updateStaffSta = {
		'e_id': '',
		's_id': ''
	};			//保存要更换站点的员工与对应的站点
	$scope.addpeoples = [{'id':1212, 'name':'帅全'},{'id':1212, 'name':'帅全'},{'id':1212, 'name':'帅全'},{'id':1212, 'name':'帅全'},{'id':1212, 'name':'帅全'},{'id':1212, 'name':'帅全'},{'id':1212, 'name':'帅全'},{'id':1212, 'name':'帅全'},{'id':1212, 'name':'帅全'},{'id':1212, 'name':'帅全'},{'id':1212, 'name':'帅全'},{'id':1212, 'name':'帅全'},{'id':1212, 'name':'帅全'},{'id':1212, 'name':'帅全'},{'id':1212, 'name':'帅全'},{'id':1212, 'name':'帅全'},{'id':1212, 'name':'帅全'}]
	$scope.deletepeole;
	$scope.updatepeople;
	
	//页面加载完成执行的函数
	$scope.$watch('$viewContentLoaded',function(){
		initMap('map-people');
		initChartAll('people-chart-all');
	});

	/**
	 * 初始化地图
	 * @param  {[type]} element [description]
	 * @return {[type]}         [description]
	 */
	var initMap = function(element){
		var map = new BMap.Map(element);
		var point = new BMap.Point(120.164785,30.2453185);
		map.centerAndZoom(point, 12);

		//初始化控件
		map.addControl(new BMap.NavigationControl({anchor:BMAP_ANCHOR_TOP_RIGHT}));
		map.addControl(new BMap.OverviewMapControl());
		$scope.map = map;

		//获取站点
		getStations();
	}

	/**
	 * 从后台取得站点数据
	 * @return {[type]} [description]
	 */
	var getStations = function(){
		var url = '/factory_vehicle/api/Station';

		$http.get(url).success(function(data){
			//console.log(data);
			$scope.stations = [];
			for(var i=0;i<data.length;i++){
				var station = {
					'name': data[i].s_name,
					'id': data[i].s_id,
					'x': data[i].longitude,
					'y': data[i].latitude
				}

				$scope.stations.push(station);
			}

			//添加站点marker
			addMarker();
		}).error(function(){
			console.log('从后台获得站点数据失败');
		});
	}

	/**
	 * 添加站点marker
	 */
	var addMarker = function(){
		var point = null;
		$scope.staMarkers = [];
		for(var i=0;i<$scope.stations.length;i++){
			point = new BMap.Point($scope.stations[i].x,$scope.stations[i].y);
			var marker = new BMap.Marker(point);
			$scope.map.addOverlay(marker);

			$scope.staMarkers.push(marker);
			//给marker添加事件
			addEvent(marker,$scope.stations[i].name);
			//给marker添加右键菜单
			addMarkerMenu(marker,$scope.stations[i].id);
		}
	}

	/**
	 * 为marker添加事件
	 * @param {[type]} marker [description]
	 * @param {[type]} name   [description]
	 */
	var addEvent = function(marker,name){
		marker.addEventListener('click',function(e){
			marker.setAnimation(null);
			Materialize.toast(name,4000);
		});
	}

	/**
	 * 为marker添加右键菜单
	 * @param {[type]} marker [站点marker]
	 * @param {int} id [站点的id]
	 */
	var addMarkerMenu = function(marker,id){

		var markerMenu = new BMap.ContextMenu();

		markerMenu.addItem(new BMap.MenuItem('更改当前员工的乘车站点为该站点',function(){
			console.log(id);
			$scope.updateStaffSta.s_id = id;
			updateEmployeeSta();
		}));

		marker.addContextMenu(markerMenu);
	};

	/**
	 * 更改员工的乘车站点
	 */
	var updateEmployeeSta = function(){
		var url;
	}

	/**
	 * 初始化人员总数信息表
	 * @param  {[type]} element [description]
	 * @return {[type]}         [description]
	 */
	var initChartAll = function(element){
		var mychart = echarts.init(document.getElementById(element));

		option = {
		    title : {
		        text: '更新人员信息',
		        left: 'center'
		    },
		    tooltip : {
		        trigger: 'item',
		        formatter: "{b} : {c} ({d}%)"
		    },
		    legend: {
		        orient: 'vertical',
		        left: 'center',
		        top: '28',
		        data: ['新增人员','修改人员','删除人员']
		    },
		    series : [
		        {
		            name: '访问来源',
		            type: 'pie',
		            radius : '40%',
		            zlevel: 5,
		            center: ['50%', '60%'],
		            data:[
		                {value:26, name:'新增人员'},
		                {value:15, name:'修改人员'},
		                {value:4, name:'删除人员'}
		            ],
		            itemStyle: {
		                emphasis: {
		                    shadowBlur: 10,
		                    shadowOffsetX: 0,
		                    shadowColor: 'rgba(0, 0, 0, 0.5)'
		                }
		            }
		        }
		    ]
		};

		mychart.setOption(option);
	}

	/**
	 * 展示选中的员工和对应的站点
	 * @param  {[type]} e_id [员工ID]
	 * @param  {[type]} s_id [站点ID]
	 */
	$scope.showEmployeeSta = function(e_id,s_id){
		$scope.updateStaffSta.e_id = e_id;
		$scope.updateStaffSta.s_id = s_id;

		var ePoint = new BMap.Point(120.198086,30.25777);
		var homeIcon = new BMap.Icon('./images/station1.png',new BMap.Size(19,25));
		var eMarker = new BMap.Marker(ePoint,{icon: homeIcon});
		$scope.map.addOverlay(eMarker);
		addEvent(eMarker,'这里是员工的家');

		s_id = 2015002;
		for(var i=0;i<$scope.stations.length;i++){
			if($scope.stations[i].id == s_id){
				var sMarker = $scope.staMarkers[i];
				break;
			}
		}

		var sPoint = sMarker.point;
		var walking = new BMap.WalkingRoute($scope.map, {renderOptions:{map: $scope.map, autoViewport: true}});
		walking.search(ePoint,sPoint);
	}
});



factoryVehicle.controller('adminReportManager',function($scope){
	$scope.stations;
	$scope.peoples;

	$scope.$watch('$viewContentLoaded', function(){
		getStation();
	});

	var getStation = function(){
		$scope.stations = [{"name":"石大路华中路路口二","x":120.209603,"y":30.338951,"id":3211},{"name":"采荷新村","x":120.198086,"y":30.25777,"id":3212},{"name":"采荷小区芙蓉邨","x":120.202036,"y":30.259711,"id":3216},{"name":"采荷小区翠柳邨","x":120.202275,"y":30.261458,"id":3217},{"name":"丁桥农贸市场","x":120.230507,"y":30.354838,"id":3221},{"name":"大农港路环丁路口","x":120.245056,"y":30.363813,"id":3222},{"name":"大农港路勤丰路口","x":120.243476,"y":30.362785,"id":3223},{"name":"长睦家园南门","x":120.241218,"y":30.362504,"id":3226},{"name":"大农港路惠兰雅路口一","x":120.236396,"y":30.360775,"id":3228},{"name":"大农港路惠兰雅路口二","x":120.237563,"y":30.360791,"id":3229},{"name":"富春路清江路口","x":120.211621,"y":30.243216,"id":3232},{"name":"解放路口富春路西","x":120.217008,"y":30.250142,"id":3234},{"name":"民心路富春路东","x":120.224265,"y":30.258144,"id":3236},{"name":"钱潮路口富春路东","x":120.227674,"y":30.262152,"id":3237},{"name":"四季花园","x":120.224959,"y":30.254166,"id":3241},{"name":"钱江路庆春路口","x":120.221491,"y":30.262293,"id":3242},{"name":"解放东路四季青儿童精装市场","x":120.202618,"y":30.254338,"id":3089},{"name":"太平门直街双菱路口","x":120.201891,"y":30.265724,"id":3100},{"name":"临丁路北一八一电杆","x":120.221833,"y":30.364577,"id":3140},{"name":"河坊街四七二号","x":120.167178,"y":30.245548,"id":1247},{"name":"复兴南街三三七号","x":120.167139,"y":30.21184,"id":1137},{"name":"石祥一号码头","x":120.17596,"y":30.329721,"id":2155},{"name":"香樟公寓","x":120.113125,"y":30.282644,"id":5017},{"name":"枫桦西路龙王沙路口","x":120.10646,"y":30.167887,"id":5178},{"name":"苏堤南口","x":120.161051,"y":30.235962,"id":8012},{"name":"沈半路一六七号","x":120.169035,"y":30.325922,"id":4084},{"name":"莫干山路石祥路口东南角","x":120.114838,"y":30.327995,"id":4133},{"name":"广电集团","x":120.183576,"y":30.279611,"id":2114},{"name":"凯西社区","x":120.197036,"y":30.268617,"id":3098},{"name":"河坊街五五四号","x":120.179093,"y":30.245895,"id":1263},{"name":"德胜东路九环路口一","x":120.260516,"y":30.315627,"id":3281},{"name":"长生路五四号","x":120.166881,"y":30.262722,"id":1284},{"name":"ES一十-十一墩","x":120.150422,"y":30.338562,"id":4168},{"name":"祥园路乐富智慧园","x":120.11744,"y":30.341032,"id":4256},{"name":"水印康庭","x":120.173917,"y":30.322049,"id":2091},{"name":"枫桦路","x":120.110025,"y":30.170447,"id":5123},{"name":"德胜路东新路口","x":120.184338,"y":30.307731,"id":2187},{"name":"石桥路石祥路口99路公交站","x":120.199761,"y":30.337362,"id":2261},{"name":"凯旋路电杆二","x":120.197945,"y":30.275619,"id":3117},{"name":"储鑫路一一号南","x":120.168223,"y":30.340915,"id":4221},{"name":"龙王沙路","x":120.102404,"y":30.17385,"id":5190},{"name":"金渡北路","x":120.066864,"y":30.3323002,"id":7305},{"name":"城市学院西（二）","x":120.161333,"y":30.331541,"id":4104},{"name":"江城路一九九号","x":120.17977,"y":30.229807,"id":1135},{"name":"石贯子巷","x":120.175826,"y":30.261408,"id":1205},{"name":"朝晖二号码头","x":120.178444,"y":30.293388,"id":2153},{"name":"西湖文化广场","x":120.170138,"y":30.279596,"id":2217}];
		// $('select').material_select();
	};

	$scope.getNameList = function(){
		$scope.peoples = [{name:'帅全'},{name:'帅全'},{name:'帅全'},{name:'帅全'},{name:'帅全'},{name:'帅全'},{name:'帅全'},{name:'帅全'},{name:'帅全'},{name:'帅全'},{name:'帅全'}];

	};

	$scope.getChartData = function(){
		createChart();
	};

	var createChart = function(){
		var mychart = echarts.init(document.getElementById('body-chart'));

		option = {
		    title: {
		        text: '各站点乘车人数图'
		    },
		    tooltip: {
		        trigger: 'axis'
		    },
		    legend: {
		        data:[
		        	{
		        		name: '乘车人数',
		        		icon: 'circle'
		        	}
		        ]
		    },
		    grid: {
		        left: '3%',
		        right: '4%',
		        bottom: '8%',
		        containLabel: true
		    },
		    toolbox: {
		        feature: {
		            saveAsImage: {},
		            dataView: {},
		            magicType: {
		            	show: true,
		            	type: ['line', 'bar']
		            }
		        }
		    },
		    dataZoom: [
		    	{
		    		type: 'slider',
		    		start: 0,
		    		end: 100,
		    		xAxisIndex: 0,
		    		bottom: '1%'
		    	},
		    	{
		    		type: 'inside',
		    		start: 0,
		    		end: 100,
		    		xAxisIndex: 0
		    	}
		    ],
		    xAxis: {
		        type: 'category',
		        boundaryGap: false,
		        data: ['周一','周二','周三','周四','周五','周六','周日','周一','周二','周三','周四','周五','周六','周日','周一','周二','周三','周四','周五','周六','周日','周一','周二','周三','周四','周五','周六','周日','周一','周二','周三','周四','周五','周六','周日','周一','周二','周三','周四','周五','周六','周日']
		    },
		    yAxis: {
		        type: 'value'
		    },
		    series: [
		        {
		            name:'乘车人数',
		            type:'line',
		            stack: '总量',
		            data:[120, 132, 101, 134, 90, 230, 210,120, 132, 101, 134, 90, 230, 210,120, 132, 101, 134, 90, 230, 210,120, 132, 101, 134, 90, 230, 210,120, 132, 101, 134, 90, 230, 210,120, 132, 101, 134, 90, 230, 210]
		        }
		    ]
		}
		mychart.setOption(option);

	}

});




factoryVehicle.controller('affairsBusManager',function($scope,$http){
	//$scope.buses = [{'brand': '安凯','logon': '2013-10-11','dated': '2019-10-11','license': '浙A56798', 'seat': 24},{'brand': '安凯','logon': '2013-10-11','dated': '2019-10-11','license': '浙A56798', 'seat': 24},{'brand': '安凯','logon': '2013-10-11','dated': '2019-10-11','license': '浙A56798', 'seat': 24},{'brand': '安凯','logon': '2013-10-11','dated': '2019-10-11','license': '浙A56798', 'seat': 24},{'brand': '安凯','logon': '2013-10-11','dated': '2019-10-11','license': '浙A56798', 'seat': 24},{'brand': '安凯','logon': '2013-10-11','dated': '2019-10-11','license': '浙A56798', 'seat': 24},{'brand': '安凯','logon': '2013-10-11','dated': '2019-10-11','license': '浙A56798', 'seat': 24},{'brand': '安凯','logon': '2013-10-11','dated': '2019-10-11','license': '浙A56798', 'seat': 24},{'brand': '安凯','logon': '2013-10-11','dated': '2019-10-11','license': '浙A56798', 'seat': 24},{'brand': '安凯','logon': '2013-10-11','dated': '2019-10-11','license': '浙A56798', 'seat': 24},{'brand': '安凯','logon': '2013-10-11','dated': '2019-10-11','license': '浙A56798', 'seat': 24},{'brand': '安凯','logon': '2013-10-11','dated': '2019-10-11','license': '浙A56798', 'seat': 24},{'brand': '安凯','logon': '2013-10-11','dated': '2019-10-11','license': '浙A56798', 'seat': 24},{'brand': '安凯','logon': '2013-10-11','dated': '2019-10-11','license': '浙A56798', 'seat': 24},{'brand': '安凯','logon': '2013-10-11','dated': '2019-10-11','license': '浙A56798', 'seat': 24}];
	$scope.buses;	//保存车辆信息
	$scope.deleteBusInfo;	//保存要删除的车辆信息
	$scope.updateBusInfo;	//保存要更新的车辆信息
	$scope.addBusInfo = {
		brand: '',
		seat: '',
		d_license: '',
		dated: '',
		logon: ''
	};		//保存新增加的车辆信息
	$scope.searchCondition = {
		condition: '',
		way: ''		//1表示按照ID,2表示按照品牌
	};		//保存查询的信息
	$scope.searchResult;		//保存查询的结果

	//页面加载完成执行的函数
	$scope.$watch('$viewContentLoaded',function(){
		initFunc();
		getBusInfo();
	});

	/**
	 * 初始化页面的模态框，与业务逻辑无关
	 * @return {[type]} [description]
	 */
	var initFunc = function(){
		$('#showBus-all').find('.heading').find('.add').click(function(){
			$('#add-bus-modal').openModal();
		});
		$('#showBus-all').find('.heading').find('.search').click(function(){
			$('#search-bus-modal').openModal();
		});

		$('#update-bus-modal').find('.modal-footer').find('.agree-update-bus').click(function(){
			console.log($scope.updateBusInfo);
			updateBus();
		});
		$('#delete-bus-modal').find('.modal-footer').find('.agree-delete-bus').click(function(){
			// console.log($scope.deleteBusInfo);
			deleteBus();
		});
		$('#add-bus-modal').find('.modal-footer').find('.agree-add-bus').click(function(){
			console.log($scope.addBusInfo);
			addBus();
		});
		$('#search-bus-modal').find('.modal-footer').find('.agree-search-bus').click(function(){
			console.log($scope.searchCondition);
			searchBus();
		});
	}

	/**
	 * 向后台请求车辆信息
	 * @return {[type]} [description]
	 */
	var getBusInfo = function(){
		var url = '/factory_vehicle/api/car_inf';
		$http.get(url).success(function(data){
			$scope.buses = data;	//将后台取得的数据保存
		}).error(function(err){
			console.log(err);
		});
	};

	/**
	 * 向后台请求删除车辆信息
	 * @return {[type]} [description]
	 */
	var deleteBus = function(){
		var url = '/factory_vehicle/api/car_inf/'+$scope.deleteBusInfo.id;

		for(var i=0;i<$scope.buses.length;i++){
			if($scope.buses[i].id == $scope.deleteBusInfo.id){
				$scope.buses.splice(i,1);
				break;
			}
		}

		$http({
			url: url,
			method: 'DELETE'
		}).success(function(){
			console.log('车辆删除成功');
		}).error(function(){
			console.log('车辆删除失败');
		})
	}

	/**
	 * 向后台请求添加车辆
	 */
	var addBus = function(){
		var url = '/factory_vehicle/api/car_inf';

		$http({
			url: url,
			method: 'POST',
			data: {
				'id': '',
				'brand': $scope.addBusInfo.brand,
				'seat': $scope.addBusInfo.seat,
				'd_license': $scope.addBusInfo.d_license,
				'logon': $scope.addBusInfo.logon,
				'dated': $scope.addBusInfo.dated
			}
		}).success(function(){
			console.log('新增车辆成功');
			getBusInfo();		//重新获取车辆数据
		}).error(function(){
			console.log('新增车辆失败');
		})
	}

	/**
	 * 向后台请求查询车辆信息
	 * @return {[type]} [description]
	 */
	var searchBus = function(){
		var url;

		if($scope.searchCondition.way == '1'){
			url = '/factory_vehicle/api/car_inf/'+$scope.searchCondition.condition;
		}else if($scope.searchCondition.way == '2'){
			url = '/factory_vehicle/api/car_inf?brand='+$scope.searchCondition.condition;
		}

		$http.get(url).success(function(data){
			console.log(data);
			$scope.searchResult = data;

		}).error(function(){
			console.log('查询车辆信息出错');
		});
	}

	/**
	 * 向后台请求修改车辆信息
	 * @return {[type]} [description]
	 */
	var updateBus = function(){
		var url = '/factory_vehicle/api/car_inf/'+$scope.updateBusInfo.id;

		$http({
			url: url,
			method: 'PUT',
			data: {
				'id': $scope.updateBusInfo.id,
				'brand': $scope.updateBusInfo.brand,
				'seat': $scope.updateBusInfo.seat,
				'logon': $scope.updateBusInfo.logon,
				'dated': $scope.updateBusInfo.dated,
				'd_license': $scope.updateBusInfo.d_license
			}
		}).success(function(){
			console.log('修改成功');
		}).error(function(){
			console.log('修改失败');
		})
	}

	/**
	 * 获得要修改的车辆信息
	 * @param  {[type]} bus [description]
	 * @return {[type]}     [description]
	 */
	$scope.getUpdateBus = function(bus){
		$scope.updateBusInfo = bus;

		$('#update-bus-modal').find('.modal-content').find('#update-brand').val(bus.brand);
		$('#update-bus-modal').find('.modal-content').find('#update-seat').val(bus.seat);
		$('#update-bus-modal').find('.modal-content').find('#update-logon').val(bus.logon);
		$('#update-bus-modal').find('.modal-content').find('#update-dated').val(bus.dated);
		$('#update-bus-modal').find('.modal-content').find('#update-license').val(bus.d_license);
		$('#update-bus-modal').openModal();
	};

	/**
	 * 获得要删除的车辆信息
	 * @param  {[type]} bus [description]
	 * @return {[type]}     [description]
	 */
	$scope.getDeleteBus = function(bus){

		$scope.deleteBusInfo = bus;
		$('#delete-bus-modal').openModal();
		
	};
});

factoryVehicle.controller('affairsRoutesManager', function($scope){
	$scope.stations;
	$scope.routes;

	//页面加载完成执行的函数
	$scope.$watch('$viewContentLoaded',function(){
		initMap('map-routes');
		getRoutes();
	});

	/**
	 * 初始化地图
	 * @param  {[type]} element [description]
	 * @return {[type]}         [description]
	 */
	var initMap = function(element){
		var map = new BMap.Map(element);
		var point = new BMap.Point(120.164785,30.2453185);
		map.centerAndZoom(point, 14);

		//初始化控件
		map.addControl(new BMap.NavigationControl({anchor:BMAP_ANCHOR_TOP_RIGHT}));
		map.addControl(new BMap.OverviewMapControl());
		$scope.map = map;

		//展示数据
		showStations();
	}

	/**
	 * 在地图上展示站点
	 * @return {[type]} [description]
	 */
	var showStations = function(){
		//数据后期异步获取，存入stations中
		$scope.stations = [{"name":"石大路华中路路口二","x":120.209603,"y":30.338951,"id":3211},{"name":"采荷新村","x":120.198086,"y":30.25777,"id":3212},{"name":"采荷小区芙蓉邨","x":120.202036,"y":30.259711,"id":3216},{"name":"采荷小区翠柳邨","x":120.202275,"y":30.261458,"id":3217},{"name":"丁桥农贸市场","x":120.230507,"y":30.354838,"id":3221},{"name":"大农港路环丁路口","x":120.245056,"y":30.363813,"id":3222},{"name":"大农港路勤丰路口","x":120.243476,"y":30.362785,"id":3223},{"name":"长睦家园南门","x":120.241218,"y":30.362504,"id":3226},{"name":"大农港路惠兰雅路口一","x":120.236396,"y":30.360775,"id":3228},{"name":"大农港路惠兰雅路口二","x":120.237563,"y":30.360791,"id":3229},{"name":"富春路清江路口","x":120.211621,"y":30.243216,"id":3232},{"name":"解放路口富春路西","x":120.217008,"y":30.250142,"id":3234},{"name":"民心路富春路东","x":120.224265,"y":30.258144,"id":3236},{"name":"钱潮路口富春路东","x":120.227674,"y":30.262152,"id":3237},{"name":"四季花园","x":120.224959,"y":30.254166,"id":3241},{"name":"钱江路庆春路口","x":120.221491,"y":30.262293,"id":3242},{"name":"解放东路四季青儿童精装市场","x":120.202618,"y":30.254338,"id":3089},{"name":"太平门直街双菱路口","x":120.201891,"y":30.265724,"id":3100},{"name":"临丁路北一八一电杆","x":120.221833,"y":30.364577,"id":3140},{"name":"河坊街四七二号","x":120.167178,"y":30.245548,"id":1247},{"name":"复兴南街三三七号","x":120.167139,"y":30.21184,"id":1137},{"name":"石祥一号码头","x":120.17596,"y":30.329721,"id":2155},{"name":"香樟公寓","x":120.113125,"y":30.282644,"id":5017},{"name":"枫桦西路龙王沙路口","x":120.10646,"y":30.167887,"id":5178},{"name":"苏堤南口","x":120.161051,"y":30.235962,"id":8012},{"name":"沈半路一六七号","x":120.169035,"y":30.325922,"id":4084},{"name":"莫干山路石祥路口东南角","x":120.114838,"y":30.327995,"id":4133},{"name":"广电集团","x":120.183576,"y":30.279611,"id":2114},{"name":"凯西社区","x":120.197036,"y":30.268617,"id":3098},{"name":"河坊街五五四号","x":120.179093,"y":30.245895,"id":1263},{"name":"德胜东路九环路口一","x":120.260516,"y":30.315627,"id":3281},{"name":"长生路五四号","x":120.166881,"y":30.262722,"id":1284},{"name":"ES一十-十一墩","x":120.150422,"y":30.338562,"id":4168},{"name":"祥园路乐富智慧园","x":120.11744,"y":30.341032,"id":4256},{"name":"水印康庭","x":120.173917,"y":30.322049,"id":2091},{"name":"枫桦路","x":120.110025,"y":30.170447,"id":5123},{"name":"德胜路东新路口","x":120.184338,"y":30.307731,"id":2187},{"name":"石桥路石祥路口99路公交站","x":120.199761,"y":30.337362,"id":2261},{"name":"凯旋路电杆二","x":120.197945,"y":30.275619,"id":3117},{"name":"储鑫路一一号南","x":120.168223,"y":30.340915,"id":4221},{"name":"龙王沙路","x":120.102404,"y":30.17385,"id":5190},{"name":"金渡北路","x":120.066864,"y":30.3323002,"id":7305},{"name":"城市学院西（二）","x":120.161333,"y":30.331541,"id":4104},{"name":"江城路一九九号","x":120.17977,"y":30.229807,"id":1135},{"name":"石贯子巷","x":120.175826,"y":30.261408,"id":1205},{"name":"朝晖二号码头","x":120.178444,"y":30.293388,"id":2153},{"name":"西湖文化广场","x":120.170138,"y":30.279596,"id":2217}];
		
		//添加站点marker
		addMarker();
	}

	/**
	 * 添加站点marker
	 */
	var addMarker = function(){
		var point = null;

		var path1 = [];
		var path2 = [];
		var path3 = [];

		for(var i=0;i<$scope.stations.length;i++){
			point = new BMap.Point($scope.stations[i].x,$scope.stations[i].y);
			var marker = new BMap.Marker(point);
			$scope.map.addOverlay(marker);

			if(i%3==0){
				path3.push(point);
			}else if(i%2==0){
				path2.push(point);
			}else{
				path1.push(point);
			}
			//给marker添加事件
			//addEvent(marker,$scope.stations[i].name);
			//给marker添加右键菜单
			//addMarkerMenu(marker,$scope.stations[i].id);
		}

		var paths = [path1,path2,path3];
		showRoutes(paths);
	};

	var getRoutes = function(){
		$scope.routes = [{'name': '线路'+Math.floor(Math.random()*100+1),'number': parseInt(Math.random()*100), 'rate': parseInt(Math.random()*90+10)+'%'},{'name': '线路'+Math.floor(Math.random()*100+1),'number': parseInt(Math.random()*100), 'rate': parseInt(Math.random()*90+10)+'%'},{'name': '线路'+Math.floor(Math.random()*100+1),'number': parseInt(Math.random()*100), 'rate': parseInt(Math.random()*90+10)+'%'},{'name': '线路'+Math.floor(Math.random()*100+1),'number': parseInt(Math.random()*100), 'rate': parseInt(Math.random()*90+10)+'%'},{'name': '线路'+Math.floor(Math.random()*100+1),'number': parseInt(Math.random()*100), 'rate': parseInt(Math.random()*90+10)+'%'},{'name': '线路'+Math.floor(Math.random()*100+1),'number': parseInt(Math.random()*100), 'rate': parseInt(Math.random()*90+10)+'%'},{'name': '线路'+Math.floor(Math.random()*100+1),'number': parseInt(Math.random()*100), 'rate': parseInt(Math.random()*90+10)+'%'},{'name': '线路'+Math.floor(Math.random()*100+1),'number': parseInt(Math.random()*100), 'rate': parseInt(Math.random()*90+10)+'%'},{'name': '线路'+Math.floor(Math.random()*100+1),'number': parseInt(Math.random()*100), 'rate': parseInt(Math.random()*90+10)+'%'},{'name': '线路'+Math.floor(Math.random()*100+1),'number': parseInt(Math.random()*100), 'rate': parseInt(Math.random()*90+10)+'%'},{'name': '线路'+Math.floor(Math.random()*100+1),'number': parseInt(Math.random()*100), 'rate': parseInt(Math.random()*90+10)+'%'},{'name': '线路'+Math.floor(Math.random()*100+1),'number': parseInt(Math.random()*100), 'rate': parseInt(Math.random()*90+10)+'%'},{'name': '线路'+Math.floor(Math.random()*100+1),'number': parseInt(Math.random()*100), 'rate': parseInt(Math.random()*90+10)+'%'},{'name': '线路'+Math.floor(Math.random()*100+1),'number': parseInt(Math.random()*100), 'rate': parseInt(Math.random()*90+10)+'%'},{'name': '线路'+Math.floor(Math.random()*100+1),'number': parseInt(Math.random()*100), 'rate': parseInt(Math.random()*90+10)+'%'},{'name': '线路'+Math.floor(Math.random()*100+1),'number': parseInt(Math.random()*100), 'rate': parseInt(Math.random()*90+10)+'%'},{'name': '线路'+Math.floor(Math.random()*100+1),'number': parseInt(Math.random()*100), 'rate': parseInt(Math.random()*90+10)+'%'},{'name': '线路'+Math.floor(Math.random()*100+1),'number': parseInt(Math.random()*100), 'rate': parseInt(Math.random()*90+10)+'%'},{'name': '线路'+Math.floor(Math.random()*100+1),'number': parseInt(Math.random()*100), 'rate': parseInt(Math.random()*90+10)+'%'},{'name': '线路'+Math.floor(Math.random()*100+1),'number': parseInt(Math.random()*100), 'rate': parseInt(Math.random()*90+10)+'%'},{'name': '线路'+Math.floor(Math.random()*100+1),'number': parseInt(Math.random()*100), 'rate': parseInt(Math.random()*90+10)+'%'},{'name': '线路'+Math.floor(Math.random()*100+1),'number': parseInt(Math.random()*100), 'rate': parseInt(Math.random()*90+10)+'%'},{'name': '线路'+Math.floor(Math.random()*100+1),'number': parseInt(Math.random()*100), 'rate': parseInt(Math.random()*90+10)+'%'},{'name': '线路'+Math.floor(Math.random()*100+1),'number': parseInt(Math.random()*100), 'rate': parseInt(Math.random()*90+10)+'%'},{'name': '线路'+Math.floor(Math.random()*100+1),'number': parseInt(Math.random()*100), 'rate': parseInt(Math.random()*90+10)+'%'},{'name': '线路'+Math.floor(Math.random()*100+1),'number': parseInt(Math.random()*100), 'rate': parseInt(Math.random()*90+10)+'%'},{'name': '线路'+Math.floor(Math.random()*100+1),'number': parseInt(Math.random()*100), 'rate': parseInt(Math.random()*90+10)+'%'},{'name': '线路'+Math.floor(Math.random()*100+1),'number': parseInt(Math.random()*100), 'rate': parseInt(Math.random()*90+10)+'%'},{'name': '线路'+Math.floor(Math.random()*100+1),'number': parseInt(Math.random()*100), 'rate': parseInt(Math.random()*90+10)+'%'}];
	};

	var showRoutes = function(paths){
		// var lushu1 = BMapLib.LuShu($scope.map,paths[0],)
		// 
	}
});


factoryVehicle.controller('affairsSchedualManager',function($scope,$http){

});


factoryVehicle.controller('affairsReportManager', function($scope){
	$scope.getChartData = function(){
		createChart();
	};

	var createChart = function(){
		var mychart = echarts.init(document.getElementById('body-chart'));

		option = {
		    title: {
		        text: '折线图堆叠'
		    },
		    tooltip: {
		        trigger: 'axis'
		    },
		    legend: {
		        data:['邮件营销','联盟广告','视频广告','直接访问','搜索引擎']
		    },
		    grid: {
		        left: '3%',
		        right: '4%',
		        bottom: '3%',
		        containLabel: true
		    },
		    toolbox: {
		        feature: {
		            saveAsImage: {}
		        }
		    },
		    xAxis: {
		        type: 'category',
		        boundaryGap: false,
		        data: ['周一','周二','周三','周四','周五','周六','周日']
		    },
		    yAxis: {
		        type: 'value'
		    },
		    series: [
		        {
		            name:'邮件营销',
		            type:'line',
		            stack: '总量',
		            data:[120, 132, 101, 134, 90, 230, 210]
		        },
		        {
		            name:'联盟广告',
		            type:'line',
		            stack: '总量',
		            data:[220, 182, 191, 234, 290, 330, 310]
		        },
		        {
		            name:'视频广告',
		            type:'line',
		            stack: '总量',
		            data:[150, 232, 201, 154, 190, 330, 410]
		        },
		        {
		            name:'直接访问',
		            type:'line',
		            stack: '总量',
		            data:[320, 332, 301, 334, 390, 330, 320]
		        },
		        {
		            name:'搜索引擎',
		            type:'line',
		            stack: '总量',
		            data:[820, 932, 901, 934, 1290, 1330, 1320]
		        }
		    ]
		}
		mychart.setOption(option);

	};
});