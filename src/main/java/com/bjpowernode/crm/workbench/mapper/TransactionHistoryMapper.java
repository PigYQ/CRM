package com.bjpowernode.crm.workbench.mapper;

import com.bjpowernode.crm.workbench.pojo.TransactionHistory;

public interface TransactionHistoryMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_tran_history
     *
     * @mbg.generated Wed Oct 12 10:28:50 CST 2022
     */
    int deleteByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_tran_history
     *
     * @mbg.generated Wed Oct 12 10:28:50 CST 2022
     */
    int insert(TransactionHistory record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_tran_history
     *
     * @mbg.generated Wed Oct 12 10:28:50 CST 2022
     */
    int insertSelective(TransactionHistory record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_tran_history
     *
     * @mbg.generated Wed Oct 12 10:28:50 CST 2022
     */
    TransactionHistory selectByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_tran_history
     *
     * @mbg.generated Wed Oct 12 10:28:50 CST 2022
     */
    int updateByPrimaryKeySelective(TransactionHistory record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_tran_history
     *
     * @mbg.generated Wed Oct 12 10:28:50 CST 2022
     */
    int updateByPrimaryKey(TransactionHistory record);
}