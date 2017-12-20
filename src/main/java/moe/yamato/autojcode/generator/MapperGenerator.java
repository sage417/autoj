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

        String sql = "CREATE TABLE `t_candidate_wx_biz` (" +
                "  `candidateId` int(11) NOT NULL DEFAULT '0' COMMENT '应聘信息Id'," +
                "  `studentId` int(11) NOT NULL DEFAULT '0' COMMENT '学员id'," +
                "  `companyId` int(11) NOT NULL DEFAULT '0' COMMENT '公司id'," +
                "  `ctime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'," +
                "  `mtime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间'," +
                "  PRIMARY KEY (`candidateId`)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";

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
