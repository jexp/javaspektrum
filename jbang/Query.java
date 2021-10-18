///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS org.postgresql:postgresql:42.2.24
//DEPS com.github.freva:ascii-table:1.2.0

// jbang Query.java "select * from customers limit 5"
// export JDBC_URL="jdbc:postgresql://db-examples.cmlvojdj5cci.us-east-1.rds.amazonaws.com/northwind?user=n4examples&password=36gdOVABr3Ex"

import static java.lang.System.*;
import java.sql.*;
import java.util.*;
import com.github.freva.asciitable.*;

public class Query {

    public static void main(String... args) throws Exception {
        try (var con=DriverManager.getConnection(getenv("JDBC_URL"));
             var stmt=con.createStatement();
             var rs=stmt.executeQuery(String.join(" ",args))) {
                var meta=rs.getMetaData();
                var cols=new String[meta.getColumnCount()];
                for (int c=1;c<=cols.length;c++) 
                    cols[c-1]=meta.getColumnName(c);
                int row=0;
                String[][] rows=new String[100][];
                while (rs.next() || row>=rows.length) {
                    rows[row]=new String[cols.length];
                    for (int c=1;c<=cols.length;c++) 
                        rows[row][c-1]=rs.getString(c);
                    row++;
                }
                out.println(AsciiTable.getTable(cols, Arrays.copyOf(rows,row)));
             }
    }
}
