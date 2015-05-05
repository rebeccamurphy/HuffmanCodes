package huffman;

/**
 * Created by rebecca on 5/4/15.
 */
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Encode {
    public static String eof = "\u0000";
    public static int k =-1;
    public static void main(String[] args) throws IOException
    {
        if(args.length >= 2)
        {
            /*
            if(args[0].equals("-c"))
            {
                //saving the canon tree
                String message = ReadFile(args[2]);
                EncodeToFile(args[3], args[1], true, message);
            }
            else if(args[0].equals("-h"))
            {
                String message = ReadFile(args[2]);
                EncodeToFile(args[3], args[1], false, message);
            }
            else*/
            {
                String message = ReadFile(args[0]);
                EncodeToFile(args[1], message);
            }
        }
        else
        {
            System.out.println("Please provide sourcefile and targetfile, or optionally sourcefile");
            //String message = ReadFile("samples//text//sample7.txt");

            EncodeToFile("butt.bin", "abbc");
        }
        //Decode.main(new String[0]);
    }
    /**
     * Read the encoded binary file
     * @param path File path to binary file
     * @return byte array representation of the binary file
     */
    static String ReadFile(String path) throws IOException
    {
        Path p = Paths.get(path);
        return new String(Files.readAllBytes(p));
    }

    public static void EncodeToFile(String outputFile, String message){

        k =0; //number of character in alphabet
        TreeMap<String, Integer> frequency = new TreeMap<String, Integer>();
        //getting the frequencies of each letter
        for (char ch : message.toCharArray()){
            if (frequency.containsKey(""+ch)){
                frequency.put(""+ch, frequency.get(""+ch)+1);
            }
            else{
                frequency.put(""+ch, 1);
            }
        }
        //end of file
        frequency.put(eof, 1);
        k = frequency.size();
        //create the normal tree
        TreeNode root;
        //curr is the note with the lowest frequency
        //codeOrder = Order(frequency, codeOrder);
        /*
        for (int a =0; a<codeOrder.size(); a++){
            String chr = ""+codeOrder.get(a).charAt(0);
            int frq = Integer.parseInt(codeOrder.get(a).substring(1));

            TreeNode next =new TreeNode(chr, frq);
            TreeNode parent = new TreeNode(curr.str+next.str, curr.count+next.count);
            curr.setParent(parent);
            next.setParent(parent);
            curr= parent;
            nodes.add(curr);
        }*/
        root = makeTree(frequency);
        root.printTree(0);
        TreeMap<String, String> codes = new TreeMap<String, String>();
        TreeMap<String, String> canonCodes = new TreeMap<String, String>();
        root.getCodes("", codes);
        for (Map.Entry<String, String> entry : codes.entrySet()) {
            System.out.println(entry.getKey()+" : "+entry.getValue());
        }
        int len = codes.get(eof).length();
        //to create a binary num with the right num of 0s
        char[] temp = new char[len];
        Arrays.fill(temp, '0');
        String firstNum = new String(temp);
        canonCodes.put(eof, firstNum);
        int binNum = 0b1;
        ArrayList<String> canonOrderList = new ArrayList<String>();
        canonOrderList = canonOrder(codes, canonOrderList);
        //do eof first

        //convert to canon codes
        for(int i=0; i<canonOrderList.size(); i++){
            String currChar = canonOrderList.get(i).charAt(0) +"";
            String oldCode = canonOrderList.get(i).substring(1);
            if (oldCode.length() != len ){
               //need to shift binary num
               binNum = binNum >> 1;
               len =oldCode.length();
            }

            String bin = Integer.toBinaryString(binNum);
            for (int j=bin.length(); j<len; j++){
                bin = "0"+bin;
            }
            canonCodes.put(currChar, bin);
            binNum++;
        }
        for (Map.Entry<String, String> entry : canonCodes.entrySet()) {
            System.out.println(entry.getKey()+" : "+entry.getValue());
        }
        writeFile(outputFile, message, canonCodes);
    }
    public static TreeNode makeTree(TreeMap<String, Integer> frequency){
        ArrayList<TreeNode> nodes = new ArrayList<TreeNode>();

        while(frequency.size()>1){
            int min =100000;
            String minKey ="";
            for (Map.Entry<String,Integer> entry :frequency.entrySet()) {
                if (entry.getValue() <=min&& (minKey.equals("") ||(minKey.length()> entry.getKey().length()))){
                    min = entry.getValue();
                    minKey = entry.getKey();
                }
            }
            TreeNode curr = null;
            for (int i =0; i<nodes.size(); i++){
                if (nodes.get(i).str.equals(minKey)){
                    curr = nodes.get(i);
                    break;
                }
            }

            curr =(curr==null)? new TreeNode(minKey,min):curr;
            System.out.println("minkey " + minKey);
            frequency.remove(minKey);
            int min2 =100000;
            String minKey2 ="";
            for (Map.Entry<String,Integer> entry :frequency.entrySet()) {
                System.out.println("TEST " + minKey2.length() + " " +entry.getKey()+" " + entry.getKey().length());
                System.out.println((minKey2.length()> entry.getKey().length()));
                if (entry.getValue() <=min2 && (minKey2.equals("") ||(minKey2.length()> entry.getKey().length()))){
                    min2 = entry.getValue();
                    minKey2 = entry.getKey();
                }
            }
            TreeNode next = null;
            for (int j =0; j<nodes.size(); j++){
                if (nodes.get(j).str.equals(minKey2)){
                    next = nodes.get(j);
                    break;
                }
            }

            next =(next==null)? new TreeNode(minKey2,min2):next;
            frequency.remove(minKey2);
            System.out.println("minkey2 " + minKey2);
            TreeNode parent = new TreeNode(curr.str+next.str, curr.count+next.count);
            curr.setParent(parent);
            next.setParent(parent);
            System.out.println("parent " + parent);
            nodes.add(parent);
            frequency.put(parent.str, parent.count);

        }
        return nodes.get(nodes.size()-1);
    }

    public static ArrayList<String> canonOrder(TreeMap<String, String> codes, ArrayList<String> array){
        codes.remove(eof);
        while (codes.size()>0){
            int max =-1;
            String key ="";
            int keyVal = 100000;
            for (Map.Entry<String, String> entry : codes.entrySet()) {
                if (entry.getValue().length() > max){
                    max= entry.getValue().length();
                }
            }
            for (Map.Entry<String, String> entry : codes.entrySet()) {
                if (entry.getValue().length()==max && Character.getNumericValue(entry.getKey().toCharArray()[0]) <keyVal) {
                    key = entry.getKey();
                    keyVal = Character.getNumericValue(entry.getKey().toCharArray()[0]);
                }
            }
            array.add(key + codes.get(key));
            codes.remove(key);
        }

        return array;
    }
    public static void writeFile(String outputFile, String message,  TreeMap<String, String> canonCodes){
        try {
            // Put some bytes in a buffer so we can
            // write them. Usually this would be
            // image data or something. Or it might
            // be unicode text.
            byte buffer;
            FileOutputStream outputStream = new FileOutputStream(outputFile, true);

            ArrayList<Integer> header = new ArrayList<Integer>();
            header.add(k);
            header.add(0);
            header.add(canonCodes.get(eof).length());

            for (Map.Entry<String, String> entry : canonCodes.entrySet()) {
                if (entry.getKey()!=eof){
                    header.add((int)(entry.getKey().charAt(0)));
                    header.add(entry.getValue().length());
                }
            }
            String secret ="";
            for (int i=0; i<message.length();i++){
                secret+=canonCodes.get(""+message.charAt(i));
            }
            System.out.println("secret: " + secret);

            for (int k=0; k<secret.length(); k+=8){
                if (k+8 <=secret.length()){
                    System.out.println(secret.substring(k, k+8));
                    header.add(Integer.parseInt(secret.substring(k, k+8), 2));
                    header.add(0);
                }
                else if (secret.length()-k+8 !=0){
                    String temp = secret.substring(k);
                    while (temp.length()<8);
                        temp+="0";
                    header.add(Integer.parseInt(temp, 2));
                }


            }
            for (int j=0; j<header.size(); j++){
                System.out.println(j +": "+header.get(j) + " " + Integer.toBinaryString(header.get(j)));
                buffer = header.get(j).byteValue();
                outputStream.write(buffer);
            }

            // write() writes as many bytes from the buffer
            // as the length of the buffer. You can also
            // use
            // write(buffer, offset, length)
            // if you want to write a specific number of
            // bytes, or only part of the buffer.

            // Always close files.
            outputStream.close();

            //System.out.println("Wrote " + header.length +" bytes");
        }
        catch(IOException ex) {
            System.out.println("Error writing file '" + outputFile + "'");

        }
    }
}
