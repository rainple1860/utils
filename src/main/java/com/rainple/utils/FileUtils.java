package com.rainple.utils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @description: 文件工具类
 * @author: rainple
 * @create: 2019-05-29 11:43
 **/
public class FileUtils {

    public static final String DESC = "desc";
    public static final String ASC = "asc";

    /**
     * 复制文件
     * @param srcPath 源文件路径
     * @param destPath 目标文件路径
     * @return 复制结果 true | false
     * @throws IOException 异常
     */
    public static boolean copy(String srcPath,String destPath) throws IOException {
        FileInputStream in = new FileInputStream(srcPath);
        FileOutputStream out = new FileOutputStream(destPath);
        return copy(in,out);
    }

    /**
     * 复制文件
     * @param inputStream 输入流
     * @param outputStream 输出流
     * @return 结果
     * @throws IOException 异常
      */
    public static boolean copy(FileInputStream inputStream,FileOutputStream outputStream) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        FileChannel inChannel = inputStream.getChannel();
        while (inChannel.read(byteBuffer) > 0) {
            byteBuffer.flip();
            outputStream.getChannel().write(byteBuffer);
            byteBuffer.clear();
        }
        outputStream.close();
        inChannel.close();
        return true;
    }

    /**
     * 复制文件
     * @param srcFile 源文件
     * @param destFile 目标文件
     * @return 结果
     * @throws IOException 异常
     */
    public static boolean copy(File srcFile,File destFile) throws IOException {
        FileInputStream in = new FileInputStream(srcFile);
        FileOutputStream out = new FileOutputStream(destFile);
        return copy(in,out);
    }

    /**
     * 往文件中写入数据
     * @param path 文件路径
     * @param content 数据
     * @param charsetName 数据编码格式
     * @throws IOException 异常
     */
    public static void write(String path,String content,String charsetName) throws IOException {
        OutputStream outputStream = new FileOutputStream(path);
        outputStream.write(content.getBytes(charsetName));
    }

    /**
     * 写数据，默认编码格式utf-8
     * @param path 文件路径
     * @param content 数据
     * @throws IOException 异常
     */
    public static void write(String path,String content) throws IOException {
        write(path,content,"utf-8");
    }

    public static String read(String path,String charsetName) throws IOException {
        FileInputStream inputStream = new FileInputStream(path);
        return read1(charsetName, inputStream);
    }

    /**
     * 读文件
     * @param charsetName 编码格式
     * @param inputStream 输入流
     * @return 读取到的数据
     * @throws IOException 异常
     */
    private static String read1(String charsetName, FileInputStream inputStream) throws IOException {
        FileChannel channel = inputStream.getChannel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        StringBuilder stringBuffer = new StringBuilder(byteBuffer.capacity());
        while (channel.read(byteBuffer) > 0) {
            byteBuffer.flip();
            stringBuffer.append(Charset.forName(charsetName).decode(byteBuffer));
            byteBuffer.clear();
        }
        inputStream.close();
        return stringBuffer.toString();
    }

    /**
     * 读取文件，默认编码格式utf-8
     * @param path 文件的路径
     * @return 结果
     * @throws IOException 异常
     */
    public static String read(String path) throws IOException {
        return read(path,"utf-8");
    }

    /**
     * 读取文件
     * @param file 文件
     * @param charSetName 编码格式
     * @return 结果
     * @throws IOException 异常
     */
    public static String read(File file,String charSetName) throws IOException {
        return read1(charSetName,new FileInputStream(file));
    }

    /**
     * 读取文件，默认编码格式utf-8
     * @param file 文件
     * @return 数据
     * @throws IOException 异常
     */
    public static String read(File file) throws IOException {
        return read(file,"utf-8");
    }

    private static  ArrayList<Character> specialChar = new ArrayList<>();
    static {
        specialChar.add('\n');
        specialChar.add('\r');
        specialChar.add(' ');
    }

    /**
     * 统计文件字符出现的次数
     * @param inputStream 文件流
     * @param charSetName 文件编码格式
     * @param sort 排序
     * @return 结果集
     */
    private static List<Map.Entry<Character,Long>> countChar(FileInputStream inputStream,String charSetName,String sort) {
        Map<Character,Long> data = new HashMap<>();
        try {
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            FileChannel channel = inputStream.getChannel();
            while (channel.read(byteBuffer) > 0) {
                byteBuffer.flip();
                CharBuffer charBuffer = Charset.forName(charSetName).decode(byteBuffer);
                char[] charArray = charBuffer.array();
                for (char c : charArray) {
                    if (specialChar.contains(c)) continue;
                    Long num = data.get(c);
                    if (num == null)
                        data.put(c,1L);
                    else
                        data.put(c,++num);
                }
            }
            List<Map.Entry<Character,Long>> list = new ArrayList<>(data.entrySet());
            Collections.sort(list, new Comparator<Map.Entry<Character, Long>>() {
                @Override
                public int compare(Map.Entry<Character, Long> o1, Map.Entry<Character, Long> o2) {
                    if (ASC.equals(sort))
                        return o1.getValue().compareTo(o2.getValue());
                    else
                        return o2.getValue().compareTo(o1.getValue());
                }
            });
            return list;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /**
     * 统计文件字符出现的次数
     * @param path 文件路径
     * @param charSetName 文件编码格式
     * @param sort 排序
     * @return 结果集
     */
    public static List<Map.Entry<Character,Long>> countChar(String path,String charSetName,String sort) {
        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            return countChar(fileInputStream,charSetName,sort);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 统计文件字符出现的次数
     * @param file 文件
     * @param charSetName 文件编码格式
     * @param sort 排序
     * @return 结果集
     */
    public static List<Map.Entry<Character,Long>> countChar(File file,String charSetName,String sort) {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            countChar(fileInputStream,charSetName,sort);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 统计文件中出现的字符的次数，默认降序
     * @param path 文件路径
     * @param charSetName 问价编码格式
     * @return 结果集
     */
    public static List<Map.Entry<Character,Long>> countChar(String path,String charSetName) {
        return countChar(path,charSetName,DESC);
    }

    /**
     * 统计文件中出现的字符的次数，默认降序
     * @param file 文件
     * @param charSetName 问价编码格式
     * @return 结果集
     */
    public static List<Map.Entry<Character,Long>> countChar(File file,String charSetName) {
        return countChar(file,charSetName,DESC);
    }

    /**
     * 统计文件中出现的字符的次数，默认降序
     * @param fileInputStream 文件路径
     * @param charSetName 问价编码格式
     * @return 结果集
     */
    public static List<Map.Entry<Character,Long>> countChar(FileInputStream fileInputStream,String charSetName) {
        return countChar(fileInputStream,charSetName,DESC);
    }

    /**
     * 统计文件字符出现次数，默认编码格式utf-8
     * @param path 文件路径
     * @return 结果集
     */
    public static List<Map.Entry<Character,Long>> countChar(String path) {
        return countChar(path,"utf-8");
    }

    /**
     * 统计文件字符出现次数，默认编码格式utf-8
     * @param file 文件路径
     * @return 结果集
     */
    public static List<Map.Entry<Character,Long>> countChar(File file) {
        return countChar(file,"utf-8");
    }

    /**
     * 统计文件字符出现次数，默认编码格式utf-8
     * @param fileInputStream 文件路径
     * @return 结果集
     */
    public static List<Map.Entry<Character,Long>> countChar(FileInputStream fileInputStream) {
        return countChar(fileInputStream,"utf-8");
    }

    /**
     * 统计指定字符在文件中出现的次数
     * @param fileInputStream 文件流
     * @param specifiedChar 指定的需要统计的字符
     * @param charsetName 文件编码格式
     * @return 结果集
     */
    public static Map<Character,Long> findCharCountPresent(FileInputStream fileInputStream, Character specifiedChar, String charsetName) {
        Map<Character,Long> map = new HashMap<>();
        if (specifiedChar == null) {
            map.put(null,0L);
            return map;
        }
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        FileChannel fileChannel = fileInputStream.getChannel();
        Long count = 0L;
        try {
            while (fileChannel.read(byteBuffer) > 0) {
                byteBuffer.flip();
                CharBuffer charBuffer = Charset.forName(charsetName).decode(byteBuffer);
                char[] chars = charBuffer.array();
                for (char c : chars) {
                    if (c == specifiedChar)
                        count++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        map.put(specifiedChar,count);
        return map;
    }

    /**
     * 统计指定字符在文件中出现的次数
     * @param path 文件路径
     * @param specifiedChar 指定的需要统计的字符
     * @param charsetName 文件编码格式
     * @return 结果集
     */
    public static Map<Character,Long> findCharCountPresent(String path, Character specifiedChar, String charsetName) {
        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            return findCharCountPresent(fileInputStream,specifiedChar,charsetName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 统计指定字符在文件中出现的次数
     * @param file 文件
     * @param specifiedChar 指定的需要统计的字符
     * @param charsetName 文件编码格式
     * @return 结果集
     */
    public static Map<Character,Long> findCharCountPresent(File file, Character specifiedChar, String charsetName) {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            return findCharCountPresent(fileInputStream,specifiedChar,charsetName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 统计指定字符在文件中出现的次数，默认编码格式utf-8
     * @param fileInputStream 文件流
     * @param specifiedChar 指定的需要统计的字符
     * @return 结果集
     */
    public static Map<Character,Long> findCharCountPresent(FileInputStream fileInputStream, Character specifiedChar) {
        return findCharCountPresent(fileInputStream,specifiedChar,"utf-8");
    }

    /**
     * 统计指定字符在文件中出现的次数，默认编码格式utf-8
     * @param path 文件路径
     * @param specifiedChar 指定的需要统计的字符
     * @return 结果集
     */
    public static Map<Character,Long> findCharCountPresent(String path, Character specifiedChar) {
        return findCharCountPresent(path,specifiedChar,"utf-8");
    }

    /**
     * 统计指定字符在文件中出现的次数，默认编码格式utf-8
     * @param file 文件
     * @param specifiedChar 指定的需要统计的字符
     * @return 结果集
     */
    public static Map<Character,Long> findCharCountPresent(File file, Character specifiedChar) {
        return findCharCountPresent(file,specifiedChar,"utf-8");
    }

    /**
     * 查找文件中出现最少的字符
     * @param fileInputStream 文件输入流
     * @param charsetName 编码格式
     * @return 结果集
     */
    public static Map<Character,Long> findMinPresentCharacter(FileInputStream fileInputStream, String charsetName) {
        List<Map.Entry<Character, Long>> entries = countChar(fileInputStream, charsetName,ASC);
        if (entries == null || entries.size() == 0)
            return null;
        Map.Entry<Character, Long> entry = entries.get(0);
        Map<Character,Long> hashMap = new HashMap<>();
        hashMap.put(entry.getKey(),entry.getValue());
        return hashMap;
    }

    public static Map<Character,Long> findMinPresentCharacter(FileInputStream fileInputStream) {
        return findMinPresentCharacter(fileInputStream,"utf-8");
    }

    public static Map<Character,Long> findMinPresentCharacter(File file, String charsetName) {
        try {
            return findMinPresentCharacter(new FileInputStream(file),charsetName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Map<Character,Long> findMinPresentCharacter(File file) {
        try {
            return findMinPresentCharacter(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Map<Character,Long> findMinPresentCharacter(String path, String charsetName) {
        try {
            return findMinPresentCharacter(new FileInputStream(path),charsetName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Map<Character,Long> findMinPresentCharacter(String path) {
        return findMinPresentCharacter(path,"utf-8");
    }

    /**
     * 查找文件中出现最多的字符
     * @param fileInputStream 文件输入流
     * @param charsetName 编码格式
     * @return 结果集
     */
    public static Map<Character,Long> findMaxPresentCharacter(FileInputStream fileInputStream, String charsetName) {
        List<Map.Entry<Character, Long>> entries = countChar(fileInputStream, charsetName);
        if (entries == null || entries.size() == 0)
            return null;
        Map.Entry<Character, Long> entry = entries.get(0);
        Map<Character,Long> hashMap = new HashMap<>();
        hashMap.put(entry.getKey(),entry.getValue());
        return hashMap;
    }

    /**
     * 查找文件中出现最多的字符
     * @param fileInputStream 文件输入流
     * @return 结果集
     */
    public static Map<Character,Long> findMaxPresentCharacter(FileInputStream fileInputStream) {
        List<Map.Entry<Character, Long>> entries = countChar(fileInputStream, "utf-8");
        if (entries == null || entries.size() == 0)
            return null;
        Map.Entry<Character, Long> entry = entries.get(0);
        Map<Character,Long> hashMap = new HashMap<>();
        hashMap.put(entry.getKey(),entry.getValue());
        return hashMap;
    }

    /**
     * 查找文件中出现最多的字符
     * @param file 文件
     * @param charsetName 编码格式
     * @return 结果集
     */
    public static Map<Character,Long> findMaxPresentCharacter(File file, String charsetName) {
        try {
            return findMaxPresentCharacter(new FileInputStream(file),charsetName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查找文件中出现最多的字符
     * @param filePath 文件路径
     * @param charsetName 编码格式
     * @return 结果集
     */
    public static Map<Character,Long> findMaxPresentCharacter(String filePath, String charsetName) throws FileNotFoundException {
        return findMaxPresentCharacter(new FileInputStream(filePath),charsetName);
    }

    /**
     * 查找文件中出现最多的字符，默认编码格式utf-8
     * @param filePath 文件路径
     * @return 结果集
     */
    public static Map<Character,Long> findMaxPresentCharacter(String filePath) throws FileNotFoundException {
        return findMaxPresentCharacter(filePath,"utf-8");
    }

    /**
     * 查找文件中出现最多的字符，默认编码格式utf-8
     * @param file 文件
     * @return 结果集
     */
    public static Map<Character,Long> findMaxPresentCharacter(File file) {
        return findMaxPresentCharacter(file,"utf-8");
    }

    /**
     * 统计文件中出现的英文单词的频次
     * @param fileInputStream 文件输入流
     * @param charsetName 字符集，默认utf-8
     * @param sort 按出现的次数排序，默认降序
     * @param ignoreCase 是否忽略大小写，默认忽略
     * @return 结果集
     * @throws IOException 异常
     */
    public static List<Map.Entry<String,Long>> countEnglishWord(FileInputStream fileInputStream,String charsetName,String sort,boolean ignoreCase) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        FileChannel channel = fileInputStream.getChannel();
        Map<String,Long> map = new HashMap<>(1024);
        while (channel.read(byteBuffer) > 0) {
            byteBuffer.flip();
            CharBuffer charBuffer = Charset.forName(charsetName).decode(byteBuffer);
            String s = String.valueOf(charBuffer);
            String[] split = s.split(" ");
            for (String str : split) {
                if (isEnglish(str) && !"".equals(str)) {
                    record(ignoreCase, map, str);
                }
                if (!isChinese(str) && !isEnglish(str)) {
                    List<String> filter = englishWordFilter(str);
                    for (String w : filter) {
                        record(ignoreCase,map,w);
                    }
                }
            }
            byteBuffer.clear();
        }
        List<Map.Entry<String,Long>> list = new ArrayList<>(map.size());
        list.addAll(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Long>>() {
            @Override
            public int compare(Map.Entry<String, Long> o1, Map.Entry<String, Long> o2) {
                if (ASC.equals(sort))
                    return o1.getValue().compareTo(o2.getValue());
                else
                    return o2.getValue().compareTo(o1.getValue());
            }
        });
        return list;
    }

    public static List<Map.Entry<String,Long>> countEnglishWord(FileInputStream fileInputStream,String charsetName,String sort) throws IOException {
        return countEnglishWord(fileInputStream,charsetName,sort,false);
    }

    public static List<Map.Entry<String,Long>> countEnglishWord(FileInputStream fileInputStream,String charsetName) throws IOException {
        return countEnglishWord(fileInputStream,charsetName,DESC);
    }

    public static List<Map.Entry<String,Long>> countEnglishWord(FileInputStream fileInputStream) throws IOException {
        return countEnglishWord(fileInputStream,"utf-8");
    }

    public static List<Map.Entry<String,Long>> countEnglishWord(File file,String charsetName) throws IOException {
        return countEnglishWord(new FileInputStream(file),charsetName,DESC);
    }

    public static List<Map.Entry<String,Long>> countEnglishWord(File file) throws IOException {
        return countEnglishWord(new FileInputStream(file));
    }

    public static List<Map.Entry<String,Long>> countEnglishWord(String path) throws IOException {
        return countEnglishWord(new FileInputStream(path));
    }

    public static List<Map.Entry<String,Long>> countEnglishWord(String path,String charsetName) throws IOException {
        return countEnglishWord(new FileInputStream(path),charsetName);
    }

    public static Map.Entry<String,Long> findMaxEnglishWordPresent(FileInputStream fileInputStream,String charsetName) {
        try {
            List<Map.Entry<String, Long>> list = countEnglishWord(fileInputStream, charsetName);
            return (list == null || list.size() == 0) ? null : list.get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Map.Entry<String,Long> findMaxEnglishWordPresent(FileInputStream fileInputStream) {
        return findMaxEnglishWordPresent(fileInputStream,"utf-8");
    }

    public static Map.Entry<String,Long> findMaxEnglishWordPresent(File file) {
        try {
            return findMaxEnglishWordPresent(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Map.Entry<String,Long> findMaxEnglishWordPresent(String path) {
        try {
            return findMaxEnglishWordPresent(new FileInputStream(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 在文件中查找指定字符串出现的次数，默认字符集utf-8
     * @param fileInputStream 文件
     * @param word 查找的字符串
     * @param charsetName 字符集
     * @return 出现的次数
     */
    public static int findWordCountPresent(FileInputStream fileInputStream,String word,String charsetName) {
        if (word == null)
            return 0;
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        FileChannel channel = fileInputStream.getChannel();
        int count = 0;
        try {
            while (channel.read(byteBuffer) > 0) {
                byteBuffer.flip();
                CharBuffer charBuffer = Charset.forName(charsetName).decode(byteBuffer);
                char[] words = word.toCharArray();
                char[] chars = charBuffer.array();
                count += wordCount(words, chars);
                byteBuffer.clear();
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
        return count;
    }

    private static int wordCount(char[] words, char[] chars) {
        int count = 0;
        int clen = chars.length;
        int wlen = words.length;
        for (int i = 0; i < chars.length;) {
            char f = words[0];
            boolean match = false;
            if (f == chars[i]) {
                int ci = i;
                int wi = 0;
                int n = 0;
                while ( ci < clen && wi < wlen && chars[ci++] == words[wi++])
                    n++;
                if (n == wlen) {
                    match = true;
                    count++;
                }
            }
            if (match)
                i += wlen;
            else
                i++;
        }
        return count;
    }

    public static int findWordCountPresent(FileInputStream fileInputStream,String word) {
        return findWordCountPresent(fileInputStream,word,"utf-8");
    }

    public static int findWordCountPresent(File file,String word,String charsetName) {
        try {
            return findWordCountPresent(new FileInputStream(file),word,charsetName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int findWordCountPresent(File file,String word) {
        return findWordCountPresent(file,word,"utf-8");
    }

    public static int findWordCountPresent(String path,String word,String charsetName) {
        try {
            return findWordCountPresent(new FileInputStream(path),word,charsetName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 查找字符串中的指定字符串的长度
     * @param srcStr 字符串
     * @param specifiedWord 指定的字符串
     * @return 次数
     */
    public static int findWordCount(String srcStr,String specifiedWord) {
        char[] chars = srcStr.toCharArray();
        char[] words = specifiedWord.toCharArray();
        return wordCount(words,chars);
    }

    public static int findWordCountPresent(String path,String word) {
        return findWordCountPresent(path, word,"utf-8");
    }

    public static boolean isPresent(FileInputStream fileInputStream,String word,String charsetName) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        FileChannel channel = fileInputStream.getChannel();
        try {
            while (channel.read(byteBuffer) > 0) {
                byteBuffer.flip();
                CharBuffer charBuffer = Charset.forName(charsetName).decode(byteBuffer);
                int count = findWordCount(charBuffer.toString(), word);
                if (count > 0)
                    return true;
                byteBuffer.clear();
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
       return false;
    }

    public static boolean isPresent(FileInputStream fileInputStream,String word) {
        return isPresent(fileInputStream,word,"utf-8");
    }

    public static boolean isPresent(File file,String word,String charsetName) {
        try {
            return isPresent(new FileInputStream(file),word,charsetName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isPresent(File file,String word) {
        return isPresent(file,word,"utf-8");
    }

    public static boolean isPresent(String path,String word,String charsetName) {
        try {
            return isPresent(new FileInputStream(path),word,charsetName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isPresent(String path,String word) {
        return isPresent(path,word,"utf-8");
    }

    private static void record(boolean ignoreCase, Map<String, Long> map, String str) {
        if (ignoreCase)
            str = str.toLowerCase();
        Long v = map.get(str);
        if (v == null)
            map.put(str,1L);
        else
            map.put(str,++v);
    }

    private static boolean isEnglish(String str) {
        return str.matches("^[a-zA-Z]*");
    }

    private static boolean isChinese(String str) {
        String regEx = "[\\u4e00-\\u9fa5]+";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        if(m.find())
            return true;
        else
            return false;
    }

    /**
     * 在中英混合的字符串中筛选出英文单词
     * @param str 被筛选的字符串
     * @return 英文单词集合
     */
    private static List<String> englishWordFilter(String str) {
        //去掉换行符
        str = str.replaceAll("\n","").replaceAll("\r","");
        char[] chars = str.toCharArray();
        List<String> english = new ArrayList<>();
        //英文单词的第一个字母
        int head = 0;
        for (int i = 0,len = chars.length; i < len; i++) {
            String value = String.valueOf(chars[i]);
            if (isEnglish(value)) {
                if (i == 0) continue;
                char prevChar = chars[i - 1];
                if (FileUtils.isChinese(String.valueOf(prevChar))) {
                    head = i;
                }
            }else {
                if (i == 0) {
                    head++;
                    continue;
                }
                char prevChar = chars[i - 1];
                if (isEnglish(String.valueOf(prevChar))) {
                    english.add(str.substring(head,i));
                }
                head = i + 1;
            }
            if (i == len -1 && head < i) {
                english.add(str.substring(head,i + 1));
            }
        }
        return english;
    }

}
