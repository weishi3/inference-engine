package cs561hw3;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;



public class homework {
    //not need to remove * based on new checking loop method
    //prune?finished?
    //factor? done by hashset
    public static HashMap<String,HashSet<ArrayList<String>>> kbMustList;
    
    
    //public static HashMap<Integer,HashSet<HashMap<String,HashSet<ArrayList<String>>>>> record; //int->used set of sentence
    public static Hashtable <String,HashSet<Integer>> possibleList;

    public static ArrayList<HashMap<String,HashSet<ArrayList<String>>>>kbSentence; 
    public static ArrayList<String>query;//to be init in main
    
   public static int countP=1;
public static void main(String[] args) throws IOException {  
        
        long startTime   = System.currentTimeMillis();
        
        FileReader fr = new FileReader("input.txt");
        BufferedReader bf = new BufferedReader(fr);
        int nOfQ=Integer.parseInt(bf.readLine());
        //read in query
        ArrayList<ArrayList<String>>queries=new ArrayList<ArrayList<String>>();
        String readTemp;
        ArrayList<String> queryTemp;
        for (int i=0;i<nOfQ;i++){
            readTemp=bf.readLine();
            
            
            int argIndexStart=readTemp.indexOf('(')+1;
            String arg=readTemp.substring(argIndexStart, readTemp.length()-1);
            queryTemp= new ArrayList<String>(Arrays.asList(arg.split(",")));
            
            //queryTemp=(ArrayList<String>) Arrays.asList(arg.split(","));
            if (readTemp.charAt(0)=='~') {//from one
                queryTemp.add(0,readTemp.substring(1, argIndexStart-1));//negate
                
            }
            else{
                queryTemp.add(0,"~"+readTemp.substring(0, argIndexStart-1));//negate
        
            }
                
            queries.add(queryTemp);
        }
        //kb
        
        ArrayList<String> args1;
        int nOfK=Integer.parseInt(bf.readLine());
        kbMustList=new HashMap<String,HashSet<ArrayList<String>>>();
        
        
        
        HashSet<ArrayList<String>>argsList;
        HashSet<Integer> senPosi;
        int sentenceCount;
        HashMap<String,HashSet<ArrayList<String>>> sentenceTemp;
        possibleList= new Hashtable <String,HashSet<Integer>>();
        kbSentence= new ArrayList<HashMap<String,HashSet<ArrayList<String>>>>();
        for (int i=0;i<nOfK;i++){
            readTemp=bf.readLine().replaceAll("\\s","");
            if (readTemp.contains("|")){//sentence
                sentenceCount=kbSentence.size();
                sentenceTemp=new HashMap<String,HashSet<ArrayList<String>>>();
                
                for (String j:Arrays.asList(readTemp.split("\\|"))){
                    
                    int argIndexStart=j.indexOf('(')+1;
                    String arg=j.substring(argIndexStart, j.length()-1);
                
                    
                    
                    args1=new ArrayList<String>(Arrays.asList(arg.split(",")));
                    for (int k=0;k<args1.size();k++){
                        if (variable(args1.get(k)))
                            args1.set(k,args1.get(k)+"_"+Integer.toString(sentenceCount));
                    }
                    
                    //args1=(ArrayList<String>) Arrays.asList(arg.split(","));
                    
                    String  symbol=j.substring(0, argIndexStart-1);
                    if (possibleList.containsKey(symbol)){
                        possibleList.get(symbol).add(sentenceCount);
                    }else{
                        senPosi=new HashSet<Integer>();
                        senPosi.add(sentenceCount);
                        possibleList.put(symbol, senPosi);
                    }
                    if (sentenceTemp.containsKey(symbol)){
                        sentenceTemp.get(symbol).add(args1);
                    }else{
                        argsList=new HashSet<ArrayList<String>>();
                        argsList.add(args1);
                        sentenceTemp.put(symbol, argsList);
                    }
                

                        
                }

                kbSentence.add(sentenceTemp);
            }else {// single literal
                int argIndexStart=readTemp.indexOf('(')+1;
                String arg=readTemp.substring(argIndexStart, readTemp.length()-1);
                args1=new ArrayList<String>( Arrays.asList(arg.split(",")));
                
                String symbol=readTemp.substring(0, argIndexStart-1);
                
            
                if (kbMustList.containsKey(symbol)){
                    kbMustList.get(symbol).add(args1);
                }else{
                    HashSet<ArrayList<String>> tempSet=new HashSet<ArrayList<String>>();
                    tempSet.add(args1);
                    kbMustList.put(symbol,tempSet);
                }
                
            }
            
        }
        //TODO:pre-processing  +- ++ --?
        //i.e. unify duplicates
        
                            
        fr.close();
        
        //start!
        
        ArrayList<Boolean>results=new ArrayList<Boolean>();
//      System.out.println(queries);
//      System.out.println(kbMustList);
//      System.out.println(kbSentence);
        
        //curVariable cannot be global
        //DFS
        HashMap<String,HashSet<ArrayList<String>>> current;
        for (ArrayList<String>i:queries){
            query=i;
            ArrayList<String>queryCP=new ArrayList<String>(i);
           
            //record=new HashMap<Integer,HashSet<HashMap<String,HashSet<ArrayList<String>>>>>();
            current=new HashMap<String,HashSet<ArrayList<String>>>();
            //query unchanged--full
            String queryPredicate=queryCP.remove(0);
            HashSet<ArrayList<String>> tempVar=new HashSet<ArrayList<String>>();
            tempVar.add(queryCP);
            current.put(queryPredicate,tempVar);
        
            
            //curVariable initially empty
            results.add(DFS(current,new HashSet<String>(),-1,new HashMap<Integer,HashSet<HashMap<String,HashSet<ArrayList<String>>>>>()));
        }
        

        
        
        
        
        FileWriter fw = new FileWriter("output.txt");
        BufferedWriter bw = new BufferedWriter(fw);
//      for (boolean i: results){
//          bw.write(Boolean.toString(i).toUpperCase());
//          bw.newLine();
//      }
        for (int i=0;i<results.size();i++){
            bw.write( Boolean.toString(results.get(i)).toUpperCase()    );
            if (i<results.size()-1)
                bw.newLine();
        }

        
        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        
        bw.close();
        fw.close();
    

       // System.out.println(totalTime+"ms");
        

        
    }

    public static boolean DFS(HashMap<String,HashSet<ArrayList<String>>>current,HashSet<String> curVariable, int lastSen,HashMap<Integer,HashSet<HashMap<String,HashSet<ArrayList<String>>>>>recordPre){
        
        
        //if empty return true
        if (current.size()==0) return true;
        
        HashMap<Integer,HashSet<HashMap<String,HashSet<ArrayList<String>>>>>record=new HashMap<Integer,HashSet<HashMap<String,HashSet<ArrayList<String>>>>>(recordPre);
        //necessary
        current=superDeepCopy(current);
        
        
        
//need re-new *5    this is the only one needed still can be replaced by deep copy

//      for (String i:current.keySet()){
//          current.put(i,new HashSet<ArrayList<String>>(current.get(i)));
//      }
        
        //test

//        if (countP<1000){
//           // if (lastSen==2){
//                System.out.println(curVariable);
//                System.out.println(current);
//               
//               // System.out.println(record);
//                
//                
//           // }
//            countP++;
//        }
        
        //to be recover
        if (lastSen!=-1){
            if (!record.containsKey(lastSen)){
                HashSet<HashMap<String,HashSet<ArrayList<String>>>>recordTemp=new HashSet<HashMap<String,HashSet<ArrayList<String>>>>();
                recordTemp.add(current);
                record.put(lastSen, recordTemp);
            }else{
                HashSet<HashMap<String,HashSet<ArrayList<String>>>>recordTemp=record.get(lastSen);
                //v2?
                if (isDuplicateV2(recordTemp,current)){
                   // System.out.println("Fa");
                    //no need to change
                    //System.out.println("h1");
                    return false;
                }
                recordTemp.add(current);
                recordTemp=new HashSet<HashMap<String,HashSet<ArrayList<String>>>>(recordTemp);
                record.put(lastSen, recordTemp);
                
            }
        }
        int countMin=1000000;
       
        
        //to be recover
        String currentSymbol=current.keySet().iterator().next();
        for (String s:current.keySet()){
            int ccount=0;
            if (opposite(s,query.get(0))) ccount++;
            String op=getOpposite(s);
            if (kbMustList.containsKey(op))
                ccount+=kbMustList.get(op).size();
            if (possibleList.containsKey(op))
                ccount+=possibleList.get(op).size();
            if (ccount==0){
                //System.out.println("h2");
                return false;
            } 
            if (ccount<countMin){
                countMin=ccount;
                currentSymbol=new String(s);
            }
            
        }
        
        
        
    
        ArrayList<String> curArgs=new ArrayList<String>(current.get(currentSymbol).iterator().next());
        //find opposite in kb,kbsentence,query
        
        
        //have multiple candidate to unify at the same time? just pick first.. then continue one by one
        //checking all sentences/literals for current
        //with query
        
        ArrayList<String> nextFU;
        HashMap<String,String>substitute=null;
        String toReplace;
        HashSet<String> resolVariable=new HashSet<String>(curVariable);
        HashSet<ArrayList<String>> argsSetTemp;
        
        HashMap<String,HashSet<ArrayList<String>>>resolvent=superDeepCopy(current);
        //HashMap<String,HashSet<ArrayList<String>>> resolvent=new HashMap<String,HashSet<ArrayList<String>>>(current);
        if (opposite(currentSymbol,query.get(0))){
            nextFU=new ArrayList<String>(query);
            nextFU.remove(0);
            substitute=unifyList(curArgs,nextFU,new HashMap<String,String>());
            
            if (substitute!=null){ 
                for (String s:substitute.keySet()){// query must be constant OK
                    resolVariable.remove(s);
                }
                argsSetTemp=resolvent.get(currentSymbol);
                argsSetTemp.remove(curArgs);
                if (argsSetTemp.size()==0) resolvent.remove(currentSymbol);
                //don't need to consider adding ***, 
                //if do need to replace something
                if (substitute.size()>0){
                    for (HashSet<ArrayList<String>>i:resolvent.values()){
                        for (ArrayList<String>j:i){
                            for (int k=0;k<j.size();k++){
                                toReplace=j.get(k);
                                if (substitute.containsKey(toReplace))
                                    j.set(k, substitute.get(toReplace));
                                 
                            }
                        }
                    }
                    
                       //function replce by deepCOPY    
//                  for (String i:resolvent.keySet()){
//                      resolvent.put(i,new HashSet<ArrayList<String>>(resolvent.get(i)));
//                  }
                }
                boolean result= DFS(resolvent,resolVariable,-1,record);
                if (result) return true;
                
            }
        }
        
        
        
        
        //if for variable in nextNode,do nothing
        substitute=null;
        
        String toCheck=getOpposite(currentSymbol);
//        if (currentSymbol.charAt(0)=='~')
//            toCheck=currentSymbol.substring(1);
//        else
//            toCheck="~"+currentSymbol;
        HashSet<ArrayList<String>> argsSetIM=kbMustList.get(toCheck);
        if (argsSetIM!=null){
            Iterator<ArrayList<String>> iterMusts=argsSetIM.iterator();
            for (int im=0;im<argsSetIM.size();im++){
        
                resolVariable=new HashSet<String>(curVariable);
                resolvent=superDeepCopy(current);
                //resolvent=new HashMap<String,HashSet<ArrayList<String>>>(current);
                nextFU=iterMusts.next();
                //
               substitute=unifyList(nextFU,curArgs,new HashMap<String,String>());
                
                //substitute=unifyList(curArgs,nextFU,new HashMap<String,String>());
//              for (String s:substitute.keySet()){// query must be constant
//                  curVariable.remove(s);
//              }
            //do this in unification process
                if (substitute!=null){ 
                    
                    //bad ~T(S) | A(x,y,z) | B(e) | C(e)
                    //~A(k,k,k) because negate unify
//                   for (String s:substitute.keySet()){//now var belongs to previous part after 
//                         resolVariable.remove(s);
//                     }
//                   //add this
//                   for (String s:curArgs){
//                       substitute
//                   }
                    // new a resolVariable
                    
                    argsSetTemp=resolvent.get(currentSymbol);
                    
                    
                    argsSetTemp.remove(curArgs);
                    
                    if (argsSetTemp.size()==0) resolvent.remove(currentSymbol);
                    //don't need to consider adding ***, 
                    //if do need to replace something
                    if (substitute.size()>0){
                        for (HashSet<ArrayList<String>>i:resolvent.values()){
                            for (ArrayList<String>j:i){
                                for (int k=0;k<j.size();k++){
                                    toReplace=j.get(k);
                                    if (substitute.containsKey(toReplace)){
                                        String subst=substitute.get(toReplace);
                                       // if (!variable(subst)){
                                            j.set(k, subst);
                                            //~T(S) | A(x,y,z) | B(e) | C(e)
                                            //~A(k,k,k)
                                            //resolVariable.remove(toReplace);
                                       // }
                                            
                                    }
                                     
                                }
                            }
                        }
                        
                        //   //function replce by deepCOPY
                        //need re-new
//                      for (String i:resolvent.keySet()){
//                          resolvent.put(i,new HashSet<ArrayList<String>>(resolvent.get(i)));
//                      }
                     //here: something to substitute   
                        resolVariable=new HashSet<String>();
                        for (HashSet<ArrayList<String>>i:resolvent.values()){
                            for (ArrayList<String>j:i){
                                for (String k:j){
                                   if (variable(k))
                                       resolVariable.add(k);
                                }
                            }
                        }
                     
                    }
                    boolean result= DFS(resolvent,resolVariable,-1,record);
                    if (result) return true;
                    
                }
            }
        }
        
        
        substitute=null;
        HashMap<String,HashSet<ArrayList<String>>>anotherCPNextSen;
        HashMap<String,HashSet<ArrayList<String>>>nextSen; //it is a deep copy
        HashSet<ArrayList<String>> argsSetIP;
        HashSet<Integer> sentenceIndexes=possibleList.get(toCheck);
        //ArrayList<HashMap<String,HashSet<ArrayList<String>>>>kbSentence
        
     //   System.out.println("s:"+sentenceIndexes);
//      System.out.println("s:"+sentenceIndexes);
//      System.out.println(possibleList);
        if (sentenceIndexes!=null){
            
        for (int si:sentenceIndexes){
            //need completely new...
            //nextSen=new HashMap<String,HashSet<ArrayList<String>>>(kbSentence.get(si));
            nextSen=superDeepCopy(kbSentence.get(si));
        
            
        
            //change/fabricate next args before update curVar, a little bit bad,doesn't matter.. add*
            //as current's args are x1-x100, next sentence's args normally would not have conflict with current
            //do this just in case
            //do add star later
            for (HashSet<ArrayList<String>>i:nextSen.values()){
                for (ArrayList<String>j:i){
                    for (int k=0;k<j.size();k++){
                        if (curVariable.contains(j.get(k))){//deep copy
                            j.set(k, j.get(k)+"*");
                            
                        }
                        
                         
                    }
                }
            }
            //OK here
            
            argsSetIP=nextSen.get(toCheck);
            Iterator<ArrayList<String>> iterPossibles=argsSetIP.iterator();
            for (int ip=0;ip<argsSetIP.size();ip++){
                
                anotherCPNextSen=superDeepCopy(nextSen);
                //anotherCPNextSen=new HashMap<String,HashSet<ArrayList<String>>>(nextSen);
                resolVariable=new HashSet<String>(curVariable);
                resolvent=superDeepCopy(current);
                //resolvent=new HashMap<String,HashSet<ArrayList<String>>>(current);
                nextFU=iterPossibles.next();

//              System.out.println("c:"+curArgs);
//              System.out.println("n:"+nextFU);
//              System.out.println(kbSentence);
                
                substitute=unifyList(curArgs,nextFU,new HashMap<String,String>());
               
                //what if null?
                
                if (substitute!=null){ 
//                  System.out.println("HH");
//                  System.out.println(substitute);
                    for (String s:substitute.keySet()){//now var belongs to previous part after 
                        resolVariable.remove(s);
                    }
                    argsSetTemp=resolvent.get(currentSymbol);
                    

                    argsSetTemp.remove(curArgs);
                    if (argsSetTemp.size()==0) resolvent.remove(currentSymbol);

                        //remove the clause from "next"? deep copy
                        //which would not change KB
                        // however, this inner loop, like mather(a,b)|mather(c,d) 
                       //need one more deep copy
                    argsSetTemp=anotherCPNextSen.get(toCheck);
                    argsSetTemp.remove(nextFU);
                    if (argsSetTemp.size()==0) anotherCPNextSen.remove(toCheck);
                    
                    
                    if (substitute.size()>0){
                        for (HashSet<ArrayList<String>>i:resolvent.values()){
                            for (ArrayList<String>j:i){
                                for (int k=0;k<j.size();k++){
                                    //no need to recursive change change in unify function
//                                  toReplace=j.get(k);
//                                  String subst="";
//                                  while (substitute.containsKey(toReplace)){
//                                      subst=substitute.get(toReplace);
//                                      j.set(k, subst);
//                                      toReplace=new String(subst);
//                                  }
//                                  if (!subst.equals("")) curVariable.add(subst);
                                        
                                    toReplace=j.get(k);
                                    if (substitute.containsKey(toReplace)){
                                        String subst=substitute.get(toReplace);
                                        j.set(k, subst);
                                        resolVariable.add(subst);//fix
                                    }
                                     
                                }
                            }
                        }
                    //System.out.println("last!"+kbSentence.get(6));
                        //need re-new
                        

                        
                        
                        //actually dont need another deep copy, current cannot unify with two at the same time
                        //-mother (x,y) mother (A,B)|mother(C,D) OK 
                        //from left to right -mother(C,y) mother (A,B)|mother(C,D) -Mother(A,B)
                        
                        for (HashSet<ArrayList<String>>i:anotherCPNextSen.values()){
                            for (ArrayList<String>j:i){
                                for (int k=0;k<j.size();k++){
                                    toReplace=j.get(k);
                                    if (substitute.containsKey(toReplace)){
                                        String subst=substitute.get(toReplace);
                                        j.set(k, subst);//???
                                    }
                                     
                                }
                            }
                        }
       
           //function replce by deepCOPY
//                      for (String i:anotherCPNextSen.keySet()){
//                          anotherCPNextSen.put(i,new HashSet<ArrayList<String>>(anotherCPNextSen.get(i)));
//                      }
                        
                    }
                    //add clauses from "next"to resolvent update resolvariable
                    
                    for (String s:anotherCPNextSen.keySet()){
                        if (!resolvent.containsKey(s)){
                            resolvent.put(s, anotherCPNextSen.get(s));
                            for (ArrayList<String> n:anotherCPNextSen.get(s)){
                                for (String nn:n){
                                    if (variable(nn)) resolVariable.add(nn);
                                }
                            }
                        }else{
                            for (ArrayList<String> n:anotherCPNextSen.get(s)){
                                resolvent.get(s).add(n); //exactly same would not add (but with *)...->arguments looks not same...?
                                for (String nn:n){
                                    if (variable(nn)) resolVariable.add(nn);
                                }
                            }
                        }
                    }
                    
                    //TODO:self Checking after combine
                    
                    boolean result= DFS(resolvent,resolVariable,si,record);
                    if (result) return true;
                        
                }
            }
            
            
        }
        }
        
    
    

        
        

       // System.out.println("fb");
       // System.out.println("h3");
        return false;
    }
    /**
     * DEEPCOPY 
     * @param a init when calling: cp after removing first two indexes
     * @param b
     * @param mapping init when calling:empty new hashmap...
     * @return
     */
    public static HashMap<String,String> unifyList(ArrayList<String>aori,ArrayList<String>bori,HashMap<String,String>mapping ){
        
        if (mapping==null) return null;
        //including the case of a/b size==0
        ArrayList<String> a=new ArrayList<String>(aori);
        ArrayList<String> b=new ArrayList<String>(bori);
        if (a.equals(b)) return mapping;
        String aFirst=a.remove(0);
        String bFirst=b.remove(0);
        
        HashMap<String,String>bp=   unifyList(a,b,unifySingle(aFirst,bFirst,mapping));
        if (bp==null) return null;
        for (String i:bp.keySet()){
            String attempKey=new String(i);
            String attempResult=new String(bp.get(attempKey));
            while (bp.containsKey(attempResult)){
                attempKey=new String(attempResult);
                attempResult=new String(bp.get(attempKey));
            }
            bp.put(i, attempResult);
        }
        return bp;
     
        //return unifyList(a,b,unifySingle(aFirst,bFirst,mapping));
    }
    
    public static HashMap<String,String> unifySingle(String aOne,String bOne,HashMap<String,String>mapping ){
        //failing... not needed
        //if (mapping==null) return null;
        if (aOne.equals(bOne)) return mapping;
        if (variable(aOne)){
            return unifyVar(aOne,bOne,mapping);
        }
        if (variable(bOne)){
            return unifyVar(bOne,aOne,mapping);
        }
        
        return null;
    }
    
    
    public static HashMap<String,String> unifyVar(String var,String x,HashMap<String,String>mapping ){
        if (mapping.containsKey(var)) return unifySingle(mapping.get(var),x,mapping);
        if (mapping.containsKey(x)) return unifySingle(var,mapping.get(var),mapping);
        mapping.put(var, x);
        return mapping;
    }
    public static boolean variable(String s){
        char firstLetter=s.charAt(0);
        if (firstLetter<='z'&&firstLetter>='a') return true;
        return false;
    }
    
    //guarantee kbSentences (args missing nextSenDeepCopy, 
    //error when multiple queries in a file) deep copy to string level
    //record would not change resolventdeepcopy
    public static HashMap<String,HashSet<ArrayList<String>>> superDeepCopy(HashMap<String,HashSet<ArrayList<String>>> ori){
        HashMap<String,HashSet<ArrayList<String>>> ret=new HashMap<String,HashSet<ArrayList<String>>>();
        for (String i:ori.keySet()){
            HashSet<ArrayList<String>>temp=new HashSet<ArrayList<String>>();
            for (ArrayList<String>j:ori.get(i)){
                ArrayList<String> temp2=new ArrayList<String>();
                for (String k:j){
                    temp2.add(new String(k));
                }
                temp.add(temp2);
            }
            ret.put(i, temp);
        }
        return ret;
    }
    public static boolean opposite(String aori, String bori){
        String a=new String(aori);
        String b=new String(bori);
        if (a.charAt(0)=='~'&& b.charAt(0)=='~' ) return false;
        if (a.charAt(0)!='~'&& b.charAt(0)!='~' ) return false;
        if (a.charAt(0)=='~') a=a.substring(1);
        else b=b.substring(1);
        return (a.equals(b));
    }
    public static boolean isDuplicate(HashSet<HashMap<String,HashSet<ArrayList<String>>>>database,HashMap<String,HashSet<ArrayList<String>>>current){
        //database contains one element, so that all of it are exactly in current
        
        boolean breakSig=false;
        for (HashMap<String,HashSet<ArrayList<String>>>i:database){
            breakSig=false;
            for (String j:i.keySet()){
                if (!current.containsKey(j)) {
                    breakSig=true;
                    break;
                }
                else{
                    breakSig=false;
                    for (ArrayList<String>k:i.get(j)){
                        //first attemp: equals+ normalization
                        // second : equals+ x_0 +*
                        
                        if (!current.get(j).contains(k)) {
                            breakSig=true;
                            break;
                        }
                    }
                    if (breakSig) break;
                }
            }
            if (!breakSig){
              //  System.out.println("HHH");
                return true;
            } 
        }
        
        return false;
    }
    
    public static boolean isDuplicateV2(HashSet<HashMap<String,HashSet<ArrayList<String>>>>database,HashMap<String,HashSet<ArrayList<String>>>current){
        //database contains one element, so that all of it are exactly in current
        
        boolean breakSig=false;
        for (HashMap<String,HashSet<ArrayList<String>>>i:database){
            breakSig=false;
            for (String j:i.keySet()){
                if (!current.containsKey(j)) {
                    breakSig=true;
                    break;
                }
                else{
                    breakSig=false;
                    if (i.get(j).size()>current.get(j).size()){
                        breakSig=true;
                        break;
                    }
                }
            }
            if (!breakSig){
              //  System.out.println("HHH");
                return true;
            } 
        }
        
        return false;
    }
    
    
    public static String getOpposite(String currentSymbol){
        String toCheck;
         if (currentSymbol.charAt(0)=='~')
             toCheck=currentSymbol.substring(1);
         else
             toCheck="~"+currentSymbol;
         return toCheck;
    }
}

