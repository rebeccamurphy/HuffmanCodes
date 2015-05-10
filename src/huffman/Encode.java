package huffman;

/**
 * Created by Rebecca Murphy on 5/4/15.
 * Huffman Codes
 *
 */
import java.io.*;
import java.lang.reflect.Field;
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
            {
                String message = ReadFile(args[0]);
                String outputFile = args[1];
                EncodeToFile(message, outputFile);
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
     * @return byte array representation of the binary file
     */
    static String ReadFile(String path) throws IOException
    {
        Path p = Paths.get(path);
        return new String(Files.readAllBytes(p));
    }

    public static void EncodeToFile(String message, String outputFile){
        /*
        * Change some hashmaps to priority queues when trying to get a minimum
        * */

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
        System.out.println("k "+k);
        //create the normal tree
        TreeNode root;
        root = makeTree(frequency);
        root.printTree(0);
        TreeMap<String, String> codes = new TreeMap<String, String>();
        TreeMap<String, String> canonCodes = new TreeMap<String, String>();
        root.getCodes("", codes);
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
        writeFile(outputFile, message, canonCodes);
    }
    public static TreeNode makeTree(TreeMap<String, Integer> frequency){

        PriorityQueue<TreeNode> nodes = new PriorityQueue<TreeNode>();
        for (Map.Entry<String, Integer> entry : frequency.entrySet()) {
            nodes.add(new TreeNode(entry.getKey(), entry.getValue()));
        }
        while(nodes.size()>1){
            TreeNode min1 = nodes.poll();
            TreeNode min2 = nodes.poll();

            System.out.println("min1 " + min1);
            System.out.println("min2 " + min2);
            TreeNode parent = new TreeNode(min1.str+min2.str, min1.count+min2.count);
            min1.setParent(parent);
            min2.setParent(parent);
            System.out.println("parent " + parent);
            nodes.add(parent);
            System.out.println("Pair: " + min1 + ", " + min2);

        }
        return nodes.peek();
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
                if (entry.getValue().length()==max &&(int)(entry.getKey().toCharArray()[0]) <keyVal) {
                    key = entry.getKey();
                    keyVal =(int)(entry.getKey().toCharArray()[0]);
                }
            }
            array.add(key + codes.get(key));
            codes.remove(key);
        }
        return array;
    }
    public static void writeFile(String outputFile, String message,  TreeMap<String, String> canonCodes){

        try {
            byte buffer;
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            outputStream.write(k);
            outputStream = new FileOutputStream(outputFile, true);
            ArrayList<Integer> header = new ArrayList<Integer>();
            header.add(0);
            header.add(canonCodes.get(eof).length());

            for (Map.Entry<String, String> entry : canonCodes.entrySet()) {
                if (entry.getKey().equals(eof)){
                    header.add((int)(entry.getKey().charAt(0)));
                    header.add(entry.getValue().length());
                }
            }

            for (int j=0; j<header.size(); j++){
                System.out.println(j +": "+header.get(j) + " " + Integer.toBinaryString(header.get(j)));
                buffer = header.get(j).byteValue();
                outputStream.write(buffer);
            }
            StringBuilder sb = new StringBuilder();
            if (message.length()<257){
                for (int i=0; i<message.length();i++){
                    sb.append(canonCodes.get("" + message.charAt(i)));
                    while (sb.length()>=8){
                        outputStream.write(Integer.parseInt(sb.toString().substring(0,8), 2));
                        sb.delete(0,8);
                    }

                }
                if (sb.toString().isEmpty())
                    outputStream.write(0);
                else{
                    while (sb.length()!=8)
                        sb.append("0");
                    outputStream.write(Integer.parseInt(sb.toString(), 2));
                    outputStream.write(0);
                }
                    

            }

            else {
                final Field field = String.class.getDeclaredField("value");
                field.setAccessible(true);
                final char[] chars = (char[]) field.get(message);
                final int len = chars.length;

                for (int i=0; i<len; i++){
                    sb.append(canonCodes.get("" + chars[i]));
                    while (sb.length()>=8){
                            outputStream.write(Integer.parseInt(sb.toString().substring(0,8), 2));
                            sb.delete(0,8);
                    }
                }
                if (sb.toString().isEmpty())
                    outputStream.write(0);
                else{
                    while (sb.length()!=8)
                        sb.append("0");
                    outputStream.write(Integer.parseInt(sb.toString(), 2));
                    for (int i=0; i<canonCodes.get(eof).length(); i+=8)
                        outputStream.write(0);
                }

            }


            // Always close files.
            outputStream.close();

        }
        catch(IOException ex) {
            System.out.println("Error writing file '" + outputFile + "'");

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
