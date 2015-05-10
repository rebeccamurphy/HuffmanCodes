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
                DecodeToFile(outputFile, secret);
            }
        }
        else
        {
            System.out.println("Please provide sourcefile and targetfile, or optionally sourcefile");
            codeLengths = new HashMap<>();
            k=-1;
            //String message = ReadFile("samples//text//sample7.txt");
            String secret = ReadFile("butt.huf");
            //System.out.println(secret);
            DecodeToFile(secret,"butt.txt");
        }
    }
    /**
     * Read the encoded binary file
     * @param path File path to binary file
     * @return byte array representation of the binary file
     */
    static String ReadFile(String path) throws IOException {
        String temp="", temp2="";

            // Use this for reading the data.
            BufferedInputStream bis = null;
            InputStream inStream = new FileInputStream(path);
            //BufferedReader in= new BufferedReader(new FileReader(path));

            bis = new BufferedInputStream(inStream);

            //byte[] contents =Files.readAllBytes(stream);
            k = bis.read();
            System.out.println("k "+ k);
            for (int i=1; i<k*2+1; i+=2){
                temp= String.format("%8s", Integer.toBinaryString(bis.read() & 0xFF)).replace(' ', '0');
                String chr = (temp.equals("00000000"))?eof :""+(char) Integer.parseInt(temp, 2);
                System.out.println(temp+ " "+Integer.parseInt(temp)+" " +chr);
                codeLengths.put(chr, (int) bis.read());
                temp="";
            }
            temp="";
            StringBuilder sb = new StringBuilder(bis.available()*8);
            while (bis.available()>0){
                sb.append(String.format("%8s", Integer.toBinaryString(bis.read() & 0xFF)).replace(' ', '0'));
            }
            //System.out.println("Secret "+temp);
        bis.close();
        inStream.close();

            // Always close files

        //temp = Arrays.copyOfRange(contents, last+1, contents.length);

        /*
            for (int i=last+1; i< contents.length; i++){
                temp+= String.format("%8s", Integer.toBinaryString(contents[i] & 0xFF)).replace(' ', '0');
                System.out.println("i: " + i + " " +contents.length);
                System.out.println(String.format("%8s", Integer.toBinaryString(contents[i] & 0xFF)).replace(' ', '0'));
            }
        */
        return sb.toString();

    }
    public static void DecodeToFile(String secret, String outputFile){
        int counter =0;
        String temp ="";
        HashMap<String, String> canonCodesList = new HashMap<>();
        System.out.println(codeLengths.size());
        counter=k*16+8;
        ArrayList<String> canonOrderList = new ArrayList<String>();
        for (Map.Entry<String, Integer> entry : codeLengths.entrySet()) {
            System.out.println(entry.getKey()+" : "+entry.getValue());
        }

        int len = codeLengths.get(eof);

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
        System.out.println("Canon codes");
        for (Map.Entry<String, String> entry : canonCodesList.entrySet()) {
            System.out.println(entry.getKey()+" : "+entry.getValue());
        }
        //System.out.println(secret);
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
        //System.out.println(decodedMsg);
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
        //codes.remove(eof);
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
