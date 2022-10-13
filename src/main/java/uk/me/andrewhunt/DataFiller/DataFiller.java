package uk.me.andrewhunt.DataFiller;

import java.sql.*;
import java.util.HashMap;

import org.apache.commons.cli.*;

public class DataFiller
{
    private Connection con;
    private final StringGenerator sg = new StringGenerator();
    public static void main(String[] args)
    {
        HashMap<String,String> opts = parseArgs(args);
        DataFiller df = new DataFiller();
        df.connect(opts);
        HashMap<String,String> tableMetaData = df.getTableData(opts.get("tablename"));
        String insertStatement = df.createInsertStatement(tableMetaData, opts.get("tablename"), Integer.parseInt(opts.get("rowcount")));
        int insertResponse = df.insertData( insertStatement, opts);
        System.out.println("Rows inserted:" + insertResponse);
    }

    private int insertData(String insertStatement, HashMap<String, String> opts)
    {
        int rowsInserted;
        try {
            Statement stmt = con.createStatement();

            boolean rs = stmt.execute(insertStatement);
            rowsInserted = stmt.getUpdateCount();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return rowsInserted;
    }

    private static HashMap<String,String> parseArgs(String[] args)
    {
        HashMap<String, String> opts = new HashMap<>();
        Options options = new Options();

        Option tableName = Option.builder("t")
                .longOpt("tablename")
                .argName("Table Name")
                .hasArg(true)
                .required(true)
                .desc("The name of the table to fill with data.")
                .build();
        Option rowCount = Option.builder("r")
                .longOpt("rowcount")
                .hasArg(true)
                .required(true)
                .desc("The number of rows to insert.")
                .build();
        Option connectString = Option.builder("c")
                .longOpt("connectstring")
                .hasArg(true)
                .required(true)
                .desc("The jdbc connection string - e.g. jdbc:mysql://localhost:3306/dremio1")
                .build();
        Option userName = Option.builder("u")
                .longOpt("username")
                .hasArg(true)
                .required(true)
                .desc("Username to connect to the DB with.")
                .build();
        Option password = Option.builder("p")
                .longOpt("password")
                .hasArg(true)
                .required(true)
                .desc("Password for the jdbc connection.")
                .build();
        options.addOption(tableName);
        options.addOption(rowCount);
        options.addOption(connectString);
        options.addOption(userName);
        options.addOption(password);

        CommandLineParser parser = new DefaultParser();
        try
        {
            // parse the command line arguments
            CommandLine line = parser.parse(options, args);

            // Put required options
            opts.put("tablename",line.getOptionValue("tablename"));
            opts.put("connectstring", line.getOptionValue("connectstring"));
            opts.put("rowcount", line.getOptionValue("rowcount"));
            opts.put("username", line.getOptionValue("username"));
            opts.put("password", line.getOptionValue("password"));
        }
        catch (ParseException e)
        {
            System.err.println("Parsing failed.  Reason: " + e.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("DataFiller", options);

            System.exit(1);
        }
        return opts;
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
                    default : System.out.println("Don't understand column type " + columnType + " - exiting.");
                            System.exit(1);
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
        HashMap<String,String> tableMetaData = new HashMap<>();
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

    private void connect(HashMap<String,String> opts)
    {
        try{
//            Class.forName("com.mysql.jdbc.Driver");
            Class.forName("com.mysql.cj.jdbc.Driver");
            con= DriverManager.getConnection(opts.get("connectstring"),opts.get("username"),opts.get("password"));

            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("select * from " + opts.get("tablename") +  " limit 1;");

            ResultSetMetaData rsmd = rs.getMetaData();
            System.out.println("No. of columns : " + rsmd.getColumnCount());
            for (int i= 1; i <= rsmd.getColumnCount(); i++ )
            {
                System.out.printf("Column %s: %s  %s %n", i , rsmd.getColumnName(i), rsmd.getColumnTypeName(i));
            }
//            con.close();
        }
        catch (Exception e)
        {
            System.out.println("Could not connect to the database");
            System.out.println(e.getMessage());
            System.exit(1);
        }

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