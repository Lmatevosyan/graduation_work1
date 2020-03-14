public class Automation { 
  int n;
  int count;

  public boolean isBlank(String state, char saw) 
      {
         for(int i = 0; i < n; ++i) {
                ++count;
         }
         return true;
        }
         

  public boolean isOtherSymbol(String state, char saw)
     {	
        if(saw == '{'){
            ++countDepth;
             for(int i = 0; i < n; ++i) {
                   ++count;
              }
           return true;
         }       
      }