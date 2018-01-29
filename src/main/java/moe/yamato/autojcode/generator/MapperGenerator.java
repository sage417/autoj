package moe.yamato.autojcode.generator;

import com.google.common.base.CaseFormat;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import freemarker.template.TemplateException;
import moe.yamato.autojcode.domain.Property;
import moe.yamato.autojcode.utils.FreemarkerUtil;
import moe.yamato.autojcode.utils.SqlUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Package: moe.yamato.autojcode
 * Author: mac
 * Date: 2017/11/16
 */
public class MapperGenerator {

    public static final Set<String> COLUMNS_NEEDNT_INSERT = Sets.newHashSet("id", "ctime", "mtime");
    public static final Set<String> COLUMNS_NEEDNT_UPDATE = Sets.newHashSet("id", "cuser", "ctime", "mtime");

    public static void generateMapper(
            String namespace, String packageName, String className, String tableName,
            Set<String> columns, String primaryKey
    ) throws IOException, TemplateException {

        Map<String, Object> dataModel = Maps.newHashMap();
        dataModel.put("resultType", packageName + "." + className);
        dataModel.put("namespace", namespace);
        dataModel.put("tableName", tableName);
        dataModel.put("columns", columns);
        dataModel.put("pk", primaryKey);
        dataModel.put("insertColumns", Sets.difference(columns, COLUMNS_NEEDNT_INSERT));
        dataModel.put("updateColumns", Sets.difference(columns, COLUMNS_NEEDNT_UPDATE));

        try (final BufferedWriter bufferedWriter = Files.newBufferedWriter(
                Paths.get(".", className + "Mapper.xml"),
                Charset.forName("utf-8"),
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING)
        ) {
            FreemarkerUtil.getTemplate("mapper.xml.ftl").process(dataModel, bufferedWriter);
        }
    }

    public static void main(String[] args) throws IOException, TemplateException {

        String sql =  "CREATE TABLE `t_plan_attendance_time` (\n" +
                "  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键id',\n" +
                "  `planId` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '外键-实施计划id',\n" +
                "  `beginTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '签到开始时间',\n" +
                "  `endTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '签到结束时间',\n" +
                "  `ctime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',\n" +
                "  `mtime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',\n" +
                "  `cuser` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '创建人',\n" +
                "  `muser` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '修改人',\n" +
                "  `status` tinyint(4) unsigned NOT NULL DEFAULT '0' COMMENT '状态(0为已删除,1为可用)',\n" +
                "  PRIMARY KEY (`id`)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='签到计划表'";

        final Set<Property> columns = SqlUtils.getProperties(sql);

        final String tableName = SqlUtils.getTableDescriber(sql).getTableName();
        String className = getClassName(tableName, "t_");

        generateMapper("", "com.yamato.domain", className, tableName,
                       Sets.newLinkedHashSet(columns.stream().map(Property::getName).collect(Collectors.toList())), SqlUtils.getTableDescriber(sql).getPrimaryKeys().get(0));
    }

    private static String getClassName(String tableName, String tableNamePrefix) {

        String tmp = tableName;

        if (tableNamePrefix.equals(Strings.commonPrefix(tableName, tableNamePrefix))) {
            tmp = tableName.substring(tableNamePrefix.length());
        }

        return CaseFormat.LOWER_UNDERSCORE.converterTo(CaseFormat.UPPER_CAMEL).convert(tmp);
    }
}
