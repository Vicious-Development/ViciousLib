package com.vicious.viciouslib.database;

import com.vicious.viciouslib.database.tracking.DefaultTrackingHandler;
import com.vicious.viciouslib.database.tracking.Trackable;
import com.vicious.viciouslib.database.sqlcomponents.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    public Connection conn;
    private String HOST,PORT,DB,USER,PASS;
    public Database(String HOST, String PORT, String DB, String USER, String PASS) throws SQLException{
        this.HOST=HOST;
        this.PORT=PORT;
        this.DB=DB;
        this.USER=USER;
        this.PASS=PASS;
        openConnection();
        DefaultTrackingHandler.init(this);
    }

    // Opens a connection to the mysql database
    public Connection openConnection() throws SQLException{
        // Initiate connection to Database
        String DB_NAME = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DB;
        conn = DriverManager.getConnection(DB_NAME, USER, PASS);
        return conn;
    }

    // Executes an SQL statement asynchronously
    public void execute(final String sqlCmd) throws SQLException{
        try {
            PreparedStatement statement = conn.prepareStatement(sqlCmd);
            statement.execute(sqlCmd);
            statement.close();
        } catch(SQLException e){
            throw new SQLException("Async SQL Task Failed: " + sqlCmd + e.getMessage());
        }
    }
    public void execute(PreparedStatement statement, String sqlCmd){
        try {
            statement.execute(sqlCmd);
            statement.close();
        } catch(SQLException e){
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }
    public void execute(final SQLCommand COMMAND) throws SQLException{
        execute(COMMAND.toString());
    }
    public void safeExecute(SQLCommand command, Trackable<?> trackable) throws SQLException{
        try {
            execute(command.toString());
        } catch (SQLException e){
            try {
                attemptFix(command, trackable);
                execute(command);
            } catch (SQLException ex){
                throw new SQLException("Totally failed to safeExecute a command: " + command + " this likely resulted due to incorrect syntax of an SQLCommand. For the safest options, use command templates in the SQLCommands class!" + ex + " caused by " + e.getMessage());
            }
        }
    }
    // Returns a query from the database
    public ResultSet safeExecuteQuery(SQLCommand command, Trackable trackable, int retries) throws SQLException{
        ResultSet resultSet = null;
        String query = command.toString();
        while (retries >= 0) {
            retries--;
            try {
                PreparedStatement statement = conn.prepareStatement(query);
                resultSet = statement.executeQuery(query);
                break;
            } catch (SQLException e) {
                try {
                    attemptFix(command, trackable);
                    PreparedStatement statement = conn.prepareStatement(query);
                    resultSet = statement.executeQuery(query);
                    break;
                } catch(SQLException ex){
                    throw new SQLException("Totally failed to safeExecute a query command: " + command + " this likely resulted due to incorrect syntax of an SQLCommand. For the safest options, use command templates in the SQLCommands class!" + ex + " caused by " + e.getMessage());
                }
            }
        }
        return resultSet;
    }

    // Returns a query from the database
    public ResultSet executeQuery(String query, int retries) throws SQLException {
        ResultSet resultSet = null;
        while (retries >= 0) {
            retries--;
            PreparedStatement statement = conn.prepareStatement(query);
            resultSet = statement.executeQuery(query);
            retries = -1;
        }
        return resultSet;
    }

    // Closes the connection to the database
    public void close() throws SQLException{
        conn.close();
    }

    // Reconnects to the database
    public void reconnect() throws SQLException{
        if (conn == null) {
            openConnection();
        } else {
            conn.close();
            openConnection();
        }
    }

    // Tests the connection to the database
    public boolean test() throws SQLException {
        return conn.isValid(5);
    }

    public Connection getConnection() {
        return conn;
    }
    public void attemptFix(SQLCommand command, Trackable<?> trackable) throws SQLException{
        SQLCommandPart tableName = null;
        //Get the tablename.
        SQLColumnList columns = new SQLColumnList();
        if (command.contains(SQLStatements.INSERT)) {
            tableName = command.getPart(command.indexOf(SQLStatements.INTO) + 1);
            columns = extrapolateExpectedColumns(trackable);
        } else if (command.contains(SQLStatements.UPDATE)) {
            tableName = command.getPart(command.indexOf(SQLStatements.UPDATE)+1);
            columns = extrapolateExpectedColumns(trackable);
        } else if (command.contains(SQLStatements.SELECT) || command.contains(SQLStatements.DELETE)) {
            tableName = command.getPart(command.indexOf(SQLStatements.FROM)+1);
            columns = extrapolateExpectedColumns(trackable);
        }
        if(tableExists(tableName.toString())){
            fixColumns(tableName.toString(), columns);
        } else{
            execute(SQLCommands.CREATETABLE(new SQLVariable(tableName), columns.primarify(), columns.getCols().get(0).getName()));
        }
    }
    public SQLColumnList extrapolateExpectedColumns(Trackable<?> trackable) {
        SQLCommand insertionCommand = trackable.getSQLNewCommand();
        int pos = insertionCommand.indexOf(SQLStatements.INTO) + 1;
        SQLListStatement varlist = (SQLListStatement) insertionCommand.getPart(pos + 1);
        return (new SQLColumnList(varlist.getVars().toArray(new String[0]), trackable.getColumnTypes()));
    }
    public boolean fixColumns(String tableName, SQLColumnList expectedCols) throws SQLException{
        ResultSet rs = executeQuery(SQLCommands.GETCOLUMNSINTABLE(tableName).toString(),1);
        List<String> columns = new ArrayList<>();
        while(rs.next()){
            columns.add(rs.getString("COLUMN_NAME"));
        }
        rs.close();
        String lastColumn = null;
        SQLColumnList toAdd = new SQLColumnList();
        for(SQLColumn s : expectedCols.getCols()){
            toAdd.add(new SQLColumn(s.getName(),s.getType()));
        }
        while(columns.size()>0){
            for(SQLColumn s : expectedCols.getCols()){
                if(s.getRawName().equals(columns.get(0))){
                    lastColumn = columns.remove(0);
                    toAdd.remove(s);
                    break;
                }
            }
        }
        if(lastColumn == null) throw new SQLException("ER0 - Failed to fix columns, this is a Frozen Lib issue, report to higher devs.");
        addColumnsToTable(new SQLVariable(tableName), new SQLVariable(lastColumn), toAdd);
        return true;
    }
    public boolean tableExists(String tableName) throws SQLException{
        ResultSet rs = executeQuery(SQLCommands.GETTABLEOFNAME(tableName).toString(),1);
        boolean exists = getResultSize(rs) > 0;
        rs.close();
        return exists;
    }
    public void addColumnsToTable(SQLVariable tableName, SQLVariable after, SQLColumnList cols) throws SQLException{
        execute(SQLCommands.ADDCOLUMNSAFTER(
                tableName,
                cols,
                after
        ));
    }
    public static int getResultSize(ResultSet rs){
        int size= 0;
        try {
            if (rs != null) {
                rs.beforeFirst();
                rs.last();
                size = rs.getRow();
            } else return 0;
            return size;
        } catch(SQLException e){
            return 0;
        }
    }
}