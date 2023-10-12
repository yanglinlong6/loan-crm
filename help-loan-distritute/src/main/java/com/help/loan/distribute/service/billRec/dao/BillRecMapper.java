package com.help.loan.distribute.service.billRec.dao;


import com.help.loan.distribute.service.billRec.model.BillRecPO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface BillRecMapper {

    int insertBillRec(BillRecPO record);

    BillRecPO selectBillRecByBillDate(String billDate);

    int updateBillRec(BillRecPO record);
}