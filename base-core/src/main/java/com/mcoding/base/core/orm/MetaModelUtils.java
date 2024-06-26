package com.mcoding.base.core.orm;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ReflectUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * 模型元数据工具类
 *
 * @author wzt on 2020/2/11.
 * @version 1.0
 */
public class MetaModelUtils {

    private static Map<String, Map<String, MetaModelField>> classMetaMapCache = new WeakHashMap<>();

    /**
     * 根据Class定义生成模型属性
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> Map<String, MetaModelField> generateMetaModelField(Class<T> clazz) {

        // 命中缓存，则直接返回
        Map<String, MetaModelField> classMeta = classMetaMapCache.get(clazz.getName());
        if (CollectionUtil.isNotEmpty(classMeta)) {
            return classMeta;
        }

        Field[] fields = ReflectUtil.getFields(clazz);
        Map<String, MetaModelField> result = new HashMap<>(fields.length);

        for (Field field : fields) {
            TableField tableField = field.getAnnotation(TableField.class);
            TableId tableId = field.getAnnotation(TableId.class);

            if (tableField == null && tableId == null) {
                continue;
            }

            Keyword keyWord = field.getAnnotation(Keyword.class);
            Like like = field.getAnnotation(Like.class);
            OrderByAsc orderByAsc = field.getAnnotation(OrderByAsc.class);
            OrderByDesc orderByDesc = field.getAnnotation(OrderByDesc.class);

            String tableFieldName = tableField != null ? tableField.value() : tableId.value();

            MetaModelField metaModelField = new MetaModelField();
            metaModelField.setClassFieldName(field.getName());
            metaModelField.setTableFieldName(tableFieldName);
            metaModelField.setModelFieldType(field.getType().getTypeName());
            metaModelField.setKeyWorldSearch(keyWord != null);
            metaModelField.setLikeSearch(like != null);
            metaModelField.setOrderByAsc(orderByAsc != null);
            metaModelField.setOrderByDesc(orderByDesc != null);

            result.put(field.getName(), metaModelField);
        }

        classMetaMapCache.put(clazz.getName(), result);

        return result;
    }

}
