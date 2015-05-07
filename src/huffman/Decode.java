package huffman;

/**
 * Created by rebecca on 5/4/15.
 */
import javax.print.DocFlavor;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
public class Decode {
    public static String eof = "\u0000";
    public static int k =-1;
    public static void main(String[] args) throws IOException
    {
        if(args.length >= 2)
        {
            {
                String secret = ReadFile(args[0]);
                String outputFile = args[1];
                DecodeToFile(outputFile, secret);
            }
        }
        else
        {
            System.out.println("Please provide sourcefile and targetfile, or optionally sourcefile");
            //String message = ReadFile("samples//text//sample7.txt");
            String secret = ReadFile("sample1.huf");
            System.out.println(secret);
            DecodeToFile("butt.text", secret);
        }
    }
    /**
     * Read the encoded binary file
     * @param path File path to binary file
     * @return byte array representation of the binary file
     */
    static String ReadFile(String path) throws IOException{
        String temp="";
            // Use this for reading the data.
            byte[] buffer = new byte[1000];

            Path stream = Paths.get(path);

            byte[] contents =Files.readAllBytes(stream);
            // Always close files.
            for (int i=0; i< contents.length; i++)
                temp+= String.format("%8s", Integer.toBinaryString(contents[i] & 0xFF)).replace(' ', '0');

        return temp;

    }
    public static void DecodeToFile(String outputFile, String secret){
        int counter =0;
        String temp ="";
        HashMap<String, Integer> codeLengths = new HashMap<>();
        HashMap<String, String> canonCodesList = new HashMap<>();
        while (counter<8){
            temp+="" +secret.charAt(counter);
            counter++;
        }
        k = Integer.parseInt(temp, 2);
        System.out.println(temp);
        System.out.println(k);
        temp="";
        System.out.println(secret.length());
        for(int i=counter; i<k*16+8;i++){
            temp +="" + secret.charAt(i);

            if (temp.length()%16==0){
                System.out.println("temp: "+  temp);
                int charNum = Integer.parseInt(temp.substring(0,8), 2);
                String chr = (charNum==0)?"0" : ""+(char) charNum;
                System.out.println("char : " + chr + " " +charNum);
                codeLengths.put(chr, Integer.parseInt(temp.substring(8), 2));
                temp="";
            }
        }
        System.out.println(codeLengths.size());
        counter=k*16+8;
        String encodedMsg = secret.substring(counter);
        ArrayList<String> canonOrderList = new ArrayList<String>();
        for (Map.Entry<String, Integer> entry : codeLengths.entrySet()) {
            System.out.println(entry.getKey()+" : "+entry.getValue());
        }

        int len = codeLengths.get("0");

        canonOrderList = canonOrder(codeLengths, canonOrderList);
        for (int i=0; i<canonOrderList.size();i++){
            System.out.println(canonOrderList.get(i));
        }
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
        //removes eof
        for (Map.Entry<String, String> entry : canonCodesList.entrySet()) {
            System.out.println(entry.getKey()+" : "+entry.getValue());
        }
        canonCodesList.remove("00");
        System.out.println(encodedMsg);
        for (int j =0; j<encodedMsg.length(); j++){
            temp+=""+encodedMsg.charAt(j);
            if (canonCodesList.containsKey(temp)){
                decodedMsg+= canonCodesList.get(temp);
                temp="";
            }
        }
        System.out.println(decodedMsg);
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
        codes.remove(eof);
        while (codes.size()>0){
            int max =-1;
            String key ="";
            int keyVal = 100000;
            for (Map.Entry<String, Integer> entry : codes.entrySet()) {
                if (entry.getValue() > max){
                    max= entry.getValue();
                }
            }
            for (Map.Entry<String, Integer> entry : codes.entrySet()) {
                if (entry.getValue()==max && Character.getNumericValue(entry.getKey().charAt(0)) <keyVal) {
                    key = entry.getKey();
                    keyVal = Character.getNumericValue(entry.getKey().toCharArray()[0]);
                }
            }
            array.add(key + codes.get(key));
            codes.remove(key);
        }
        return array;
    }

}
