//获取环境变量中的 appId appKey
appId = pm.environment.get("appId");
appKey = pm.environment.get("appKey");
timestamp = Date.now();
pm.environment.unset("_timestamp");
pm.environment.set("_timestamp", timestamp);

var sign = "";

if(pm.request.method == "GET"){
    var query = pm.request.url.query; //获取接口入参
    console.log("query",query);
    var params = {}
    var del_key = []; 
    //过滤参数
    query.each(function(item){
        //隐藏的参数不计入
        if(item.disabled){
            return;
        }
        var key = item.key;
        //判断参数是否有重复的
        var params_value = params[key];
        if(params_value == undefined){
            params[item.key] = item.value;
        } else {
            //重复的标记删除
            if(del_key.indexOf(key) == -1){
                del_key.push(key)
            }
        }
    })

    console.log("del_key",del_key);
    del_key.each(function(item){
        //删除重复的参数
        delete params[item];
    })

    //参数排序
    keys = Object.keys(params).sort() //请求参数名按照ASCII码升序排序
    console.log("keys",keys); // 看日志

    //拼接待签名字符串
    var str = []
    for (var p = 0; p < keys.length; p++) {
        var val = params[keys[p]];
        str.push(keys[p] + "=" + (val == null ? "" : val));
    }
    sign = str.join("&")
} else {
    sign = request.data;
}
console.log("sign",sign); // 看日志

//拼接其他参数
if(sign != ""){
    sign += "&"
}

sign += "_appId=";
sign += appId;
sign += "&";

sign += "_appKey=";
sign += appKey;
sign += "&";

sign += "_timestamp=";
sign += timestamp;

console.log("sign",sign); // 看日志
//MD5加密签名规格，并赋值给环境变量`sign`
pm.environment.unset("sign");
pm.environment.set("sign", CryptoJS.MD5(sign).toString());