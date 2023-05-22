package com.mybank.tui;

import com.mybank.data.DataSource;
import com.mybank.domain.*;
import jexer.*;
import jexer.event.TMenuEvent;
import jexer.menu.TMenu;

import java.io.File;
import java.net.URLDecoder;

/**
 *
 * @author Alexander 'Taurus' Babich
 */
public class TUIdemo extends TApplication {
    private static final int ABOUT_APP = 2000;
    private static final int CUST_INFO = 2010;

    public static void main(String[] args) throws Exception {
        TUIdemo tdemo = new TUIdemo();
        //Loading data
        File currentClass = new File(URLDecoder.decode(TUIdemo.class
                .getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getPath(), "UTF-8"));
        String classDirectory = currentClass.getParent();
        new DataSource(classDirectory+"\\35-tui-1-serhio-sys\\test.dat").loadData();
        Bank.addCustomer("Serhii","Kolodiazhnyi");
        Bank.getCustomer(Bank.getNumberOfCustomers()-1).addAccount(new CheckingAccount(1000,10));
        //Loading data
        (new Thread(tdemo)).start();
    }

    public TUIdemo() throws Exception {
        super(BackendType.SWING);

        addToolMenu();
        //custom 'File' menu
        TMenu fileMenu = addMenu("&File");
        fileMenu.addItem(CUST_INFO, "&Customer Info");
        fileMenu.addItem(CUST_INFO+1,"&Customer Report");
        fileMenu.addDefaultItem(TMenu.MID_SHELL);
        fileMenu.addSeparator();
        fileMenu.addDefaultItem(TMenu.MID_EXIT);
        //end of 'File' menu  

        addWindowMenu();

        //custom 'Help' menu
        TMenu helpMenu = addMenu("&Help");
        helpMenu.addItem(ABOUT_APP, "&About...");
        //end of 'Help' menu 

        setFocusFollowsMouse(true);
        //Customer window
        ShowCustomerDetails();
    }

    @Override
    protected boolean onMenu(TMenuEvent menu) {
        if (menu.getId() == ABOUT_APP) {
            messageBox("About", "\t\t\t\t\t   Just a simple Jexer demo.\n\nCopyright \u00A9 2019 Alexander \'Taurus\' Babich").show();
            return true;
        }
        if (menu.getId() == CUST_INFO+1){
            messageBox("Customer Report", CustomerReport.generateReport());
        }
        if (menu.getId() == CUST_INFO) {
            ShowCustomerDetails();
            return true;
        }
        return super.onMenu(menu);
    }

    private void ShowCustomerDetails() {
        TWindow custWin = addWindow("Customer Window", 2, 1, 40, 14, TWindow.NOZOOMBOX);
        custWin.newStatusBar("Enter valid customer number and press Show...");

        custWin.addLabel("Enter customer number: ", 2, 2);
        custWin.addLabel("Enter account id: ",2,4);
        TField accNo = custWin.addField(24,4,3,false);
        TField custNo = custWin.addField(24, 2, 3, false);
        TText details = custWin.addText("Owner Name: \nAccount Type: \nAccount Balance: ", 2, 6, 38, 18);
        custWin.addButton("&Show", 28, 4, new TAction() {
            @Override
            public void DO() {
                try {
                    int custNum = Integer.parseInt(custNo.getText());
                    int accNum = Integer.parseInt(accNo.getText());
                    Customer customer = Bank.getCustomer(custNum);
                    Account account = customer.getAccount(accNum);
                    String type;
                    if (account instanceof CheckingAccount){
                        type = "'Checking'";
                    }
                    else {
                        type = "'Savings'";
                    }
                    //details about customer with index==custNum
                    details.setText("Owner Name: "+customer.getFirstName()+" "+customer.getLastName()+" (id="+custNum+")\nAccount Type: "+type+"\nAccount Balance:"+" - "+account.getBalance()+" $\n");
                    custWin.addButton("&Add", 2, 10, new TAction() {
                        @Override
                        public void DO() {
                            TInputBox custNo = custWin.inputBox("Entering","Enter deposit num: ");
                            try{
                                account.deposit(Double.parseDouble(custNo.getText()));
                                messageBox("Success","Successfully added " +custNo.getText()+ " =)").show();
                                details.setText("Owner Name: "+customer.getFirstName()+" "+customer.getLastName()+" (id="+custNum+")\nAccount Type: "+type+"\nAccount Balance:"+" - "+account.getBalance()+" $\n");
                            }
                            catch (Exception e){
                                messageBox("Error","Not number").show();
                            }
                        }
                    });
                    custWin.addButton("&Remove", 12, 10, new TAction() {
                        @Override
                        public void DO() {
                            TInputBox custNo = custWin.inputBox("Entering","Enter withdraw num: ");
                            try{
                                account.withdraw(Double.parseDouble(custNo.getText()));
                                messageBox("Success","Successfully removed - "+custNo.getText()+" =)").show();
                                details.setText("Owner Name: "+customer.getFirstName()+" "+customer.getLastName()+" (id="+custNum+")\nAccount Type: "+type+"\nAccount Balance:"+" - "+account.getBalance()+" $\n");
                            }
                            catch (Exception e){
                                messageBox("Error","Not number").show();
                            }
                        }
                    });
                } catch (Exception e) {
                    messageBox("Error", "You must provide a valid customer number and account!").show();
                }
            }
        });
    }

}
