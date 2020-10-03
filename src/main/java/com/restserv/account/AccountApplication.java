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
import com.fasterxml.jackson.core.type.TypeReference;
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
    //Method to handle the GET request at "/process" URI with orgId & userId as Header parameter
    @GetMapping(value = "/process")
    public ResponseEntity getAccountSync(@RequestHeader(value="orgId") String orgId, @RequestHeader(value="userId") String userId) {
        try {
            //Code to Sync Account
            ResponseModal resp = new ResponseModal();
            resp.status = "success";
            resp.error = "";
            return ResponseEntity.ok(resp);
        }catch (Exception ex){
            ResponseModal resp = new ResponseModal();
            resp.status = "error";
            resp.error = ex.getMessage();
            return ResponseEntity.ok(resp);
        }
    }
    //Method to handle the POST request at "/batch" URI with orgId, userId & accounts (List of Account with Id, Name, Type properties) as   Header parameter
    @PostMapping(value = "/batch")
    public ResponseEntity accountBatch(@RequestHeader(value="orgId") String org, @RequestHeader(value="userId") String user, @RequestHeader(value="accounts") String accounts) {
        //Code to add Account
        try {
            ObjectMapper mapper = new ObjectMapper();
            String orgId = mapper.readValue(org, String.class);
            String userId = mapper.readValue(user, String.class);

            if ((orgId == null) || (orgId.trim() == "")) {//Checking for Valid orgId, should not be blank or null
                ResponseModal resp = new ResponseModal();
                resp.status = "error";
                resp.error = "WEBSERVICE NULL EXCEPTION : orgId is NULL";
                //Returning Error Response
                return ResponseEntity.ok(resp);
            } else if ((userId == null) || (userId.trim() == "")) {//userId for Valid userId, should not be blank or null
                ResponseModal resp = new ResponseModal();
                resp.status = "error";
                resp.error = "WEBSERVICE NULL EXCEPTION : userId is NULL";
                //Returning Error Response
                return ResponseEntity.ok(resp);
            } else if ((accounts == null) || (accounts == "")) {//Checking for Valid accounts, should not be blank or null
                ResponseModal resp = new ResponseModal();
                resp.status = "error";
                resp.error = "WEBSERVICE NULL EXCEPTION : accounts is NULL";
                //Returning Error Response
                return ResponseEntity.ok(resp);
            } else {

                String status = "";
                String error = "";
                Integer count = 0;

                //Converting JSON object to AccountModal list to Process
                List<AccountModal> accountList = mapper.readValue(accounts, new TypeReference<List<AccountModal>>() {});

                //Processgin AccountModal records to add to Master Account List
                for (AccountModal eachAccount : accountList) {
                    if ((userId == null) || (userId.trim() == "") || (userId == null) || (userId.trim() == "") || (userId == null) || (userId.trim() == "")) {
                        //Checking if any property is blank for Account record
                        if (status.contains("success")) {
                            status = "partial success";
                        } else {
                            status = "error";
                        }
                        if (error == "") {
                            error = "Index " + count + ": Account Object is incomplete.";
                        } else {
                            error += " Index " + count + ": Account Object is incomplete.";
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
}