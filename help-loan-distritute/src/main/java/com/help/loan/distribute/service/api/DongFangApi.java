package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.utils.*;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
import com.help.loan.distribute.service.api.utils.LoanAmountUtil;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserDTO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 东方融资网：
 */
public class DongFangApi implements ApiSender {

    private static final Logger LOG = LoggerFactory.getLogger(DongFangApi.class);

    /**撞库地址*/
    private static final String URL_CHECK = "http://mirzr.rongzi.com/rzr/TransferV2/IsRegistered";

    /**注册地址*/
    private static final String URL_REGIST = "http://mirzr.rongzi.com/rzr/TransferV2/Register";

    /**密钥*/
    private static final String SECRET_KEY = "rongzi.com_8763!";

    private static final  int utmSource = 619;

    @Autowired
    private DispatcheRecDao dispatcheRecDao;

    /**城市对照表*/
    private static final Map<String,String> CITY_MAP = new HashMap<>();
    static {
        CITY_MAP.put("北京市","BEIJING");
        CITY_MAP.put("天津市","TIANJIN");
        CITY_MAP.put("上海市","SHANGHAI");
        CITY_MAP.put("重庆市","CHONGQING");
        CITY_MAP.put("邯郸市","HANDAN");
        CITY_MAP.put("石家庄市","SHIJIAZHUANG");
        CITY_MAP.put("保定市","BAODING");
        CITY_MAP.put("张家口市","ZHANGJIAKOU");
        CITY_MAP.put("承德市","CHENGDE");
        CITY_MAP.put("唐山市","TANGSHAN");
        CITY_MAP.put("廊坊市","LANGFANG");
        CITY_MAP.put("沧州市","CANGZHOU");
        CITY_MAP.put("衡水市","HENGSHUI");
        CITY_MAP.put("邢台市","XINGTAI");
        CITY_MAP.put("秦皇岛市","QINHUANGDAO");
        CITY_MAP.put("朔州市","SHUOZHOU");
        CITY_MAP.put("忻州市","XINZHOU");
        CITY_MAP.put("太原市","TAIYUAN");
        CITY_MAP.put("大同市","DATONG");
        CITY_MAP.put("阳泉市","YANGQUAN");
        CITY_MAP.put("晋中市","JINZHONG");
        CITY_MAP.put("长治市","CHANGZHI");
        CITY_MAP.put("晋城市","JINCHENG");
        CITY_MAP.put("临汾市","LINFEN");
        CITY_MAP.put("吕梁市","LVLIANG");
        CITY_MAP.put("运城市","YUNCHENG");
        CITY_MAP.put("沈阳市","SHENYANG");
        CITY_MAP.put("铁岭市","TIELING");
        CITY_MAP.put("大连市","DALIAN");
        CITY_MAP.put("鞍山市","ANSHAN");
        CITY_MAP.put("抚顺市","FUSHUN");
        CITY_MAP.put("本溪市","BENXI");
        CITY_MAP.put("丹东市","DANDONG");
        CITY_MAP.put("锦州市","JINZHOU");
        CITY_MAP.put("营口市","YINGKOU");
        CITY_MAP.put("阜新市","FUXIN");
        CITY_MAP.put("辽阳市","LIAOYANG");
        CITY_MAP.put("朝阳市","CHAOYANG");
        CITY_MAP.put("盘锦市","PANJIN");
        CITY_MAP.put("葫芦岛市","HULUDAO");
        CITY_MAP.put("长春市","CHANGCHUN");
        CITY_MAP.put("吉林市","JILIN");
        CITY_MAP.put("延边朝鲜族自治州","YANBIAN");
        CITY_MAP.put("四平市","SIPING");
        CITY_MAP.put("通化市","TONGHUA");
        CITY_MAP.put("白城市","BAICHENG");
        CITY_MAP.put("辽源市","LIAOYUAN");
        CITY_MAP.put("松原市","SONGYUAN");
        CITY_MAP.put("白山市","BAISHAN");
        CITY_MAP.put("哈尔滨市","HAERBIN");
        CITY_MAP.put("齐齐哈尔市","QIQIHAER");
        CITY_MAP.put("鸡西市","JIXI");
        CITY_MAP.put("牡丹江市","MUDANJIANG");
        CITY_MAP.put("七台河市","QITAIHE");
        CITY_MAP.put("佳木斯市","JIAMUSI");
        CITY_MAP.put("鹤岗市","HEGANGSHI");
        CITY_MAP.put("双鸭山市","SHUANGYASHAN");
        CITY_MAP.put("绥化市","SUIHUASHI");
        CITY_MAP.put("黑河市","HEIHESHI");
        CITY_MAP.put("大兴安岭地区","DAXINGANLINGDIQU");
        CITY_MAP.put("伊春市","YICHUN1");
        CITY_MAP.put("大庆市","DAQING");
        CITY_MAP.put("南京市","NANJING");
        CITY_MAP.put("无锡市","WUXI");
        CITY_MAP.put("镇江市","ZHENJIANG");
        CITY_MAP.put("苏州市","SUZHOU");
        CITY_MAP.put("南通市","NANTONG");
        CITY_MAP.put("扬州市","YANGZHOU");
        CITY_MAP.put("盐城市","YANCHENG");
        CITY_MAP.put("徐州市","XUZHOU");
        CITY_MAP.put("淮安市","HUAIAN");
        CITY_MAP.put("连云港市","LIANYUNGANG");
        CITY_MAP.put("常州市","CHANGZHOU");
        CITY_MAP.put("泰州市","TAIZHOU");
        CITY_MAP.put("宿迁市","SUQIAN");
        CITY_MAP.put("舟山市","ZHOUSHAN");
        CITY_MAP.put("衢州市","QUZHOU");
        CITY_MAP.put("杭州市","HANGZHOU");
        CITY_MAP.put("湖州市","HUZHOU");
        CITY_MAP.put("嘉兴市","JIAXING");
        CITY_MAP.put("宁波市","NINGBO");
        CITY_MAP.put("绍兴市","SHAOXING");
        CITY_MAP.put("温州市","WENZHOU");
        CITY_MAP.put("丽水市","LISHUI");
        CITY_MAP.put("金华市","JINHUA");
        CITY_MAP.put("台州市","TAIZHOU1");
        CITY_MAP.put("合肥市","HEFEI");
        CITY_MAP.put("芜湖市","WUHU");
        CITY_MAP.put("蚌埠市","BENGBU");
        CITY_MAP.put("淮南市","HUAINAN");
        CITY_MAP.put("马鞍山市","MAANSHAN");
        CITY_MAP.put("淮北市","HUAIBEI");
        CITY_MAP.put("铜陵市","TONGLING");
        CITY_MAP.put("安庆市","ANQING");
        CITY_MAP.put("黄山市","HUANGSHAN");
        CITY_MAP.put("滁州市","CHUZHOU");
        CITY_MAP.put("阜阳市","FUYANG");
        CITY_MAP.put("宿州市","SUZHOU1");
        CITY_MAP.put("巢湖市","CHAOHU");
        CITY_MAP.put("六安市","LIUAN");
        CITY_MAP.put("亳州市","BOZHOU");
        CITY_MAP.put("池州市","CHIZHOU");
        CITY_MAP.put("宣城市","XUANCHENG");
        CITY_MAP.put("福州市","FUZHOU");
        CITY_MAP.put("厦门市","XIAMEN");
        CITY_MAP.put("宁德市","NINGDE");
        CITY_MAP.put("莆田市","PUTIAN");
        CITY_MAP.put("泉州市","QUANZHOU");
        CITY_MAP.put("漳州市","ZHANGZHOU");
        CITY_MAP.put("龙岩市","LONGYAN");
        CITY_MAP.put("三明市","SANMING");
        CITY_MAP.put("南平市","NANPING");
        CITY_MAP.put("鹰潭市","YINGTAN");
        CITY_MAP.put("新余市","XINYU");
        CITY_MAP.put("南昌市","NANCHANG");
        CITY_MAP.put("九江市","JIUJIANG");
        CITY_MAP.put("上饶市","SHANGRAO");
        CITY_MAP.put("抚州市","FUZHOU1");
        CITY_MAP.put("宜春市","YICHUN");
        CITY_MAP.put("吉安市","JIAN");
        CITY_MAP.put("赣州市","GANZHOU");
        CITY_MAP.put("景德镇市","JINGDEZHEN");
        CITY_MAP.put("萍乡市","PINGXIANG");
        CITY_MAP.put("菏泽市","HEZE");
        CITY_MAP.put("济南市","JINAN");
        CITY_MAP.put("青岛市","QINGDAO");
        CITY_MAP.put("淄博市","ZIBO");
        CITY_MAP.put("德州市","DEZHOU");
        CITY_MAP.put("烟台市","YANTAI");
        CITY_MAP.put("潍坊市","WEIFANG");
        CITY_MAP.put("济宁市","JINING");
        CITY_MAP.put("泰安市","TAIAN");
        CITY_MAP.put("临沂市","LINYI");
        CITY_MAP.put("滨州市","BINZHOU");
        CITY_MAP.put("东营市","DONGYING");
        CITY_MAP.put("威海市","WEIHAI");
        CITY_MAP.put("枣庄市","ZAOZHUANG");
        CITY_MAP.put("日照市","RIZHAO");
        CITY_MAP.put("莱芜市","LAIWU");
        CITY_MAP.put("聊城市","LIAOCHENG");
        CITY_MAP.put("商丘市","SHANGQIU");
        CITY_MAP.put("郑州市","ZHENGZHOU");
        CITY_MAP.put("安阳市","ANYANG");
        CITY_MAP.put("新乡市","XINXIANG");
        CITY_MAP.put("许昌市","XUCHANG");
        CITY_MAP.put("平顶山市","PINGDINGSHAN");
        CITY_MAP.put("信阳市","XINYANG");
        CITY_MAP.put("南阳市","NANYANG");
        CITY_MAP.put("开封市","KAIFENG");
        CITY_MAP.put("洛阳市","LUOYANG");
        CITY_MAP.put("济源市","JIYUAN");
        CITY_MAP.put("焦作市","JIAOZUO");
        CITY_MAP.put("鹤壁市","HEBI");
        CITY_MAP.put("濮阳市","PUYANG");
        CITY_MAP.put("周口市","ZHOUKOU");
        CITY_MAP.put("漯河市","LUOHE");
        CITY_MAP.put("驻马店市","ZHUMADIAN");
        CITY_MAP.put("三门峡市","SANMENXIA");
        CITY_MAP.put("武汉市","WUHAN");
        CITY_MAP.put("襄樊市","XIANGFAN");
        CITY_MAP.put("鄂州市","EZHOU");
        CITY_MAP.put("孝感市","XIAOGANSHI");
        CITY_MAP.put("黄冈市","HUANGGANG");
        CITY_MAP.put("黄石市","HUANGSHI");
        CITY_MAP.put("咸宁市","XIANNING");
        CITY_MAP.put("荆州市","JINGZHOU");
        CITY_MAP.put("宜昌市","YICHANG");
        CITY_MAP.put("恩施土家族苗族自治州","ENSHI");
        CITY_MAP.put("神农架林区","SHENNONGJIALINQU");
        CITY_MAP.put("十堰市","SHIYAN");
        CITY_MAP.put("随州市","SUIZHOU");
        CITY_MAP.put("荆门市","JINGMEN");
        CITY_MAP.put("仙桃市","XIANTAO");
        CITY_MAP.put("天门市","TIANMEN");
        CITY_MAP.put("潜江市","QIANJIANGSHI");
        CITY_MAP.put("岳阳市","YUEYANG");
        CITY_MAP.put("长沙市","CHANGSHA");
        CITY_MAP.put("湘潭市","XIANGTAN");
        CITY_MAP.put("株洲市","ZHUZHOU");
        CITY_MAP.put("衡阳市","HENGYANG");
        CITY_MAP.put("郴州市","CHENZHOU");
        CITY_MAP.put("常德市","CHANGDE");
        CITY_MAP.put("益阳市","YIYANG");
        CITY_MAP.put("娄底市","LOUDI");
        CITY_MAP.put("邵阳市","SHAOYANG");
        CITY_MAP.put("湘西土家族苗族自治州","XIANGXITUJIAZUMIAOZUZIZHIZHOU");
        CITY_MAP.put("张家界市","ZHANGJIAJIE");
        CITY_MAP.put("怀化市","HUAIHUA");
        CITY_MAP.put("永州市","YONGZHOU");
        CITY_MAP.put("广州市","GUANGZHOU");
        CITY_MAP.put("汕尾市","SHANWEISHI");
        CITY_MAP.put("阳江市","YANGJIANG");
        CITY_MAP.put("揭阳市","JIEYANGSHI");
        CITY_MAP.put("茂名市","MAOMING");
        CITY_MAP.put("惠州市","HUIZHOU");
        CITY_MAP.put("江门市","JIANGMEN");
        CITY_MAP.put("韶关市","SHAOGUAN");
        CITY_MAP.put("梅州市","MEIZHOU");
        CITY_MAP.put("汕头市","SHANTOU");
        CITY_MAP.put("深圳市","SHENZHEN");
        CITY_MAP.put("珠海市","ZHUHAI");
        CITY_MAP.put("佛山市","FOSHAN");
        CITY_MAP.put("肇庆市","ZHAOQING");
        CITY_MAP.put("湛江市","ZHANJIANG");
        CITY_MAP.put("中山市","ZHONGSHAN");
        CITY_MAP.put("河源市","HEYUAN");
        CITY_MAP.put("清远市","QINGYUAN");
        CITY_MAP.put("云浮市","YUNFUSHI");
        CITY_MAP.put("潮州市","CHAOZHOU");
        CITY_MAP.put("东莞市","DONGGUAN");
        CITY_MAP.put("兰州市","LANZHOU");
        CITY_MAP.put("金昌市","JINCHANGSHI");
        CITY_MAP.put("白银市","BAIYIN");
        CITY_MAP.put("天水市","TIANSHUI");
        CITY_MAP.put("嘉峪关市","JIAYUGUANSHI");
        CITY_MAP.put("武威市","WUWEI");
        CITY_MAP.put("张掖市","ZHANGYE");
        CITY_MAP.put("平凉市","PINGLIANG");
        CITY_MAP.put("酒泉市","JIUQUAN");
        CITY_MAP.put("庆阳市","QINGYANG");
        CITY_MAP.put("定西市","DINGXISHI");
        CITY_MAP.put("陇南市","LONGNANSHI");
        CITY_MAP.put("临夏回族自治州","LINXIA");
        CITY_MAP.put("甘南藏族自治州","GANNANZANGZUZIZHIZHOU");
        CITY_MAP.put("成都市","CHENGDU");
        CITY_MAP.put("攀枝花市","PANZHIHUA");
        CITY_MAP.put("自贡市","ZIGONG");
        CITY_MAP.put("绵阳市","MIANYANG");
        CITY_MAP.put("南充市","NANCHONG");
        CITY_MAP.put("达州市","DAZHOU");
        CITY_MAP.put("遂宁市","SUINING");
        CITY_MAP.put("广安市","GUANGAN");
        CITY_MAP.put("巴中市","BAZHONG");
        CITY_MAP.put("泸州市","LUZHOU");
        CITY_MAP.put("宜宾市","YIBIN");
        CITY_MAP.put("资阳市","ZIYANG");
        CITY_MAP.put("内江市","NEIJIANG");
        CITY_MAP.put("乐山市","LESHAN");
        CITY_MAP.put("眉山市","MEISHAN");
        CITY_MAP.put("凉山彝族自治州","LIANGSHANYIZUZIZHIZHOU");
        CITY_MAP.put("雅安市","YAAN");
        CITY_MAP.put("甘孜藏族自治州","GANZIZANGZUZIZHIZHOU");
        CITY_MAP.put("阿坝藏族羌族自治州","ABAZANGZUQIANGZUZIZHIZHOU");
        CITY_MAP.put("德阳市","DEYANG");
        CITY_MAP.put("广元市","GUANGYUAN");
        CITY_MAP.put("贵阳市","GUIYANG");
        CITY_MAP.put("遵义市","ZUNYI");
        CITY_MAP.put("安顺市","ANSHUN");
        CITY_MAP.put("黔南布依族苗族自治州","QIANNAN");
        CITY_MAP.put("黔东南苗族侗族自治州","QIANDONGNANMIAOZUDONGZUZIZHIZHOU");
        CITY_MAP.put("铜仁地区","TONGREN");
        CITY_MAP.put("毕节地区","BIJIEDIQU");
        CITY_MAP.put("六盘水市","LIUPANSHUI");
        CITY_MAP.put("黔西南布依族苗族自治州","QIANXINANBUYIZUMIAOZUZIZHIZHOU");
        CITY_MAP.put("海口市","HAIKOU");
        CITY_MAP.put("三亚市","SANYA");
        CITY_MAP.put("五指山市","WUZHISHANSHI");
        CITY_MAP.put("琼海市","QIONGHAI");
        CITY_MAP.put("儋州市","DANZHOUSHI");
        CITY_MAP.put("文昌市","WENCHANGSHI");
        CITY_MAP.put("万宁市","WANNINGSHI");
        CITY_MAP.put("东方市","DONGFANGSHI");
        CITY_MAP.put("澄迈县","CHENGMAIXIAN");
        CITY_MAP.put("定安县","DINGANXIAN");
        CITY_MAP.put("屯昌县","TUNCHANGXIAN");
        CITY_MAP.put("临高县","LINGAOXIAN");
        CITY_MAP.put("白沙黎族自治县","BAISHALIZUZIZHIXIAN");
        CITY_MAP.put("昌江黎族自治县","CHANGJIANGLIZUZIZHIXIAN");
        CITY_MAP.put("乐东黎族自治县","LEDONGLIZUZIZHIXIAN");
        CITY_MAP.put("陵水黎族自治县","LINGSHUILIZUZIZHIXIAN");
        CITY_MAP.put("保亭黎族苗族自治县","BAOTINGLIZUMIAOZUZIZHIXIAN");
        CITY_MAP.put("琼中黎族苗族自治县","QIONGZHONGLIZUMIAOZUZIZHIXIAN");
        CITY_MAP.put("西双版纳傣族自治州","XISHUANGBANNADAIZUZIZHIZHOU");
        CITY_MAP.put("德宏傣族景颇族自治州","DEHONGDAIZUJINGPOZUZIZHIZHOU");
        CITY_MAP.put("昭通市","ZHAOTONG");
        CITY_MAP.put("昆明市","KUNMING");
        CITY_MAP.put("大理白族自治州","DALI");
        CITY_MAP.put("红河哈尼族彝族自治州","HONGHEHANIZUYIZUZIZHIZHOU");
        CITY_MAP.put("曲靖市","QUJING");
        CITY_MAP.put("保山市","BAOSHAN");
        CITY_MAP.put("文山壮族苗族自治州","WENSHANZHUANGZUMIAOZUZIZHIZHOU");
        CITY_MAP.put("玉溪市","YUXI");
        CITY_MAP.put("楚雄彝族自治州","CHUXIONG");
        CITY_MAP.put("普洱市","PUERSHI");
        CITY_MAP.put("临沧市","LINCANG");
        CITY_MAP.put("怒江傈傈族自治州","NUJIANGLILIZUZIZHIZHOU");
        CITY_MAP.put("迪庆藏族自治州","DIQINGZANGZUZIZHIZHOU");
        CITY_MAP.put("丽江市","LIJIANG");
        CITY_MAP.put("海北藏族自治州","HAIBEIZANGZUZIZHIZHOU");
        CITY_MAP.put("西宁市","XINING");
        CITY_MAP.put("海东地区","HAIDONGDIQU");
        CITY_MAP.put("黄南藏族自治州","HUANGNANZANGZUZIZHIZHOU");
        CITY_MAP.put("海南藏族自治州","HAINANZANGZUZIZHIZHOU");
        CITY_MAP.put("果洛藏族自治州","GUOLUOZANGZUZIZHIZHOU");
        CITY_MAP.put("玉树藏族自治州","YUSHUZANGZUZIZHIZHOU");
        CITY_MAP.put("海西蒙古族藏族自治州","HAIXIMENGGUZUZANGZUZIZHIZHOU");
        CITY_MAP.put("西安市","XIAN");
        CITY_MAP.put("咸阳市","XIANYANG");
        CITY_MAP.put("延安市","YANAN");
        CITY_MAP.put("榆林市","YULIN1");
        CITY_MAP.put("渭南市","WEINAN");
        CITY_MAP.put("商洛市","SHANGLUO");
        CITY_MAP.put("安康市","ANKANG");
        CITY_MAP.put("汉中市","HANZHONG");
        CITY_MAP.put("宝鸡市","BAOJI");
        CITY_MAP.put("铜川市","TONGCHUAN");
        CITY_MAP.put("防城港市","FANGCHENGGANG");
        CITY_MAP.put("南宁市","NANNING");
        CITY_MAP.put("崇左市","CHONGZUO");
        CITY_MAP.put("来宾市","LAIBIN");
        CITY_MAP.put("柳州市","LIUZHOU");
        CITY_MAP.put("桂林市","GUILIN");
        CITY_MAP.put("梧州市","WUZHOU");
        CITY_MAP.put("贺州市","HEZHOUSHI");
        CITY_MAP.put("贵港市","GUIGANG");
        CITY_MAP.put("玉林市","YULIN");
        CITY_MAP.put("百色市","BAISESHI");
        CITY_MAP.put("钦州市","QINZHOU");
        CITY_MAP.put("河池市","HECHISHI");
        CITY_MAP.put("北海市","BEIHAI");
        CITY_MAP.put("拉萨市","LASA");
        CITY_MAP.put("日喀则地区","RIKAZE");
        CITY_MAP.put("山南地区","SHANNANDIQU");
        CITY_MAP.put("林芝地区","LINZHIDIQU");
        CITY_MAP.put("昌都地区","CHANGDUDIQU");
        CITY_MAP.put("那曲地区","NAQUDIQU");
        CITY_MAP.put("阿里地区","ALIDIQU");
        CITY_MAP.put("银川市","YINCHUAN");
        CITY_MAP.put("石嘴山市","SHIZUISHANSHI");
        CITY_MAP.put("吴忠市","WUZHONGSHI");
        CITY_MAP.put("固原市","GUYUAN");
        CITY_MAP.put("中卫市","ZHONGWEISHI");
        CITY_MAP.put("塔城地区","TACHENG");
        CITY_MAP.put("哈密地区","HAMI");
        CITY_MAP.put("和田地区","HETIAN");
        CITY_MAP.put("阿勒泰地区","ALETAIDIQU");
        CITY_MAP.put("克孜勒苏柯尔克孜自治州","KEZILESUKEERKEZIZIZHIZHOU");
        CITY_MAP.put("博尔塔拉蒙古自治州","BOERTALAMENGGUZIZHIZHOU");
        CITY_MAP.put("克拉玛依市","KELAMAYI");
        CITY_MAP.put("乌鲁木齐市","WULUMUQI");
        CITY_MAP.put("石河子市","SHIHEZI");
        CITY_MAP.put("昌吉回族自治州","CHANGJI");
        CITY_MAP.put("五家渠市","WUJIAQUSHI");
        CITY_MAP.put("吐鲁番地区","TULUFAN");
        CITY_MAP.put("巴音郭楞蒙古自治州","BAYINGUOLENGMENGGUZIZHIZHOU");
        CITY_MAP.put("阿克苏地区","AKESU");
        CITY_MAP.put("阿拉尔市","ALAERSHI");
        CITY_MAP.put("喀什地区","KASHI");
        CITY_MAP.put("图木舒克市","TUMUSHUKESHI");
        CITY_MAP.put("伊犁哈萨克自治州","YILI");
        CITY_MAP.put("呼伦贝尔市","HULUNBEIERSHI");
        CITY_MAP.put("呼和浩特市","HUHEHAOTE");
        CITY_MAP.put("包头市","BAOTOU");
        CITY_MAP.put("乌海市","WUHAI");
        CITY_MAP.put("乌兰察布市","WULANCHABUSHI");
        CITY_MAP.put("通辽市","TONGLIAO");
        CITY_MAP.put("赤峰市","CHIFENG");
        CITY_MAP.put("鄂尔多斯市","EERDUOSI");
        CITY_MAP.put("巴彦淖尔市","BAYANNAOERSHI");
        CITY_MAP.put("锡林郭勒盟","XILINGUOLEMENG");
        CITY_MAP.put("兴安盟","XINGANMENG");
        CITY_MAP.put("阿拉善盟","ALASHANMENG");
        CITY_MAP.put("台北市","TAIBEISHI");
        CITY_MAP.put("高雄市","GAOXIONGSHI");
        CITY_MAP.put("基隆市","JILONGSHI");
        CITY_MAP.put("台中市","TAIZHONGSHI");
        CITY_MAP.put("台南市","TAINANSHI");
        CITY_MAP.put("新竹市","XINZHUSHI");
        CITY_MAP.put("嘉义市","JIAYISHI");
        CITY_MAP.put("澳门特别行政区","AOMENTEBIEXINGZHENGQU");
        CITY_MAP.put("香港特别行政区","XIANGGANGTEBIEXINGZHENGQU");
    }

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {

        try {
            return send2(po,select);
        }catch (Exception e){
            LOG.error("[东方融资网]分发异常：手机-{}",po.getMobile(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,e.getMessage()));
            return new SendResult(false,"[东方融资网]分发异常："+e.getMessage());
        }
    }

    public SendResult send2(UserAptitudePO po, UserDTO select) throws Exception {
        Integer loanAmount = Integer.valueOf(LoanAmountUtil.transformToWan(po.getLoanAmount()));
        if(loanAmount.intValue() <= 5)
            loanAmount = 5;
        isHaveAptitude(po);
        String cellPhoneNumber = MD5Util.getMd5String(po.getMobile());
        String timeStamp = DateUtil.formatToString(new Date(),DateUtil.yyyyMMddHHmmss);
        String signature = MD5Util.getMd5String(cellPhoneNumber+timeStamp+SECRET_KEY);
        // 验证手机号码是否已注册
        JSONObject checkMobileData = new JSONObject();
        checkMobileData.put("CellPhoneNumber",cellPhoneNumber);
        checkMobileData.put("TimeStamp",timeStamp);
        checkMobileData.put("Signature",signature);
        String checkResult = HttpUtil.postForJSON(URL_CHECK,checkMobileData.toJSONString());
        LOG.info("[东方融资网]验证手机号码是否注册验证结果：手机号码-{}，验证结果：{}",po.getMobile(),checkResult);
        JSONObject checkResultJson = JSONUtil.toJSON(checkResult);
        //Code=0 并且 IsRegistered=false，表示用户未注册
        if(!checkResultJson.getBooleanValue("IsRegistered") && "0".equals(checkResultJson.getString("Code"))){
            return register(po,select,loanAmount,timeStamp);
        }else {
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"[东方融资网]用户已注册"));
            return new SendResult(false, "[东方融资网]用户-" + po.getMobile() + "已注册");
        }
    }

    private SendResult register(UserAptitudePO po, UserDTO select,Integer loanAmount,String timeStamp) throws Exception {
        JSONObject userJson = new JSONObject();
        if(StringUtils.isBlank(po.getCity())){
            String city = MobileLocationUtil.getCity(po.getMobile());
            userJson.put("CityName",CITY_MAP.get(city));
        }else userJson.put("CityName",CITY_MAP.get(po.getCity()));
        userJson.put("CellPhoneNumber",po.getMobile());
        userJson.put("RealName",po.getName());
        userJson.put("LoanAmount", loanAmount);
        userJson.put("UtmSource",utmSource);
        userJson.put("TimeStamp",timeStamp);
        String userSignature = MD5Util.getMd5String(CITY_MAP.get(po.getCity())+po.getMobile()+po.getName()+loanAmount+utmSource+timeStamp+SECRET_KEY);
        userJson.put("Signature",userSignature);
        JSONObject registData = new JSONObject();
        registData.put("UtmSource",utmSource);
        registData.put("EncryptionData", AESUtil.encrypt(userJson.toJSONString(),SECRET_KEY));
        String registResult = HttpUtil.postForJSON(URL_REGIST,registData);
        LOG.info("[东方融资网]注册：手机-{}，结果：{}",po.getMobile(),registData);
        JSONObject registResultJson = JSONUtil.toJSON(registResult);
        //code 为 0 并且 IsRegistered 为 false，表示新用户注册成功
        //code 为 0 并且 IsRegistered 为 true，表示已经是老用户，系统不接受
        if("0".equals(registResultJson.getString("Code")) && !registResultJson.getBooleanValue("IsRegistered ")){
//            if(null != select){
//                String url = "http://htsj.daofen100.com/dialogue/lender/recommend?uuid=" + po.getUserId();
//                String result = HttpUtil.getForObject(url);
//                LOG.info("[东方融资网]推网贷：{}",result);
//            }
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,registResult));
            return new SendResult(true,"[东方融资网]用户-"+po.getMobile()+"注册成功");
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,registResult));
        return new SendResult(false,"[东方融资网]用户-"+po.getMobile()+"注册：未知失败");
    }


//    public static void main(String[] args){
//
//        DongFangApi api = new DongFangApi();
//
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId("f4f8ccab843d43a4b0a221e534fbed99");
//        po.setCar(4);
//        po.setHouse(2);
//        po.setCity("深圳市");
//        po.setCompany(1);
//        po.setGetwayIncome(1);
//        po.setInsurance(2);
//        po.setLoanAmount("《3-5万》");
//        po.setMobile("13632965535");
//        po.setName("测试张先森三");
//        po.setOccupation(1);
//        po.setPublicFund("有，个人月缴300-500元");
//        po.setCreditCard(1);
//        po.setCar(1);
//
//        SendResult result = api.send(po,null);
//        System.out.println(JSONUtil.toJsonString(result));
//    }

}
