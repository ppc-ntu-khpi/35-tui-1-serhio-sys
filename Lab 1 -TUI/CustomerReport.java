package com.mybank.tui;

import com.mybank.domain.*;

public class CustomerReport {
    private CustomerReport(){

    }

    public static String generateReport() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\t\t\tCUSTOMERS REPORT");
        stringBuilder.append("\t\t\t================");

        for(int cust_idx = 0; cust_idx < Bank.getNumberOfCustomers(); ++cust_idx) {
            Customer customer = Bank.getCustomer(cust_idx);
            stringBuilder.append("\n");
            stringBuilder.append("Customer: " + customer.getLastName() + ", " + customer.getFirstName());

            for(int acct_idx = 0; acct_idx < customer.getNumberOfAccounts(); ++acct_idx) {
                Account account = customer.getAccount(acct_idx);
                String account_type = "";
                if (account instanceof SavingsAccount) {
                    account_type = "Savings Account";
                } else if (account instanceof CheckingAccount) {
                    account_type = "Checking Account";
                } else {
                    account_type = "Unknown Account Type";
                }

                stringBuilder.append("    " + account_type + ": current balance is " + account.getBalance());
                stringBuilder.append("\n");
            }
        }
        return stringBuilder.toString();
    }
}
