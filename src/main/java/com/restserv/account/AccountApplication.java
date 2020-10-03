/*
###########################################################################
# File............................: AccountApplication
# Version......................: 1.0
# Created by.................:  Nishant Bansal
# Created Date..............:  03 Oct 2020
# Last Modified by.........: 03 Oct 2020
# Last Modified Date......: Nishant Bansal
# Description.................: This is a Sprint Boot Rest class to upload list of Account and to sync. Two seperate GET & POST have been provided, one to upload Account and other to call the sync process at once.
# Test Class…................: AccountApplicationTest
# Change Log................:
# Recommendation : Recommended Account List Size is upto 1000, any size above this could cause webservice failure
#############################################################################
*/
package com.restserv.account;
//Import of Dependent resources
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.http.ResponseEntity;
import java.util.ArrayList;
import java.util.List;

@Controller
@SpringBootApplication
public class AccountApplication {

    private List<RequestModal> myAccountList = new ArrayList();

    public AccountApplication(){

    }
    //Method to call on URL load
    @RequestMapping("/")
    @ResponseBody
    String home() {
        return "Welcome to Account Sync Service";
    }
    //Method to initiate Spring Application
    public static void main(String[] args) {
        SpringApplication.run(AccountApplication.class, args);
    }
    //Method to handle the GET request at "/getprocess" URI with orgId & userId as Header parameter
    @GetMapping(value = "/getprocess")
    public ResponseEntity getAccountSync(@RequestHeader(value="orgId") String org, @RequestHeader(value="userId") String user) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String orgId = mapper.readValue(org, String.class);
            String userId = mapper.readValue(user, String.class);
            if ((orgId == null) || (orgId.trim().equals(""))) {//Checking for Valid orgId, should not be blank or null
                ResponseModal resp = new ResponseModal();
                resp.status = "error";
                resp.error = "WEBSERVICE NULL EXCEPTION : orgId is NULL";
                //Returning Error Response
                return ResponseEntity.ok(resp);
            } else if ((userId == null) || (userId.trim().equals(""))) {//userId for Valid userId, should not be blank or null
                ResponseModal resp = new ResponseModal();
                resp.status = "error";
                resp.error = "WEBSERVICE NULL EXCEPTION : userId is NULL";
                //Returning Error Response
                return ResponseEntity.ok(resp);
            }else{
                List<RequestModal> userAccountList = new ArrayList();
                //Code to get Account List for Specific User & Org //This have been added to Validate
                for(RequestModal eachAcc : myAccountList){
                    if((orgId.equals(eachAcc.getOrgId())) && (userId.equals(eachAcc.getUserId()))){
                        userAccountList.add(eachAcc);
                    }
                }

                if(userAccountList.size() == 0){
                    ResponseModal resp = new ResponseModal();
                    resp.status = "error";
                    resp.error = "No matching record found for mentioned User & Org";
                    return ResponseEntity.ok(resp);
                }else{
                    return ResponseEntity.ok(userAccountList);
                }
            }
        }catch (Exception ex){
            ResponseModal resp = new ResponseModal();
            resp.status = "error";
            resp.error = ex.getMessage();
            return ResponseEntity.ok(resp);
        }
    }
    //Method to handle the GET request at "/process" URI with orgId & userId as Header parameter
    @PostMapping(value = "/process")
    public ResponseEntity accountSyncProcess(@RequestBody String reqBody) {
        try {
            //Converting JSON object to UserModal list to Process
            ObjectMapper mapper = new ObjectMapper();
            UserModal reqObj = mapper.readValue(reqBody, UserModal.class);
            String orgId = reqObj.getOrgId();
            String userId = reqObj.getUserId();
            if ((orgId == null) || (orgId.trim().equals(""))) {//Checking for Valid orgId, should not be blank or null
                ResponseModal resp = new ResponseModal();
                resp.status = "error";
                resp.error = "WEBSERVICE NULL EXCEPTION : orgId is NULL";
                //Returning Error Response
                return ResponseEntity.ok(resp);
            } else if ((userId == null) || (userId.trim().equals(""))) {//userId for Valid userId, should not be blank or null
                ResponseModal resp = new ResponseModal();
                resp.status = "error";
                resp.error = "WEBSERVICE NULL EXCEPTION : userId is NULL";
                //Returning Error Response
                return ResponseEntity.ok(resp);
            }else{
                List<RequestModal> userAccountList = new ArrayList();
                //Code to get Account List for Specific User & Org //This have been added to Validate
                for(RequestModal eachAcc : myAccountList){
                    if((orgId.equals(eachAcc.getOrgId())) && (userId.equals(eachAcc.getUserId()))){
                        userAccountList.add(eachAcc);
                    }
                }

                if(userAccountList.size() == 0){
                    ResponseModal resp = new ResponseModal();
                    resp.status = "error";
                    resp.error = "No matching record found for mentioned User & Org";
                    return ResponseEntity.ok(resp);
                }else{
                    //Code to Sync Account - To MySQL/SQL/Oracle
                    ResponseModal resp = new ResponseModal();
                    resp.status = "success";
                    resp.error = "";
                    return ResponseEntity.ok(resp);
                }

            }
        }catch (Exception ex){
            ResponseModal resp = new ResponseModal();
            resp.status = "error";
            resp.error = ex.getMessage();
            return ResponseEntity.ok(resp);
        }
    }
    //Method to handle the POST request at "/batch" URI with orgId, userId & accounts (List of Account with Id, Name, Type properties) as   Header parameter
    @PostMapping(value = "/batch")
    public ResponseEntity accountBatch(@RequestBody String reqBody) {
        //Code to add Account
        try {
            //Converting JSON object to RequestBodyModal list to Process
            ObjectMapper mapper = new ObjectMapper();
            RequestBodyModal reqObj = mapper.readValue(reqBody, RequestBodyModal.class);
            String orgId = reqObj.getOrgId();
            String userId = reqObj.getUserId();
            List<AccountModal> accounts = reqObj.getAccounts();

            if ((orgId == null) || (orgId.trim().equals(""))) {//Checking for Valid orgId, should not be blank or null
                ResponseModal resp = new ResponseModal();
                resp.status = "error";
                resp.error = "WEBSERVICE NULL EXCEPTION : orgId is NULL";
                //Returning Error Response
                return ResponseEntity.ok(resp);
            } else if ((userId == null) || (userId.trim().equals(""))) {//userId for Valid userId, should not be blank or null
                ResponseModal resp = new ResponseModal();
                resp.status = "error";
                resp.error = "WEBSERVICE NULL EXCEPTION : userId is NULL";
                //Returning Error Response
                return ResponseEntity.ok(resp);
            } else if ((accounts == null) || (accounts.size() == 0)) {//Checking for Valid accounts, should not be blank or null
                ResponseModal resp = new ResponseModal();
                resp.status = "error";
                resp.error = "WEBSERVICE NULL EXCEPTION : accounts is NULL";
                //Returning Error Response
                return ResponseEntity.ok(resp);
            } else {

                String status = "";
                String error = "";
                Integer count = 0;

                //Processgin AccountModal records to add to Master Account List
                for (AccountModal eachAccount : accounts) {
                    if ((eachAccount.getId() == null) || (eachAccount.getId().trim().equals("")) || (eachAccount.getName() == null) || (eachAccount.getName().trim().equals("")) || (eachAccount.getType() == null) || (eachAccount.getType().trim().equals(""))) {
                        //Checking if any property is blank for Account record
                        if (status.contains("success")) {
                            status = "partial success";
                        } else {
                            status = "error";
                        }
                        if (error.equals("")) {
                            error = "Index " + count + ": Account Information is incomplete.";
                        } else {
                            error += " Index " + count + ": Account Information is incomplete.";
                        }

                    } else {
                        if (status.contains("error") || status.contains("partial")) {
                            status = "partial success";
                        } else {
                            status = "success";
                        }
                        //Adding verified Account record to Master Account List
                        myAccountList.add(new RequestModal(orgId, userId, eachAccount));
                    }
                    count++;
                }
                //Returning - Account Process Status (success/error/partial success)
                ResponseModal resp = new ResponseModal();
                resp.status = status;
                resp.error = error;
                return ResponseEntity.ok(resp);
            }

        }catch (Exception ex){
            //Response in case of any runtime error
            ResponseModal resp = new ResponseModal();
            resp.status = "error";
            resp.error = ex.getMessage();
            return ResponseEntity.ok(resp);
        }

    }
    //AccountModal wrapper class to hold Account Properites
    public static class AccountModal {

        private String id;
        private String name;
        private String type;

        AccountModal(){

        }
        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setType(String type) {
            this.type = type;
        }

    }
    //ResponseModal wrapper class to hold status (success/error/partial success) and error message, if any
    public class ResponseModal {

        private String status;
        private String error;

        ResponseModal (String status, String error){
            this.status = status;
            this.error = error;
        }

        public ResponseModal() {

        }

        public String getStatus() {
            return status;
        }

        public String getError() {
            return error;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public void setError(String error) {
            this.error = error;
        }
    }
    //RequestModal wrapper class to hold orgId, userId & account properties (received from /batch) later to be used for sync (/process)
    public class RequestModal {

        private String orgId;
        private String userId;
        private AccountModal account;

        RequestModal(String orgId, String userId,AccountModal account){
            this.orgId = orgId;
            this.userId = userId;
            this.account = account;
        }

        public String getOrgId() {
            return orgId;
        }

        public String getUserId() {
            return userId;
        }

        public AccountModal getAccount() {
            return account;
        }

        public void setOrgId(String orgId) {
            this.orgId = orgId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public void setAccount(AccountModal account) {
            this.account = account;
        }
    }
    //RequestBodyModal wrapper class to hold orgId, userId & account properties (received from /batch) later to be used for sync (/process)
    public static class RequestBodyModal {

        private String orgId;
        private String userId;
        private List<AccountModal> accounts;

        RequestBodyModal(){
        }

        public String getOrgId() {
            return orgId;
        }

        public String getUserId() {
            return userId;
        }

        public List<AccountModal> getAccounts() {
            return accounts;
        }

        public void setOrgId(String orgId) {
            this.orgId = orgId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public void setAccounts(List<AccountModal> accounts) {
            this.accounts = accounts;
        }
    }
    //UserModal wrapper class to hold orgId, userId & account properties (received from /batch) later to be used for sync (/process)
    public static class UserModal {

        private String orgId;
        private String userId;

        UserModal(){
        }

        public String getOrgId() {
            return orgId;
        }

        public String getUserId() {
            return userId;
        }

        public void setOrgId(String orgId) {
            this.orgId = orgId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

    }
}