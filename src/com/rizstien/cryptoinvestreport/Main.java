package com.rizstien.cryptoinvestreport;

import com.rizstien.cryptoinvestreport.dao.CryptoInvestmentDAO;
import com.rizstien.cryptoinvestreport.util.CryptoInvestmentUtil;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static String FILE_NAME;
    public static List<String> TOKENS_LIST;

    public static void main(String[] args) {
        Connection conn = null;
        HashMap<String,String> tokenUSDVal;
        try{

            System.out.println("Please enter file parent directory path");
            Scanner scanner = new Scanner(System.in);
            String dir = scanner.next();
            System.out.println("Please enter file filename");
            String fileName = scanner.next();
            FILE_NAME = fileName;

            conn = CryptoInvestmentUtil.initCSVJDBC(dir,conn);

            System.out.println("System is bootstraping, please wait .....");

            tokenUSDVal = CryptoInvestmentUtil.getTokenValUSD(conn);

            while(true){
                scanner = new Scanner(System.in);

                System.out.println("\n**************************************");

                System.out.println("Enter 1 to print latest portfolio value per token in USD ");
                System.out.println("Enter 2 to print latest portfolio value for given token in USD ");
                System.out.println("Enter 3 to print portfolio value per token in USD on given date (MM/dd/yyyy) ");
                System.out.println("Enter 4 to print portfolio value of given token in USD on given date (MM/dd/yyyy) ");

                System.out.println("**************************************\n");

                String dateInput,tokenInput;
                String input = scanner.next();

                switch (input){
                    case "1":
                        for (String token : TOKENS_LIST) {
                            Double tokenBalance = CryptoInvestmentDAO.tokenValUSD(token, conn);
                            System.out.println("Total balance of token : "+token+" is "+tokenBalance*Double.parseDouble(tokenUSDVal.get(token))+" USD");
                        }
                        break;
                    case "2":
                        System.out.println("Please enter token symbol: ");
                        tokenInput = scanner.next();
                        Double tokenBalance = CryptoInvestmentDAO.tokenValUSD(tokenInput, conn);
                        System.out.println("Total balance of token : "+tokenInput+" is "+tokenBalance*Double.parseDouble(tokenUSDVal.get(tokenInput))+" USD");
                        break;
                    case "3":
                        System.out.println("Please enter date in format MM/dd/yyyy: ");
                        dateInput = scanner.next();
                        for (String token : TOKENS_LIST) {
                            tokenBalance = CryptoInvestmentDAO.tokenValUSDDate(token,dateInput, conn);
                            System.out.println("Total balance of token : "+token+" is "+tokenBalance*Double.parseDouble(tokenUSDVal.get(token))+" USD on date "+dateInput);
                        }
                        break;
                    case "4":
                        System.out.println("Please enter token symbol: ");
                        tokenInput = scanner.next();
                        System.out.println("Please enter date in format MM/dd/yyyy: ");
                        dateInput = scanner.next();
                        tokenBalance = CryptoInvestmentDAO.tokenValUSDDate(tokenInput,dateInput, conn);
                        System.out.println("Total balance of token : "+tokenInput+" is "+tokenBalance*Double.parseDouble(tokenUSDVal.get(tokenInput))+" USD on date "+dateInput);
                        break;
                    default:
                        break;
                }
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }catch(Exception e) {
            System.out.println("An Exception occurred : " + e.getMessage());
        }finally {
            try{
                conn.close();
            }catch(SQLException se){
                System.out.println("A SQL Exception occurred : "+se.getMessage());
            }
        }
    }
}