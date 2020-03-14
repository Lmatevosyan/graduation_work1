public class Automation { 
  int i;
  int n;
  int count;

  public boolean isBlank(String state, char saw) 
      {
          if(ArrayUtils.contains(blankSymbols, saw)) {
              switch (state) {
                     case "initialState":
                             for(int i = 0; i < n; ++i) {
                                 ++count;
                             }
                             return true;
              }
          }
          return false;
      }

  public boolean isOtherSymbol(String state, char saw)
     {	
    	switch (state) {
                         case "initialState":
                                 if(saw == '{'){
                                    ++countDepth;
                                    for(int j = 0; j < n; ++j) {
                                       ++count;
                                    }
                                    return true;
                                 }
                                else if(saw == '}') {
                                          for(int i = identifier.size()-1; i >= 0; --i) {
                                              if(identifier.get(i).depth == countDepth) {
                                                identifier.remove(i);
                                                --countOfIdentifiers;
                                              }
                                          }
                                          --countDepth;
                                          return true;
                                }
                                else if(ArrayUtils.contains(otherSymbols, saw)) {
                                          return true;
                                }
                  }
    	return false;
      }