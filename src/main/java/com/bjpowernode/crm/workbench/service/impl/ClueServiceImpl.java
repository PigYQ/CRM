package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.commons.utils.Constant;
import com.bjpowernode.crm.commons.utils.DateUtils;
import com.bjpowernode.crm.commons.utils.UUIDUtils;
import com.bjpowernode.crm.settings.pojo.User;
import com.bjpowernode.crm.workbench.mapper.*;
import com.bjpowernode.crm.workbench.pojo.*;
import com.bjpowernode.crm.workbench.service.ClueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class ClueServiceImpl implements ClueService {

    @Autowired
    private ClueMapper clueMapper;

    @Autowired
    private ClueRemarkMapper clueRemarkMapper;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private CustomerRemarkMapper customerRemarkMapper;

    @Autowired
    private ContactsMapper contactsMapper;

    @Autowired
    private ContactsRemarkMapper contactsRemarkMapper;

    @Autowired
    private ClueActivityRelationMapper clueActivityRelationMapper;

    @Autowired
    private ContactsActivityRelationMapper contactsActivityRelationMapper;

    @Autowired
    private TransactionRemarkMapper transactionRemarkMapper;

    @Autowired
    private TransactionMapper transactionMapper;

    @Override
    public List<Clue> queryClueForPage(Map<String, Object> map) {
        return clueMapper.selectClueForPage(map);
    }

    @Override
    public int queryClueNums(Map<String, Object> map) {
        return clueMapper.selectClueNums(map);
    }

    @Override
    public Clue queryByIdUnion(String id) {
        return clueMapper.selectByIdUnion(id);
    }

    @Override
    public Clue queryClueById(String id) {
        return clueMapper.selectById(id);
    }

    @Override
    public int updateById(Clue clue) {
        return clueMapper.updateById(clue);
    }

    @Override
    public int deleteByIds(String[] id) {
        return clueMapper.deleteByIds(id);
    }

    @Override
    public int saveClue(Clue clue) {
        return clueMapper.insertSelective(clue);
    }

    @Override
    public void saveConvert(Map<String, Object> map) {
        String clueId = (String) map.get("clueId");
        User user = (User)map.get(Constant.USER_INFO);
        Clue clue = clueMapper.selectById(clueId);
        //增加一条客户信息
        Customer customer = new Customer();
        customer.setId(UUIDUtils.getId());
        customer.setOwner(user.getId());
        customer.setName(clue.getCompany());
        customer.setWebsite(clue.getWebsite());
        customer.setPhone(clue.getPhone());
        customer.setCreateBy(user.getId());
        customer.setCreateTime(DateUtils.formatDateTime(new Date()));
        customer.setContactSummary(clue.getContactSummary());
        customer.setNextContactTime(clue.getNextContactTime());
        customer.setDescription(clue.getDescription());
        customer.setAddress(clue.getAddress());
        customerMapper.insertSelective(customer);
        //增加一条联系人信息
        Contacts contacts = new Contacts();
        contacts.setId(UUIDUtils.getId());
        contacts.setOwner(user.getId());
        contacts.setSource(clue.getSource());
        contacts.setCustomerId(customer.getId());
        contacts.setFullname(clue.getFullname());
        contacts.setAppellation(clue.getAppellation());
        contacts.setEmail(clue.getEmail());
        contacts.setMphone(clue.getMphone());
        contacts.setJob(clue.getJob());
        contacts.setCreateBy(user.getId());
        contacts.setCreateTime(DateUtils.formatDateTime(new Date()));
        contacts.setDescription(clue.getDescription());
        contacts.setContactSummary(clue.getContactSummary());
        contacts.setNextContactTime(clue.getNextContactTime());
        contacts.setAddress(clue.getAddress());
        contactsMapper.insertSelective(contacts);
        //如果需要创建交易，往交易表中添加一份记录
        String isCreate = (String) map.get("isCreate");
        Transaction transaction = null;
        if ("true".equals(isCreate)){
            transaction = new Transaction();
            transaction.setId(UUIDUtils.getId());
            transaction.setOwner(user.getId());
            transaction.setMoney((String) map.get("money"));
            transaction.setName((String) map.get("name"));
            transaction.setExpectedDate((String) map.get("expectedDate"));
            transaction.setCustomerId(customer.getId());
            transaction.setStage(clue.getState());
            transaction.setActivityId((String) map.get("activityId"));
            transaction.setCreateBy(user.getId());
            transaction.setCreateTime(DateUtils.formatDateTime(new Date()));
            transaction.setContactsId(contacts.getId());
            transactionMapper.insertSelective(transaction);
        }
        //查询线索下的所有备注
        List<ClueRemark> clueRemarkList = clueRemarkMapper.selectClueRemarkByClueIdNoUnion(clueId);
        //将备注加入客户备注表
        for (ClueRemark clueRemark : clueRemarkList) {
            //将备注添加到客户备注表中
            CustomerRemark customerRemark = new CustomerRemark();
            customerRemark.setId(UUIDUtils.getId());
            customerRemark.setNoteContent(clueRemark.getNoteContent());
            customerRemark.setCreateTime(DateUtils.formatDateTime(new Date()));
            customerRemark.setCreateBy(user.getId());
            customerRemark.setEditBy(clueRemark.getEditBy());
            customerRemark.setEditTime(clueRemark.getEditTime());
            customerRemark.setEditFlag(clueRemark.getEditFlag());
            customerRemark.setCustomerId(customer.getId());
            customerRemarkMapper.insertSelective(customerRemark);
            //将备注添加到联系人备注表中
            ContactsRemark contactsRemark = new ContactsRemark();
            contactsRemark.setId(UUIDUtils.getId());
            contactsRemark.setNoteContent(clueRemark.getNoteContent());
            contactsRemark.setCreateTime(DateUtils.formatDateTime(new Date()));
            contactsRemark.setCreateBy(user.getId());
            contactsRemark.setEditBy(clueRemark.getEditBy());
            contactsRemark.setEditTime(clueRemark.getEditTime());
            contactsRemark.setEditFlag(clueRemark.getEditFlag());
            contactsRemark.setContactsId(contacts.getId());
            contactsRemarkMapper.insertSelective(contactsRemark);

            //如果需要创建交易，则往交易备注表里增加备注
            if ("true".equals(isCreate)){
                TransactionRemark transactionRemark = new TransactionRemark();
                transactionRemark.setId(UUIDUtils.getId());
                transactionRemark.setNoteContent(clueRemark.getNoteContent());
                transactionRemark.setCreateBy(user.getId());
                transactionRemark.setCreateTime(DateUtils.formatDateTime(new Date()));
                transactionRemark.setEditBy(clueRemark.getEditBy());
                transactionRemark.setEditTime(clueRemark.getEditTime());
                transactionRemark.setEditFlag(clueRemark.getEditFlag());
                transactionRemark.setTranId(transaction.getId());
                transactionRemarkMapper.insertSelective(transactionRemark);
            }
        }

        //根据clueId查询该线索与市场活动的关联关系
        System.err.println("======================================");
        List<ClueActivityRelation> relationList = clueActivityRelationMapper.selectByClueId(clueId);
        System.err.println("======================================");
        //将关联关系转换为联系人与市场活动的关联关系
        for (ClueActivityRelation relation : relationList) {
            //往联系人与市场活动关联表增加一条数据
            ContactsActivityRelation contactsActivityRelation = new ContactsActivityRelation();
            contactsActivityRelation.setId(UUIDUtils.getId());
            contactsActivityRelation.setActivityId(relation.getActivityId());
            contactsActivityRelation.setContactsId(contacts.getId());
            contactsActivityRelationMapper.insertSelective(contactsActivityRelation);
        }

        //删除该线索下所有备注
        for (ClueRemark clueRemark : clueRemarkList) {
            String id = clueRemark.getId();
            clueRemarkMapper.deleteByPrimaryKey(id);
        }
        //删除该线索和市场活动的关联关系
        for (ClueActivityRelation relation : relationList) {
            String id = relation.getId();
            clueActivityRelationMapper.deleteByPrimaryKey(id);
        }
    }
}
