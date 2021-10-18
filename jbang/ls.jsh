// jshell -R-Darg=. ls.jsh
import java.io.*;

var arg=System.getProperty("arg");
for (var f : new File(arg).listFiles()) 
    System.out.println(f);
/exit
