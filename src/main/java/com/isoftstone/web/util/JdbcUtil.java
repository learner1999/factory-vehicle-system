package com.isoftstone.web.util;

import java.sql.Connection;  
import java.sql.DriverManager;  
import java.sql.ResultSet;  
import java.sql.SQLException;  
import java.sql.Statement;  
  
public class JdbcUtil {  
    private static String url = "jdbc:mysql://159.203.212.44:3306/factory_vehicle_system";  
    private static String username = "factory_vehicle";  
    private static String password = "MfPwtdHpycq3BXtV";  
    private static String driverName = "com.mysql.jdbc.Driver";  
  
    public JdbcUtil() {  
        super();  
        // TODO Auto-generated constructor stub  
    }  
  
    static {  
        try {  
            Class.forName(driverName);  
        } catch (ClassNotFoundException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
    }  
  
    public static Connection getConnection() throws SQLException {  
        return DriverManager.getConnection(url, username, password);  
    }  
  
    public static void close(ResultSet rs, Statement st, Connection conn) {  
        try {  
            if (rs != null) {  
                rs.close();  
  
            }  
        } catch (SQLException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }finally{  
            try {  
                if(st!=null){  
                    st.close();  
                }  
            } catch (SQLException e) {  
                // TODO Auto-generated catch block  
                e.printStackTrace();  
            }finally{  
                if(conn!=null){  
                    try {  
                        conn.close();  
                    } catch (SQLException e) {  
                        // TODO Auto-generated catch block  
                        e.printStackTrace();  
                    }  
                }  
            }  
        }  
  
    }  
}  