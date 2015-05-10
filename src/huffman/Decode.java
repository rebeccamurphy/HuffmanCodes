package huffman;

/**
 * Created by rebecca on 5/4/15.
 */
import javax.print.DocFlavor;
import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
public class Decode {
    public static String eof = "\u0000";
    public static int k;
    public static HashMap<String, Integer> codeLengths;
    public static void main(String[] args) throws IOException
    {
        if(args.length >= 2)
        {
            {
                String secret = ReadFile(args[0]);
                String outputFile = args[1];
                codeLengths = new HashMap<>();
                k=-1;
                DecodeToFile(outputFile, secret);
            }
        }
        else
        {
            System.out.println("Please provide sourcefile and targetfile, or optionally sourcefile");

        }
    }
    /**
     * Read the encoded binary file
     * @param path File path to binary file
     * @return String  representation of encoded message
     */
    static String ReadFile(String path) throws IOException {
        String temp="", temp2="";

            // Use this for reading the data.
            BufferedInputStream bis = null;
            InputStream inStream = new FileInputStream(path);

            bis = new BufferedInputStream(inStream);

            k = bis.read();
            System.out.println("k "+ k);
            for (int i=1; i<k*2+1; i+=2){
                temp= String.format("%8s", Integer.toBinaryString(bis.read() & 0xFF)).replace(' ', '0');
                String chr = (temp.equals("00000000"))?eof :""+(char) Integer.parseInt(temp, 2);
                codeLengths.put(chr, (int) bis.read());

            }
            StringBuilder sb = new StringBuilder(bis.available()*8);
            while (bis.available()>0){
                sb.append(String.format("%8s", Integer.toBinaryString(bis.read() & 0xFF)).replace(' ', '0'));
            }
            bis.close();
            inStream.close();

            // Always close files
        return sb.toString();

    }
    public static void DecodeToFile(String secret, String outputFile){
        String temp ="";
        HashMap<String, String> canonCodesList = new HashMap<>();
        ArrayList<String> canonOrderList = new ArrayList<String>();
        for (Map.Entry<String, Integer> entry : codeLengths.entrySet()) {
            System.out.println(entry.getKey()+" : "+entry.getValue());
        }

        int len = codeLengths.get(eof);

        canonOrderList = canonOrder(codeLengths, canonOrderList);

        int binNum = 0b0;

        for(int i=0; i<canonOrderList.size(); i++){
            String currChar = canonOrderList.get(i).charAt(0) +"";
            int oldCode = Integer.parseInt(canonOrderList.get(i).substring(1));
            if (oldCode!= len ){
                //need to shift binary num
                binNum = binNum >> 1;
                len =oldCode;
            }
            String bin = Integer.toBinaryString(binNum);
            for (int j=bin.length(); j<len; j++){
                bin = "0"+bin;
            }
            canonCodesList.put(bin, currChar);
            binNum++;
        }
        temp="";
        String decodedMsg ="";

        StringBuilder sb = new StringBuilder();
        for (int j =0; j<secret.length(); j++){
            temp+=""+secret.charAt(j);
            if (canonCodesList.containsKey(temp)){
                if (!canonCodesList.get(temp).equals(eof)){
                    sb.append(canonCodesList.get(temp));
                    temp="";
                }
                else
                    break;
            }
        }
        decodedMsg =sb.toString();
        writeToFile(outputFile, decodedMsg);
    }
    public static void writeToFile(String outputFile, String decodedMsg){
        try {
            PrintWriter out = new PrintWriter(outputFile);
            out.println(decodedMsg);
            out.close();

        }
        catch(IOException ex) {
            System.out.println("Error writing file '" + outputFile + "'");

        }

    }
    public static ArrayList<String> canonOrder(HashMap<String, Integer> codes, ArrayList<String> array){
        while (codes.size()>0){
            int max =-1;
            String key ="";
            int keyVal = 100000;
            for (Map.Entry<String, Integer> entry : codes.entrySet()) {
                if (entry.getValue() > max){
                    max= entry.getValue();
                }
            };
            for (Map.Entry<String, Integer> entry : codes.entrySet()) {
                if (entry.getValue()==max &&(int)(entry.getKey().toCharArray()[0]) <keyVal) {
                    key = entry.getKey();
                    keyVal =(int)(entry.getKey().toCharArray()[0]);
                }
            }
            array.add(key + codes.get(key));
            codes.remove(key);
        }
        return array;
    }

}
