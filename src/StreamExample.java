import java.sql.*;                                           // line 1
import java.io.*;

class StreamExample
{
    public static void main (String args [])
            throws SQLException, IOException
    {
        // Load the driver
        DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());

        // Connect to the database
        // You can put a database name after the @ sign in the connection URL.
        Connection conn =
                DriverManager.getConnection ("jdbc:oracle:oci8:@", "scott", "tiger");

        // It's faster when you don't commit automatically
        conn.setAutoCommit (false);                             // line 18

        // Create a Statement
        Statement stmt = conn.createStatement ();

        // Create the example table
        try
        {
            stmt.execute ("drop table streamexample");
        }
        catch (SQLException e)
        {
            // An exception would be raised if the table did not exist
            // We just ignore it
        }

        // Create the table                                     // line 34
        stmt.execute ("create table streamexample (NAME varchar2 (256), DATA long)");

        File file = new File ("StreamExample.java");            // line 37
        InputStream is = new FileInputStream ("StreamExample.java");
        PreparedStatement pstmt =
                conn.prepareStatement ("insert into streamexample (name, data) values (?, ?)");
        pstmt.setString (1, "StreamExample");
        pstmt.setAsciiStream (2, is, (int)file.length ());
        pstmt.execute ();                                      // line 44

        // Do a query to get the row with NAME 'StreamExample'
        ResultSet rset =
                stmt.executeQuery ("select DATA from streamexample where NAME='StreamExample'");

                        // Get the first row                                    // line 51
        if (rset.next ())
        {
            // Get the data as a Stream from Oracle to the client
            InputStream gif_data = rset.getAsciiStream (1);

            // Open a file to store the gif data
            FileOutputStream os = new FileOutputStream ("example.out");

            // Loop, reading from the gif stream and writing to the file
            int c;
            while ((c = gif_data.read ()) != -1)
                os.write (c);

            // Close the file
            os.close ();                                          // line 66
        }
    }
}