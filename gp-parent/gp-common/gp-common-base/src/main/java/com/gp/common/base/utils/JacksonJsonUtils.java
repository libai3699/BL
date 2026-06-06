package com.gp.common.base.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

/**
 * jackson序列化工具类
 *
 * @author axing
 * @version 1.0
 * @date 2023/10/31/031 14:42
 */
public class JacksonJsonUtils {

    // 定义jackson对象
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * 将对象转换成json字符串。
     * <p>Title: pojoToJson</p>
     * <p>Description: </p>
     *
     * @param data
     * @return
     */
    public static String objectToJson(Object data) {
        try {
            String string = MAPPER.writeValueAsString(data);
            return string;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将json结果集转化为对象
     *
     * @param jsonData json数据
     * @param beanType 对象中的object类型
     * @return
     */
    public static <T> T jsonToPojo(String jsonData, Class<T> beanType) {
        try {
            T t = MAPPER.readValue(jsonData, beanType);
            return t;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将json数据转换成pojo对象list
     * <p>Title: jsonToList</p>
     * <p>Description: </p>
     *
     * @param jsonData
     * @param beanType
     * @return
     */
    public static <T> List<T> jsonToList(String jsonData, Class<T> beanType) {
        JavaType javaType = MAPPER.getTypeFactory().constructParametricType(List.class, beanType);
        try {
            List<T> list = MAPPER.readValue(jsonData, javaType);
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


//    public static void main(String[] args) {
//         //Java内部类创建对象，要想直接创建内部类的对象，必须使用外部类的对象来创建内部类对象
//        List<Student> students = new ArrayList<Student>();
//        for (int i = 0; i < 20; i++) {
//            Student student = new Student();
//            student.setId(1008611 + i);
//            student.setName("张三三" + i);
//            student.setAddress("北京市西城区什刹海街道西什库大街31号院" + i);
//
//            // 1、将实体类对象转换为json格式
//            String objectToJson = JsonUtils.objectToJson(student);
//            System.out.println("1、将实体类对象转换为json格式: " + objectToJson);
//
//            students.add(student);
//        }
//
//        // 2、将Json格式的数据转换为实体类
//        String jsonToPojo = JsonUtils.objectToJson(students);
//        System.out.println("2、将Json格式的数据转换为实体类: " + jsonToPojo);
//
//        // 3、将json数据转换成pojo对象list
//        List<Student> jsonToList = JsonUtils.jsonToList(jsonToPojo, Student.class);
//        System.out.println("3、将json数据转换成pojo对象list: " + jsonToList.toString());
//    }
}
