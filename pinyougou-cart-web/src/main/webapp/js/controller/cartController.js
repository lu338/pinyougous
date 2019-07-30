var app = new Vue({
    el: "#app",
    data: {
        //当前登录用户名
        username: "",
        //购物车列表
        cartList: [],
        //总价格和总数
        totalValue:{"totalNum":0, "totalMoney":0}
    },
    methods: {

        //增减购物车中商品
        addItemToCartList: function (itemId, num) {
            axios.get("cart/addItemToCartList.do?itemId=" + itemId + "&num=" + num).then(function (response) {
                if (response.data.success) {
        //重新刷新列表数据
                    app.findCartList();
                } else {
                    alert(response.data.message);
                }
            });
        },

        //查询购物车列表方法
        findCartList: function () {
            axios.get("cart/findCartList.do").then(function (response) {
                app.cartList = response.data;
                //计算总价格和总数
                app.totalValue = app.sumTotalValue(response.data);
            });
        },
        //计算总价格和总数
        sumTotalValue: function(cartList){
            var totalValue = {"totalNum":0, "totalMoney":0};
            for (var i = 0; i < cartList.length; i++) {
                var cart = cartList[i];
                for (var j = 0; j < cart.orderItemList.length; j++) {
                    var orderItem = cart.orderItemList[j];
                    totalValue.totalNum += orderItem.num;
                    totalValue.totalMoney += orderItem.totalFee;
                }
            }
            return totalValue;
        },
        //查询用户名方法
        getUsername: function () {
            axios.get("cart/getUsername.do").then(function (response) {
                app.username = response.data.username;
            });
        }
    },
    created() {
        //查询用户名
        this.getUsername();
        //查询购物车列表
        this.findCartList();
    }
});