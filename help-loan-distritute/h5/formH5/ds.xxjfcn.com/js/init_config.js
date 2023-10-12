var baseUrl = "http://47.107.99.75:8080/weilai/";  //测试环境
//var baseUrl = "http://47.107.120.11:8080/weilai/";
var $env = getQueryString("env")
if ($env == 'pro') {
    baseUrl = "http://47.107.120.11:8080/weilai"; //生产环境
}
var marketUrl = "http://wl.benfen.tech/da2/loanmarket.html"; //贷款超市
var $marketUrl = getQueryString("marketUrl")
if ($marketUrl) {
    marketUrl = $marketUrl
}

function getQueryString(t) {
    var e = new RegExp("(^|&)" + t + "=([^&]*)(&|$)", "i"), n = window.location.search.substr(1).match(e);
    return null != n ? unescape(n[2]) : null
}
