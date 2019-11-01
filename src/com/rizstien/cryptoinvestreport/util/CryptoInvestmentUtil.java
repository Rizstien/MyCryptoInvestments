package com.rizstien.cryptoinvestreport.util;

import com.rizstien.cryptoinvestreport.Main;
import com.rizstien.cryptoinvestreport.dao.CryptoInvestmentDAO;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Created by Rizstien on 11/2/2019.
 */
public class CryptoInvestmentUtil {

    public static Connection initCSVJDBC(String dir, Connection conn){
        try {
            Class.forName("org.relique.jdbc.csv.CsvDriver");
            String path = "jdbc:relique:csv:" + dir + "?" +
                    "separator=," + "&" + "fileExtension=.csv";
           conn = DriverManager.getConnection(path);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }catch(SQLException e) {
            System.out.println("Invalid Directory");
        }catch(Exception e) {
            System.out.println("An Exception occurred : " + e.getMessage());
        }
        return conn;
    }


    public static HashMap<String,String> getTokenValUSD(Connection conn) {
        HttpURLConnection apiConn = null;
        HashMap<String,String> tokenUSDVal = null;
        try{
            Main.TOKENS_LIST = CryptoInvestmentDAO.allTokenSymbols(conn);
            tokenUSDVal = new HashMap<>();
            StringBuilder urlString = new StringBuilder("https://min-api.cryptocompare.com/data/pricemulti?fsyms=");
            for (String token : Main.TOKENS_LIST) {
                urlString.append(token).append(",");
            }
            if(Main.TOKENS_LIST.size()>0){
                urlString.deleteCharAt(urlString.lastIndexOf(","));
            }
            urlString.append("&tsyms=USD&api_key=6bcec0f329476c061131b37d2875869b84617a35b4fa0950bdf594ce83c60fac");

            URL url = new URL(urlString.toString());

            apiConn = (HttpURLConnection) url.openConnection();
            apiConn.setRequestMethod("GET");
            apiConn.setRequestProperty("Accept", "application/json");

            if (apiConn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + apiConn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((apiConn.getInputStream())));
            String output = br.readLine();
            HashMap<String,Object> results = new ObjectMapper().readValue(output, HashMap.class);
            for(Object result : results.keySet()){
                String str = results.get(result).toString();
                str = str.substring(str.indexOf("=")+1,str.indexOf("}")).trim();
                tokenUSDVal.put(result.toString(),str);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }catch(Exception e) {
            System.out.println("An Exception occurred : " + e.getMessage());
        }finally {
            apiConn.disconnect();
            return tokenUSDVal;
        }
    }
}
