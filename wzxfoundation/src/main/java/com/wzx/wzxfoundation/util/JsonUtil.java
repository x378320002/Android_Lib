package com.wzx.wzxfoundation.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;

/**
 * 用于解析json工具类
 */
public class JsonUtil {
    /**
     * 对象转换成json字符串,bean或者集合
     */
    public static Gson sGson = new GsonBuilder().disableHtmlEscaping().create();
    public static String toJson(Object obj) {
//        Gson gson = new Gson();
        return sGson.toJson(obj);
    }

    /**
     * json字符串转成对象，用于转化复杂的结构，如果map或者list集合
     */
    public static <T> T fromJson(String str, Type type) {
//        Gson gson = new Gson();
        return sGson.fromJson(str, type);
    }

    /**
     * json字符串转成对象，转换简单的bean
     */
    public static <T> T fromJson(String str, Class<T> type) {
//        Gson gson = new Gson();
        return sGson.fromJson(str, type);
    }

    public static <T> T fromJson(File file, Type type) throws FileNotFoundException, UnsupportedEncodingException {
        InputStreamReader reader = new InputStreamReader(new FileInputStream(file));
        return sGson.fromJson(reader, type);
    }

    public static <T> T fromJson(InputStream inputStream, Type type) throws FileNotFoundException, UnsupportedEncodingException {
        InputStreamReader reader = new InputStreamReader(inputStream);
        return sGson.fromJson(reader, type);
    }

    /**
     * 一般对象的转换
     */
//        public static void objectToJson(){
//            Person person = new Person();
//            person.setId(1);
//            person.setName("one");
//            //javabean转换成json字符串
//            String jsonStr = JsonUtil.toJson(person);
//            System.out.println(jsonStr);
//
//            //json字符串转换成javabean
//            Person newPerson = JsonUtil.fromJson(jsonStr, Person.class);
//            System.out.println(person == newPerson);
//            System.out.println(newPerson.getId()+","+newPerson.getName());
//        }

    /**
     * 复合结构数据转换(List)
     */
//    public static void listToJson(){
//        Person person1 = new Person();
//        person1.setId(1);
//        person1.setName("one");
//
//        Person person2 = new Person();
//        person2.setId(2);
//        person2.setName("two");
//
//        List<Person> list = new ArrayList<Person>();
//        list.add(person1);
//        list.add(person2);
//
//        //javabean转换成json字符串
//        String jsonStr = JsonUtil.toJson(list);
//        System.out.println(jsonStr);
//
//
//        //json字符串转换成javabean对象
//        List<Person> rtn = JsonUtil.fromJson(jsonStr, new TypeToken<List<Person>>(){}.getType());
//        for(Person person : rtn){
//            System.out.println(person.getId()+","+person.getName());
//        }
//    }

    /**
     * 复合结构数据转换(Map)
     */
//    public static void mapToJson(){
//        Person person1 = new Person();
//        person1.setId(1);
//        person1.setName("one");
//        Person person2 = new Person();
//        person2.setId(2);
//        person2.setName("two");
//
//        Map<Integer,Person> map = new HashMap<Integer,Person>();
//        map.put(person1.getId(), person1);
//        map.put(person2.getId(), person2);
//
//        //javabean转换成json字符串
//        String jsonStr = JsonUtil.toJson(map);
//        System.out.println(jsonStr);
//
//        //json字符串转换成Map对象
//        Map<Integer,Person> rtn = JsonUtil.fromJson(jsonStr, new TypeToken<Map<Integer,Person>>(){}.getType());
//        for(Entry<Integer, Person> entry : rtn.entrySet()){
//            Integer key = entry.getKey();
//            Person newPerson = entry.getValue();
//            System.out.println("key:"+key+","+newPerson.getId()+","+newPerson.getName());
//        }
//    }
}
