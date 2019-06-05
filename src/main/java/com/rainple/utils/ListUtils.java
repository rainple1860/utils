package com.rainple.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * @description:
 * @author: rainple
 * @create: 2019-06-05 09:23
 **/
public class ListUtils {

    /**
     * 对集合进行分组
     * @param src 原集合
     * @param groupSize 每个组元素数量
     * @param total 总元素数量
     * @return 分好组的集合
     */
    public static List<List> split(List src,int groupSize,int total) {
        List<List> lists = new ArrayList<>();
        for (int index = 0;index < total; index += groupSize) {
            if (index + groupSize > total) {
                lists.add(src.subList(index,total));
            } else {
                lists.add(src.subList(index,index + groupSize));
            }
        }
        return lists;
    }

    /**
     * 判断集合是否为空
     * @param list 集合
     * @return true | false
     */
    public static boolean isBlank(List list) {
        return list == null || list.size() == 0;
    }

    /**
     * 判断集合是否不为空
     * @param list 集合
     * @return true | false
     */
    public static boolean isNotBlank(List list) {
        return !isBlank(list);
    }

    /**
     * 根据list集合中对象某个属性进行排序，可排序字段类型：char、short、int、long、float、double、（包括封装类型）、String、Date
     * @param list 排序集合
     * @param direct 排序顺序，默认降序
     * @param field 排序属性字段
     */
    public static void sort(List<?> list,final String direct,final String field) {
        list.sort(new Comparator<Object>() {
            int ret;
            @Override
            public int compare(Object o1, Object o2) {
                try {
                    Field f = o1.getClass().getDeclaredField(field);
                    f.setAccessible(true);
                    Class<?> type = f.getType();
                    if (type == int.class || type == Integer.class)
                        ret = Integer.compare(f.getInt(o1), f.getInt(o2));
                    else if (type == double.class || type == Double.class)
                        ret = Double.compare(f.getDouble(o1),f.getDouble(o2));
                    else if (type == float.class || type == Float.class)
                        ret = Float.compare(f.getFloat(o1),f.getFloat(o2));
                    else if (type == short.class || type == Short.class)
                        ret = Short.compare(f.getShort(o1),f.getShort(o2));
                    else if (type == char.class || type == Character.class)
                        ret = Character.compare(f.getChar(o1),f.getChar(o2));
                    else if (type == long.class || type == Long.class)
                        ret = Long.compare(f.getLong(o1),f.getLong(o2));
                    else if (type == Date.class)
                        ret = ((Date)f.get(o1)).compareTo((Date) f.get(o2));
                    else if (type == String.class)
                        ret = String.valueOf(o1).compareTo(String.valueOf(o2));
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
                if ("ASC".equalsIgnoreCase(direct))
                    return ret;
                else
                    return -ret;
            }
        });
    }

}
