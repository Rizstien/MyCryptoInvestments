package com.rizstien.cryptoinvestreport.dao;

import com.rizstien.cryptoinvestreport.Main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rizstien on 11/2/2019.
 */
public class CryptoInvestmentDAO {

    public static Double tokenValUSD(String token, Connection conn){
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Double tokenBalance = 0.0;
        try {
            stmt = conn.prepareStatement("SELECT transaction_type,token,amount FROM "+ Main.FILE_NAME+" where token=?");
            stmt.setString(1,token);

            rs = stmt.executeQuery();
            while(rs.next()){
                if("DEPOSIT".equalsIgnoreCase(rs.getString("transaction_type"))){
                    tokenBalance+=rs.getDouble("amount");
                }else{
                    tokenBalance-=rs.getDouble("amount");
                }
            }
        }catch(SQLException se){
            System.out.println("A SQL Exception occurred : "+se.getMessage());
        }finally {
            try{
                rs.close();
                stmt.close();
            }catch(SQLException se){
                System.out.println("A SQL Exception occurred : "+se.getMessage());
            }
            return tokenBalance;
        }
    }

    public static Double tokenValUSDDate(String token, String date, Connection conn) throws Exception{
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Double tokenBalance = 0.0;
        try {
            stmt = conn.prepareStatement("SELECT transaction_type,token,amount FROM "+Main.FILE_NAME+" where token=? and timestamp < ?");
            stmt.setString(1,token);
            stmt.setString(2,Long.toString(new SimpleDateFormat("MM/dd/yyyy").parse(date).getTime()));
            //stmt.setString(3,Long.toString((new SimpleDateFormat("MM/dd/yyyy").parse(date).getTime())+86400000));

            rs = stmt.executeQuery();
            while(rs.next()){
                if("DEPOSIT".equalsIgnoreCase(rs.getString("transaction_type"))){
                    tokenBalance+=rs.getDouble("amount");
                }else{
                    tokenBalance-=rs.getDouble("amount");
                }
            }

        }catch (SQLException se){
            System.out.println("A SQL Exception occurred : "+se.getMessage());
        }finally {
            try{
                rs.close();
                stmt.close();
            }catch(SQLException se){
                System.out.println("A SQL Exception occurred : "+se.getMessage());
            }
            return tokenBalance;
        }
    }

    public static List<String> allTokenSymbols(Connection conn) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<String> tokens = new ArrayList<String>();
        try {
            stmt = conn.prepareStatement("SELECT DISTINCT token FROM " + Main.FILE_NAME);
            rs = stmt.executeQuery();
            while (rs.next()) {
                tokens.add(rs.getString("token"));
            }
        } catch (SQLException se) {
            System.out.println("A SQL Exception occurred : " + se.getMessage());
        } finally {
            try {
                rs.close();
                stmt.close();
            } catch (SQLException se) {
                System.out.println("A SQL Exception occurred : " + se.getMessage());
            }
            return tokens;
        }
    }
}