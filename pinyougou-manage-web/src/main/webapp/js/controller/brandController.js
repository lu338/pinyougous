var app = new Vue({
    el: "#app",
    data: {
        //列表数据
        entityList: [],
        //总记录数
        total: 1,
        //页号
        pageNum: 1,
        //页大小
        pageSize: 10,
        //实体
        entity: {},
        //选着的id数组
        ids: [],
        //定义一个空的搜索条件对象
        searchEntity: {},
        checkAll:false,

    },
    methods: {
        //全选
        selectAll:function(){
            if(!this.checkAll){
                //选上
                this.ids = [];
                for (let i = 0; i < this.entityList.length; i++) {
                    const entity = this.entityList[i];
                    this.ids.push(entity.id);
                }
            } else {
                this.ids = [];
            }
        },
        
        
        searchList: function (pageNum) {
            this.pageNum = pageNum;

            /*axios.get("../brand/findPage.do?pageNum=" + this.pageNum + "&pageSize="+this.pageSize).then(function (response) {
                //总记录数
                app.total = response.data.total;
                app.entityList = response.data.list;
            });*/
            axios.post("../brand/search.do?pageNum=" + this.pageNum + "&pageSize=" + this.pageSize, this.searchEntity).then(function (response) {
                app.total = response.data.total;
                app.entityList = response.data.list;
            })
        },

        //删除
        deleteList: function () {
            if (this.ids.length == 0) {
                alert("请选择要删除的记录");
                return;
            }
            if (confirm("确定要删除选中的记录吗？")) {
                axios.get("../brand/delete.do?ids=" + this.ids).then(function (response) {
                    if (response.data.success) {
                        app.searchList(1);
                        app.ids = [];
                    } else {
                        alert(response.data.message);
                    }
                });
            }
        },
        //根据主键查询
        findOne: function (id) {
            axios.get("../brand/findOne/" + id + ".do").then(function (response) {
                app.entity = response.data;
            });
        },
        //保存
        save: function () {
            var method = "add";
            if (this.entity.id != null) {
                //如果id存在说明是修改
                method = "update"
            }
            axios.post("../brand/" + method + ".do", this.entity).then(function (response) {
                if (response.data.success) {
                    //刷新列表
                    app.searchList(app.pageNum);
                    app.entity = {};
                } else {
                    alert(response.data.message);
                }
            });
        }
    },
    //监控，vue实例的数据属性
    watch:{
        ids:{
            //开启深度监控
            deep:true,
            handler:function (newValue, oldValue) {
                if (this.ids.length == this.entityList.length) {
                    this.checkAll = true;
                } else {
                    this.checkAll = false;
                }
            }
        }
    },
    created: function () {
        /*axios.get("../brand/findAll.do").then(function (response) {
            //response里面的属性有：data,status,statusText,headers,config
            console.log(response);
            //this表示窗口对象，期望的是vue实例,app就是vue实例变量
            app.entityList = response.data;
        }).catch(function () {
            //如果网络中断，服务器宕机
            alert("获取数据失败!");
        });*/
        this.searchList(this.pageNum);
    }
});