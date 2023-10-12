/**
 * Created by FSUser on 2017/4/17.
 */
$(function () {
    var location_url = window.location.href
    var arr = location_url.split('?');
    var bs64 = encode(arr[0]);
    var wechatCode = GetQueryString("wechatCode") || '';
    //广告上报参数
    var gdt_vid = GetQueryString("gdt_vid") || '';
    //对应的微信原始ID
    var wechatId = codeConverts.wechatCodeToId[wechatCode] || ''
    console.log('bs64', bs64, wechatCode, gdt_vid);

    // var requestHost = 'http://admin.xxjfcn.com';//'http://service.loan.com';
    var requestHost = 'https://backend.zdt360.com';//'http://service.loan.com';

    var channelCode = GetQueryString('channelCode') || '';
    var activityCode = GetQueryString('activityCode') || '';
    var profile = GetQueryString('profile') || '';
    var hotCityCode = GetQueryString('hotCityCode') || '';
    //对应的热门城市ID
    var hotCityId = codeConverts.hotCityCodeToId[hotCityCode] || ''

    var timer = null;
    var isCanSendVerifyCode = true; //是否允许发送验证码
    var countdown = 60;

    //获取验证码
    $('#getVerifyCode').click(function () {
        if (!isCanSendVerifyCode) { return; }
        if (checkPhone("#telphone")) {
            var mobile = $("#telphone").val();
            $.ajax({
                type: 'POST',
                url: requestHost + '/apis/customerFromH5/v2sendCodev2',
                data: {
                    phone: mobile
                },
                success: function (res) {
                    console.log(res);
                    if (res.status == 1) {
                        //成功
                        tipMsg('获取验证码成功');
                        isCanSendVerifyCode = false;
                        countdown = 60;
                        timer = setInterval(function () {
                            if (countdown == 0) {
                                clearInterval(timer);
                                isCanSendVerifyCode = true;
                                $("#getVerifyCode").html('发验证码');
                            } else {
                                $("#getVerifyCode").html('发验证码(' + countdown + ')');
                                countdown = countdown - 1;
                            }

                        }, 1000);

                    } else {
                        //失败
                        tipMsg(res.msg);
                    }
                },
                error: function (XMLHttpRequest, textStatus, errorThrown) {
                    //失败
                    tipMsg('验证码发送失败，请稍后重试！');
                }

            });
        }

    });


    scales('.main', 750);
    $('.slide-block').click(function () {

        if (!$(this).hasClass('sliding')) {
            $(this).parent().find('.slide-block').removeClass('sliding');
            $(this).addClass('sliding');
        }
        var sex = $('.sliding').text() == '先生' ? "男" : '女';
        $('input[name="sex"]').val(sex);

    });
    //附属信息
    $(".message-item").bind('click', function () {
        if ($(this).hasClass('checked')) {
            $(this).removeClass('checked');
            $(this).next('input').val('N');
        } else {
            $(this).addClass('checked');
            $(this).next('input').val('Y');
        }
    });
    $(".message-item").each(function () {
        var tmp = $(this).next('input').val();
        if (tmp == 'Y') {
            $(this).addClass('checked');
        }
    });

    for (var i = 18; i <= 55; i++) {
        if (i != '25') {
            $('#age').append('<option value="' + i + '">' + i + '</option>');
        } else {
            $('#age').append('<option value="' + i + '" selected="selected">' + i + '</option>');
        }

    }

    // /*初始化*/
    $('.agree').click(function () {
        if ($(this).hasClass('agreed')) {
            $(this).removeClass('agreed');
            $("input[name='need_insurance']").val('N');
        } else {
            $(this).addClass('agreed');
            $("input[name='need_insurance']").val('Y');
        }
    });

    /*初始化*/
    money = 10 //贷款金额
    $('.loan-money').html(money);
    $('#loan_amount').val(money);
    time = parseInt($('.sel').text());//还款期限;
    refund_money = parseInt((money * 10000) * 0.0069 + (money * 10000) / time);//每月还款
    $('.refund-money').html(refund_money);
    $('#loan').blur(function () {
        checkLoan('#loan');
        time = parseInt($('.sel').text());
        $('.loan-money').html(money);
        $('#loan_amount').val(money);
        refund_money = parseInt((money * 10000) * 0.0069 + (money * 10000) / time);
        $('.refund-money').html(refund_money);
    });
    $('.loan-time-item').click(function () {
        if ($(this).hasClass('sel')) {
            return;
        } else {
            $(this).parent().find('.loan-time-item').removeClass('sel');
            $(this).addClass('sel');
            var time = parseInt($(this).text());
            $('#loan_time').val(time);
            var refund_money = parseInt((money * 10000) * 0.0069 + (money * 10000) / time);
            $('.refund-money').html(refund_money);
        }
    });

    $("select").each(function () {
        if ($(this).find('option:selected').text() == '请选择您的职业类型' || $(this).find('option:selected').text() == '每月收入') {
            $(this).css('color', '#bbbbbb');
        }
        $(this).change(function () {
            if ($(this).find('option:selected').text() != '请选择您的职业类型' && $(this).find('option:selected').text() != '每月收入') {
                $(this).css('color', '#000000');
            } else {
                $(this).css('color', '#bbbbbb');
            }
        });
    });
    $('#occup').change(function () {
        /*$('.message-item').removeClass('checked');
        $('.message-item').next('input').val('N');
        if ($(this).find('option:selected').text() == '上班族') {
            //社保+公积金
            $('#h_shebao').addClass('checked');
            $('input[name="shebao"]').val('Y');
 
            $('#h_fund').addClass('checked');
            $('input[name="have_fund"]').val('Y');
 
        } else if ($(this).find('option:selected').text() == '个体户' || $(this).find('option:selected').text() == '无固定职业') {
            //社保
            $('#h_shebao').addClass('checked');
            $('input[name="shebao"]').val('Y');
        } else if ($(this).find('option:selected').text() == '企业主') {
            //车
            $('#h_car').addClass('checked');
            $('input[name="have_car"]').val('Y');
        } else {
 
        }*/

    })

    $('#verifyCode').change(function () {
        if ($("#verifyCode").val() != '') {
            if ($('.agree').hasClass('agreed')) {
                return;
            } else {
                $('.agree').addClass('agreed');
                $("input[name='need_insurance']").val('Y');
            }
        }

    })


    $("#telphone").blur(function () {

        checkPhone('#telphone');
    });


    if ($("#idCard").length > 0) {
        $("#idCard").blur(function () {
            checkIdCard('#idCard');
        });
    }


    //提交
    $('.submit').click(function () {
        if (checkLoan('#loan') && checkName('#name') && checkPhone('#telphone') && checkBirthday('#birthday') && checkIdCard('#idCard') && checkVerifyCode('#verifyCode') && checkSelVoc('#occup') && checkCity('#city') && checkSelInc('#salary') && checkPayWay('#payway')) {
            //贷款用户第一次请求接口

            var _loanAmount = $("#loan").val();
            var loanAmount = _loanAmount;

            /*var _loan_time=$('#loan_time').val();
            var loan_time="";
            if(_loan_time==6){
                loan_time=0;
            }else if(_loan_time==12){
                loan_time=1;
            }else if(_loan_time==24){
                loan_time=2;
            }*/

            var _idCard = $("#idCard").length > 0 ? $("#idCard").val() : '';
            var _birthday = $("#birthday").val();
            var _verifyCode = $("#verifyCode").length > 0 ? $("#verifyCode").val() : '';

            var requestObj = {
                channelCode: channelCode,
                activityCode: activityCode,
                profile: profile,
                mobile: $("#telphone").val(),
                idCard: _idCard,
                birthday: _birthday,
                name: $("#name").val(),
                loanAmount: loanAmount,  //贷款区间 0：1-3万 1：3-5万 2：5-10万 3：10-20万 4：20万以上
                //  timeLimit:loan_time, //0：1-6个月 1：6-12月 2：12月以上
                captcha: _verifyCode,  //验证码
                sex: $("#sex").val() == '女' ? '0' : '1', //0：女 1：男
                workIdentity: $("#occup").val(),  //0：上班族 1：个体户 2：无固定职业 3：企业主
                monthlySalary: $("#salary").val(), //0:<=5000,1:5000-10000,2:10000-20000,3:20000以上
                payDayWay: $("#payway").val(), //0：银行转账 1：现金发薪
                haveSecurity: $('input[name="shebao"]').val() == 'N' ? 0 : 1, //社保 0：无 1：有
                haveFound: $('input[name="have_fund"]').val() == 'N' ? 0 : 1, //公积金
                haveCreditCard: $('input[name="have_credit_card"]').val() == 'N' ? 0 : 1, //信用卡
                havePolicy: $('input[name="have_baoxian"]').val() == 'N' ? 0 : 1, //保单
                haveCar: $('input[name="have_car"]').val() == 'N' ? 0 : 1,//车产
                haveHouse: $('input[name="have_house"]').val() == 'N' ? 0 : 1,//房产
                haveWld: $('input[name="has_wld_loan"]').val() == 'N' ? 0 : 0, //微粒贷
                city: $("#city").val(), // 0:上海 1:南京 2:福州 3:长沙
            };

            var loanMarketUrl = './loanmarket.html?';

            if (requestHost.indexOf("120") != -1) {

                loanMarketUrl = loanMarketUrl + "env=pro"
            }
            data = {};
            data.messageCode = requestObj.captcha; //验证码




            data.has_house = requestObj.haveHouse; //是否有房
            data.has_car = requestObj.haveCar; //是否有车
            data.has_social_insurance = requestObj.haveSecurity; //是否有社保
            data.has_accumulation_fund = requestObj.haveFound; //是否有公积金
            data.has_creditcard = requestObj.haveCreditCard; //是否有信用卡
            data.has_warranty = requestObj.havePolicy; //是否有保单
            //贷款金额
            data.money = requestObj.loanAmount;
            //贷款时间
            /*if(requestObj.timeLimit == 1) {
                data.loan_term = 12;
            }else if(requestObj.timeLimit == 2) {
                data.loan_term = 13;
            }else{
                data.loan_time = 6;
            }*/
            // //客户职业
            // if (requestObj.workIdentity == 0) {
            //     param.profession = '工薪族';
            // } else if (requestObj.workIdentity == 1) {
            //     param.profession = '公务员';
            // } else if (requestObj.workIdentity == 2) {
            //     param.profession = '企业主';
            // } else if (requestObj.workIdentity == 3) {
            //     param.profession = '其他';
            // }
            // //客户月薪
            // if (requestObj.monthlySalary == 1) {
            //     data.salary = 10000;
            // } else if (requestObj.monthlySalary == 2) {
            //     data.salary = 20000;
            // } else {
            //     data.salary = 5000;
            // }
            //客户薪资发放方式
            if (requestObj.payDayWay == 1) {
                data.salary_method = 3;
            } else {
                data.salary_method = 1;
            }

            wechatId = 'gh_19c8027281c6'
            param = {
                Wx_official_account: wechatId,//公众号ID
                WxOfficialAccountName: '老钱呗',//公众号名字
                Name: requestObj.name,//名字
                Sex: requestObj.sex == 1 ? 1 : 2,//性别
                City: '',//城市
                Birthday: requestObj.birthday,//生日
                Mobile: requestObj.mobile,//手机号
                Sourve: 'h5',//来源
                Channel: 'h5',//渠道
                House: requestObj.haveHouse,//房,1-有,0-无
                Car: requestObj.haveCar,//车,1-有,0-无
                SheBao: requestObj.haveSecurity,//社保,1-有,0-无
                LifeInsurance: requestObj.havePolicy,//保险,1-有,0-无
                Gongjijin: requestObj.haveFound,//公积金,1-有,0-无
                LoanAmount: requestObj.loanAmount,//贷款金额,单位万元
                Credit: requestObj.haveCreditCard,//信用卡,1-有,0-无
                WorkIdentity: requestObj.workIdentity,//职业,0-工薪族,1-公务员,2-企业主3.其他 
                Salary: requestObj.monthlySalary,//工资,0:<=5000,1:5000-10000,2:10000-20000,3:20000以上
            }

            //城市
            if (requestObj.city == 1) {
                param.city = "深圳";
            } else if (requestObj.city == 2) {
                param.city = "北京";
            } else if (requestObj.city == 3) {
                param.city = "上海";
            }


            $.ajax({
                type: 'POST',
                url: requestHost + '/apis/customerFormH5/submit',
                data: param,
                success: function (res) {
                    if (res.status == 1) {
                        var action_url = 'https://backend.zdt360.com/server/apis/center/wechat/marketing/add?urlBase64=' + bs64 + '&clickId=' + gdt_vid + '&wechatId=' + wechatId;
                        $.get(action_url, function (result) {
                            if (result.code = 1000) {
                                tipMsg('提交成功');
                                location.href = './success.html' + '?codea=' + getQueryString("codea");
                            }
                        });
                    } else {
                        tipMsg(res.msg);
                    }

                },
                error: function (err) {
                    console.log(err);
                }

            });
        } else {
            $('#error_msg').html('资料有误,请检查后再提交!');
        }
    });
});

$(window).resize(function () {
    scales('.main', 750);
});

// 按照实际页面主体与设计图的比例得出放缩尺度，动态改变预设的html字体大小
function scales(exact, design) {
    var scale = $(exact).width() / design;
    $('html').css('font-size', 100 * scale + 'px');
}

function checkLoan(target) {
    var loan = $(target).val();
    var bool = (/^[1-9]\d*$/).test(loan);
    if (bool == false) {
        $('.tips').html('请输入正确的借款金额！');
        money = 10;
        $('input[name="loan_amount"]').val(money);
        $('.tips').show(function () {
            setTimeout(function () {
                $('.tips').hide();
            }, 1000);
            $(document).click(function () {
                $('.tips').hide();
                return;
            });
        });
        return false;
    } else if (loan > 50) {
        bool = false;
        $('.tips').html('借款金额不得超过50万！');
        money = 50;
        $('input[name="loan_amount"]').val(money);
        $('.tips').show(function () {
            setTimeout(function () {
                $('.tips').hide();
            }, 1000);
            $(document).click(function () {
                $('.tips').hide();
                return;
            });
        });
        return false;
    } else {
        money = loan;
    }
    $(document).unbind('click');
    return bool;
}

function checkName(target) {
    var name = $(target).val();
    var bool1 = (/^[\u4e00-\u9fa5]{2,4}$/).test(name);
    if (bool1 == false) {
        $('.tips').html('请输入正确的名字！');
        $('.tips').show(function () {
            setTimeout(function () {
                $('.tips').hide();
            }, 1000);
            $(document).click(function () {
                $('.tips').hide();
                return;
            });
        });
        return false;
    }
    $(document).unbind('click');
    return bool1;
}

function checkBirthday(target) {
    var num = $(target).val();
    var numReg = /^\d{2}$/;
    var bool2 = numReg.test(num);
    if (bool2) {
        if (num >= 20 && num <= 60) {
            return true;
        } else {
            tipMsg('年龄要在20到60岁之间！');
            return false;
        }
    } else {
        if (num == '') {
            tipMsg('请输入您的年龄！');
        } else {
            tipMsg('年龄格式错误！');
        }
        return false;
    }
}

function checkPhone(target) {
    var num = $(target).val();
    // alert(num);
    var numReg = /^1[34578]\d{9}$/;
    var bool2 = numReg.test(num);
    if (bool2 == true) {
        $('.tips').hide();
    } else {
        if (num == '') {
            tipMsg('请输入您的手机号码！');
        } else {
            tipMsg('手机号码格式错误！');
        }

        return false;
    }
    $(document).unbind('click');
    return bool2;
}

//验证身份证
function checkIdCard(target) {
    if ($(target).length > 0) {
        if ($(target).val() == '') {
            $('.tips').html('请输入你的身份证号码！');
            $('.tips').show(function () {
                var timer = setTimeout(function () {
                    $('.tips').hide();
                }, 1000);
                $(document).click(function () {
                    clearTimeout(timer);
                    $('.tips').hide();
                });
            });
            return false;
        } else {
            //验证身份证格式
            var reg = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/;
            if (reg.test($(target).val()) === false) {
                $('.tips').html('身份证号码格式错误！');
                $('.tips').show(function () {
                    var timer = setTimeout(function () {
                        $('.tips').hide();
                    }, 1000);
                    $(document).click(function () {
                        clearTimeout(timer);
                        $('.tips').hide();
                    });
                });
                return false;
            }
        }
    }
    return true;
}

//验证码非空验证
function checkVerifyCode(target) {
    if ($(target).length > 0 && $(target).val() == '') {
        // $('.tips_sel').hide();
        $('.tips').html('请输入短信验证码！');
        $('.tips').show(function () {
            var timer = setTimeout(function () {
                $('.tips').hide();
            }, 1000);
            $(document).click(function () {
                clearTimeout(timer);
                $('.tips').hide();
            });
        });
        return false;
    }
    return true;
}


function checkSelVoc(target) {
    if ($(target).find('option:selected').text() == '请选择您的职业类型') {
        $('.tips').html('请选择您的职业类型！');
        $('.tips').show(function () {
            setTimeout(function () {
                $('.tips').hide();
            }, 1000);
            $(document).click(function () {
                $('.tips').hide();
                return;
            });
        });
        return false;
    } else {
        $('.tips').hide();
        return true;
    }
}

function checkCity(target) {
    if ($(target).find('option:selected').text() == '请选择您的城市') {
        $('.tips').html('请选择您的城市！');
        $('.tips').show(function () {
            setTimeout(function () {
                $('.tips').hide();
            }, 1000);
            $(document).click(function () {
                $('.tips').hide();
                return;
            });
        });
        return false;
    } else {
        $('.tips').hide();
        return true;
    }
}


function checkSelInc(target) {
    if ($(target).find('option:selected').text() == '每月收入') {
        $('.tips').html('请选择您的每月收入！');
        $('.tips').show(function () {
            setTimeout(function () {
                $('.tips').hide();
            }, 1000);
            $(document).click(function () {
                $('.tips').hide();
                return;
            });
        });
        return false;
    } else {
        $('.tips').hide();
        return true;
    }
}

function checkPayWay(target) {
    if ($(target).find('option:selected').text() == '请选择您的发薪方式') {
        $('.tips').html('请选择您的发薪方式！');
        $('.tips').show(function () {
            setTimeout(function () {
                $('.tips').hide();
            }, 1000);
            $(document).click(function () {
                $('.tips').hide();
                return;
            });
        });
        return false;
    } else {
        $('.tips').hide();
        return true;
    }
}

//获取当前url的get参数
function GetQueryString(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);
    if (r != null) return unescape(r[2]); return null;
}

//提示
function tipMsg(msg) {
    $('.tips').html(msg);
    $('.tips').show(function () {
        setTimeout(function () {
            $('.tips').hide();
        }, 1000);
    });
}

//字符串转base64
function encode(str) {
    // 对字符串进行编码
    var encode = encodeURI(str);
    // 对编码的字符串转化base64
    var base64 = btoa(encode);
    return base64;
}
