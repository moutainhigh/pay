package com.pay.merchant.service;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.pay.merchant.dao.PayAccProfileDAO;
import com.pay.merchant.dao.PayAccProfile;

/**
 * Object PAY_ACC_PROFILE service. 
 * @author Administrator
 *
 */
public class PayAccProfileService {
    /**
     * Get records list(json).
     * @return
     */
    public String getPayAccProfileList(PayAccProfile payAccProfile,int page,int rows,String sort,String order){
        try {
            PayAccProfileDAO payAccProfileDAO = new PayAccProfileDAO();
            List list = payAccProfileDAO.getPayAccProfileList(payAccProfile, page, rows, sort, order);
            JSONObject json = new JSONObject();
            json.put("total", String.valueOf(payAccProfileDAO.getPayAccProfileCount(payAccProfile)));
            JSONArray row = new JSONArray();
            for(int i = 0; i<list.size(); i++){
                row.put(i, ((PayAccProfile)list.get(i)).toJson());
            }
            json.put("rows", row);
            return json.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    /**
     * 根据客户类型和客户账号查询该账户余额
     * @param acType
     * @param acNo
     * @return
     * @throws Exception
     */
    public PayAccProfile detailPayAccProfileByAcTypeAndAcNo(String acType,String acNo) throws Exception {
        return new PayAccProfileDAO().detailPayAccProfileByAcTypeAndAcNo(acType,acNo);
    }
    /**
     * add PayAccProfile
     * @param payAccProfile
     * @throws Exception
     */
    public void addPayAccProfile(PayAccProfile payAccProfile) throws Exception {
        new PayAccProfileDAO().addPayAccProfile(payAccProfile);
    }
    /**
     * remove PayAccProfile
     * @param id
     * @throws Exception
     */
    public void removePayAccProfile(String id) throws Exception {
        new PayAccProfileDAO().removePayAccProfile(id);
    }
    /**
     * update PayAccProfile
     * @param payAccProfile
     * @throws Exception
     */
    public void updatePayAccProfile(PayAccProfile payAccProfile) throws Exception {
        new PayAccProfileDAO().updatePayAccProfile(payAccProfile);
    }
    /**
     * detail PayAccProfile
     * @param id
     * @throws Exception
     */
    public PayAccProfile detailPayAccProfile(String id) throws Exception {
        return new PayAccProfileDAO().detailPayAccProfile(id);
    }
}