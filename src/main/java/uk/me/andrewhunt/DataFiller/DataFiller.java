package uk.me.andrewhunt.DataFiller;

import java.sql.*;
import java.util.HashMap;

public class DataFiller
{
    private Connection con;
    private final StringGenerator sg = new StringGenerator();
    public static void main(String[] args)
    {
        DataFiller df = new DataFiller();
        df.connect();
        HashMap<String,String> tableMetaData = df.getTableData("Persons");
        String insertStatement = df.createInsertStatement(tableMetaData, "Persons", 5);
        System.out.println(insertStatement);
    }

    private String createInsertStatement(HashMap<String,String> tableMetaData, String tableName, int numRows)
    {
        StringBuilder sb = new StringBuilder("INSERT INTO ");
        sb.append(tableName);
        sb.append(" (");
        sb.append(String.join(",", tableMetaData.keySet()));
        sb.append(")");
        sb.append(" VALUES ");
        for (int i = 1;i <= numRows;i++)
        {
            sb.append(" (");
            for (String columnType : tableMetaData.values()) {
                switch (columnType) {
                    case "INT":
                        sb.append(sg.generateNumericString(3));
                        break;
                    case "MEDIUMTEXT":
                    case "VARCHAR":
                        sb.append("\"").append(sg.generateString(20)).append("\"");
                        break;
                }
                sb.append(",");
            }
            sb.deleteCharAt(sb.lastIndexOf(","));
            sb.append("),");
        }
        sb.deleteCharAt(sb.lastIndexOf(","));
        sb.append("  ;");

        return sb.toString();
    }

    private HashMap<String,String> getTableData(String tableName)
    {
        HashMap<String,String> tableMetaData = new HashMap<String,String>();
        ResultSetMetaData rsmd;
        try
        {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select * from " + tableName + " limit 1;");
            rsmd = rs.getMetaData();
            for (int i= 1; i <= rsmd.getColumnCount(); i++ )
            {
                tableMetaData.put(rsmd.getColumnName(i), rsmd.getColumnTypeName(i));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return tableMetaData;
    }

    private void connect()
    {
        try{
//            Class.forName("com.mysql.jdbc.Driver");
            Class.forName("com.mysql.cj.jdbc.Driver");
            con= DriverManager.getConnection("jdbc:mysql://xps-15:3306/dremio1","andy","XXXXXX");

            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("select * from Persons limit 1;");
//            System.out.printf("%-15s| %-15s| %-5s| %-5s| %-15s| %s%n","Field","Type","Null","Key","Default","Extra");
//            while(rs.next())
//            {
//                System.out.printf("%-15s  %-15s  %-5s  %-5s  %-15s  %s%n",
//                        rs.getString(1),
//                        rs.getString(2),
//                        rs.getString(3),
//                        rs.getString(4),
//                        rs.getString(5),
//                        rs.getString(6));
//            }

            ResultSetMetaData rsmd = rs.getMetaData();
            System.out.println("No. of columns : " + rsmd.getColumnCount());
            for (int i= 1; i <= rsmd.getColumnCount(); i++ )
            {
                System.out.printf("Column %s: %s  %s %n", i , rsmd.getColumnName(i), rsmd.getColumnTypeName(i));
            }
//            con.close();
        }catch(Exception e){ System.out.println(e);}

    }

    private void generate20Words()
    {
        for (int i = 1; i<=20; i++)
        {
            System.out.println(sg.generateString(10));
        }
        System.out.println("=================================");
        for (int i = 1; i<=20; i++)
        {
            System.out.println(sg.generateAlphaNumericString(10));
        }
    }
}